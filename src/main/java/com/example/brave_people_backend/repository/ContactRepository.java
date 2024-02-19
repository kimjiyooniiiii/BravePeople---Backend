package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    //두 사람 사이에 의뢰가 있는지 조회
    boolean existsByWriterAndOtherAndPost(@Param("writer") Member writer, @Param("other") Member other,
                                          @Param("postId") Post post);

    //두 사람 사이의 의뢰 중 contactStatus가 진행중인 의뢰가 있는지 조회
    @Query("select c from Contact c where c.writer = :writer and c.other = :other and c.writerStatus = '진행중' and c.otherStatus = '진행중'")
    List<Contact> findContactOneByStatus(@Param("writer") Member writer,
                                         @Param("other") Member other);
    //해당 postId로 진행중인 의뢰가 있는지 조회
    @Query("select c from Contact c where c.post.postId = :postId and c.writerStatus = '진행중' and c.otherStatus = '진행중'")
    List<Contact> findContactOneByStatusAndPostId(@Param("postId") Long postId);

    //현재 post에서 생성된 의뢰 조회
    List<Contact> findContactsByPost(@Param("postId") Post post);

}
