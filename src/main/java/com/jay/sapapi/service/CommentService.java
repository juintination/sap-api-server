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
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .content(comment.getContent())
                .commenterId(comment.getCommenter().getUserId())
                .commenterEmail(comment.getCommenter().getEmail())
                .commenterNickname(comment.getCommenter().getNickname())
                .regDate(comment.getRegDate())
                .modDate(comment.getModDate())
                .build();
    }

}
