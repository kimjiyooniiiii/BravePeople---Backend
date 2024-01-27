package com.example.brave_people_backend.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * (GET /posts) 응답 DTO
 */
@Builder
@Getter
public class PostListResponseDto {

    private boolean hasNext;
    private List<PostListVo> data;

    public static PostListResponseDto of(boolean hasNext, List<PostListVo> data) {
        return PostListResponseDto.builder()
                .hasNext(hasNext)
                .data(data)
                .build();
    }

}
