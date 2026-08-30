/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.WhatsAppBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WhatsAppBusinessRepository extends JpaRepository<WhatsAppBusiness, Long> {
    
    List<WhatsAppBusiness> findByEstado(String estado);
    List<WhatsAppBusiness> findByTipo(String tipo);
    List<WhatsAppBusiness> findByTelefono(String telefono);
    List<WhatsAppBusiness> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    List<WhatsAppBusiness> findByLeidoFalse();
    List<WhatsAppBusiness> findByRespondidoFalse();
}
