package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.chat.dto.SendRequestDto;
import com.example.brave_people_backend.chat.dto.SendResponseDto;
import com.example.brave_people_backend.entity.Chat;
import com.example.brave_people_backend.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations template;
    private final ChatRepository chatRepository;

    // 메시지 전송
    public void sendMessage(SendRequestDto sendRequestDto, Long roomId) {
        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .roomId(roomId)
                .senderId(sendRequestDto.getSenderId())
                .message(sendRequestDto.getMessage())
                .url(sendRequestDto.getImg())
                .isRead(false)
                .build();

        template.convertAndSend("/sub/" + roomId, SendResponseDto.of(chatRepository.save(chat)));
    }
}
