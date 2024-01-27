package com.example.brave_people_backend.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 위치 변경 요청 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {

    //request body에서 위도, 경도 정보를 String으로 받음
    @NotBlank @Pattern(regexp = "(^\\d+$)|(^\\d+\\.\\d{1,15}$)")    //정수부 세 자리, 소수점 이하 15자리까지
    private String lat;
    @NotBlank @Pattern(regexp = "(^\\d+$)|(^\\d+\\.\\d{1,15}$)")
    private String lng;

}
