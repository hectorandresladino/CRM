package com.crm.repository;

import com.crm.entity.GdprConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GdprConsentRepository extends JpaRepository<GdprConsent, Long> {
    List<GdprConsent> findByTenantId(Long tenantId);
    List<GdprConsent> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    List<GdprConsent> findByTenantIdAndProspectoId(Long tenantId, Long prospectoId);
}
