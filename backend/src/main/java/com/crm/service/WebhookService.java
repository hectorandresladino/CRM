/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Webhook;
import com.crm.repository.WebhookRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WebhookService {
    private final WebhookRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Webhook> findAll(Long tenantId) {
        return repository.findByTenantIdAndEsActivo(tid(), true);
    }

    public Webhook save(Webhook webhook) {
        webhook.setTenantId(tid());
        return repository.save(webhook);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Webhook no encontrado")));
    }

    public void triggerEvent(Long tenantId, String event, Object payload) {
        List<Webhook> hooks = repository.findByTenantIdAndEvento(tenantId, event);
        for (Webhook hook : hooks) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (hook.getSecretToken() != null) {
                    headers.set("X-Webhook-Secret", hook.getSecretToken());
                }
                HttpEntity<Object> entity = new HttpEntity<>(payload, headers);
                ResponseEntity<String> response = restTemplate.exchange(hook.getUrl(), HttpMethod.POST, entity, String.class);
                hook.setUltimoEnvio(LocalDateTime.now());
                hook.setUltimoEstado(response.getStatusCodeValue());
                hook.setTotalEnvios(hook.getTotalEnvios() + 1);
                if (response.getStatusCode().is2xxSuccessful()) {
                    hook.setTotalExitos(hook.getTotalExitos() + 1);
                } else {
                    hook.setTotalFallos(hook.getTotalFallos() + 1);
                }
                repository.save(hook);
            } catch (Exception e) {
                hook.setUltimoEnvio(LocalDateTime.now());
                hook.setTotalFallos(hook.getTotalFallos() + 1);
                repository.save(hook);
            }
        }
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
