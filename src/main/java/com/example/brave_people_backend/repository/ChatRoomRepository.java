package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}

    @Query("select r.chatRoomId from ChatRoom r where (r.memberA.memberId = :aMemberId and r.memberB.memberId = :bMemberId)" +
            "or(r.memberA.memberId = :bMemberId and r.memberB.memberId = :aMemberId)")
    Long findChatRoom(Long aMemberId, Long bMemberId);
}