package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    @Query(sort = "{send_at:-1}")
    List<Chat> findByRoomId(Long roomId, Pageable pageable);
}
