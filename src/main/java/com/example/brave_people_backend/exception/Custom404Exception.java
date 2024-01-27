package com.example.brave_people_backend.exception;

import lombok.Getter;

// 404, NOT_FOUND 반환시키는 예외
@Getter
public class Custom404Exception extends RuntimeException {

    private String object;

    public Custom404Exception() {
        super("기본 에러 메시지");
    }

    public Custom404Exception(String message) {
        super(message);
    }

    public Custom404Exception(String object, String message) {
        super(message);
        this.object = object;
    }
}
