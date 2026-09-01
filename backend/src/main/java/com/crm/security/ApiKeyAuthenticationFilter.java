/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

import com.crm.entity.ApiKey;
import com.crm.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-Key");

        if (apiKey != null && !apiKey.isEmpty()) {
            ApiKey key = apiKeyRepository.findByKeyAndEsActivo(apiKey, true).orElse(null);
            
            if (key != null) {
                // Set tenant context from API key
                TenantContext.setCurrentTenant(key.getTenantId());
                
                // Create authentication for API key user
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "api-key-" + key.getId(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_KEY"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Update usage stats
                key.setUltimoUso(java.time.LocalDateTime.now());
                key.setTotalUsos(key.getTotalUsos() + 1);
                apiKeyRepository.save(key);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid API Key");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
