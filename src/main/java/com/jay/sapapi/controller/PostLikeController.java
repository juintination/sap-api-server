package com.jay.sapapi.controller;

import com.jay.sapapi.dto.PostLikeDTO;
import com.jay.sapapi.service.PostLikeService;
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
@RequestMapping("/api/likes/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @GetMapping("/{postId}/users/{userId}")
    public Map<String, Object> get(@PathVariable Long postId, @PathVariable Long userId) {
        PostLikeDTO dto = postLikeService.get(postId, userId);
        return Map.of("message", "success", "data", Map.of("data", dto));
    }

    @GetMapping("/{postId}")
    public Map<String, Object> getHeartsByPost(@PathVariable Long postId) {
        return Map.of("message", "success", "data", postLikeService.getHeartsByPost(postId));
    }

    @PostMapping("/")
    @PreAuthorize("#dto.userId == authentication.principal.userId")
    public ResponseEntity<?> register(PostLikeDTO dto) {
        long heartId = postLikeService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "data", Map.of("id", heartId)));
    }

    @DeleteMapping("/{postId}/users/{userId}")
    public Map<String, Object> remove(@PathVariable Long postId, @PathVariable Long userId) {
        postLikeService.remove(postId, userId);
        return Map.of("message", "removeSuccess");
    }

}
