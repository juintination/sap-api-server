package com.jay.sapapi.service;

import com.jay.sapapi.dto.TokensDTO;
import com.jay.sapapi.util.JWTUtil;
import com.jay.sapapi.util.exception.CustomJWTException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${spring.jwt.access-token.expiration}")
    private int accessTokenExpiration;

    @Value("${spring.jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;

    @Value("${spring.jwt.refresh.threshold}")
    private int refreshThreshold;

    @Override
    public TokensDTO refreshTokens(String authorizationHeader, String refreshToken) {
        validateTokens(authorizationHeader, refreshToken);
        String accessToken = extractAccessToken(authorizationHeader);

        if (!isExpired(accessToken)) {
            return new TokensDTO(accessToken, refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        String newAccessToken = JWTUtil.generateToken(claims, accessTokenExpiration);
        String newRefreshToken = updateRefreshToken(claims, refreshToken);
        return new TokensDTO(newAccessToken, newRefreshToken);
    }

    private void validateTokens(String authorizationHeader, String refreshToken) {
        if (refreshToken == null) {
            throw new CustomJWTException("nullRefreshToken");
        }
        if (authorizationHeader == null || authorizationHeader.length() < 7) {
            throw new CustomJWTException("invalidToken");
        }
    }

    private String extractAccessToken(String authorizationHeader) {
        return authorizationHeader.substring(7);
    }

    private boolean isExpired(String token) {
        try {
            JWTUtil.validateToken(token);
            return false;
        } catch (CustomJWTException e) {
            if ("Expired".equals(e.getMessage())) {
                return true;
            }
            throw e;
        }
    }

    private String updateRefreshToken(Map<String, Object> claims, String currentRefreshToken) {
        return shouldRefresh(claims) ? JWTUtil.generateToken(claims, refreshTokenExpiration) : currentRefreshToken;
    }

    private boolean shouldRefresh(Map<String, Object> claims) {
        Instant expiration = Instant.ofEpochSecond((Integer) claims.get("exp"));
        long minutesLeft = Duration.between(Instant.now(), expiration).toMinutes();
        return minutesLeft < refreshThreshold;
    }
}
