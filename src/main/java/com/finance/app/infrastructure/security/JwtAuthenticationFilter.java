package com.finance.app.infrastructure.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.MDC;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                DecodedJWT decodedJWT = jwtTokenProvider.validateToken(token);
                
                String userIdStr = decodedJWT.getClaim("userId").asString();
                if (Objects.isNull(userIdStr)) {
                    throw new JWTVerificationException("Missing userId claim in token");
                }
                
                UUID userId = UUID.fromString(userIdStr);
                String email = jwtTokenProvider.getEmail(decodedJWT);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId,
                        email, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                MDC.put("userId", userId.toString());
            } catch (TokenExpiredException exception) {
                SecurityContextHolder.clearContext();
                request.setAttribute("jwt_error_message",
                        "Token has expired. Please login again to obtain a new token");
            } catch (IllegalArgumentException | JWTVerificationException exception) {
                SecurityContextHolder.clearContext();
                request.setAttribute("jwt_error_message",
                        "Invalid token format or missing claims. Please check the token provided");
            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
                request.setAttribute("jwt_error_message",
                        "An error occurred while processing the authentication token");
            }
        }

        filterChain.doFilter(request, response);
    }

}
