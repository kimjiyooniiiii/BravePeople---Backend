package com.example.brave_people_backend.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 다음 Filter인 JwtFilter를 수행 후, 예외가 발생하면 catch문 실행
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            sendJwtErrorResponse(request, response, e);
        }
    }

    // Client로 JWT 토큰에 관한 Error Message 전송
    private void sendJwtErrorResponse(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        throw new JwtException("토큰 만료");
    }
}
