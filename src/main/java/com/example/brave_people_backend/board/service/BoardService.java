package com.example.brave_people_backend.board.service;

import com.example.brave_people_backend.board.dto.CreatePostRequestDto;
import com.example.brave_people_backend.board.dto.PostListResponseDto;
import com.example.brave_people_backend.board.dto.PostListVo;
import com.example.brave_people_backend.board.dto.PostResponseDto;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.exception.Custom404Exception;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.BoardRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
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
                    .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

            //Member 엔티티에서 위도 경도 데이터 반환
            BigDecimal lat = findMember.getLat();
            BigDecimal lng = findMember.getLng();

            //Repository에 act(원정대/의뢰인), distance(반경), lat(위도), lng(경도), PageRequest 정보를 넘기고
            //검색된 Post 엔티티를 Slice<Post>로 반환 받음
            postList = boardRepository.findPostListByRadius(Act.valueOf(type), distance, lat, lng, pageRequest);
        }

        return PostListResponseDto.of(postList.hasNext(), postList.map(PostListVo::of).toList());

    }

    @Transactional
    //글 작성
    public void createPost(CreatePostRequestDto createPostRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

        //게시글 저장
        boardRepository.save(createPostRequestDto.toPost(findMember));
    }

    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post findPost = boardRepository.findById(postId).
                orElseThrow(() -> new Custom404Exception("게시글 없음"));

        if (findPost.isDeleted()) {
            throw new CustomException("삭제된 게시글");
        }

        return PostResponseDto.of(findPost);
    }
}
