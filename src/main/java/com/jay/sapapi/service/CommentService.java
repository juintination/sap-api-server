package com.jay.sapapi.service;

import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.dto.CommentDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CommentService {

    CommentDTO get(Long commentId);

    List<CommentDTO> getCommentsByPostId(Long postId);

    Long register(CommentDTO commentDTO);

    void modify(CommentDTO commentDTO);

    void remove(Long commentId);

    Comment dtoToEntity(CommentDTO commentDTO);

    default CommentDTO entityToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .commenterId(comment.getCommenter().getId())
                .commenterEmail(comment.getCommenter().getEmail())
                .commenterNickname(comment.getCommenter().getNickname())
                .commenterProfileImageUrl(comment.getCommenter().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

}
