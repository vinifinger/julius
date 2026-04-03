package com.finance.app.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String DURATION_MS = "duration_ms";
    private static final String HTTP_METHOD = "http_method";
    private static final String REQUEST_URI = "request_uri";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().replace("-", "");

        try {
            MDC.put(REQUEST_ID, requestId);
            MDC.put(HTTP_METHOD, request.getMethod());
            MDC.put(REQUEST_URI, request.getRequestURI());

            if (!request.getRequestURI().contains("/actuator/health")) {
                log.atInfo().log("Request Started: {} {}", request.getMethod(), request.getRequestURI());
            }

            filterChain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put(DURATION_MS, String.valueOf(duration));

            if (!request.getRequestURI().contains("/actuator/health")) {
                log.atInfo().log("Request Completed: {} {} - Status: {} ({}ms)",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
            }
            
            MDC.clear();
        }
    }
}
