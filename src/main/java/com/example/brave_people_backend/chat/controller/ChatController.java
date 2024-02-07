package com.example.brave_people_backend.chat.controller;

import com.example.brave_people_backend.repository.ChatRepository;
import com.example.brave_people_backend.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRepository chatRepository;

    @GetMapping("/chat")
    public void testChat() {
        System.out.println("testChat()");
        Chat chat = Chat.builder()
                .roomId(2L)
                .senderId(3L)
                .message("나의 EC2 채팅 메시지")
                .isRead(false)
                .url(null)
                .build();

        chatRepository.save(chat);
    }
}
