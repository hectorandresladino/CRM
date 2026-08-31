/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByTenantId(Long tenantId);
    List<Account> findByTenantIdAndParentAccountId(Long tenantId, Long parentAccountId);
    List<Account> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
    List<Account> findByTenantIdAndStatus(Long tenantId, Account.AccountStatus status);
    Optional<Account> findByTenantIdAndId(Long tenantId, Long id);
}
