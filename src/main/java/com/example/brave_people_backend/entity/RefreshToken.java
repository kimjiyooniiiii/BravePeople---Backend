package com.example.brave_people_backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String memberId;     // memberId를 String으로 저장

    @Column(length = 512)
    private String refreshToken;      // refresh token

    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    @Builder
    public RefreshToken(String memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken updateValue(String refreshToken) {
        this.updateAt = LocalDateTime.now();
        this.refreshToken = refreshToken;
        return this;
    }
}
