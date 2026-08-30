package com.crm.repository;

import com.crm.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    List<Currency> findByTenantId(Long tenantId);
    Optional<Currency> findByTenantIdAndCode(Long tenantId, String code);
    List<Currency> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
