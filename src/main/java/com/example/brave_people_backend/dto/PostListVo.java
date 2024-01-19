package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 게시물 리스트 반환 형식을 따르는 VO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListVo {

    private String type;
    private String title;
    private int price;
    private String gender;
    private String createdAt;
    private String category;
    private Long postId;

    public static PostListVo of(Post post) {
        return PostListVo.builder()
                .type(post.getAct().toString())
                .title(post.getTitle())
                .price(post.getPrice())
                .gender(post.getMember().isGender() ? "여성" : "남성") //남자면 false, 여자면 true
                .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .category(post.getCategory().toString())
                .postId(post.getPostId())
                .build();
    }
}
