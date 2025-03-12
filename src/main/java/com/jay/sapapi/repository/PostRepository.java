package com.jay.sapapi.repository;

import com.jay.sapapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p, w FROM Post p LEFT JOIN p.writer w WHERE p.postId = :postId")
    Object getPostByPostId(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p JOIN FETCH p.writer")
    List<Post> findAllWithWriter();

}
