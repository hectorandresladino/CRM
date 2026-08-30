package com.crm.repository;

import com.crm.entity.CampoPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampoPersonalizadoRepository extends JpaRepository<CampoPersonalizado, Long> {
    List<CampoPersonalizado> findByTenantIdAndEntidadOrderByOrden(Long tenantId, String entidad);
}
