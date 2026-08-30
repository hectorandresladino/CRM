package com.crm.service;

import com.crm.entity.TenantConfiguracion;
import com.crm.repository.TenantConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantConfiguracionService {
    private final TenantConfiguracionRepository repository;

    public TenantConfiguracion findByTenantId(Long tenantId) {
        return repository.findByTenantId(tenantId).orElseGet(() -> {
            TenantConfiguracion config = new TenantConfiguracion();
            config.setTenantId(tenantId);
            return repository.save(config);
        });
    }

    public TenantConfiguracion save(TenantConfiguracion config) {
        return repository.save(config);
    }

    public String getNextFacturaNumber(Long tenantId) {
        TenantConfiguracion config = findByTenantId(tenantId);
        int next = config.getConsecutivoFactura();
        config.setConsecutivoFactura(next + 1);
        repository.save(config);
        return (config.getPrefijoFacturacion() != null ? config.getPrefijoFacturacion() : "FAC-") + String.format("%05d", next);
    }

    public String getNextCotizacionNumber(Long tenantId) {
        TenantConfiguracion config = findByTenantId(tenantId);
        int next = config.getConsecutivoCotizacion();
        config.setConsecutivoCotizacion(next + 1);
        repository.save(config);
        return (config.getPrefijoCotizacion() != null ? config.getPrefijoCotizacion() : "COT-") + String.format("%05d", next);
    }
}
