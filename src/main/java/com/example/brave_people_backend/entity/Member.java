package com.example.brave_people_backend.entity;

import com.example.brave_people_backend.enumclass.Authority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memberId;

    private String username;

    private String pw;

    private String email;

    private String nickname;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean gender;

    @Enumerated(EnumType.STRING)
    private Authority authority;
}
