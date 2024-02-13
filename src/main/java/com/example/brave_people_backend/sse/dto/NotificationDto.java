package com.example.brave_people_backend.sse.dto;

import com.example.brave_people_backend.enumclass.NotificationType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NotificationDto {

    private final NotificationType type;
}
