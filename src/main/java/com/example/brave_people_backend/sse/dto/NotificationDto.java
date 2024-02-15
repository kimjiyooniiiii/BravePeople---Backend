package com.example.brave_people_backend.sse.dto;

import com.example.brave_people_backend.enumclass.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDto {

    private NotificationType type;
    private String message;

    //새로 생성된 알림을 전달하는 Dto
    public static NotificationDto of(NotificationType type, String message) {
        return NotificationDto.builder()
                .type(type)
                .message(message)
                .build();
    }
}
