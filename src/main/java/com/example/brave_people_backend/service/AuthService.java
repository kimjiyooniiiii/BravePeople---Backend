package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.entity.Email;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.jwt.TokenProvider;
import com.example.brave_people_backend.repository.EmailRepository;
import com.example.brave_people_backend.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final JavaMailSender javaMailSender;

    // 회원가입 service
    @Transactional
    public void signup(SignupRequestDto signupRequestDto){

        // 1. 아이디 중복체크
        if (memberRepository.existsByUsername(signupRequestDto.getUsername())) {
            throw new CustomException("아이디 중복");
        }

        // 2. 닉네임 중복체크
        if (memberRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new CustomException("닉네임 중복");
        }

        // 3. 이메일 미인증시 예외 발생
        Long emailId = signupRequestDto.getEmailId();
        if (emailId == null) {
            throw new CustomException("이메일 미인증");
        }
        Email emailEntity = emailRepository.findById(emailId).orElseThrow(
                () -> new CustomException("이메일 미인증"));
        if (!emailEntity.isAuthStatus()) {
            throw new CustomException("이메일 미인증");
        }

        // 4. 중복된 Member가 없을 경우, DB 저장
        Member savedMember = memberRepository.save(signupRequestDto.toMember(passwordEncoder));

        // 5. 가입이 완료되었으므로 EMAIL 테이블에서 똑같은 EMAIL 모두 삭제
        emailRepository.deleteByEmailAddress(savedMember.getEmail());
    }

    // 로그인 service
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // loginRequestDto를 UsernamePasswordAuthenticationToken 형식으로 변경
        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthentication();

        //authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Member DB에서 사용자 검색
        Member member = memberRepository.findById(Long.parseLong(authenticate.getName()))
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        // Access Token, Refresh Token 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authenticate);

        // 회원 DB의 Refresh Token 업데이트
        member.changeRefreshToken(tokenDto.getRefreshToken());

        return LoginResponseDto.builder()
                .memberId(authenticate.getName())
                .nickname(member.getNickname())
                .lat(String.valueOf(member.getLat()))
                .lng(String.valueOf(member.getLng()))
                .profileImg(member.getProfileImg())
                .tokenDto(tokenDto)
                .build();
    }

    // refresh token으로 access token 재발급 받기
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 사용자의 가입 아이디 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        String memberId = authentication.getName();

        if(!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new JwtException("Refresh Token 만료");
        }

        // 사용자가 직접 로그아웃을 했을 경우
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new CustomException("로그아웃 된 사용자입니다."));

        // DB에 저장된 refreshToken과 사용자가 입력한 refreshToken 비교
        if(!member.getRefreshToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new InsufficientAuthenticationException("Refresh Token의 정보가 일치하지 않습니다.");
        }

        // refreshToken이 확인되면 새로운 token 발급
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // DB에 refreshToken 업데이트
        member.changeRefreshToken(tokenDto.getRefreshToken());

        return tokenDto;
    }

    // 이메일로 Member 찾기
    @Transactional
    public UsernameResponseDto findByEmail(String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 이메일"));
        return UsernameResponseDto.of((findMember.getUsername()).
                substring(0, (findMember.getUsername()).length()-3) + "***");
    }

    @Transactional
    public Long emailCheckAndSendMail(String email) {
        // 1. MEMBER 테이블에서 이메일 중복체크 먼저
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException("이메일 중복");
        }

        // 2. EMAIL 테이블에서 status = true인 레코드가 있는지 확인
        // status = true인 레코드가 있을 경우 중복이므로 400 에러 반환
        if (emailRepository.findByEmailAddress(email).stream().anyMatch(Email::isAuthStatus)) {
            throw new CustomException("이미 가입 진행 중인 이메일");
        }

        // 3. Email Entity 생성 및 테이블에 저장
        Email emailEntity = Email.builder()
                .emailAddress(email)
                .authCode(generateAuthCode())
                .authStatus(false)
                .build();

        emailRepository.save(emailEntity);

        // 이메일 전송 및 전송 실패시 예외 처리
        try {
            sendSignupMail(emailEntity);
            return emailEntity.getEmailId();
        } catch (MessagingException e) {
            throw new CustomException("이메일 전송 오류");
        }

    }

    //테이블의 authCode와 getParameter로 넘어온 authCode를 비교하고 동일하면 authStatus=true 변경
    @Transactional
    public String codeConfirm(Long emailId, int authCode) {
        Optional<Email> emailEntity = emailRepository.findById(emailId); //테이블의 emailEntity

        if (emailEntity.isEmpty()) {
            return "유효하지 않은 이메일 주소입니다. URL 주소를 확인하여 주세요.";
        }

        if (emailEntity.get().getAuthCode() == authCode) {
            emailEntity.get().onAuthStatus(); // authCode가 일치하면 authStatus = true 변경
            return "이메일 인증을 완료했습니다. 홈페이지로 이동해 로그인하여 주시기 바랍니다.";
        } else {
            return "유효하지 않은 인증코드입니다. URL 주소를 확인하여 주세요.";
        }
    }

    @Transactional
    public void findPasswordAndSendMail(String username, String email) {
        // 1. 아이디 체크
        Member findMember = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException("존재하지 않는 아이디"));
        // 2. 이메일 체크
        if (!findMember.getEmail().equals(email)) { throw new CustomException("존재하지 않는 이메일"); }
        // 3. Email Entity 생성 및 테이블에 저장
        Email emailEntity = Email.builder()
                .emailAddress(email)
                .authCode(generateAuthCode())
                .authStatus(false)
                .build();
        emailRepository.save(emailEntity);
        // 4. 재설정 링크(:3000) 전송
        try {
            sendFindPwMail(emailEntity);
        } catch (MessagingException e) {
            throw new CustomException("이메일 전송 오류");
        }
    }


    public void sendSignupMail(Email emailEntity) throws MessagingException {
        String fromMail = "brave.knu@gmail.com"; //email-config에 설정한 자신의 이메일 주소(보내는 사람)
        String toMail = emailEntity.getEmailAddress(); //받는 사람
        String title = "[용감한원정대] 회원가입 인증 링크"; //제목
        String authLink =
                "http://13.209.77.50:8080/auth/code-confirm?id=" + emailEntity.getEmailId()
                        + "&code=" + emailEntity.getAuthCode();
        String text =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "<div style=\"margin:100px;\">\n" +
                        "    <h1> 안녕하세요.</h1>\n" +
                        "    <h1> 용감한원정대 BravePeople 입니다.</h1>\n" +
                        "    <br>\n" +
                        "        <p> 아래 링크를 클릭해 이메일 인증을 완료해 주세요.</p>\n" +
                        "    <br>\n" +
                        "    <div align=\"center\" style=\"border:1px solid black; font-family:verdana;\">\n" +
                        "    <h3> " + "<a href=\"" + authLink + "\">" + authLink + "</h3>\n" +
                        "    </div>\n" +
                        "    <br/>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(fromMail); //보내는 이메일
        message.addRecipients(MimeMessage.RecipientType.TO, toMail); //보낼 이메일 설정
        message.setSubject(title); //제목 설정
        message.setText(text, "utf-8", "html");

        javaMailSender.send(message);
    }

    public void sendFindPwMail(Email emailEntity) throws MessagingException {
        String fromMail = "brave.knu@gmail.com"; //email-config에 설정한 자신의 이메일 주소(보내는 사람)
        String toMail = emailEntity.getEmailAddress(); //받는 사람
        String title = "[용감한원정대] 비밀번호 찾기 링크"; //제목
        String authLink =
                "http://localhost:3000/resetpw?emailid=" + emailEntity.getEmailId()
                        + "&code=" + emailEntity.getAuthCode();
        String text =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "<div style=\"margin:100px;\">\n" +
                        "    <h1> 안녕하세요.</h1>\n" +
                        "    <h1> 용감한원정대 BravePeople 입니다.</h1>\n" +
                        "    <br>\n" +
                        "        <p> 아래 링크를 클릭해 비밀번호 찾기를 계속해 주세요.</p>\n" +
                        "    <br>\n" +
                        "    <div align=\"center\" style=\"border:1px solid black; font-family:verdana;\">\n" +
                        "    <h3> " + "<a href=\"" + authLink + "\">" + authLink + "</h3>\n" +
                        "    </div>\n" +
                        "    <br/>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(fromMail); //보내는 이메일
        message.addRecipients(MimeMessage.RecipientType.TO, toMail); //보낼 이메일 설정
        message.setSubject(title); //제목 설정
        message.setText(text, "utf-8", "html");

        javaMailSender.send(message);
    }

    public int generateAuthCode() {
        return new Random().nextInt(888888) + 111111;
    }
}
