package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.chat.dto.SendRequestDto;
import com.example.brave_people_backend.chat.dto.SendResponseDto;
import com.example.brave_people_backend.entity.Chat;
import com.example.brave_people_backend.entity.ChatRoom;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.MessageType;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.ChatRepository;
import com.example.brave_people_backend.repository.ChatRoomRepository;
import com.example.brave_people_backend.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations template;
    private final ChatRepository chatRepository;
    private final SseService sseService;
    private final ChatRoomRepository chatRoomRepository;

    // 메시지 전송
    public void sendMessage(SendRequestDto sendRequestDto, Long roomId, SimpMessageHeaderAccessor headerAccessor) {
        MessageType type = sendRequestDto.getType();

        // 채팅방 입장 Logic
        if(type == MessageType.ENTER) {
            // WebSocket Session에 userId, roomId 저장
            headerAccessor.getSessionAttributes().put("userId", sendRequestDto.getSenderId());
            headerAccessor.getSessionAttributes().put("roomId", roomId);
        }
        // 채팅 메시지 전송 Logic
        else if(type == MessageType.TALK) {
            Chat chat = Chat.builder()
                    .id(UUID.randomUUID().toString())
                    .roomId(roomId)
                    .senderId(sendRequestDto.getSenderId())
                    .message(sendRequestDto.getMessage())
                    .url(sendRequestDto.getImg())
                    .isRead(false)
                    .build();

            template.convertAndSend("/sub/" + roomId, SendResponseDto.of(chatRepository.save(chat)));
        }

        //roomId로 채팅방을 찾음
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));

        // 받는 사람을 찾음
        Member other = chatRoom.getMemberA().getMemberId().equals(sendRequestDto.getSenderId()) ? chatRoom.getMemberB() : chatRoom.getMemberA();
        //받는 사람에게 sse로 알림 전송, TALK일 때만 알림 전송
        if (type == MessageType.TALK) {
            sseService.sendEventToClient(other.getMemberId(), chatRoom.getChatRoomId());
        }

    }


    // WebSocket 연결 해제
    @Transactional
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        // WebSocket Session에서 userId, roomId 가져오기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = Long.parseLong(headerAccessor.getSessionAttributes().get("userId").toString());
        Long roomId = Long.parseLong(headerAccessor.getSessionAttributes().get("roomId").toString());

        // 채팅방 DB의 읽음 처리 false -> true
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new CustomException(String.valueOf(roomId),"존재하지 않는 roomId"));

        // 채팅 읽기 여부, 읽음 처리
        if(chatRoom.getMemberA().getMemberId().equals(userId)) {
            chatRoom.changeAIsRead();
        } else if(chatRoom.getMemberB().getMemberId().equals(userId)) {
            chatRoom.changeBIsRead();
        }
    }
}
