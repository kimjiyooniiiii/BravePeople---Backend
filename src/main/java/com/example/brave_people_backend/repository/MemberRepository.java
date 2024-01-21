package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 가입 아이디로 사용자 검색
    Optional<Member> findByUsername(String username);

    // 같은 아이디 있는지 검색
    boolean existsByUsername(String username);

    // 이메일로 사용자 검색
    Optional<Member> findByEmail(@Param("email") String email);

    // 같은 이메일 있는지 검색
    boolean existsByEmail(String email);

    // 회원가입 - 아이디 또는 닉네임 중복체크
    List<Member> findByUsernameOrNickname(String username, String nickname);

}
