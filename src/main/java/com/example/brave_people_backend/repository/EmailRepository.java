package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    List<Email> findByEmailAddress(String emailAddress);
}
