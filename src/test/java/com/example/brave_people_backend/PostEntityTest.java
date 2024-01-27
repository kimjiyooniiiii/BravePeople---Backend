package com.example.brave_people_backend;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.enumclass.Category;
import com.example.brave_people_backend.repository.BoardRepository;
import com.example.brave_people_backend.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
public class PostEntityTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;

    /**
     * 더미데이터 삽입을 위한 메서드
     */
    @Test
    @Transactional
    @Rollback(false)
    public void insertPostTest() {

        Member findMember = memberRepository.findById(1L).get();
        Post post = Post.builder()
                .member(findMember)
                .category(Category.values()[0])
                .title("제목" + 38 + " 춘천" + 38 + " 제목" + 38 + " 춘천" + 38 + " 제목" + 38 + " 춘천" + 38)
                .contents("내용내용내용내용내용내용내용내용")
                .price(10000)
                .lat(new BigDecimal("37.8735918177534151"))
                .lng(new BigDecimal("127.742263121389912"))
                .act(Act.원정대)
                .build();
        boardRepository.save(post);

    }
}
