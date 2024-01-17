package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {

    private String type;
    private String title;
    private int price;
    private String gender;
    private String createdAt;
    private String category;
    private Long postId;

    public static PostListResponseDto of(Post post) {
        return PostListResponseDto.builder()
                .type(post.getAct().toString())
                .title(post.getTitle())
                .price(post.getPrice())
                .gender(post.getMember().isGender() ? "여성" : "남성") //남자면 false, 여자면 true
                .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                .category(post.getCategory().toString())
                .postId(post.getPostId())
                .build();
    }

}
