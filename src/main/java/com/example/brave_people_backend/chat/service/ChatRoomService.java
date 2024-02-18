package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.board.dto.ContactResponseDto;
import com.example.brave_people_backend.chat.dto.ChatResponseDto;
import com.example.brave_people_backend.chat.dto.ChatRoomResponseVo;
import com.example.brave_people_backend.chat.dto.ContactResponseVo;
import com.example.brave_people_backend.chat.dto.SendResponseDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final BoardRepository boardRepository;
    private final ContactRepository contactRepository;
    private final SseService sseService;

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
            Chat lastChat = chatRepository.findChatOneByRoomId(chatRoom.getChatRoomId(), pageRequest).get(0);

            //마지막 메시지가 사진이면 message를 "사진을 보냈습니다."로 설정
            if (lastChat.getMessage() == null) {
                lastChat.setMessageWhenImage("사진을 보냈습니다.");
            }
            //채팅방, 상대방, 마지막 채팅을 파라미터로 넘겨주고 result 리스트에 추가
            result.add(ChatRoomResponseVo.of(chatRoom, other, lastChat));
        }
        //마지막 채팅 최신순으로 정렬
        Collections.sort(result);
        return result;
    }

    //기존 채팅내역 불러오기
    public ChatResponseDto getChatList(Long roomId) {
        // 멤버(나)와 채팅방 초기화
        Long currentId = SecurityUtil.getCurrentId();
        Member me = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        Contact contact = contactRepository.findById(chatRoom.getContact().getContactId())
                .orElseThrow(() -> new CustomException(String.valueOf(chatRoom.getChatRoomId()), "존재하지 않는 의뢰ID"));

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

        return ChatResponseDto.of(other, messages, ContactResponseVo.of(contact));
    }

    //의뢰 만들기
    public ContactResponseDto makeContact(Long postId) {
        //로그인한 멤버 관련 데이터 초기화
        Long currentId = SecurityUtil.getCurrentId();
        Member currentMember = memberRepository.findById(currentId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));

        //게시글 관련 데이터 초기화
        Post currentPost = boardRepository.findPostById(postId)
                .orElseThrow(() -> new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글"));
        Act currentAct = currentPost.getAct();
        Member postMember = currentPost.getMember();

        //본인과의 채팅방이 개설되지 않게 함
        if(postMember == currentMember) {
            throw new CustomException(String.valueOf(postId), "본인의 게시글");
        }

        //Act에 따른 helper, client 초기화
        Member helper, client;
        if(Act.원정대 == currentAct) {
            helper = postMember;
            client = currentMember;
        } else {
            helper = currentMember;
            client = postMember;
        }

        //helper와 client 사이에 진행중인 의뢰가 있으면 오류
        if (contactRepository.existsByContactStatusAndClientAndHelper(ContactStatus.진행중, client, helper)) {
            throw new CustomException(client.getMemberId() + ", " + helper.getMemberId(), "진행중인 의뢰 존재");
        }

        //같은 사람이 같은 게시글에 중복해서 부탁하기/달려가기 하면 오류
        if (contactRepository.existsByClientAndHelper(client, helper)) {
            throw new CustomException(String.valueOf(currentPost.getPostId()), "의뢰 중복");
        }

        //해당 postId로 진행중인 의뢰가 있으면 오류
        if (contactRepository.existsByContactStatusAndPost(ContactStatus.진행중, currentPost)) {
            throw new CustomException(String.valueOf(currentPost.getPostId()), "진행중인 의뢰 존재");
        }

        //의뢰가 진행 가능한 상태이면 새 contact 생성
        Contact contact  = Contact.builder()
                .helper(helper)
                .client(client)
                .post(currentPost)
                .contactStatus(ContactStatus.대기중)
                .writerStatus(ContactStatus.대기중)
                .otherStatus(ContactStatus.진행중)
                .isHelperFinished(false)
                .isClientFinished(false)
                .isDeleted(false)
                .build();

        contactRepository.save(contact);

        //Contact 테이블에 이미 생성된 채팅방이 있는지 조회하여 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findChatRoom(helper, client)
                .orElseGet(() -> {
                    ChatRoom newRoom = createChatRoom(helper, client);
                    //새로 생성된 채팅방에 대한 알림을 전송
                    sseService.sendEventToClient(NotificationType.NEW_CHAT_ROOM, postMember.getMemberId(), currentMember.getNickname()+"님이 채팅방을 개설하였습니다.");
                    //빈 채팅 생성 후 save
                    Chat makeChat = Chat.builder()
                            .senderId(-1L)
                            .roomId(newRoom.getChatRoomId())
                            .isRead(false)
                            .sendAt(LocalDateTime.now())
                            .message(currentMember.getNickname() + "님이 채팅방을 개설하였습니다.")
                            .url(null)
                            .build();
                    chatRepository.save(makeChat);
                    return newRoom;
                });
        chatRoom.changeContact(contact);

        return ContactResponseDto.of(chatRoom.getChatRoomId());
    }

    public void acceptContact(Long roomId){

        //현재 채팅방을 찾음
        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        //채팅방에 연결된 contact를 찾음
        Contact currentContact = contactRepository.findById(currentRoom.getContact().getContactId()).orElseThrow(
                () -> new CustomException(String.valueOf(currentRoom.getContact().getContactId()), "존재하지 않는 의뢰")
        );

        //현재 contact의 글 작성자의 상태를 진행중으로 바꿈 -> writer, other 모두 진행중 상태가 됨
        currentContact.changeWriterStatus(ContactStatus.진행중);

        //현재 post를 찾음
        Post currentPost = boardRepository.findPostById(currentContact.getPost().getPostId()).orElseThrow(() ->
                new CustomException(String.valueOf(currentContact.getPost().getPostId()), "존재하지 않는 채팅방ID"));

        //같은 postId로 생성된 contact를 찾음 -> 같은 게시글에서 생성된 의뢰들의 상태를 취소로 변경
        List<Contact> findContacts = contactRepository.findContactsByPost(currentPost);
        for (Contact findContact : findContacts) {
            //찾은 의뢰ID가 현재 의뢰ID와 다르고, 작성자의 상태가 대기중이면 해당 의뢰에 관련된 member의 상태를 취소로 변경
            if ((!findContact.getContactId().equals(currentContact.getContactId())) && (findContact.getWriterStatus().equals(ContactStatus.대기중))) {
                findContact.changeWriterStatus(ContactStatus.취소);
                findContact.changeOtherStatus(ContactStatus.취소);
            }
        }
    }

    public void cancelContact(Long roomId) {

        //roomId에 연결되어있는 contactId로 contact를 찾음 -> contact의 writer,other의 ContactStatus를 취소로 바꿈

        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));

        Contact currentContact = contactRepository.findById(currentRoom.getContact().getContactId()).orElseThrow(() ->
                new CustomException(String.valueOf(currentRoom.getContact().getContactId()), "존재하지 않는 의뢰"));

        currentContact.changeWriterStatus(ContactStatus.취소);
        currentContact.changeOtherStatus(ContactStatus.취소);

    }
}
