package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByContactAndMember(Contact contact, Member member);

    List<Review> findByContact(Contact contact);
}
