package com.example.brave_people_backend.exception;

import lombok.Getter;

// 400, BAD_REQUEST 반환시키는 예외
@Getter
public class CustomException extends RuntimeException{

    private String object;

    public CustomException() {
        super("기본 에러 메시지");
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String object, String message) {
        super(message);
        this.object = object;
    }
}
