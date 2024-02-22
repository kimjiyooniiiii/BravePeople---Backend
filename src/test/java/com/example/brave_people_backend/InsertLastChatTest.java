package com.example.brave_people_backend;

import com.example.brave_people_backend.entity.Chat;
import com.example.brave_people_backend.entity.ChatRoom;
import com.example.brave_people_backend.repository.ChatRepository;
import com.example.brave_people_backend.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class InsertLastChatTest {

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void insertLastChat() {
        List<ChatRoom> all = chatRoomRepository.findAll();
        for(ChatRoom r : all) {
            Long roomId = r.getChatRoomId();

            Chat lastChat = chatRepository.findTopByRoomId(roomId);
            r.changeALastReadId(lastChat.getId());
            r.changeBLastReadId(lastChat.getId());
        }
    }
}
