package com.example.brave_people_backend.chat.controller;

import com.example.brave_people_backend.chat.dto.SendRequestDto;
import com.example.brave_people_backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 메시지 전송
    @MessageMapping("/{roomId}")
    public void sendMessage(@Payload SendRequestDto sendRequestDto, @DestinationVariable("roomId") Long roomId) {
        chatService.sendMessage(sendRequestDto, roomId);
    }

}
