package com.example.brave_people_backend;

import com.example.brave_people_backend.repository.ChatRepository;
import com.example.brave_people_backend.entity.Chat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringBootTest
public class MongoTest {

    @Autowired
    ChatRepository chatRepository;

    @Test
    public void insertTest() {
        Chat chat = Chat.builder()
                .roomId(2L)
                .senderId(3L)
                .message("chatId 얻어버려~")
                .isRead(false)
                .url(null)
                .build();

        chatRepository.insert(chat);
        System.out.println(chat.getId());
    }

    @Test
    public void selectTest() {
        PageRequest pageRequest = PageRequest.of(0, 1); //출력할 page와 amount 및 sort 기준 설정 (pageable 구현체)
        List<Chat> byRoomId = chatRepository.findByRoomId(2L, pageRequest);
        System.out.println(byRoomId.get(0).getMessage());
    }
}
