package com.crm.repository;

import com.crm.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByTenantIdOrderByFechaProgramadaDesc(Long tenantId);
    List<Actividad> findByTenantIdAndAsignadoA(Long tenantId, String asignadoA);
    List<Actividad> findByTenantIdAndEstado(Long tenantId, String estado);
    List<Actividad> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    List<Actividad> findByTenantIdAndProspectoId(Long tenantId, Long prospectoId);
}
