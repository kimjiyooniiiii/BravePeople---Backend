package com.example.brave_people_backend.exception;

// 404, NOT_FOUND 반환시키는 예외
public class Custom404Exception extends RuntimeException {

    public Custom404Exception() {
        super("기본 에러 메시지");
    }

    public Custom404Exception(String message) {
        super(message);
    }
}
