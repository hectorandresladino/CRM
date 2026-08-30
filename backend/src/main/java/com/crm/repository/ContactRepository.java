/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByTenantId(Long tenantId);
    List<Contact> findByTenantIdAndAccountId(Long tenantId, Long accountId);
    List<Contact> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
    Optional<Contact> findByTenantIdAndEmail(Long tenantId, String email);
    long countByTenantId(Long tenantId);
}
