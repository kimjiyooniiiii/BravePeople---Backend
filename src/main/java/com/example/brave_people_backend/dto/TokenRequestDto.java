package com.example.brave_people_backend.dto;

import lombok.Getter;

@Getter
public class TokenRequestDto {

    private String accessToken;
    private String refreshToken;
}
