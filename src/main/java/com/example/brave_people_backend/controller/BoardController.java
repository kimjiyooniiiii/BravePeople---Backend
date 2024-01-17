package com.example.brave_people_backend.controller;

import com.example.brave_people_backend.dto.PostListResponseDto;
import com.example.brave_people_backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public List<PostListResponseDto> getPostList(@RequestParam("type") String type,
                                                 @RequestParam(name = "distance", required = false) Integer distance,
                                                 @RequestParam(name = "lastid", required = false) Integer lastid,
                                                 @RequestParam("amount") Integer amount) {

        return boardService.getPostList(type, distance, lastid, amount);
    }
}
