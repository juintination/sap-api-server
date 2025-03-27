package com.jay.sapapi.controller;

import com.jay.sapapi.dto.postlike.PostLikeDTO;
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
@RequestMapping("/api/posts/{postId}/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @GetMapping("/users/{userId}")
    public Map<String, Object> get(@PathVariable Long postId, @PathVariable Long userId) {
        PostLikeDTO dto = postLikeService.get(postId, userId);
        return Map.of("message", "success", "data", Map.of("data", dto));
    }

    @GetMapping("/")
    public Map<String, Object> getHeartsByPost(@PathVariable Long postId) {
        return Map.of("message", "success", "data", postLikeService.getHeartsByPost(postId));
    }

    @PostMapping("/users/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> register(@PathVariable Long postId, @PathVariable Long userId) {
        long heartId = postLikeService.register(postId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "data", Map.of("id", heartId)));
    }

    @DeleteMapping("/users/{userId}")
    public Map<String, Object> remove(@PathVariable Long postId, @PathVariable Long userId) {
        postLikeService.remove(postId, userId);
        return Map.of("message", "removeSuccess");
    }

}
