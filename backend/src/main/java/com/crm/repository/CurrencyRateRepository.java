package com.crm.repository;

import com.crm.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    List<CurrencyRate> findByTenantId(Long tenantId);
    Optional<CurrencyRate> findByTenantIdAndBaseAndTarget(Long tenantId, String base, String target);
}
