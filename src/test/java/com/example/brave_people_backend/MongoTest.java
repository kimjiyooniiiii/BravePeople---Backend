package com.example.brave_people_backend;

import com.example.brave_people_backend.repository.ChatRepository;
import com.example.brave_people_backend.entity.Chat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                .message("시간문제 해결???")
                .isRead(false)
                .url(null)
                .build();

        chatRepository.save(chat);
    }

    @Test
    public void selectTest() {
        List<Chat> all = chatRepository.findAll();
        for (Chat chat : all) {
            System.out.println(chat.getMessage() + ", " + chat.getSendAt().toString());
        }
    }
}
