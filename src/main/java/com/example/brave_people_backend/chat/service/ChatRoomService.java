package com.example.brave_people_backend.chat.service;

import com.example.brave_people_backend.board.dto.ContactResponseDto;
import com.example.brave_people_backend.chat.dto.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ReviewRepository reviewRepository;

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

        // 내가 참여한 채팅방 iter 순회
        for (ChatRoom chatRoom : chatRoomList) {
            // 멤버(상대방) 초기화
            Member other = chatRoom.getMemberA() == me ? chatRoom.getMemberB() : chatRoom.getMemberA();

            // 마지막 채팅을 받아옴
            Chat lastChat = chatRepository.findTopByRoomId(chatRoom.getChatRoomId());

            //마지막 메시지가 사진이면 message를 "사진을 보냈습니다."로 설정
            if (lastChat.getMessage() == null) {
                lastChat.setMessageWhenImage("사진을 보냈습니다.");
            }
            //채팅방, 상대방, 마지막 채팅을 파라미터로 넘겨주고 result 리스트에 추가
            result.add(ChatRoomResponseVo.of(chatRoom, other, lastChat,
                    getContactStatus(chatRoom.getContact(), currentId).getStatus()));
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
        List<SendResponseDto> messages = chatRepository.findTop300ByRoomId(roomId)
                .stream()
                .map(SendResponseDto::of)
                .collect( // 리스트로 바꾸고 컬렉션을 반전하여 리스트 반환
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> {Collections.reverse(list); return list;})
                );

        return ChatResponseDto.of(other, messages);
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
        Member postMember = currentPost.getMember();

        //본인과의 채팅방이 개설되지 않게 함
        if(postMember == currentMember) {
            throw new CustomException(String.valueOf(postId), "본인의 게시글");
        }

        /* writer -> 글 작성자
        * other -> 부탁하기/달려가기 누르는 사람, 즉 currentMember
        * postMember은 항상 writer, currentMember은 항상 other가 됨
        * */

        List<Contact> findContact = contactRepository.findContactOneByStatus(postMember, currentMember);

        //writer와 other 사이에 진행중인 의뢰가 있으면 오류
        if(!findContact.isEmpty()) {
            throw new CustomException(postMember.getMemberId() + ", " + currentMember.getMemberId(), "상대방과 진행중인 의뢰 존재");
        }

        findContact = contactRepository.findContactOneByStatusAndPost(currentPost);
        //해당 postId로 진행중인 의뢰가 있으면 오류
        if (!findContact.isEmpty()) {
            throw new CustomException(String.valueOf(currentPost.getPostId()), "글 작성자가 의뢰 진행 중");
        }

        //두 사람 사이에 같은 게시글에서 대기중인 의뢰가 있는지 확인
        //원정대 게시글 -> 같은 멤버가 대기중/진행중일 땐 중복 부탁/달려가기 안됨, 취소/완료일 땐 가능
        //진행중인 경우는 위에서 걸러짐
        //의뢰인 게시글 -> 완료되면 어차피 disable 됨
        if (contactRepository.existsByWriterAndOtherAndWriterStatusAndOtherStatusAndPost(postMember, currentMember, ContactStatus.대기중, ContactStatus.진행중, currentPost)) {
            throw new CustomException(String.valueOf(currentPost.getPostId()), "이미 신청한 의뢰");
        }

        //의뢰가 진행 가능한 상태이면 새 contact 생성
        Contact contact  = Contact.builder()
                .writer(postMember)
                .other(currentMember)
                .post(currentPost)
                .writerStatus(ContactStatus.대기중)
                .otherStatus(ContactStatus.진행중)
                .isDeleted(false)
                .build();

        contactRepository.save(contact);

        //Contact 테이블에 이미 생성된 채팅방이 있는지 조회하여 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findChatRoom(postMember, currentMember)
                .orElseGet(() -> {
                    ChatRoom newRoom = createChatRoom(postMember, currentMember);
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

        //글 작성자에게 새 의뢰가 생성됨을 알림
        sseService.sendEventToClient(NotificationType.NEW_CONTACT, postMember.getMemberId(), "새로운 의뢰 생성");

        return ContactResponseDto.of(chatRoom.getChatRoomId());
    }

    public ContactStatusResponseDto acceptContact(Long roomId){

        //현재 채팅방을 찾음
        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        //채팅방에 연결된 contact를 찾음
        Contact currentContact = currentRoom.getContact();
        Long currentId = SecurityUtil.getCurrentId();

        validateIsMyContact(currentContact, currentId);

        //현재 contact의 글 작성자의 상태를 진행중으로 바꿈 -> writer, other 모두 진행중 상태가 됨
        currentContact.changeStatus("writer", ContactStatus.진행중);

        //현재 post를 찾음
        Post currentPost = boardRepository.findPostById(currentContact.getPost().getPostId()).orElseThrow(() ->
                new CustomException(String.valueOf(currentContact.getPost().getPostId()), "존재하지 않는 게시글"));

        //같은 postId로 생성된 contact를 찾음 -> 같은 게시글에서 생성된 의뢰들의 상태를 취소로 변경
        List<Contact> findContacts = contactRepository.findContactsByPost(currentPost);
        for (Contact findContact : findContacts) {
            //찾은 의뢰ID가 현재 의뢰ID와 다르고, 작성자의 상태가 대기중이면 해당 의뢰에 관련된 member의 상태를 취소로 변경
            if ((!findContact.getContactId().equals(currentContact.getContactId())) && (findContact.getWriterStatus().equals(ContactStatus.대기중))) {
                findContact.changeStatus("writer", ContactStatus.취소);
                findContact.changeStatus("other", ContactStatus.취소);
            }
        }

        // 상대방에게 상태가 변화되었다는 알림을 보냄
        sendNewStatusAlert(currentContact.getOther().getMemberId());

        return getContactStatus(currentContact, currentId);
    }

    public ContactStatusResponseDto cancelContact(Long roomId) {

        //roomId에 연결되어있는 contactId로 contact를 찾음 -> contact의 writer,other의 ContactStatus를 취소로 바꿈

        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        Contact currentContact = currentRoom.getContact();
        Long currentId = SecurityUtil.getCurrentId();

        validateIsMyContact(currentContact, currentId);

        currentContact.changeStatus("writer", ContactStatus.취소);
        currentContact.changeStatus("other", ContactStatus.취소);

        // 내가 writer면, 알림 받는 사람은 other
        if (currentId.equals(currentContact.getWriter().getMemberId())) {
            sendNewStatusAlert(currentContact.getOther().getMemberId());
        }
        // 내가 other면, 알림 받는 사람은 writer
        else {
            sendNewStatusAlert(currentContact.getWriter().getMemberId());
        }

        return getContactStatus(currentContact, currentId);
    }

    public ContactStatusResponseDto finishContact(Long roomId) {
        // ChatRoom, Contact, currentId 선언 및 초기화
        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        Contact currentContact = currentRoom.getContact();
        Long currentId = SecurityUtil.getCurrentId();

        validateIsMyContact(currentContact, currentId);

        // currentId == writer인 경우
        if (currentId.equals(currentContact.getWriter().getMemberId())) {
            currentContact.changeStatus("writer", ContactStatus.완료);
            // writer, other 둘 다 완료 상태인 경우 기존 후기 활성화
            if (currentContact.getOtherStatus() == ContactStatus.완료) {
                reviewRepository.findByContact(currentContact).forEach(r -> r.changeIsDisabled(false));
            }
            sendNewStatusAlert(currentContact.getOther().getMemberId());
        } else { //currentId == other인 경우
            currentContact.changeStatus("other", ContactStatus.완료);
            // writer, other 둘 다 완료 상태인 경우 기존 후기 활성화
            if (currentContact.getWriterStatus() == ContactStatus.완료) {
                reviewRepository.findByContact(currentContact).forEach(r -> r.changeIsDisabled(false));
            }
            sendNewStatusAlert(currentContact.getWriter().getMemberId());
        }

        //contactStatus가 둘 다 완료이고 의뢰인 게시글이면 비활성화 함
        ContactStatusResponseDto contactStatus = getContactStatus(currentContact, currentId);
        if (contactStatus.getStatus().equals(ContactStatus.완료) && currentContact.getPost().getAct().equals(Act.의뢰인)) {
            currentContact.getPost().onDisabled();
        }

        return contactStatus;
    }

    public void reviewContact(Long roomId, ReviewRequestDto reviewRequestDto) {
        // ChatRoom, Contact, currentId, other 선언 및 초기화
        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        Contact currentContact = currentRoom.getContact();
        Long currentId = SecurityUtil.getCurrentId();
        Member other;
        ContactStatus writerStatus = currentContact.getWriterStatus();
        ContactStatus otherStatus = currentContact.getOtherStatus();

        // 내가 writer인 경우, 상대방은 other
        if (currentId.equals(currentContact.getWriter().getMemberId())) {
            if (writerStatus != ContactStatus.완료) {
                throw new CustomException(String.valueOf(currentContact.getContactId()), "미완료된 의뢰");
            }
            other = currentContact.getOther();
        }
        // 내가 other인 경우, 상대방은 writer
        else if (currentId.equals(currentContact.getOther().getMemberId())) {
            if (otherStatus != ContactStatus.완료) {
                throw new CustomException(String.valueOf(currentContact.getContactId()), "미완료된 의뢰");
            }
            other = currentContact.getWriter();
        }
        // 내가 해당 의뢰에서 writer도, other도 아닌 경우
        else {
            throw new CustomException(String.valueOf(currentContact.getContactId()), "나의 의뢰가 아님");
        }

        if (reviewRepository.existsByContactAndMember(currentContact, other)) {
            throw new CustomException(String.valueOf(currentContact.getContactId()), "이미 리뷰 존재");
        }

        // writerStatus와 otherStatus가 모두 완료일 때만 false
        boolean isDisabled = !(writerStatus == ContactStatus.완료 && otherStatus == ContactStatus.완료);

        Review review = Review.builder()
                .member(other)
                .contact(currentContact)
                .score(reviewRequestDto.getScore())
                .contents((reviewRequestDto.getContents()))
                .isDisabled(isDisabled)
                .build();

        reviewRepository.save(review);
    }

    // 컨트롤러에서 GET /contact/{roomId}/status 로 호출되는 메서드
    public ContactStatusResponseDto getContactStatus(Long roomId) {
        //ChatRoom, Contact 초기화
        ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(String.valueOf(roomId), "존재하지 않는 채팅방ID"));
        Contact currentContact = currentRoom.getContact();
        Long currentId = SecurityUtil.getCurrentId();
        validateIsMyContact(currentContact, currentId);

        return validateStatus(currentContact, currentId);
    }

    // 수락,취소,완료 메서드에서 호출되는 메서드
    private ContactStatusResponseDto getContactStatus(Contact currentContact, Long currentId) {
        return validateStatus(currentContact, currentId);
    }

    // 실제 검증되는 로직을 구현한 메서드
    private ContactStatusResponseDto validateStatus(Contact currentContact, Long currentId) {
        // 나의 상태와 상대방의 상태 초기화
        ContactStatus myStatus;
        ContactStatus otherStatus;
        if (currentId.equals(currentContact.getWriter().getMemberId())) {
            myStatus = currentContact.getWriterStatus();
            otherStatus = currentContact.getOtherStatus();
        } else {
            myStatus = currentContact.getOtherStatus();
            otherStatus = currentContact.getWriterStatus();
        }

        // 둘 중 하나라도 취소된 상태면, 그 의뢰는 취소된 것이다.
        if (myStatus == ContactStatus.취소 || otherStatus == ContactStatus.취소) {
            return ContactStatusResponseDto.of(ContactStatus.취소, false);
        }

        // 만약 내가 대기중이라면, 의뢰 상태는 대기중이며, 수락 버튼을 활성화 해야 한다.
        if (myStatus == ContactStatus.대기중) {
            return ContactStatusResponseDto.of(ContactStatus.대기중, true);
        }

        // 만약 내가 진행중 상태이고
        if (myStatus == ContactStatus.진행중) {
            // 상대가 대기중 상태라면, 의뢰 상태는 대기중이며, 수락 버튼을 비활성화 해야 한다.
            if (otherStatus == ContactStatus.대기중) {
                return ContactStatusResponseDto.of(ContactStatus.대기중, false);
            }
            // 상대가 진행중 이거나 완료한 상태이면, 의뢰 상태는 진행중이며, 완료/취소 버튼을 활성화 해야 한다.
            else { //otherStatus == ContactStatus.진행중 || otherStatus == ContactStatus.완료
                return ContactStatusResponseDto.of(ContactStatus.진행중, true);
            }
        }
        // 만약 내가 완료 상태이고
        else { //myStatus == ContactStatus.완료
            // 상대가 진행중 상태이면, 의뢰 상태는 진행중이며, 완료/취소 버튼을 비활성화 해야 한다.
            if (otherStatus == ContactStatus.진행중) {
                return ContactStatusResponseDto.of(ContactStatus.진행중, false);
            }
            // 상대가 완료 상태이면, 의뢰 상태는 완료이며, 완료/취소 버튼을 비활성화 해야 한다.
            else { //otherStatus == ContactStatus.완료
                return ContactStatusResponseDto.of(ContactStatus.완료, false);
            }
        }
    }

    // 처리하려는 의뢰가 내가 참여한 의뢰인지 검증하고 아니면 예외를 발생시키는 메서드
    private void validateIsMyContact(Contact currentContact, Long currentId) {
        if (!currentId.equals(currentContact.getWriter().getMemberId())
            && !currentId.equals(currentContact.getOther().getMemberId())) {
            throw new CustomException(String.valueOf(currentContact), "나의 의뢰가 아님");
        }
    }

    // SSE로 NEW_STATUS 알림을 보내는 메서드
    private void sendNewStatusAlert(Long receiverId) {
        sseService.sendEventToClient(NotificationType.NEW_STATUS, receiverId, null);
    }
}
