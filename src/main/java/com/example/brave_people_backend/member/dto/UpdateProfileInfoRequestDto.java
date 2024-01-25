package com.example.brave_people_backend.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileInfoRequestDto {

    //request body에서 nickname, introduction을 String으로 받음
    @NotBlank
    @Size(min=2, max=6)
    String nickname;

    @Size(max = 50)
    String introduction;
}
