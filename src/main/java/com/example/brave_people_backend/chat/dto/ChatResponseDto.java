package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatResponseDto {
    private Long otherId;
    private String otherNickname;
    private String otherProfileImg;
    private List<SendResponseDto> messages;

    public static ChatResponseDto of(Member other, List<SendResponseDto> messages) {
        return ChatResponseDto.builder()
                .otherId(other.getMemberId())
                .otherNickname(other.getNickname())
                .otherProfileImg(other.getProfileImg())
                .messages(messages)
                .build();
    }
}
