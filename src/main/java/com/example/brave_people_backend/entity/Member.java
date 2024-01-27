package com.example.brave_people_backend.entity;

import com.example.brave_people_backend.enumclass.Authority;
import com.example.brave_people_backend.member.dto.UpdateProfileInfoRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 20, unique = true, updatable = false)
    private String username;    // 아이디

    private String pw;

    @Column(length = 30, unique = true, updatable = false)
    private String email;

    @Column(length = 6, unique = true)
    private String nickname;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean gender;

//    @ColumnDefault("37.566770140877412") //위치 정보가 없는 경우 default 위도 : 서울특별시청
    @Column(precision = 18, scale=15, nullable = false) //전체 자릿수는 18개, 소수점 자릿수는 15개
    private BigDecimal lat;

//    @ColumnDefault("126.978640955202641") //위치 정보가 없는 경우 default 경도 : 서울특별시청
    @Column(precision = 18, scale=15, nullable = false)
    private BigDecimal lng;

    @Column(length = 50)
    private String introduction;

    @Column(length = 260)
    private String profileImg;

    @Column(columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(length = 5, updatable = false)
    private String name;

    @Column(length = 512)
    private String refreshToken;

    //위도, 경도 변환 setter
    public void changeLatAndLng(BigDecimal lat, BigDecimal lng) {
        this.lat = lat;
        this.lng = lng;
    }

    //비밀번호 setter
    public void changePw(String pw) {
        this.pw = pw;
    }

    // Refresh Token setter
    public void changeRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public void changeProfileInfo(UpdateProfileInfoRequestDto updateProfileInfoRequestDto) {
        this.nickname = updateProfileInfoRequestDto.getNickname();
        this.introduction = updateProfileInfoRequestDto.getIntroduction();
        this.profileImg = updateProfileInfoRequestDto.getProfileImg();
    }
}
