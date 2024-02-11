package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.ChatRoom;
import com.example.brave_people_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    //기존에 생성된 채팅방이 있는지 찾음
    @Query("select r from ChatRoom r where (r.memberA = :memberA and r.memberB = :memberB)" +
            "or(r.memberA = :memberB and r.memberB = :memberA)")
    Optional<ChatRoom> findChatRoom(@Param("memberA") Member memberA, @Param("memberB") Member memberB);

//    @Query("select c.chatRoom.chatRoomId, c.helper, c.client, c.contactStatus " +
//            "from Contact c " +
//            "where c.chatRoom = (select r from ChatRoom r where r.memberA = :member or r.memberB = :member)")
//    @Query("select c from Contact c join fetch c.chatRoom r " +
//            "where c.client = :member or c.helper = :member")
    @Query("select r from ChatRoom r join fetch r.contact " +
            "where r.memberA = :member or r.memberB = :member")
    List<ChatRoom> getChatRoomList(@Param("member") Member member);

    // 내가 참여중인 채팅방 찾기
    @Query("select r.chatRoomId from ChatRoom r where r.memberA.memberId = :memberId or r.memberB.memberId = :memberId")
    List<Long> findChatRoomByMemberId(@Param("memberId") Long memberId);
}