package com.example.brave_people_backend.exception;

import com.example.brave_people_backend.exception.dto.ApiExceptionDto;
import org.springframework.http.HttpHeaders;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// 예외를 처리하는 class
@RestControllerAdvice
public class ApiExceptionAdvice extends ResponseEntityExceptionHandler {

    // 아이디, 비밀번호 불일치 시 발생하는 예외
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiExceptionDto exceptionHandler(final BadCredentialsException e) {
        return ApiExceptionDto.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage("아이디, 비밀번호를 확인해주세요.")
                .build();
    }

    // CustomExceptionHandler
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiExceptionDto exceptionHandler(final CustomException e) {
        return ApiExceptionDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .errorMessage(e.getMessage())
                .build();
    }

    @Override //TODO Pattern 에러메시지 자연스럽게, "errorMessage"를 바로 alert창에 띄울 수 있도록 자연스럽게
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {

        ApiExceptionDto apiExceptionDto = ApiExceptionDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .errorMessage("Invalid request content.")
                .build();

        return new ResponseEntity<>(apiExceptionDto, HttpStatus.BAD_REQUEST);
    }

    // Refresh Token 만료
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiExceptionDto exceptionHandler(final JwtException e) {
        return ApiExceptionDto.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage("Refresh Token 만료")
                .build();
    }
}
