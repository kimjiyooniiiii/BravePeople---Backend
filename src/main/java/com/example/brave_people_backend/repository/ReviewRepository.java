package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByContactAndMember(Contact contact, Member member);

    List<Review> findByContact(Contact contact);

    // 나의 활성화된 후기 검색
    @Query("select r from Review r where r.member.memberId = :memberId and r.isDisabled = false")
    List<Review> findActiveReview(@Param("memberId") Long memberId, Sort sort);
}
