package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByPostIdDesc();
}
