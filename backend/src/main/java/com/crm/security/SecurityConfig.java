/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final com.crm.security.RateLimitFilter rateLimitFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, com.crm.security.RateLimitFilter rateLimitFilter, ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(opts -> {})
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/client-portal/login").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/health/**").permitAll()
                .requestMatchers("/swagger-ui/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/v3/api-docs/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/swagger-ui.html").hasRole("SUPER_ADMIN")
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/superadmin/**", "/api/v1/devops/**")
                    .hasRole("SUPER_ADMIN")
                .requestMatchers(
                    "/api/api-keys/**", "/api/integrations/**", "/api/sso/**",
                    "/api/tenant-config/**", "/api/webhooks/**", "/api/campos-personalizados/**",
                    "/api/v1/rbac/**", "/api/v1/security/**", "/api/v1/security-compliance/**",
                    "/api/v1/platform/**", "/api/v1/automation/**")
                    .hasAnyRole("TENANT_OWNER", "ADMIN")
                .requestMatchers(
                    "/api/v1/billing/**", "/api/facturas/**", "/api/pagos/**",
                    "/api/impuestos/**", "/api/v1/commerce/**")
                    .hasAnyRole("TENANT_OWNER", "ADMIN", "ACCOUNTING")
                .requestMatchers(
                    "/api/campanas-marketing/**", "/api/email-marketing/**", "/api/email-templates/**",
                    "/api/formularios-web/**", "/api/lead-scores/**", "/api/v1/marketing-advanced/**")
                    .hasAnyRole("TENANT_OWNER", "ADMIN", "MANAGER", "MARKETING")
                .requestMatchers(
                    "/api/servicio-cliente/**", "/api/mesa-ayuda/**", "/api/pqrs/**",
                    "/api/encuestas-satisfaccion/**", "/api/sla/**", "/api/v1/service-cloud/**",
                    "/api/v1/service-advanced/**")
                    .hasAnyRole("TENANT_OWNER", "ADMIN", "MANAGER", "SUPPORT")
                .requestMatchers(
                    "/api/ventas/**", "/api/cotizaciones/**", "/api/pedidos/**", "/api/cpq/**",
                    "/api/productos/**", "/api/metas/**", "/api/v1/sales-cloud/**",
                    "/api/v1/sales-advanced/**", "/api/v1/revenue-ai/**")
                    .hasAnyRole("TENANT_OWNER", "ADMIN", "MANAGER", "SALES", "ACCOUNTING")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                })
            )
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Tenant-Id", "X-API-Key"));
        configuration.setExposedHeaders(List.of("X-Total-Count", "X-Page-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
