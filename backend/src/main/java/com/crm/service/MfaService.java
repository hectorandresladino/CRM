/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Usuario;
import com.crm.repository.UsuarioRepository;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.crm.security.TenantContext;

import java.util.*;
import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MfaService {

    private final UsuarioRepository usuarioRepository;
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final SecureRandom secureRandom = new SecureRandom();

    public Map<String, String> setupMfa(String username) {
        return setupMfa(username, null);
    }

    public Map<String, String> setupMfa(String username, String currentCode) {
        Usuario user = findCurrentUser(username);
        if (Boolean.TRUE.equals(user.getMfaEnabled()) && !verifyLoginCode(user, currentCode)) {
            throw new RuntimeException("Confirme el MFA actual antes de reemplazarlo");
        }

        String secret = secretGenerator.generate();
        user.setMfaSecret(secret);
        user.setMfaEnabled(false);
        usuarioRepository.save(user);

        QrData data = new QrData.Builder()
                .label(user.getEmail() != null ? user.getEmail() : username)
                .secret(secret)
                .issuer("CRM SaaS")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        String qrUrl;
        try {
            byte[] qrImage = qrGenerator.generate(data);
            String base64 = Base64.getEncoder().encodeToString(qrImage);
            qrUrl = "data:image/png;base64," + base64;
        } catch (QrGenerationException e) {
            log.error("Error generating QR", e);
            qrUrl = data.getUri();
        }

        List<String> recoveryCodes = generateRecoveryCodes();
        user.setMfaRecoveryCodes(String.join(",", recoveryCodes));
        usuarioRepository.save(user);

        Map<String, String> result = new HashMap<>();
        result.put("secret", secret);
        result.put("qrCode", qrUrl);
        result.put("otpAuthUrl", data.getUri());
        result.put("recoveryCodes", String.join(", ", recoveryCodes));
        return result;
    }

    public boolean verifyAndEnableMfa(String username, String code) {
        Usuario user = findCurrentUser(username);

        if (user.getMfaSecret() == null) {
            throw new RuntimeException("MFA no configurado. Ejecute setup primero.");
        }

        boolean valid = verifier.isValidCode(user.getMfaSecret(), code);
        if (valid) {
            user.setMfaEnabled(true);
            usuarioRepository.save(user);
            log.info("MFA activado para usuario {}", username);
        }
        return valid;
    }

    public boolean verifyCode(String username, String code) {
        Usuario user = findCurrentUser(username);
        return verifyLoginCode(user, code);
    }

    /** Verifies TOTP or a single-use recovery code before issuing a session. */
    public boolean verifyLoginCode(Usuario user, String code) {

        if (code == null || code.isBlank() || user.getMfaSecret() == null
                || !Boolean.TRUE.equals(user.getMfaEnabled())) {
            return false;
        }

        if (user.getMfaRecoveryCodes() != null && !user.getMfaRecoveryCodes().isEmpty()) {
            Set<String> codes = new HashSet<>(Arrays.asList(user.getMfaRecoveryCodes().split(",")));
            if (codes.contains(code)) {
                codes.remove(code);
                user.setMfaRecoveryCodes(String.join(",", codes));
                usuarioRepository.save(user);
                log.info("Recovery code usado para usuario {}", user.getUsername());
                return true;
            }
        }

        return verifier.isValidCode(user.getMfaSecret(), code);
    }

    public void disableMfa(String username, String code) {
        Usuario user = findCurrentUser(username);

        if (!verifyCode(username, code)) {
            throw new RuntimeException("Codigo MFA invalido");
        }

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        user.setMfaRecoveryCodes(null);
        usuarioRepository.save(user);
        log.info("MFA desactivado para usuario {}", username);
    }

    public List<String> regenerateRecoveryCodes(String username, String currentCode) {
        Usuario user = findCurrentUser(username);
        if (!verifyLoginCode(user, currentCode)) {
            throw new RuntimeException("Código MFA inválido");
        }

        List<String> codes = generateRecoveryCodes();
        user.setMfaRecoveryCodes(String.join(",", codes));
        usuarioRepository.save(user);
        return codes;
    }

    private List<String> generateRecoveryCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int code = 100000000 + secureRandom.nextInt(900000000);
            codes.add(String.valueOf(code));
        }
        return codes;
    }

    public boolean isMfaRequired(String username) {
        return Boolean.TRUE.equals(findCurrentUser(username).getMfaEnabled());
    }

    private Usuario findCurrentUser(String username) {
        Usuario user = (TenantContext.hasTenant()
                ? usuarioRepository.findByTenantIdAndUsername(TenantContext.requireCurrentTenant(), username)
                : usuarioRepository.findByUsernameAndTenantIdIsNull(username))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!TenantContext.hasTenant() && user.getRol() != Usuario.Role.SUPER_ADMIN) {
            throw new RuntimeException("Usuario de plataforma inválido");
        }
        return user;
    }
}
