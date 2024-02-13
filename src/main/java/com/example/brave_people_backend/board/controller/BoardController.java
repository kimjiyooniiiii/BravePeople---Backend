package com.example.brave_people_backend.board.controller;

import com.example.brave_people_backend.board.dto.ContactResponseDto;
import com.example.brave_people_backend.board.dto.PostListResponseDto;
import com.example.brave_people_backend.board.dto.PostRequestDto;
import com.example.brave_people_backend.board.dto.PostResponseDto;
import com.example.brave_people_backend.board.service.BoardService;
import com.example.brave_people_backend.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ChatRoomService chatRoomService;

    //글 목록 조회
    @GetMapping
    public PostListResponseDto getPostList(@RequestParam("type") String type,
                                                 @RequestParam(name = "distance", required = false) Integer distance,
                                                 @RequestParam(name = "page") Integer page,
                                                 @RequestParam("amount") Integer amount) {

        return boardService.getPostList(type, distance, page, amount);
    }

    //글 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        boardService.createPost(postRequestDto);
    }

    //글 조회
    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") Long postId) {
        return boardService.getPost(postId);
    }

    //글 수정
    @PatchMapping("/{postId}")
    public void updatePost(@PathVariable("postId") Long postId, @Valid @RequestBody PostRequestDto postRequestDto) {
        boardService.updatePost(postId, postRequestDto);
    }

    //글 삭제
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable("postId") Long postId) {
        boardService.deletePost(postId);
    }

    //의뢰 만들기
    @GetMapping("/{postId}/request")
    public ContactResponseDto makeContact (@PathVariable("postId") Long postId) {
        return chatRoomService.makeContact(postId);
    }

}
