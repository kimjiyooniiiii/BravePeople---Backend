package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.PostListResponseDto;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<PostListResponseDto> getPostList(String type, Integer distance, Integer lastid, Integer amount) {

        System.out.println("type = " + type);
        System.out.println("distance = " + distance);
        System.out.println("lastid = " + lastid);
        System.out.println("amount = " + amount);

        List<Post> allPostList = boardRepository.findAllByOrderByPostIdDesc();

        return allPostList.stream().map(PostListResponseDto::of).toList();
    }

}
