/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.BookingPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingPageRepository extends JpaRepository<BookingPage, Long> {
    List<BookingPage> findByTenantId(Long tenantId);
    List<BookingPage> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
    Optional<BookingPage> findBySlug(String slug);
    Optional<BookingPage> findByTenantIdAndSlug(Long tenantId, String slug);
    List<BookingPage> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
