package com.jay.sapapi.service;

import com.jay.sapapi.dto.TokensDTO;
import com.jay.sapapi.util.JWTUtil;
import com.jay.sapapi.util.exception.CustomJWTException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

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
        String newAccessToken = JWTUtil.generateToken(claims, 10);
        String newRefreshToken = shouldRefresh(claims) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

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
        Integer exp = (Integer) claims.get("exp");
        Date expDate = new Date((long) exp * 1000);
        long gap = expDate.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;
    }

}
