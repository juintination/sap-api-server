package com.jay.sapapi.controller;

import com.jay.sapapi.dto.MemberDTO;
import com.jay.sapapi.service.MemberService;
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
@RequestMapping("/api/users")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(@PathVariable("userId") Long userId) {
        MemberDTO dto = memberService.get(userId);
        return ResponseEntity.ok(Map.of("message", "success", "data", dto));
    }

    @PostMapping("/")
    public ResponseEntity<?> register(@RequestBody MemberDTO dto) {
        long userId = memberService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "data", Map.of("userId", userId)));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> modify(@PathVariable("userId") Long userId, @RequestBody MemberDTO dto) {
        dto.setUserId(userId);
        memberService.modify(dto);
        return ResponseEntity.ok(Map.of("message", "modifySuccess"));
    }

    @PutMapping("/{userId}/password")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> changePassword(@PathVariable("userId") Long userId, @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        memberService.checkPassword(userId, oldPassword);
        String newPassword = passwordMap.get("newPassword");
        MemberDTO dto = memberService.get(userId);
        dto.setPassword(newPassword);
        memberService.modify(dto);
        return ResponseEntity.ok(Map.of("message", "modifySuccess"));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> remove(@PathVariable("userId") Long userId) {
        memberService.remove(userId);
        return ResponseEntity.ok(Map.of("message", "userDeleted"));
    }

}
