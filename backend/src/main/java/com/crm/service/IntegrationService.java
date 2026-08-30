package com.crm.service;

import com.crm.entity.Integration;
import com.crm.repository.IntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IntegrationService {

    private final IntegrationRepository integrationRepository;

    public List<Integration> findAll(Long tenantId) {
        return integrationRepository.findByTenantId(tenantId);
    }

    public Integration connect(Integration integration) {
        integration.setConnected(true);
        return integrationRepository.save(integration);
    }

    public Integration disconnect(Long id) {
        Integration integration = integrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Integración no encontrada"));
        integration.setConnected(false);
        integration.setSyncEnabled(false);
        return integrationRepository.save(integration);
    }

    public Integration toggleSync(Long id) {
        Integration integration = integrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Integración no encontrada"));
        integration.setSyncEnabled(!integration.getSyncEnabled());
        return integrationRepository.save(integration);
    }

    public void delete(Long id) {
        integrationRepository.deleteById(id);
    }
}
