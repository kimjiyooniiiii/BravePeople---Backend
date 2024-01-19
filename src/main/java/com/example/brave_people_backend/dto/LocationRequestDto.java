package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// 위치 변경 요청 Dto
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {

    private String lat;
    private String lng;

}
