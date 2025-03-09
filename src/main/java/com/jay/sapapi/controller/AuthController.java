package com.jay.sapapi.controller;

import com.jay.sapapi.util.JWTUtil;
import com.jay.sapapi.util.exception.CustomJWTException;
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

    @PutMapping("/tokens")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader,
                                     @RequestParam("refreshToken") String refreshToken) {

        if (refreshToken == null) {
            throw new CustomJWTException("nullRefreshToken");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("invalidToken");
        }

        String accessToken = authHeader.substring(7);

        if (!checkExpiredToken(accessToken)) {
            return ResponseEntity.ok(Map.of("message", "refreshSuccess", "data",
                    Map.of("accessToken", accessToken, "refreshToken", refreshToken)));
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("Refresh, Claims: " + claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);
        String newRefreshToken = checkTime((Integer) claims.get("exp"))
                ? JWTUtil.generateToken(claims, 60 * 24)
                : refreshToken;
        return ResponseEntity.ok(Map.of("message", "refreshSuccess", "data",
                Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken)));
    }

    private boolean checkTime(Integer exp) {
        java.util.Date expDate = new java.util.Date((long) exp * 1000);
        long gap = expDate.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException e) {
            if (e.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }

}
