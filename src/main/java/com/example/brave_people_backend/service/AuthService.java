package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.RefreshToken;
import com.example.brave_people_backend.exception.DuplicatedMemberException;
import com.example.brave_people_backend.jwt.TokenProvider;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.repository.RefreshTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JavaMailSender javaMailSender;

    // 회원가입 service
    @Transactional
    public void signup(SignupRequestDto signupRequestDto){

        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();

        // 중복된 아이디, 닉네임이 있을 경우 예외 발생
        if(!memberRepository.findByUsernameOrNickname(username, nickname).isEmpty()) {
            throw new DuplicatedMemberException();
        }

        // 중복된 Member가 없을 경우, DB 저장
        memberRepository.save(signupRequestDto.toMember(passwordEncoder));
    }

    // 로그인 service
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // loginRequestDto를 UsernamePasswordAuthenticationToken 형식으로 변경
        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthentication();

        //authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Access Token, Refresh Token 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authenticate);

        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(authenticate.getName())
                .refreshToken(tokenDto.getRefreshToken())
                .build();


        // refresh token -> db 저장
        refreshTokenRepository.save(refreshToken);
        Member member = memberRepository.findById(Long.parseLong(authenticate.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .memberId(authenticate.getName())
                .nickname(member.getNickname())
                .lat(String.valueOf(member.getLat()))
                .lng(String.valueOf(member.getLng()))
                .tokenDto(tokenDto)
                .build();

        return loginResponseDto;
    }

    // refresh token으로 access token 재발급 받기
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 사용자의 가입 아이디 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        String memberId = authentication.getName();

        if(!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 사용자가 직접 로그아웃을 했을 경우
        RefreshToken refreshToken = refreshTokenRepository.findRefreshToken(memberId)
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // redis에 저장된 refreshToken과 사용자가 입력한 refreshToken 비교
        if(!refreshToken.getTokenNumber().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token의 정보가 일치하지 않습니다.");
        }

        // refreshToken이 확인되면 새로운 token 발급
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // redis에 저장된 refreshToken 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }

    // 이메일로 Member 찾기
    @Transactional
    public UsernameResponseDto findByEmail(String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일"));
        return UsernameResponseDto.of(findMember);
    }

    @Transactional
    public void confirmEmail(String email) {
        // 이메일 중복체크 먼저
        if (memberRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 중복");
        }
    }

    public void sendMail(String email) throws MessagingException {
        String fromMail = "brave.knu@gmail.com"; //email-config에 설정한 자신의 이메일 주소(보내는 사람)
        String toMail = email; //받는 사람
        String title = "[용감한원정대] 회원가입 인증 링크"; //제목
        String authLink = "http://13.209.77.50:8080/auth/mailcode?id=";// + email + "&authCode=" + member.();
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

    //테이블의 authCode와 getParameter로 넘어온 authCode를 비교하고 동일하면 update authStatus=true 변경
    public String authMailCode(int emailId, int authCode) {
        return null;
    }
}
