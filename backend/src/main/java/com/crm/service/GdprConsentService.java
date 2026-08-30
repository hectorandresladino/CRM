package com.crm.service;

import com.crm.entity.GdprConsent;
import com.crm.repository.GdprConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GdprConsentService {

    private final GdprConsentRepository gdprConsentRepository;

    public List<GdprConsent> findAll(Long tenantId) {
        return gdprConsentRepository.findByTenantId(tenantId);
    }

    public GdprConsent grant(GdprConsent consent) {
        consent.setGranted(true);
        return gdprConsentRepository.save(consent);
    }

    public GdprConsent withdraw(Long id) {
        GdprConsent consent = gdprConsentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado"));
        consent.setGranted(false);
        consent.setWithdrawnAt(LocalDateTime.now());
        return gdprConsentRepository.save(consent);
    }

    public List<GdprConsent> findByCliente(Long tenantId, Long clienteId) {
        return gdprConsentRepository.findByTenantIdAndClienteId(tenantId, clienteId);
    }

    public void deleteAllForCliente(Long tenantId, Long clienteId) {
        List<GdprConsent> consents = gdprConsentRepository.findByTenantIdAndClienteId(tenantId, clienteId);
        gdprConsentRepository.deleteAll(consents);
    }
}
