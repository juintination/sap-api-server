package com.jay.sapapi.repository;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.PostLike;
import com.jay.sapapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT h FROM PostLike h " +
            "JOIN FETCH h.post " +
            "JOIN FETCH h.member " +
            "WHERE h.post.id = :postId " +
            "AND h.member.id = :userId")
    Optional<PostLike> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    List<PostLike> getPostLikesByPostOrderByCreatedAt(Post post);

    List<PostLike> getPostLikesByMemberOrderByCreatedAt(Member member);

}
