/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProviderFactory {

    private final List<PaymentProvider> providers;
    private final Map<String, PaymentProvider> providerMap = new HashMap<>();

    public PaymentProvider getProvider(String name) {
        if (providerMap.isEmpty()) {
            for (PaymentProvider p : providers) {
                providerMap.put(p.getProviderName(), p);
            }
        }
        PaymentProvider provider = providerMap.get(name.toUpperCase());
        if (provider == null) {
            throw new RuntimeException("Payment provider not found: " + name);
        }
        return provider;
    }

    public Map<String, Boolean> getAvailableProviders() {
        if (providerMap.isEmpty()) {
            for (PaymentProvider p : providers) {
                providerMap.put(p.getProviderName(), p);
            }
        }
        Map<String, Boolean> result = new HashMap<>();
        providerMap.forEach((name, provider) -> result.put(name, provider.isConfigured()));
        return result;
    }
}
