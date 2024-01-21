package com.example.brave_people_backend.exception;

// 400, BAD_REQUEST 반환시키는 예외
public class CustomException extends RuntimeException{

    public CustomException() {
        super("기본 에러 메시지");
    }

    public CustomException(String message) {
        super(message);
    }
}
