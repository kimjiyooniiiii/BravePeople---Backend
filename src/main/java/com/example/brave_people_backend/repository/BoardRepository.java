package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.Act;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Post, Long> {

    // 반경 계산을 위한 하버사인 공식 적용 (distance=1 -> 1km)

    @Query("select p from Post p where p.act = :act and p.isDeleted = false and p.isDisabled = false and" +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(p.lat)) * cos(radians(p.lng) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(p.lat)))) <= :distance")
    Slice<Post> findPostListByRadius(@Param("act") Act act,
                                     @Param("distance") int distance,
                                     @Param("lat") BigDecimal lat,
                                     @Param("lng") BigDecimal lng,
                                Pageable pageable);

    @Query("select p from Post p where p.act = :act and p.isDeleted = false and p.isDisabled = false")
    Slice<Post> findPostList(@Param("act") Act act, Pageable pageable);

    @Query("select p from Post p join fetch p.member where p.member.memberId = :memberId and p.isDeleted = false")
    Slice<Post> findPostListByProfilePage(@Param("memberId") Long memberId, Pageable pageable);

    Optional<Post> findByPostId(@Param("postId") Long postId);
}
