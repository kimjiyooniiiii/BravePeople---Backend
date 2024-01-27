package com.example.brave_people_backend.board.controller;

import com.example.brave_people_backend.board.dto.CreatePostRequestDto;
import com.example.brave_people_backend.board.dto.PostListResponseDto;
import com.example.brave_people_backend.board.dto.PostResponseDto;
import com.example.brave_people_backend.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

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
    public void createPost(@Valid @RequestBody CreatePostRequestDto createPostRequestDto) {
        boardService.createPost(createPostRequestDto);
    }

    //글 조회
    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") Long postId) {
        return boardService.getPost(postId);
    }
}
