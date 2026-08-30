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

import java.util.*;

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

    public Map<String, String> setupMfa(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getMfaSecret() == null || !Boolean.TRUE.equals(user.getMfaEnabled())) {
            return true;
        }

        if (user.getMfaRecoveryCodes() != null && !user.getMfaRecoveryCodes().isEmpty()) {
            Set<String> codes = new HashSet<>(Arrays.asList(user.getMfaRecoveryCodes().split(",")));
            if (codes.contains(code)) {
                codes.remove(code);
                user.setMfaRecoveryCodes(String.join(",", codes));
                usuarioRepository.save(user);
                log.info("Recovery code usado para usuario {}", username);
                return true;
            }
        }

        return verifier.isValidCode(user.getMfaSecret(), code);
    }

    public void disableMfa(String username, String code) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!verifyCode(username, code)) {
            throw new RuntimeException("Codigo MFA invalido");
        }

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        user.setMfaRecoveryCodes(null);
        usuarioRepository.save(user);
        log.info("MFA desactivado para usuario {}", username);
    }

    public List<String> regenerateRecoveryCodes(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<String> codes = generateRecoveryCodes();
        user.setMfaRecoveryCodes(String.join(",", codes));
        usuarioRepository.save(user);
        return codes;
    }

    private List<String> generateRecoveryCodes() {
        Random random = new Random();
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int code = 100000000 + random.nextInt(900000000);
            codes.add(String.valueOf(code));
        }
        return codes;
    }

    public boolean isMfaRequired(String username) {
        return usuarioRepository.findByUsername(username)
                .map(u -> Boolean.TRUE.equals(u.getMfaEnabled()))
                .orElse(false);
    }
}
