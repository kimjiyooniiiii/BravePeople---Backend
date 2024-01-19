package com.example.brave_people_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {

    private String username;
    private String nickname;
    private String gender;
    private String introduction;
    private double score;

}
