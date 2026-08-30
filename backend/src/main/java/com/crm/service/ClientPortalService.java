package com.crm.service;

import com.crm.entity.ClientPortalAccess;
import com.crm.repository.ClientPortalAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientPortalService {

    private final ClientPortalAccessRepository clientPortalAccessRepository;

    public List<ClientPortalAccess> findAll(Long tenantId) {
        return clientPortalAccessRepository.findByTenantId(tenantId);
    }

    public ClientPortalAccess createAccess(Long tenantId, Long clienteId, String email) {
        ClientPortalAccess existing = clientPortalAccessRepository
                .findByTenantIdAndClienteId(tenantId, clienteId).orElse(null);
        if (existing != null) {
            return existing;
        }
        ClientPortalAccess access = new ClientPortalAccess();
        access.setTenantId(tenantId);
        access.setClienteId(clienteId);
        access.setEmail(email);
        access.setPortalToken(UUID.randomUUID().toString());
        access.setActive(true);
        return clientPortalAccessRepository.save(access);
    }

    public ClientPortalAccess login(String portalToken) {
        ClientPortalAccess access = clientPortalAccessRepository.findByPortalToken(portalToken)
                .orElseThrow(() -> new RuntimeException("Token de portal inválido"));
        if (!access.getActive()) {
            throw new RuntimeException("Acceso de portal desactivado");
        }
        access.setLastLoginAt(LocalDateTime.now());
        access.setLoginCount(access.getLoginCount() + 1);
        return clientPortalAccessRepository.save(access);
    }

    public void revoke(Long id) {
        ClientPortalAccess access = clientPortalAccessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acceso no encontrado"));
        access.setActive(false);
        clientPortalAccessRepository.save(access);
    }
}
