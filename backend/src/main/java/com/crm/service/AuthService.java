/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.JwtTokenProvider;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MfaService mfaService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    @Transactional
    public AuthResponse login(String username, String password) {
        return authenticate(resolveUserWithoutTenant(username), password, null);
    }

    @Transactional
    public AuthResponse login(String username, String password, String tenantSlug) {
        return login(username, password, tenantSlug, null);
    }

    @Transactional
    public AuthResponse login(String username, String password, String tenantSlug, String mfaCode) {
        if (tenantSlug == null || tenantSlug.isBlank()) {
            return authenticate(resolveUserWithoutTenant(username), password, mfaCode);
        }
        Tenant tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new RuntimeException("Credenciales invÃ¡lidas"));
        Usuario usuario = usuarioRepository.findByTenantIdAndUsername(tenant.getId(), username)
                .orElseThrow(() -> new RuntimeException("Credenciales invÃ¡lidas"));
        return authenticate(usuario, password, mfaCode);
    }

    private Usuario resolveUserWithoutTenant(String username) {
        Usuario platformAdmin = usuarioRepository.findByUsernameAndTenantIdIsNull(username).orElse(null);
        if (platformAdmin != null && platformAdmin.getRol() == Usuario.Role.SUPER_ADMIN) {
            return platformAdmin;
        }

        List<Usuario> matches = usuarioRepository.findAllByUsername(username);
        if (matches.size() != 1) {
            throw new RuntimeException("Credenciales inválidas; indique la empresa para iniciar sesión");
        }
        return matches.get(0);
    }

    private AuthResponse authenticate(Usuario usuario, String password, String mfaCode) {

        if (usuario.getAccountLockedUntil() != null &&
            usuario.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cuenta bloqueada. Intente mÃ¡s tarde.");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            registerFailedAttempt(usuario);
            throw new RuntimeException("Credenciales invÃ¡lidas");
        }

        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (Boolean.TRUE.equals(usuario.getMfaEnabled())
                && (mfaCode == null || mfaCode.isBlank() || !mfaService.verifyLoginCode(usuario, mfaCode))) {
            registerFailedAttempt(usuario);
            throw new RuntimeException("Código MFA requerido o inválido");
        }

        usuario.setFailedLoginAttempts(0);
        usuario.setAccountLockedUntil(null);
        usuario.setLastLoginAt(LocalDateTime.now());

        String accessToken = jwtTokenProvider.generateAccessToken(usuario);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario);
        usuario.setRefreshToken(refreshToken);
        usuario.setRefreshTokenExpires(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshExpirationMs() / 1000));
        usuarioRepository.save(usuario);

        return new AuthResponse(accessToken, refreshToken, usuario.getRol().name(), usuario.getTenantId());
    }

    @Transactional
    public AuthResponse registerTenant(TenantRegistrationRequest request) {
        if (tenantRepository.existsBySlug(generateSlug(request.getCompanyName()))) {
            throw new RuntimeException("Ya existe una empresa con ese nombre");
        }
        Plan plan = planRepository.findByName(request.getPlanName())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + request.getPlanName()));

        Tenant tenant = new Tenant();
        tenant.setName(request.getCompanyName());
        tenant.setSlug(generateSlug(request.getCompanyName()));
        tenant.setCountry(request.getCountry());
        tenant.setCurrency(request.getCurrency());
        tenant.setTimezone(request.getTimezone());
        tenant.setLocale(request.getLocale());
        tenant.setStatus(Tenant.TenantStatus.TRIAL);
        tenant.setPlanId(plan.getId());
        tenant.setTrialEndsAt(LocalDateTime.now().plusDays(plan.getTrialDays()));
        tenant.setMaxUsers(null);
        tenant.setMaxClients(plan.getMaxClients());
        tenant.setMaxStorageMb(plan.getMaxStorageMb());
        tenant = tenantRepository.save(tenant);

        Usuario admin = new Usuario();
        admin.setTenantId(tenant.getId());
        admin.setUsername(request.getAdminUsername());
        admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setEmail(request.getAdminEmail());
        admin.setNombre(request.getAdminName());
        admin.setRol(Usuario.Role.TENANT_OWNER);
        admin.setEmailVerificationToken(UUID.randomUUID().toString());
        admin = usuarioRepository.save(admin);

        Subscription sub = new Subscription();
        sub.setTenantId(tenant.getId());
        sub.setPlanId(plan.getId());
        sub.setStatus(Subscription.SubscriptionStatus.TRIAL);
        sub.setTrialStart(LocalDateTime.now());
        sub.setTrialEnd(LocalDateTime.now().plusDays(plan.getTrialDays()));
        sub.setCurrentPeriodStart(LocalDateTime.now());
        sub.setCurrentPeriodEnd(LocalDateTime.now().plusDays(plan.getTrialDays()));
        sub.setAutoRenew(true);
        subscriptionRepository.save(sub);

        String accessToken = jwtTokenProvider.generateAccessToken(admin);
        String refreshToken = jwtTokenProvider.generateRefreshToken(admin);
        admin.setRefreshToken(refreshToken);
        admin.setRefreshTokenExpires(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshExpirationMs() / 1000));
        usuarioRepository.save(admin);

        return new AuthResponse(accessToken, refreshToken, admin.getRol().name(), admin.getTenantId());
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token invÃ¡lido");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Long tenantId = jwtTokenProvider.getTenantIdFromToken(refreshToken);
        Usuario usuario = (tenantId != null
                ? usuarioRepository.findByTenantIdAndUsername(tenantId, username)
                : usuarioRepository.findByUsernameAndTenantIdIsNull(username))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (tenantId == null && usuario.getRol() != Usuario.Role.SUPER_ADMIN) {
            throw new RuntimeException("Refresh token sin tenant válido");
        }

        if (!refreshToken.equals(usuario.getRefreshToken()) ||
            usuario.getRefreshTokenExpires() != null &&
            usuario.getRefreshTokenExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(usuario);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(usuario);
        usuario.setRefreshToken(newRefreshToken);
        usuario.setRefreshTokenExpires(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshExpirationMs() / 1000));
        usuarioRepository.save(usuario);

        return new AuthResponse(newAccessToken, newRefreshToken, usuario.getRol().name(), usuario.getTenantId());
    }

    @Transactional
    public void logout(String username) {
        Usuario usuario = (TenantContext.hasTenant()
                ? usuarioRepository.findByTenantIdAndUsername(TenantContext.requireCurrentTenant(), username)
                : usuarioRepository.findByUsernameAndTenantIdIsNull(username))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!TenantContext.hasTenant() && usuario.getRol() != Usuario.Role.SUPER_ADMIN) {
            throw new RuntimeException("Usuario de plataforma inválido");
        }
        usuario.setRefreshToken(null);
        usuario.setRefreshTokenExpires(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        List<Usuario> matches = usuarioRepository.findAllByEmail(email);
        if (matches.size() != 1) {
            throw new RuntimeException("Email no encontrado; indique la empresa");
        }
        issuePasswordReset(matches.get(0));
    }

    @Transactional
    public void requestPasswordReset(String email, String tenantSlug) {
        if (tenantSlug == null || tenantSlug.isBlank()) {
            requestPasswordReset(email);
            return;
        }
        Tenant tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));
        Usuario usuario = usuarioRepository.findByTenantIdAndEmail(tenant.getId(), email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));
        issuePasswordReset(usuario);
    }

    private void issuePasswordReset(Usuario usuario) {
        usuario.setPasswordResetToken(UUID.randomUUID().toString());
        usuario.setPasswordResetExpires(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token invÃ¡lido"));
        if (usuario.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario.setPasswordResetToken(null);
        usuario.setPasswordResetExpires(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void verifyEmail(String token) {
        Usuario usuario = usuarioRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token invÃ¡lido"));
        usuario.setEmailVerified(true);
        usuario.setEmailVerificationToken(null);
        usuarioRepository.save(usuario);
    }

    public Long getCurrentTenantId() {
        return TenantContext.getCurrentTenant();
    }

    private void registerFailedAttempt(Usuario usuario) {
        int attempts = (usuario.getFailedLoginAttempts() == null ? 0 : usuario.getFailedLoginAttempts()) + 1;
        usuario.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            usuario.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        }
        usuarioRepository.save(usuario);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public static class AuthResponse {
        public String accessToken;
        public String refreshToken;
        public String role;
        public Long tenantId;

        public AuthResponse(String accessToken, String refreshToken, String role, Long tenantId) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.role = role;
            this.tenantId = tenantId;
        }
    }

    public static class TenantRegistrationRequest {
        private String companyName;
        private String adminName;
        private String adminUsername;
        private String adminEmail;
        private String adminPassword;
        private String country;
        private String currency;
        private String timezone;
        private String locale;
        private String planName;

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        public String getAdminUsername() { return adminUsername; }
        public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
        public String getAdminEmail() { return adminEmail; }
        public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
        public String getAdminPassword() { return adminPassword; }
        public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }
    }
}
