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
@Table(name = "chat_room")
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ColumnDefault("true")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean aIsPartIn;

    @ColumnDefault("true")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean bIsPartIn;

    @CreatedDate
    @Column(columnDefinition = "timestamp", nullable = false)
    private LocalDateTime aEnteredAt;

    @CreatedDate
    @Column(columnDefinition = "timestamp", nullable = false)
    private LocalDateTime bEnteredAt;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean aIsRead;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean bIsRead;

    private String aLastReadId;       // A가 마지막으로 읽은 채팅 메시지의 ID

    private String bLastReadId;       // B가 마지막으로 읽은 채팅 메시지의 ID

    public void changeContact(Contact contact) {
        this.contact = contact;
    }

    public void changeAIsRead(boolean aIsRead) { this.aIsRead = aIsRead; }

    public void changeBIsRead(boolean bIsRead) { this.bIsRead = bIsRead; }

    public void changeALastReadId(String chatId) { this.aLastReadId = chatId; }

    public void changeBLastReadId(String chatId) { this.bLastReadId = chatId; }

    // A와 B의 참여 여부를 true 혹은 false로 업데이트
    public void changeIsPartIn(String identity, boolean isPartIn) {
        if(identity.equals("A")) {
            this.aIsPartIn = isPartIn;
        } else {
            this.bIsPartIn = isPartIn;
        }
    }

    public void changeEnteredAt(String identity, LocalDateTime enteredAt) {
        if (identity.equals("A")) {
            this.aEnteredAt = enteredAt;
        }
        else {
            this.bEnteredAt = enteredAt;
        }
        this.aEnteredAt = aEnteredAt;
    }
}
