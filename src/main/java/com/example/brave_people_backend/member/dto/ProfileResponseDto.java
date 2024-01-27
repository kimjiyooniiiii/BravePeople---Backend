package com.example.brave_people_backend.member.dto;

import com.example.brave_people_backend.board.dto.PostListVo;
import com.example.brave_people_backend.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ProfileResponseDto {

    private String username;
    private String nickname;
    private String gender;
    private String introduction;
    private double score;
    private int medalCount;
    private String profileImage;
    private Long memberId;
    private List<PostListVo> postListVo;

    public static ProfileResponseDto of(Member member, List<PostListVo> postListVo) {
        return ProfileResponseDto.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .gender(member.isGender() ? "여성" : "남성")
                .introduction(member.getIntroduction())
                .score(3.5)
                .medalCount(28)
                .profileImage(member.getProfileImg())
                .memberId(member.getMemberId())
                .postListVo(postListVo)
                .build();
    }

}
