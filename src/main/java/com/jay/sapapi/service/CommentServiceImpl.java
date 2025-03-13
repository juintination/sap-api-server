package com.jay.sapapi.service;

import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.CommentDTO;
import com.jay.sapapi.repository.CommentRepository;
import com.jay.sapapi.util.exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public CommentDTO get(Long commentId) {
        Optional<Comment> result = commentRepository.getCommentByCommentId(commentId);
        Comment comment = result.orElseThrow(() -> new NoSuchElementException("commentNotFound"));
        return entityToDTO(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> result = commentRepository.getCommentsByPostOrderByCommentId(Post.builder().postId(postId).build());
        return result.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public Long register(CommentDTO commentDTO) {
        if (commentDTO.getContent() == null) {
            throw new CustomValidationException("invalidCommentContent");
        }
        Comment result = commentRepository.save(dtoToEntity(commentDTO));
        return result.getCommentId();
    }

    @Override
    public void modify(CommentDTO commentDTO) {
        Optional<Comment> result = commentRepository.findById(commentDTO.getCommentId());
        Comment comment = result.orElseThrow(() -> new NoSuchElementException("commentNotFound"));
        comment.changeContent(commentDTO.getContent());
        commentRepository.save(comment);
    }

    @Override
    public void remove(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NoSuchElementException("commentNotFound");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment dtoToEntity(CommentDTO commentDTO) {
        return Comment.builder()
                .commentId(commentDTO.getCommentId())
                .content(commentDTO.getContent())
                .post(Post.builder().postId(commentDTO.getPostId()).build())
                .commenter(Member.builder().userId(commentDTO.getCommenterId()).build())
                .build();
    }

}
