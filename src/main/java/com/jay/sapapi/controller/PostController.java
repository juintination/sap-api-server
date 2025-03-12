package com.jay.sapapi.controller;

import com.jay.sapapi.dto.PostDTO;
import com.jay.sapapi.service.PostService;
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
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public Map<String, Object> get(@PathVariable Long postId) {
        postService.incrementViewCount(postId);
        PostDTO dto = postService.get(postId);
        return Map.of("message", "success", "data", dto);
    }

    @PostMapping("/")
    @PreAuthorize("#dto.writerId == authentication.principal.userId && #dto.writerEmail == authentication.principal.username")
    public ResponseEntity<?> register(PostDTO dto) {
        long postId = postService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "data", Map.of("postId", postId)));
    }

    @PutMapping("/{postId}")
    @PreAuthorize("#dto.writerId == authentication.principal.userId && #dto.writerEmail == authentication.principal.username")
    public Map<String, Object> modify(@PathVariable Long postId, PostDTO dto) {
        dto.setPostId(postId);
        postService.modify(dto);
        return Map.of("message", "modifySuccess");
    }

    @DeleteMapping("/{postId}")
    public Map<String, Object> remove(@PathVariable Long postId) {
        postService.remove(postId);
        return Map.of("message", "postDeleted");
    }

}
