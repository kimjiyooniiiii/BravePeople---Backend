package com.example.brave_people_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePwRequestDto {

    //request body에서 newpassword 꺼냄
    @NotBlank @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$") // 영문 + 숫자, 8자리 아상
    String newPassword;

    Long emailId;

    int authCode;
}
