package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.security.SecurityUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// 위치 변경 요청 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {

    //request body에서 위도, 경도 정보를 String으로 받음
    @NotBlank @Pattern(regexp = "(^\\d+$)|(^\\d+\\.\\d{1,15}$)", message = "잘못된 위치 정보")    //정수부 세 자리, 소수점 이하 15자리까지
    private String lat;
    @NotBlank @Pattern(regexp = "(^\\d+$)|(^\\d+\\.\\d{1,15}$)", message = "잘못된 위치 정보")
    private String lng;

}
