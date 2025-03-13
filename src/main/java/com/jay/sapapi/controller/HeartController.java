package com.jay.sapapi.controller;

import com.jay.sapapi.dto.HeartDTO;
import com.jay.sapapi.service.HeartService;
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
@RequestMapping("/api/hearts")
public class HeartController {

    private final HeartService heartService;

    @GetMapping("/posts/{postId}/users/{userId}")
    public Map<String, Object> get(@PathVariable Long postId, @PathVariable Long userId) {
        HeartDTO dto = heartService.get(postId, userId);
        return Map.of("message", "success", "data", Map.of("data", dto));
    }

    @GetMapping("/posts/{postId}")
    public Map<String, Object> getHeartsByPost(@PathVariable Long postId) {
        return Map.of("message", "success", "data", heartService.getHeartsByPost(postId));
    }

    @PostMapping("/")
    @PreAuthorize("#dto.userId == authentication.principal.userId")
    public ResponseEntity<?> register(HeartDTO dto) {
        long heartId = heartService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "heartId", heartId));
    }

    @DeleteMapping("/posts/{postId}/users/{userId}")
    public Map<String, Object> remove(@PathVariable Long postId, @PathVariable Long userId) {
        heartService.remove(postId, userId);
        return Map.of("message", "removeSuccess");
    }

}
