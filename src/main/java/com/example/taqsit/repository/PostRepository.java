package com.example.taqsit.repository;

import com.example.taqsit.entity.Post;
import com.example.taqsit.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByUser(User user, Pageable pageable);

    List<Post> findAllByUser(User user);
}
