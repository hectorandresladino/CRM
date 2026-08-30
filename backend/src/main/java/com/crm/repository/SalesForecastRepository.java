package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesForecastRepository extends JpaRepository<SalesForecast, Long> {
    List<SalesForecast> findByTenantId(Long tenantId);
    List<SalesForecast> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<SalesForecast> findByTenantIdAndPeriodYear(Long tenantId, Integer year);
}
