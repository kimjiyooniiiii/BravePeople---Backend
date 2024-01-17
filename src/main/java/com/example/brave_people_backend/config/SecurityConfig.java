package com.example.brave_people_backend.config;

import com.example.brave_people_backend.jwt.JwtAccessDeniedHandler;
import com.example.brave_people_backend.jwt.JwtAuthenticationEntryPoint;
import com.example.brave_people_backend.jwt.JwtFilter;
import com.example.brave_people_backend.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    private final AuthenticationFailureHandler loginFailureHandler;

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

                // 로그인 실패 처리
                .formLogin((e) -> e.failureHandler(loginFailureHandler))

                // 인증이 안되었거나 권한이 없으면 예외처리
                .exceptionHandling((e)-> e
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler))

                // 세션 기능을 사용하지 않으므로 STATELESS 설정
                .sessionManagement((s)->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL 접근 권한 설정
                .authorizeHttpRequests((a) -> a
                        .requestMatchers("/auth/**").permitAll()    // 로그인, 회원가입은 접근 허용
                        .anyRequest().authenticated()                       // 나머지 페이지는 인증시 허용
                )

                // UsernamePasswordAuthenticationFilter 이전에 JwtFilter 수행
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                /*.with(new JwtSecurityConfig(tokenProvider),(d)->d.configure(http))*/
                .build();
    }
}
