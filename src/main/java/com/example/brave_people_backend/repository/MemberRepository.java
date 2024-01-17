package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 가입 아이디로 사용자 검색
    Optional<Member> findByUsername(String username);

    // 같은 아이디 있는지 검색
    boolean existsByUsername(String username);
}
