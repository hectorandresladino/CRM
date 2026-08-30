/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.ESignatureRequest;
import com.crm.repository.ESignatureRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ESignatureService {

    private final ESignatureRequestRepository repository;

    public List<ESignatureRequest> findAll(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    public ESignatureRequest create(ESignatureRequest request) {
        request.setSignatureToken(UUID.randomUUID().toString());
        request.setStatus("PENDING");
        if (request.getExpiresAt() == null) {
            request.setExpiresAt(LocalDateTime.now().plusDays(30));
        }
        return repository.save(request);
    }

    public ESignatureRequest sign(String token, String signerIp) {
        ESignatureRequest request = repository.findBySignatureToken(token)
                .orElseThrow(() -> new RuntimeException("Token de firma invÃ¡lido"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Este documento ya fue firmado o expirÃ³");
        }

        if (request.getExpiresAt() != null && request.getExpiresAt().isBefore(LocalDateTime.now())) {
            request.setStatus("EXPIRED");
            repository.save(request);
            throw new RuntimeException("El enlace de firma ha expirado");
        }

        request.setStatus("SIGNED");
        request.setSignedAt(LocalDateTime.now());
        request.setSignerIp(signerIp);
        request.setSignatureHash(UUID.randomUUID().toString().replace("-", ""));
        request.setAuditTrail("Document signed at " + LocalDateTime.now() + " from IP " + signerIp);

        return repository.save(request);
    }

    public void cancel(Long id) {
        ESignatureRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        request.setStatus("CANCELLED");
        repository.save(request);
    }
}
