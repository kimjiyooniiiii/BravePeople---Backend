package com.example.brave_people_backend.board.service;

import com.example.brave_people_backend.board.dto.*;
import com.example.brave_people_backend.chat.service.ChatRoomService;
import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.enumclass.ContactStatus;
import com.example.brave_people_backend.exception.Custom404Exception;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.BoardRepository;
import com.example.brave_people_backend.repository.ChatRoomRepository;
import com.example.brave_people_backend.repository.ContactRepository;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomService chatRoomService;
    private final ContactRepository contactRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 글 목록 불러오기
    @Transactional(readOnly = true)
    public PostListResponseDto getPostList(String type, Integer distance, Integer page, Integer amount) {

        Long currentMemberId = SecurityUtil.getCurrentId(); //현재 로그인 한 MemberId 반환
        Sort sort = Sort.by(Sort.Direction.DESC, "postId"); //POST 테이블의 post_id 기준 내림차순 정렬 설정
        PageRequest pageRequest = PageRequest.of(page, amount, sort); //출력할 page와 amount 및 sort 기준 설정 (pageable 구현체)

        Slice<Post> postList;

        //비로그인 회원 OR distance 파라미터가 null OR distance 파라미터 값이 0일 경우
        if (currentMemberId == null || distance == null || distance == 0) {
            //Repository에 act(원정대/의뢰인), PageRequest 정보를 넘기고
            //검색된 Post 엔티티를 Slice<Post>로 반환 받음
            postList = boardRepository.findPostList(Act.valueOf(type), pageRequest);
        }
        //로그인한 회원일 경우
        else {
            //currentMemberId로 현재 로그인 한 Member 엔티티를 반환
            Member findMember = memberRepository.findById(currentMemberId)
                    .orElseThrow(() -> new CustomException(String.valueOf(currentMemberId), "존재하지 않는 멤버ID"));

            //Member 엔티티에서 위도 경도 데이터 반환
            BigDecimal lat = findMember.getLat();
            BigDecimal lng = findMember.getLng();

            //Repository에 act(원정대/의뢰인), distance(반경), lat(위도), lng(경도), PageRequest 정보를 넘기고
            //검색된 Post 엔티티를 Slice<Post>로 반환 받음
            postList = boardRepository.findPostListByRadius(Act.valueOf(type), distance, lat, lng, pageRequest);
        }

        return PostListResponseDto.of(postList.hasNext(), postList.map(PostListVo::of).toList());

    }

    //글 작성
    public void createPost(PostRequestDto postRequestDto) {
        Long currentMemberId = SecurityUtil.getCurrentId();

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new CustomException(String.valueOf(currentMemberId), "존재하지 않는 멤버ID"));

        //게시글 저장
        boardRepository.save(postRequestDto.toPost(findMember));
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post findPost = boardRepository.findById(postId).
                orElseThrow(() -> new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글"));

        if (findPost.isDeleted()) {
            throw new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글");
        }

        return PostResponseDto.of(findPost);
    }

    //글 수정
    public void updatePost(Long postId, PostRequestDto postRequestDto) {
        //pathvariable에서 받은 postId로 post 객체 검색
        Post findPost = boardRepository.findPostById(postId)
                .orElseThrow(() -> new CustomException(String.valueOf(postId), "존재하지 않는 게시글"));

        //현재 Post의 작성자와 다르면
        if (!findPost.getMember().getMemberId().equals(SecurityUtil.getCurrentId())) {
            throw new CustomException(String.valueOf(postId), "권한 없음");
        }

        if (findPost.isDisabled() || findPost.isDeleted()) {
            throw new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글");
        }

        findPost.updatePost(postRequestDto);
    }

    //글 삭제
    public void deletePost(Long postId) {

        //pathvariable에서 받은 postId로 post 객체 검색
        Post findPost = boardRepository.findPostById(postId)
                .orElseThrow(() -> new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글"));

        //현재 Post의 작성자와 다르면
        if (!findPost.getMember().getMemberId().equals(SecurityUtil.getCurrentId())) {
            throw new CustomException(String.valueOf(postId), "권한 없음");
        }

        if (findPost.isDeleted()) {
            throw new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글");
        }

        findPost.onDeleted();
    }

    //의뢰 만들기
    public ContactResponseDto makeContact(Long postId) {
        Member helper = new Member();
        Member client = new Member();
        Long currentId = SecurityUtil.getCurrentId();

        //postId로 현재 post를 찾음
        Post currentPost = boardRepository.findPostById(postId)
                .orElseThrow(() -> new Custom404Exception(String.valueOf(postId), "존재하지 않는 게시글"));

        Act currentAct = currentPost.getAct();
        //현재 post의 Act가 "원정대"이면 -> "원정대" 게시글이면
        if(currentAct.equals(Act.원정대)) {
            //현재 post의 member은 helper이고, 토큰에서 찾은 member은 client가 됨
            helper = currentPost.getMember();
            client = memberRepository.findById(currentId)
                    .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        }
        //현재 post의 Act가 "의뢰인"이면 -> "의뢰인" 게시글이면
        else if(currentAct.equals(Act.의뢰인)) {
            //현재 post의 member은 client이고, 토큰에서 찾은 member은 helper가 됨
            client = currentPost.getMember();
            helper = memberRepository.findById(currentId)
                    .orElseThrow(() -> new CustomException(String.valueOf(currentId), "존재하지 않는 멤버ID"));
        }

        //본인과의 채팅방이 개설되지 않게 함
        if(client.getMemberId().equals(helper.getMemberId())) {
            throw new CustomException(String.valueOf(postId), "본인의 게시글");
        }

        //Contact 테이블에 이미 생성된 채팅방이 있는지 조회하여 있으면 roomId 반환
        Long roomId = chatRoomRepository.findChatRoom(helper.getMemberId(), client.getMemberId());

        //존재하는 채팅방이 없으면 새로운 채팅방 생성
        if (roomId == null) {
            Contact contact  = Contact.builder()
                    .helper(helper)
                    .client(client)
                    .post(currentPost)
                    .contactStatus(ContactStatus.대기중)
                    .isHelperFinished(false)
                    .isClientFinished(false)
                    .isDeleted(false)
                    .build();

            contactRepository.save(contact);
            //새 채팅방을 생성하여 roomId를 받음
            roomId = chatRoomService.createChatRoom(helper, client);
        }
        return ContactResponseDto.of(roomId);
    }
}
