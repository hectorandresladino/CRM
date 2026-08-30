package com.crm.repository;

import com.crm.entity.ReglaAutomatica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaAutomaticaRepository extends JpaRepository<ReglaAutomatica, Long> {
    List<ReglaAutomatica> findByTenantId(Long tenantId);
    List<ReglaAutomatica> findByTenantIdAndEntidadAndEventoAndEsActiva(Long tenantId, String entidad, String evento, Boolean esActiva);
}
