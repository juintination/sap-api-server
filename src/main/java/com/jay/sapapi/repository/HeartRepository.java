package com.jay.sapapi.repository;

import com.jay.sapapi.domain.Heart;
import com.jay.sapapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    @Query("SELECT h FROM Heart h " +
            "JOIN FETCH h.post " +
            "JOIN FETCH h.member " +
            "WHERE h.post.id = :postId " +
            "AND h.member.id = :userId")
    Optional<Heart> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    List<Heart> getHeartsByPostOrderByRegDate(Post post);

}
