package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    @Query(sort = "{send_at:1}")
    List<Chat> findByRoomId(Long roomId, Pageable pageable);

    @Query(sort = "{send_at:-1}")
    List<Chat> findChatOneByRoomId(Long roomId, Pageable pageable);

    // 내가 참여중인 채팅방의 읽지 않은 메시지 검색
    @Query("{ is_read : false, room_id : { $in: ?0 } }")
    List<Chat> findChatByReadIs(List<Long> roomId);
}
