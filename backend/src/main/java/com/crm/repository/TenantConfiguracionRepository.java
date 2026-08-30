package com.crm.repository;

import com.crm.entity.TenantConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantConfiguracionRepository extends JpaRepository<TenantConfiguracion, Long> {
    Optional<TenantConfiguracion> findByTenantId(Long tenantId);
}
