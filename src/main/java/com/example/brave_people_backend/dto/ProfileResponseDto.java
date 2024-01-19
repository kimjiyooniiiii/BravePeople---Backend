package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {

    private String username;
    private String nickname;
    private String gender;
    private String introduction;
    private double score;
    private int medalCount;
    private String profileImage;
    private Long memberId;

    public static ProfileResponseDto of(Member member) {
        return ProfileResponseDto.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .gender(member.isGender() ? "여성" : "남성")
                .introduction(member.getIntroduction())
                .score(3.5)
                .medalCount(11)
                .profileImage("https://images.ctfassets.net/h6goo9gw1hh6/2sNZtFAWOdP1lmQ33VwRN3/24e953b920a9cd0ff2e1d587742a2472/1-intro-photo-final.jpg")
                .memberId(member.getMemberId())
                .build();
    }

}
