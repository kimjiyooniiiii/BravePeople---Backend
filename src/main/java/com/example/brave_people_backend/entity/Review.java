package com.example.brave_people_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member; //후기 받은사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false, updatable = false)
    private Member writer; //후기 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false, updatable = false)
    private Contact contact;

    @Column(nullable = false)
    private int score;

    @Column(length = 200)
    private String contents;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isDisabled;

    public void changeIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}
