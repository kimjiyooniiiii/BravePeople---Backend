package com.example.brave_people_backend.board.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContactResponseDto {

    private Long roomId;

    public static ContactResponseDto of(Long roomId) {
        return ContactResponseDto.builder()
                .roomId(roomId)
                .build();
    }
}
