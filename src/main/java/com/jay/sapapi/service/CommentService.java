package com.jay.sapapi.service;

import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.dto.comment.request.CommentCreateRequestDTO;
import com.jay.sapapi.dto.comment.request.CommentModifyRequestDTO;
import com.jay.sapapi.dto.comment.response.CommentResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CommentService {

    @Transactional(readOnly = true)
    CommentResponseDTO get(Long commentId);

    @Transactional(readOnly = true)
    List<CommentResponseDTO> getCommentsByPostId(Long postId);

    Long register(CommentCreateRequestDTO commentDTO);

    void modify(Long commentId, CommentModifyRequestDTO commentDTO);

    void remove(Long commentId);

    Comment dtoToEntity(CommentCreateRequestDTO commentDTO);

    default CommentResponseDTO entityToDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .userId(comment.getCommenter().getId())
                .commenterEmail(comment.getCommenter().getEmail())
                .commenterNickname(comment.getCommenter().getNickname())
                .commenterProfileImageUrl(comment.getCommenter().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

}
