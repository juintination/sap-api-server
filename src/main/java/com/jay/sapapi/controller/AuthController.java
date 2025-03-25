package com.jay.sapapi.controller;

import com.jay.sapapi.dto.TokensDTO;
import com.jay.sapapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PutMapping("/tokens")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader,
                                     @RequestParam("refreshToken") String refreshToken) {
        TokensDTO tokensDTO = authService.refreshTokens(authHeader, refreshToken);
        return ResponseEntity.ok(Map.of(
                "message", "refreshSuccess",
                "data", tokensDTO
        ));
    }

}
