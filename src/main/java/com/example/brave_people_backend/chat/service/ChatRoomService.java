package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.chat.dto.ChatResponseDto;
import com.example.brave_people_backend.chat.dto.ChatRoomResponseVo;
import com.example.brave_people_backend.chat.dto.SendResponseDto;
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

    // 달려가기, 부탁하기 -> 채팅방 생성
    public ChatRoom createChatRoom(Member memberA, Member memberB) {
        ChatRoom chatRoom = ChatRoom.builder()
                .memberA(memberA)
                .memberB(memberB)
                .aIsPartIn(true)
                .bIsPartIn(true)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    // TODO 참여중인 채팅방만 불러오도록 추가해야 함
    // 채팅방 리스트 불러오기
    public List<ChatRoomResponseVo> getChatRoomList() {
        // 멤버(나) 초기화
        Long currentId = SecurityUtil.getCurrentId();
        Member me = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        // 내가 참여한 채팅방 리스트 초기화
        List<ChatRoom> chatRoomList = chatRoomRepository.getChatRoomList(me);
        // 결과값으로 반환할 List<ChatRoomResponseVo> 초기화
        List<ChatRoomResponseVo> result = new ArrayList<>();

        // lastChat은 딱 하나 가져오는 것이므로 pageable 구현 객체 초기화
        PageRequest pageRequest = PageRequest.of(0, 1); //출력할 page와 amount (pageable 구현체)
        // 내가 참여한 채팅방 iter 순회
        for (ChatRoom chatRoom : chatRoomList) {
            // 멤버(상대방) 초기화
            Member other = chatRoom.getMemberA() == me ? chatRoom.getMemberB() : chatRoom.getMemberA();
            // 마지막 채팅을 List<Chat> 형태로 받아옴
            List<Chat> chatListOne = chatRepository.findChatOneByRoomId(chatRoom.getChatRoomId(), pageRequest);
            // 채팅방에 채팅이 1개도 존재하지 않으면 chatListOne.isEmpty == true 이므로 임의 채팅(최근 채팅 없음) 생성
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

            //마지막 메시지가 사진이면 message를 "사진을 보냈습니다."로 설정
            if (lastChat.getMessage() == null) {
                lastChat.setMessageWhenImage("사진을 보냈습니다.");
            }
            //채팅방, 상대방, 마지막 채팅을 파라미터로 넘겨주고 result 리스트에 추가
            result.add(ChatRoomResponseVo.of(chatRoom, other, lastChat));
        }

        return result;
    }

    //기존 채팅내역 불러오기
    public ChatResponseDto getChatList(Long roomId) {
        // 멤버(나)와 채팅방 초기화
        Long currentId = SecurityUtil.getCurrentId();
        Member me = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방"));

        // 채팅방 참여자가 아닌 경우 예외 발생
        if (chatRoom.getMemberA() != me && chatRoom.getMemberB() != me) {
            throw new CustomException(String.valueOf(roomId), "채팅방 참여자가 아님");
        }

        // 멤버(상대방) 초기화
        Member other = chatRoom.getMemberA() == me ? chatRoom.getMemberB() : chatRoom.getMemberA();

        // 최근 채팅데이터 300개를 불러와 List<Chat>에 넣고 List<SendResponseDto>로 변환
        PageRequest pageRequest = PageRequest.of(0, 300);
        List<SendResponseDto> messages = chatRepository.findByRoomId(roomId, pageRequest)
                .stream()
                .map(SendResponseDto::of)
                .toList();

        return ChatResponseDto.of(other, messages);
    }
}
