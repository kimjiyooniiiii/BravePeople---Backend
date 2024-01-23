package com.example.brave_people_backend.member.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {

    private BigDecimal lat;
    private BigDecimal lng;

    // LocationResponseDto 객체를 생성하는 함수 (of 함수를 이용함으로서 유지보수 편리)
    public static LocationResponseDto of(Member member) {
        return LocationResponseDto.builder()
                .lat(member.getLat())
                .lng(member.getLng())
                .build();
    }
}
