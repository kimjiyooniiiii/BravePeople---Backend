package com.example.brave_people_backend.dto;

import lombok.Builder;
import lombok.Getter;

// 예외 발생 시, 응답 형식
@Getter
public class ApiExceptionDto {

    private String status;
    private String errorMessage;

    @Builder
    public ApiExceptionDto(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
