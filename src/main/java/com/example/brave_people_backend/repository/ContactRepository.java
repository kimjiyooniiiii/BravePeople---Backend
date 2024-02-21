package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Post;
import com.example.brave_people_backend.enumclass.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    //현재 게시글에서 두 사람 사이에 대기중인 의뢰가 있는지 조회 -> 같은 게시글에서 대기중 상태에서 중복 의뢰 방지
    boolean existsByWriterAndOtherAndWriterStatusAndOtherStatusAndPost(@Param("writer") Member writer,
                                                                       @Param("other") Member other,
                                                                       @Param("WriterStatus") ContactStatus writerStatus,
                                                                       @Param("OtherStatus") ContactStatus otherStatus,
                                                                       @Param("post") Post post);

    //두 사람 사이에 현재 post와 관계 없이 진행중인 의뢰가 있는지 조회  -> 같은 사람끼리는 동시에 한 가지 의뢰만 가능
    @Query("select c from Contact c where ((c.writer = :writer and c.other = :other) or " +
            "(c.writer = :other and c.other = :writer)) " +
            "and ((c.writerStatus = '진행중' and c.otherStatus = '진행중') or " +
            "(c.writerStatus = '완료' and c.otherStatus = '진행중') or " +
            "(c.writerStatus = '진행중' and c.otherStatus = '완료'))")
    List<Contact> findContactOneByStatus(@Param("writer") Member writer,
                                         @Param("other") Member other);

    //해당 게시글에서 진행중인 의뢰가 있는지 조회  -> 한 게시글에는 한번에 한 의뢰만 가능
    @Query("select c from Contact c where c.post = :post and ((c.writerStatus = '진행중' and c.otherStatus = '진행중')" +
            "or (c.writerStatus = '진행중' and c.otherStatus = '완료')" +
            "or (c.writerStatus = '완료' and c.otherStatus = '진행중'))")
    List<Contact> findContactOneByStatusAndPost(@Param("post") Post post);

    //현재 post에서 생성된 의뢰 조회  ->  한 게시글에서 생성된 어떤 의뢰가 진행중 상황이 되면 나머지 의뢰들을 취소 상태로 만듦
    List<Contact> findContactsByPost(@Param("postId") Post post);

}
