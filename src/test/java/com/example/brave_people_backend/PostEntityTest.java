package com.example.brave_people_backend;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.Act;
import com.example.brave_people_backend.enumclass.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
public class PostEntityTest {

    @PersistenceContext
    EntityManager em;

    /**
     * 더미데이터 삽입을 위한 메서드
     */
    @Test
    @Transactional
    @Rollback(false)
    public void insertPostTest() {

        Member findMember = em.find(Member.class, 1L);

        for(int i=1; i<=10; i++) {
            Post post = Post.builder()
                    .member(findMember)
                    .category(Category.values()[i%4])
                    .title("제목" + i + " 춘천" + i + " 제목" + i + " 춘천" + i + " 제목" + i + " 춘천" + i)
                    .contents("내용내용내용내용내용내용내용내용")
                    .price(10000 * i)
                    .lat(new BigDecimal("37.8735918177534151"))
                    .lng(new BigDecimal("127.742263121389912"))
                    .act(Act.원정대)
                    .build();
            em.persist(post);
        }

        for(int i=11; i<=20; i++) {
            Post post = Post.builder()
                    .member(findMember)
                    .category(Category.values()[i%4])
                    .title("제목" + i + " 서울" + i + " 제목" + i + " 서울" + i + " 제목" + i + " 서울" + i)
                    .contents("내용내용내용내용내용내용내용내용")
                    .price(10000 * i)
                    .lat(new BigDecimal("37.5668151927169941"))
                    .lng(new BigDecimal("126.978652261394212"))
                    .act(Act.원정대)
                    .build();
            em.persist(post);
        }

        em.flush();
        em.clear();

    }
}
