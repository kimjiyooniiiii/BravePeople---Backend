package com.example.brave_people_backend.chat.repository;

import com.example.brave_people_backend.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
}
