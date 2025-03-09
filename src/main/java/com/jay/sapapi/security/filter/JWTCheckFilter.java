package com.jay.sapapi.security.filter;

import com.google.gson.Gson;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.security.dto.CustomUserDetails;
import com.jay.sapapi.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI();
        if (request.getMethod().equals("POST")) {
            if (path.equals("/api/users/") || path.startsWith("/api/auth/")) {
                return true;
            }
        } else if (request.getMethod().equals("GET")) {
            if (path.startsWith("/api/users/")) {
                return true;
            } else if (path.equals("/") || path.equals("/docs")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String authHeaderStr = request.getHeader("Authorization");
        try {
            // Bearer accessToken...
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.info("JWT claims: " + claims);

            long userId = Long.parseLong(claims.get("userId").toString());
            String email = (String) claims.get("email");
            String nickname = (String) claims.get("nickname");
            String role = (String) claims.get("role");
            MemberRole memberRole = MemberRole.valueOf(role);

            Member member = Member.builder()
                    .userId(userId)
                    .email(email)
                    .nickname(nickname)
                    .memberRole(memberRole)
                    .build();
            log.info("Member: " + member);

            CustomUserDetails customUserDetails = new CustomUserDetails(member);
            log.info("CustomUserDetails: " + customUserDetails);

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(customUserDetails, "", customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch(Exception e) {
            log.error("JWT Error: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(Map.of("message", "invalidToken"));
            PrintWriter printWriter = response.getWriter();
            printWriter.write(jsonResponse);
            printWriter.flush();
        }
    }
}
