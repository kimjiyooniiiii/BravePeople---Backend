package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.chat.dto.ChatRequestDto;
import com.example.brave_people_backend.chat.dto.ChatResponseDto;
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
    public void sendMessage(ChatRequestDto chatRequestDto, Long roomId) {
        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .roomId(roomId)
                .senderId(chatRequestDto.getSenderId())
                .message(chatRequestDto.getMessage())
                .url(chatRequestDto.getImg())
                .isRead(false)
                .build();

        template.convertAndSend("/sub/" + roomId, ChatResponseDto.of(chatRepository.save(chat)));
    }
}
