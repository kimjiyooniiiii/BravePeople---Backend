package com.example.brave_people_backend.jwt;

import com.example.brave_people_backend.dto.ApiExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private void sendJwtErrorResponse(HttpServletRequest request, HttpServletResponse response, Throwable e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        ApiExceptionDto apiExceptionDto = ApiExceptionDto.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage("Access Token 만료")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(apiExceptionDto);
        response.getWriter().write(result);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

    }
}
