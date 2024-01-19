package com.example.brave_people_backend.exception;

import com.example.brave_people_backend.dto.ApiExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// 예외를 처리하는 class
@RestControllerAdvice
public class ApiExceptionAdvice extends ResponseEntityExceptionHandler {

    // 아이디, 비밀번호 불일치 시 발생하는 예외
    @ExceptionHandler
    public ResponseEntity<ApiExceptionDto> exceptionHandler(final BadCredentialsException e) {
        ApiExceptionDto apiExceptionDto = ApiExceptionDto.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage("아이디, 비밀번호를 확인해주세요.")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(apiExceptionDto);
    }

    // 비회원 접근 시
    @ExceptionHandler
    public ResponseEntity<ApiExceptionDto> exceptionHandler(final InsufficientAuthenticationException e) {
        ApiExceptionDto apiExceptionDto = ApiExceptionDto.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage("로그인 후 이용해주세요.")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(apiExceptionDto);
    }

    // 회원가입 시, 중복된 아이디, 닉네임이 있을 경우
    @ExceptionHandler
    public ResponseEntity<ApiExceptionDto> exceptionHandler(final DuplicatedMemberException e) {
        ApiExceptionDto apiExceptionDto = ApiExceptionDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .errorMessage("중복된 사용자가 있습니다.")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiExceptionDto);
    }

}
