package com.jay.sapapi.dto.tokens;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class TokensDTO {

    private String accessToken;

    private String refreshToken;

}
