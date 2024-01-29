package com.example.brave_people_backend.board.dto;

import com.example.brave_people_backend.board.ChronoUtil;
import com.example.brave_people_backend.board.DecimalUtil;
import com.example.brave_people_backend.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostResponseDto {

    private String type;
    private String title;
    private String contents;
    private String postImg;
    private String price;
    private String gender;
    private String createdAt;
    private String category;
    private String nickname;
    private String profileImg;
    private String lat;
    private String lng;
    private double score;
    private boolean isDisabled;
    private Long memberId;
    private Long postId;

    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .type(post.getAct().toString())
                .title(post.getTitle())
                .contents(post.getContents())
                .postImg(post.getUrl())
                .price(DecimalUtil.intToStringPrice(post.getPrice()))
                .gender(post.getMember().isGender() ? "여성" : "남성")
                .createdAt(ChronoUtil.timesAgo(post.getCreatedAt()))
                .category(post.getCategory().toString())
                .nickname(post.getMember().getNickname())
                .profileImg(post.getMember().getProfileImg())
                .lat(post.getLat().toString())
                .lng(post.getLng().toString())
                .score(4.5)
                .isDisabled(post.isDisabled())
                .memberId(post.getMember().getMemberId())
                .postId(post.getPostId())
                .build();
    }
}
