package com.jay.sapapi.controller;

import com.jay.sapapi.dto.member.request.MemberModifyRequestDTO;
import com.jay.sapapi.dto.member.request.MemberPasswordChangeRequestDTO;
import com.jay.sapapi.dto.member.request.MemberSignupRequestDTO;
import com.jay.sapapi.dto.member.response.MemberResponseDTO;
import com.jay.sapapi.service.MemberService;
import com.jay.sapapi.util.exception.CustomValidationException;
import jakarta.validation.Valid;
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
        MemberResponseDTO responseDTO = memberService.get(userId);
        return ResponseEntity.ok(Map.of("message", "success", "data", responseDTO));
    }

    @GetMapping("/emails/{email}")
    public ResponseEntity<?> existsByEmail(@PathVariable("email") String email) {
        boolean exists = memberService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("message", "success", "data", exists));
    }

    @GetMapping("/nicknames/{nickname}")
    public ResponseEntity<?> existsByNickname(@PathVariable("nickname") String nickname) {
        boolean exists = memberService.existsByNickname(nickname);
        return ResponseEntity.ok(Map.of("message", "success", "data", exists));
    }

    @PostMapping("/")
    public ResponseEntity<?> register(@RequestBody @Valid MemberSignupRequestDTO dto) {
        long userId = memberService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "data", Map.of("id", userId)));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> modify(
            @PathVariable Long userId,
            @RequestBody @Valid MemberModifyRequestDTO dto) {
        memberService.modify(userId, dto);
        return ResponseEntity.ok(Map.of("message", "modifySuccess"));
    }

    @PutMapping("/{userId}/password")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody @Valid MemberPasswordChangeRequestDTO dto) {
        memberService.checkPassword(userId, dto.getOldPassword());
        memberService.changePassword(userId, dto.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "modifySuccess"));
    }

    @PostMapping("/{userId}/password/verification")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> verifyPassword(@PathVariable("userId") Long userId, @RequestBody Map<String, String> passwordMap) {
        String password = passwordMap.get("password");
        try {
            memberService.checkPassword(userId, password);
        } catch (CustomValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "incorrect"));
        }
        return ResponseEntity.ok(Map.of("message", "correct"));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> remove(@PathVariable("userId") Long userId) {
        memberService.remove(userId);
        return ResponseEntity.ok(Map.of("message", "userDeleted"));
    }

}
