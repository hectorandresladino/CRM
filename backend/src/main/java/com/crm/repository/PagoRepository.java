package com.crm.repository;

import com.crm.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<Pago> findByTenantIdAndEstado(Long tenantId, String estado);
    List<Pago> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
}
