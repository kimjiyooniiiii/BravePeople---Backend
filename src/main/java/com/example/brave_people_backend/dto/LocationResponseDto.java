package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {

    private BigDecimal lat;
    private BigDecimal lng;

    public static LocationResponseDto of(Member member, String lat, String lng) {
        return LocationResponseDto.builder()
                .lat(new BigDecimal(lat))
                .lng(new BigDecimal(lng))
                .build();
    }
}
