package com.crm.repository;

import com.crm.entity.SLAConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SLAConfiguracionRepository extends JpaRepository<SLAConfiguracion, Long> {
    List<SLAConfiguracion> findByTenantIdAndActivo(Long tenantId, Boolean activo);
    List<SLAConfiguracion> findByTenantIdAndPrioridad(Long tenantId, String prioridad);
}
