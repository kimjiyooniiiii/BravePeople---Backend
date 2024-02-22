package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    //보낸시각 최신순 정렬 후 채팅 300개 가져오기
    @Query(sort = "{send_at:-1}")
    List<Chat> findTop300ByRoomId(Long roomId);

    //보낸시각 최신순 정렬 후 채팅 1개 가져오기
    @Query(sort = "{send_at:-1}")
    Chat findTopByRoomId(Long roomId);

    // 채팅 메시지 삭제
    void deleteByRoomId(Long roomId);
}
