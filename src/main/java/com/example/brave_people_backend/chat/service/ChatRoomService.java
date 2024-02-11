package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.chat.dto.ChatRoomResponseVo;
import com.example.brave_people_backend.entity.Chat;
import com.example.brave_people_backend.entity.ChatRoom;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.ChatRepository;
import com.example.brave_people_backend.repository.ChatRoomRepository;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    public ChatRoom createChatRoom(Member memberA, Member memberB) {
        ChatRoom chatRoom = ChatRoom.builder()
                .memberA(memberA)
                .memberB(memberB)
                .aIsPartIn(true)
                .bIsPartIn(true)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomResponseVo> getChatRoomList() {
        Long currentId = SecurityUtil.getCurrentId();
        Member me = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        List<ChatRoom> chatRoomList = chatRoomRepository.getChatRoomList(me);
        List<ChatRoomResponseVo> result = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(0, 1); //출력할 page와 amount 및 sort 기준 설정 (pageable 구현체)
        for (ChatRoom chatRoom : chatRoomList) {
            Member other = chatRoom.getMemberA() == me ? chatRoom.getMemberB() : chatRoom.getMemberA();
            List<Chat> chatListOne = chatRepository.findByRoomId(chatRoom.getChatRoomId(), pageRequest);
            Chat lastChat = chatListOne.isEmpty() ?
                    Chat.builder()
                            .senderId(other.getMemberId())
                            .roomId(chatRoom.getChatRoomId())
                            .isRead(false)
                            .sendAt(null)
                            .message("최근 채팅 없음")
                            .url(null)
                            .build()
                    : chatListOne.get(0);

            result.add(ChatRoomResponseVo.of(chatRoom, other, lastChat));
        }

        return result;
    }
}
