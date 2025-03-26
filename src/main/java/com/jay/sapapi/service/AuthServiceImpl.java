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
        if (refreshToken == null) {
            throw new CustomJWTException("nullRefreshToken");
        }
        if (authorizationHeader == null || authorizationHeader.length() < 7) {
            throw new CustomJWTException("invalidToken");
        }

        String accessToken = authorizationHeader.substring(7);

        if (!isExpired(accessToken)) {
            return new TokensDTO(accessToken, refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        String newAccessToken = JWTUtil.generateToken(claims, accessTokenExpiration);
        String newRefreshToken = shouldRefresh(claims) ? JWTUtil.generateToken(claims, refreshTokenExpiration) : refreshToken;

        return new TokensDTO(newAccessToken, newRefreshToken);
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

    private boolean shouldRefresh(Map<String, Object> claims) {
        Instant expiration = Instant.ofEpochSecond((Integer) claims.get("exp"));
        long minutesLeft = Duration.between(Instant.now(), expiration).toMinutes();
        return minutesLeft < refreshThreshold;
    }

}
