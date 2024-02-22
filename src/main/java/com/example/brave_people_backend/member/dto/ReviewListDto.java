package com.example.brave_people_backend.member.dto;

import com.example.brave_people_backend.entity.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 프로필 페이지 API로 응답되는 후기 Dto
public class ReviewListDto {

    private Long reviewId;

    private String nickName;

    private String profileImg;

    private String content;

    private double score;

    public static ReviewListDto of(Review review) {
        return ReviewListDto.builder()
                .reviewId(review.getReviewId())
                .nickName(review.getWriter().getNickname())
                .profileImg(review.getWriter().getProfileImg())
                .content(review.getContents())
                .score(review.getScore())
                .build();
    }
}
