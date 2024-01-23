package com.example.brave_people_backend.board.controller;

import com.example.brave_people_backend.board.dto.PostListResponseDto;
import com.example.brave_people_backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public PostListResponseDto getPostList(@RequestParam("type") String type,
                                                 @RequestParam(name = "distance", required = false) Integer distance,
                                                 @RequestParam(name = "page") Integer page,
                                                 @RequestParam("amount") Integer amount) {

        return boardService.getPostList(type, distance, page, amount);
    }
}
