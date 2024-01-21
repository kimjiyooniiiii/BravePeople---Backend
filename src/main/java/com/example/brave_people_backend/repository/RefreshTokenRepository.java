package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 사용자 기본키로 검색 (기본키가 문자열로 저장되어 있음)
    @Query(value = "SELECT * "
            + "FROM refresh_token r "
            + "WHERE r.update_at = (SELECT MAX(rt.update_at) FROM refresh_token rt WHERE rt.member_id = ?1)", nativeQuery = true)
    Optional<RefreshToken> findRefreshToken(String memberId);

}
