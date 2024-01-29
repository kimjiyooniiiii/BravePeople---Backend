package com.example.brave_people_backend.jwt;

import com.example.brave_people_backend.exception.dto.ApiExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증 실패시 핸들링 - 401에러
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        ApiExceptionDto apiExceptionDto = ApiExceptionDto.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage("비회원 접근 불가")
                .build();
        log.error("비회원 접근 불가");

        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(apiExceptionDto);
        response.getWriter().write(result);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
