package com.example.brave_people_backend.config;

import com.example.brave_people_backend.jwt.*;
import com.example.brave_people_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final MemberRepository memberRepository;

    // 비밀번호 암호화 저장
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/favicon.ico");   // 웹 페이지 아이콘 접근 허용
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                // stateless한 rest api를 개발할 것이므로 csrf 공격에 대한 옵션은 꺼둔다.
                .csrf(AbstractHttpConfigurer::disable)

                // 인증이 안되었거나 권한이 없으면 예외처리
                .exceptionHandling((e)-> e
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler))

                // 세션 기능을 사용하지 않으므로 STATELESS 설정
                .sessionManagement((s)->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL 접근 권한 설정
                .authorizeHttpRequests((a) -> a
                        .requestMatchers(HttpMethod.GET, "/posts").permitAll()
                        .requestMatchers("/","/auth/**", "/member/pw").permitAll()    // 로그인, 회원가입은 접근 허용
                        .anyRequest().authenticated()                       // 나머지 페이지는 인증시 허용
                )

                // Filtering 순서 : JwtExceptionFilter -> JwtFilter -> UsernamePasswordAuthenticationFilter
                .addFilterBefore(new JwtFilter(tokenProvider, memberRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class)
                .build();
    }
}
