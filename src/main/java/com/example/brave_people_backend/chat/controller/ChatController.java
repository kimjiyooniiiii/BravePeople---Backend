package com.example.brave_people_backend.chat.controller;

import com.example.brave_people_backend.chat.dto.ChatRequestDto;
import com.example.brave_people_backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 메시지 전송
    @MessageMapping("/{roomId}")
    public void sendMessage(@Payload ChatRequestDto chatRequestDto, @DestinationVariable("roomId") Long roomId) {
        chatService.sendMessage(chatRequestDto, roomId);
    }

}
