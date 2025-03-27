package com.jay.sapapi.service;

import com.jay.sapapi.dto.tokens.TokensDTO;

public interface AuthService {

    TokensDTO refreshTokens(String authHeader, String refreshToken);

}
