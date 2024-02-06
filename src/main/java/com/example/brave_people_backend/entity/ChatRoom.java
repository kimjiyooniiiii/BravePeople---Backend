package com.example.brave_people_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_member_id", nullable = false, updatable = false)
    private Member memberA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_member_id", nullable = false, updatable = false)
    private Member memberB;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isPartIn; //참여 여부 (?)

    @CreatedDate
    @Column(columnDefinition = "timestamp", nullable = false, updatable = false)
    private LocalDateTime aEnteredAt;

    @CreatedDate
    @Column(columnDefinition = "timestamp", nullable = false, updatable = false)
    private LocalDateTime bEnteredAt;
}
