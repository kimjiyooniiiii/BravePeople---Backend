package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Getter
@Builder
public class ChatResponseDto {

    private String chatId;
    private Long senderId;
    private String message;
    private String date;
    private String time;
    private String img;

    public static ChatResponseDto of(Chat chat) {
        return ChatResponseDto.builder()
                .chatId(chat.getId())
                .senderId(chat.getSenderId())
                .message(chat.getMessage())
                .date(chat.getSendAt().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                .time(chat.getSendAt().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                .img(chat.getUrl())
                .build();
    }

}
