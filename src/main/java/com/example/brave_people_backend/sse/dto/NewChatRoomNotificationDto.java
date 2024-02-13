package com.example.brave_people_backend.sse.dto;

import com.example.brave_people_backend.enumclass.NotificationType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NewChatRoomNotificationDto extends NotificationDto {

    private Long roomId;

    //새로운 채팅방이 생성된 알림을 전달하는 Dto
    public static NewChatRoomNotificationDto of(Long roomId) {
        return NewChatRoomNotificationDto.builder()
                .type(NotificationType.NEW_CHAT_ROOM)
                .roomId(roomId)
                .build();
    }
}
