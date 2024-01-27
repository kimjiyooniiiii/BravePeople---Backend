package com.example.brave_people_backend.entity;

import com.example.brave_people_backend.board.dto.PostRequestDto;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.enumclass.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)", nullable = false)
    private Category category;

    @Column(length = 40, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String contents;

    @Column(nullable = false)
    private int price;

    @CreatedDate
    @Column(columnDefinition = "timestamp", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(precision = 18, scale=15, nullable = false)
    private BigDecimal lat;

    @Column(precision = 18, scale=15, nullable = false)
    private BigDecimal lng;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isDisabled;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isDeleted;

    @Column(columnDefinition = "varchar(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Act act;

    @Column(length = 260)
    private String url;


    public void updatePost(PostRequestDto postRequestDto) {
        this.category = Category.valueOf(postRequestDto.getCategory());
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
        this.price = postRequestDto.getPrice();
        this.url = postRequestDto.getImg();
    }

    public void onDeleted() {
        isDeleted = true;
    }
}
