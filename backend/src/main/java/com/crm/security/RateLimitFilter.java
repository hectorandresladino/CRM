/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_MS = 60_000L;

    private final Map<String, RateBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api/auth/") || path.startsWith("/api/v1/auth/")) {
            String ip = getClientIp(request);
            RateBucket bucket = buckets.computeIfAbsent(ip, k -> new RateBucket());

            synchronized (bucket) {
                long now = System.currentTimeMillis();
                if (now - bucket.windowStart > WINDOW_MS) {
                    bucket.windowStart = now;
                    bucket.count.set(0);
                }
                if (bucket.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                    response.setStatus(429);
                    response.getWriter().write("Demasiadas peticiones. Intente mas tarde.");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateBucket {
        long windowStart = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
    }
}
