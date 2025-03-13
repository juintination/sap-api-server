package com.jay.sapapi.controller;

import com.jay.sapapi.dto.CommentDTO;
import com.jay.sapapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public Map<String, Object> get(@PathVariable Long commentId) {
        CommentDTO dto = commentService.get(commentId);
        return Map.of("message", "success", "data", dto);
    }

    @GetMapping("/posts/{postId}")
    public Map<String, Object> getCommentsByPost(@PathVariable Long postId) {
        return Map.of("message", "success", "data", commentService.getCommentsByPostId(postId));
    }

    @PostMapping("/")
    @PreAuthorize("#dto.commenterId == authentication.principal.userId")
    public ResponseEntity<?> register(CommentDTO dto) {
        long commentId = commentService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "commentId", commentId));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("#dto.commenterId == authentication.principal.userId")
    public Map<String, Object> modify(@PathVariable Long commentId, CommentDTO dto) {
        dto.setCommenterId(commentId);
        commentService.modify(dto);
        return Map.of("message", "modifySuccess");
    }

    @DeleteMapping("/{commentId}")
    public Map<String, Object> remove(@PathVariable Long commentId) {
        commentService.remove(commentId);
        return Map.of("message", "removeSuccess");
    }

}
