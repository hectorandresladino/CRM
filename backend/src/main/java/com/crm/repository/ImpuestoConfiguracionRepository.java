package com.crm.repository;

import com.crm.entity.ImpuestoConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImpuestoConfiguracionRepository extends JpaRepository<ImpuestoConfiguracion, Long> {
    List<ImpuestoConfiguracion> findByTenantIdAndEsActivo(Long tenantId, Boolean esActivo);
    List<ImpuestoConfiguracion> findByTenantIdAndPais(Long tenantId, String pais);
}
