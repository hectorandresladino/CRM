/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.EmailMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailMarketingRepository extends JpaRepository<EmailMarketing, Long> {
    
    List<EmailMarketing> findByEstado(String estado);
    List<EmailMarketing> findByTipo(String tipo);
    List<EmailMarketing> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    List<EmailMarketing> findByRemitente(String remitente);
}
