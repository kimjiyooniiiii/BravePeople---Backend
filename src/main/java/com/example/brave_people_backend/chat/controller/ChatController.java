package com.example.brave_people_backend.chat.controller;

import com.example.brave_people_backend.chat.dto.SendRequestDto;
import com.example.brave_people_backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 메시지 전송
    @MessageMapping("/{roomId}")
    public void sendMessage(@Payload SendRequestDto sendRequestDto,
                            @DestinationVariable("roomId") Long roomId,
                            SimpMessageHeaderAccessor headerAccessor) {

        chatService.sendMessage(sendRequestDto, roomId, headerAccessor);
    }

    // WebSocket 연결 해제
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        chatService.webSocketDisconnectListener(event);
    }

}
