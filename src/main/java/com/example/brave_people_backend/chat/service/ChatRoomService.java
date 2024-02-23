package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.board.dto.ContactResponseDto;
import com.example.brave_people_backend.chat.dto.*;
import com.example.brave_people_backend.contact.dto.ContactStatusResponseDto;
import com.example.brave_people_backend.contact.service.ContactService;
import com.example.brave_people_backend.entity.*;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.enumclass.ContactStatus;
import com.example.brave_people_backend.enumclass.NotificationType;
import com.example.brave_people_backend.exception.Custom404Exception;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.*;
import com.example.brave_people_backend.security.SecurityUtil;
import com.example.brave_people_backend.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final SimpMessageSendingOperations template;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ContactService contactService;
    private final BoardRepository boardRepository;
    private final ContactRepository contactRepository;
    private final SseService sseService;
    private final ReviewRepository reviewRepository;

    // 채팅방 리스트 불러오기
    public List<ChatRoomResponseVo> getChatRoomList() {
        // 멤버(나) 초기화
        Long currentId = SecurityUtil.getCurrentId();
        Member me = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));

        // 결과값으로 반환할 List<ChatRoomResponseVo> 초기화
        List<ChatRoomResponseVo> chatRooms = chatRoomRepository.getChatRoomList(me)
                .stream()
                .map(chatRoom -> {
                    // 마지막 채팅 초기화
                    Chat lastChat = chatRepository.findTopByRoomId(chatRoom.getChatRoomId());
                    Member other;
                    boolean isRead;
                    // A가 나면서, A가 채팅방에 참여한 경우
                    if (chatRoom.getMemberA() == me && chatRoom.isAIsPartIn()) {
                        other = chatRoom.getMemberB(); //B가 상대
                        isRead = lastChat.getId().equals(chatRoom.getALastReadId());
                    }
                    // B가 나면서, B가 채팅방에 참여한 경우
                    else if (chatRoom.getMemberB() == me && chatRoom.isBIsPartIn()) {
                        other = chatRoom.getMemberA(); //A가 상대
                        isRead = lastChat.getId().equals(chatRoom.getBLastReadId());
                    }
                    // 채팅방에 참여하지 않은 경우
                    else {
                        return null;
                    }

                    //마지막 메시지가 사진이면 message를 "사진을 보냈습니다."로 설정
                    if (lastChat.getMessage() == null) {
                        lastChat.setMessageWhenImage("사진을 보냈습니다.");
                    }

                    //채팅방, 상대방, 마지막 채팅, 읽음여부, 상태를 파라미터로 넘겨주고 결과에 추가
                    return ChatRoomResponseVo.of(chatRoom, other, lastChat, isRead,
                            contactService.getContactStatus(chatRoom.getContact(), currentId).getStatus());
                })
                .filter(Objects::nonNull) //내가 채팅방에 참여하지 않은 경우(null) stream에서 제외
                .sorted()  // 마지막 채팅 최신순으로 정렬
                .toList(); // 리스트로 변환

        return chatRooms;
    }

    //기존 채팅내역 불러오기
    public ChatResponseDto getChatList(Long roomId) {
        // 멤버(나)와 채팅방 초기화
        Long currentId = SecurityUtil.getCurrentId();
        Member me = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));

        // 채팅방 참여자가 아닌 경우 예외 발생
        if (chatRoom.getMemberA() != me && chatRoom.getMemberB() != me) {
            throw new CustomException(String.valueOf(roomId), "채팅방 참여자가 아님");
        }

        Member other;
        LocalDateTime myEnteredAt; // 나의 입장 시각
        // A가 나면
        if (chatRoom.getMemberA() == me) {
            other = chatRoom.getMemberB(); // B는 상대방
            myEnteredAt = chatRoom.getAEnteredAt(); // A(나)의 입장 시각
        }
        // B가 나면
        else {
            other = chatRoom.getMemberA(); // A는 상대방
            myEnteredAt = chatRoom.getBEnteredAt(); // B(나)의 입장 시각
        }

        // 최근 채팅데이터 300개를 불러와 List<Chat>에 넣고 List<SendResponseDto>로 변환
        List<SendResponseDto> messages = chatRepository.findTop300ByRoomId(roomId, myEnteredAt)
                .stream()
                .map(SendResponseDto::of)
                .collect( // 리스트로 바꾸고 컬렉션을 반전하여 리스트 반환
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> {Collections.reverse(list); return list;})
                );

        return ChatResponseDto.of(other, messages);
    }

    // 채팅방 나가기
    public void exitChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(roomId.toString(), "존재하지 않는 채팅방ID"));

        // 현재 로그인 한 회원의 ID
        Long currentId = SecurityUtil.getCurrentId();

        String identity, nickName;
        Long opponentId;

        // 채팅방 테이블에서 내가 A이면
        if(chatRoom.getMemberA().getMemberId().equals(currentId)) {
            identity = "A";
            nickName = chatRoom.getMemberA().getNickname();
            opponentId = chatRoom.getMemberB().getMemberId();
        }
        // 채팅방 테이블에서 내가 B이면
        else if(chatRoom.getMemberB().getMemberId().equals(currentId)) {
            identity = "B";
            nickName = chatRoom.getMemberB().getNickname();
            opponentId = chatRoom.getMemberA().getMemberId();
        }
        else{
            throw new CustomException(currentId.toString(), "채팅방 참여자가 아님");
        }

        // 의뢰 테이블에서 나의 "의뢰상태" 검색
        String name = "writer";
        if(chatRoom.getContact().getOther().getMemberId().equals(currentId)) { name = "other"; }

        ContactStatus status = name.equals("writer") ? chatRoom.getContact().getWriterStatus() : chatRoom.getContact().getOtherStatus();

        // 나의 의뢰가 이미 "완료된 상태"가 아니면 의뢰상태를 "취소"로 바꿈
        if(!status.equals(ContactStatus.완료)) {
            chatRoom.getContact().changeStatus(name, ContactStatus.취소);
        }

        // 채팅방 삭제를 위한 상대방의 참여여부 확인
        boolean isOtherPartIn = identity.equals("A") ? chatRoom.isBIsPartIn() : chatRoom.isAIsPartIn();

        // 상대방도 참여중이지 않으면 채팅방과 메시지 모두 삭제
        if(isOtherPartIn == false) {
            chatRepository.deleteByRoomId(roomId);
            chatRoomRepository.deleteById(roomId);
        }

        // 상대방은 참여중이면
        else{
            // 나의 참여여부만 false로 업데이트
            chatRoom.changeIsPartIn(identity, false);

            // 상대방에게 채팅방을 나갔음을 메시지로 전송
            Chat chat = Chat.builder()
                    .id(UUID.randomUUID().toString())
                    .roomId(roomId)
                    .senderId(-1L)
                    .message(nickName + "님이 채팅방을 나갔습니다.")
                    .url(null)
                    .build();

            template.convertAndSend("/sub/" + roomId, SendResponseDto.of(chatRepository.save(chat)));

            // 상대방의 "의뢰 상태" 변화를 알림
            contactService.sendNewStatusAlert(opponentId, roomId);
        }
    }
}
