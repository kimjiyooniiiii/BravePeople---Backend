package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 가입 아이디로 사용자 검색
    Optional<Member> findByUsername(String username);

    // 아이디 중복 체크
    boolean existsByUsername(String username);

    // 이메일로 사용자 검색
    Optional<Member> findByEmail(String email);

    // 같은 이메일 있는지 검색
    boolean existsByEmail(String email);

    //닉네임 중복 체크
    boolean existsByNickname(String nickname);

}
