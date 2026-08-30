package com.crm.service;

import com.crm.entity.Usuario;
import com.crm.repository.PlanRepository;
import com.crm.repository.SubscriptionRepository;
import com.crm.repository.TenantRepository;
import com.crm.repository.UsuarioRepository;
import com.crm.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UsuarioRepository users;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokens;
    private MfaService mfa;
    private AuthService service;

    @BeforeEach
    void setup() {
        users = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokens = mock(JwtTokenProvider.class);
        mfa = mock(MfaService.class);
        service = new AuthService(users, mock(TenantRepository.class), mock(PlanRepository.class),
                mock(SubscriptionRepository.class), passwordEncoder, tokens, mfa);
    }

    @Test
    void enabledMfaMustBeVerifiedBeforeTokensAreIssued() {
        Usuario user = user("owner", 8L, Usuario.Role.TENANT_OWNER);
        user.setMfaEnabled(true);
        when(users.findAllByUsername("owner")).thenReturn(List.of(user));
        when(passwordEncoder.matches("secret", user.getPassword())).thenReturn(true);
        when(mfa.verifyLoginCode(user, "000000")).thenReturn(false);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.login("owner", "secret", null, "000000"));

        assertTrue(error.getMessage().contains("MFA"));
        assertEquals(1, user.getFailedLoginAttempts());
        verify(tokens, never()).generateAccessToken(any());
    }

    @Test
    void platformAdminCanRefreshWithoutTenantClaim() {
        Usuario admin = user("platform", null, Usuario.Role.SUPER_ADMIN);
        admin.setRefreshToken("old-refresh");
        admin.setRefreshTokenExpires(LocalDateTime.now().plusHours(1));
        when(tokens.validateToken("old-refresh")).thenReturn(true);
        when(tokens.getUsernameFromToken("old-refresh")).thenReturn("platform");
        when(tokens.getTenantIdFromToken("old-refresh")).thenReturn(null);
        when(users.findByUsernameAndTenantIdIsNull("platform")).thenReturn(Optional.of(admin));
        when(tokens.generateAccessToken(admin)).thenReturn("new-access");
        when(tokens.generateRefreshToken(admin)).thenReturn("new-refresh");
        when(tokens.getRefreshExpirationMs()).thenReturn(60_000L);

        AuthService.AuthResponse response = service.refreshToken("old-refresh");

        assertEquals("new-access", response.accessToken);
        assertEquals("new-refresh", response.refreshToken);
        assertNull(response.tenantId);
    }

    private Usuario user(String username, Long tenantId, Usuario.Role role) {
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setTenantId(tenantId);
        user.setRol(role);
        user.setPassword("encoded");
        user.setActivo(true);
        user.setFailedLoginAttempts(0);
        return user;
    }
}
