package com.jay.sapapi.security.handler;

import com.google.gson.Gson;
import com.jay.sapapi.security.dto.CustomUserDetails;
import com.jay.sapapi.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
@AllArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    private int accessTokenExpiration, refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("Authentication: " + authentication);
        log.info("accessTokenExpiration: " + accessTokenExpiration);
        log.info("refreshTokenExpiration: " + refreshTokenExpiration);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> claims = customUserDetails.getClaims();

        String accessToken = JWTUtil.generateToken(claims, accessTokenExpiration);
        String refreshToken = JWTUtil.generateToken(claims, refreshTokenExpiration);

        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        Map<String, Object> responseBody = Map.of(
                "message", "loginSuccess",
                "data", claims
        );

        Gson gson = new Gson();
        String jsonStr = gson.toJson(responseBody);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }

}
