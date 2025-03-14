package com.jay.sapapi.repository;

import com.jay.sapapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p, w, COUNT(DISTINCT c), COUNT(DISTINCT h) " +
            "FROM Post p " +
            "LEFT JOIN p.writer w " +
            "LEFT JOIN Comment c ON c.post = p " +
            "LEFT JOIN PostLike h ON h.post = p " +
            "WHERE p.id = :postId " +
            "GROUP BY p, w")
    Object getPostByPostId(@Param("postId") Long postId);

    @Query("SELECT p, w, COUNT(DISTINCT c), COUNT(DISTINCT h) " +
            "FROM Post p " +
            "LEFT JOIN p.writer w " +
            "LEFT JOIN Comment c ON c.post = p " +
            "LEFT JOIN PostLike h ON h.post = p " +
            "GROUP BY p, w")
    List<Object> getAllPosts();

}
