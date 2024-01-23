package com.example.brave_people_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TokenDto {

    private String grantType;   // Bearer Type
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
