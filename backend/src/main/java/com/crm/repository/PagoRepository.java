/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<Pago> findByTenantIdAndEstado(Long tenantId, String estado);
    List<Pago> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    Optional<Pago> findByTenantIdAndId(Long tenantId, Long id);
}
