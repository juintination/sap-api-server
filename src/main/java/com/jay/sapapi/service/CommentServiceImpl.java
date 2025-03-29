package com.jay.sapapi.service;

import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.comment.request.CommentCreateRequestDTO;
import com.jay.sapapi.dto.comment.request.CommentModifyRequestDTO;
import com.jay.sapapi.dto.comment.response.CommentResponseDTO;
import com.jay.sapapi.repository.CommentRepository;
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

    private final PostService postService;

    @Override
    public CommentResponseDTO get(Long commentId) {
        Optional<Comment> result = commentRepository.getCommentByCommentId(commentId);
        Comment comment = result.orElseThrow(() -> new NoSuchElementException("commentNotFound"));
        return entityToDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        List<Comment> result = commentRepository.getCommentsByPostOrderById(Post.builder().id(postId).build());
        return result.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public Long register(CommentCreateRequestDTO commentDTO) {
        Comment result = commentRepository.save(dtoToEntity(commentDTO));
        return result.getId();
    }

    @Override
    public void modify(Long commentId, CommentModifyRequestDTO commentDTO) {
        Optional<Comment> result = commentRepository.findById(commentId);
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
    public Comment dtoToEntity(CommentCreateRequestDTO commentDTO) {
        return Comment.builder()
                .content(commentDTO.getContent())
                .post(Post.builder().id(commentDTO.getPostId()).build())
                .commenter(Member.builder().id(commentDTO.getUserId()).build())
                .build();
    }

}
