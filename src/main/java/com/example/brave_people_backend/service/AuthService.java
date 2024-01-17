package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.RefreshToken;
import com.example.brave_people_backend.jwt.TokenProvider;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입 service
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto){
        if(memberRepository.existsByUsername(signupRequestDto.getUsername())){
            throw new RuntimeException("이미 가입되어 있는 아이디 입니다.");
        }

        Member member = signupRequestDto.toMember(passwordEncoder);
        return SignupResponseDto.of(memberRepository.save(member));
    }

    // 로그인 service
    @Transactional
    public TokenDto login(LoginRequestDto loginRequestDto) {
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

        return tokenDto;
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

        // refreshTokenRepository에 정보가 없으면 로그아웃 됨 -> 다시 로그인 해야함
        RefreshToken refreshToken = refreshTokenRepository.findLatestRefreshToken(memberId)
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // redis에 저장된 refreshToken과 사용자가 입력한 refreshToken 비교
        if(!refreshToken.getRefreshToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token의 정보가 일치하지 않습니다.");
        }

        // refreshToken이 확인되면 새로운 token 발급
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // redis에 저장된 refreshToken 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}
