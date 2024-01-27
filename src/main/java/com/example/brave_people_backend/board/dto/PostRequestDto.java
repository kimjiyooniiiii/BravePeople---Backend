package com.example.brave_people_backend.board.dto;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.enumclass.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    //게시글 작성 request dto
    @Pattern(regexp = "^(의뢰인|원정대)$")
    private String type;

    @NotBlank @Size(max = 40)
    private String title;

    @NotBlank @Size(max = 1000)
    private String contents;

    @Range(min = -1, max = 999999)
    private int price;

    @Pattern(regexp = "^(벌레|전화|환불|기타)$")
    private String category;

    private String img;

    //Post 객체 생성
    public Post toPost(Member member) {
        return Post.builder()
                .member(member)
                .category(Category.valueOf(category))
                .title(title)
                .contents(contents)
                .price(price)
                .lat(member.getLat())
                .lng(member.getLng())
                .act(Act.valueOf(type))
                .url(img)
                .build();
    }
}