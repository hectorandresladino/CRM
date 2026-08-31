/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Pago;
import com.crm.repository.PagoRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {
    private final PagoRepository repository;

    public List<Pago> findAll(Long tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tid());
    }

    public List<Pago> findByEstado(Long tenantId, String estado) {
        return repository.findByTenantIdAndEstado(tid(), estado);
    }

    public Pago save(Pago pago) {
        pago.setTenantId(tid());
        if (pago.getReferencia() == null || pago.getReferencia().isEmpty()) {
            pago.setReferencia(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (pago.getEstado() == null) {
            pago.setEstado("PENDIENTE");
        }
        return repository.save(pago);
    }

    public Pago markAsPaid(Long id, String transactionId) {
        Pago pago = repository.findByTenantIdAndId(tid(), id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pago.setEstado("APROBADO");
        pago.setTransactionId(transactionId);
        pago.setFechaPago(LocalDateTime.now());
        return repository.save(pago);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
