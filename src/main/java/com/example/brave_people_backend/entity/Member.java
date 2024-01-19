package com.example.brave_people_backend.entity;

import com.example.brave_people_backend.enumclass.Authority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    private String username;

    private String pw;

    @Column(length = 30, unique = true, updatable = false)
    private String email;

    @Column(length = 15, unique = true)
    private String nickname;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean gender;

    @ColumnDefault("37.566770140877412") //위치 정보가 없는 경우 default 위도 : 서울특별시청
    @Column(precision = 18, scale=15, nullable = false)
    private BigDecimal lat;

    @ColumnDefault("126.978640955202641") //위치 정보가 없는 경우 default 경도 : 서울특별시청
    @Column(precision = 18, scale=15, nullable = false)
    private BigDecimal lng;

    @Column(length = 50)
    private String introduction;

    @Column(length = 260)
    private String profile_img;

    @Column(columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
}
