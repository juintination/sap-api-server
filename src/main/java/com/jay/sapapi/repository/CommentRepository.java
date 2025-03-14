package com.jay.sapapi.repository;

import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.post " +
            "JOIN FETCH c.commenter " +
            "WHERE c.id = :commentId")
    Optional<Comment> getCommentByCommentId(@Param("commentId") Long commentId);

    List<Comment> getCommentsByPostOrderById(Post post);

}
