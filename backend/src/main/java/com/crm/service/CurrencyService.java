/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.CurrencyRate;
import com.crm.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CurrencyService {

    private final CurrencyRateRepository currencyRateRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String ECB_URL = "https://api.frankfurter.app/latest?from=USD";

    public List<CurrencyRate> findAll(Long tenantId) {
        return currencyRateRepository.findByTenantId(tenantId);
    }

    public CurrencyRate save(CurrencyRate rate) {
        rate.setFetchedAt(LocalDateTime.now());
        return currencyRateRepository.save(rate);
    }

    public void fetchLatestRates(Long tenantId) {
        try {
            Map<String, Object> response = restTemplate.getForObject(ECB_URL, Map.class);
            if (response == null) return;

            @SuppressWarnings("unchecked")
            Map<String, Number> rates = (Map<String, Number>) response.get("rates");

            if (rates != null) {
                for (Map.Entry<String, Number> entry : rates.entrySet()) {
                    CurrencyRate rate = currencyRateRepository
                            .findByTenantIdAndBaseAndTarget(tenantId, "USD", entry.getKey())
                            .orElse(new CurrencyRate());
                    rate.setTenantId(tenantId);
                    rate.setBase("USD");
                    rate.setTarget(entry.getKey());
                    rate.setRate(BigDecimal.valueOf(entry.getValue().doubleValue()));
                    rate.setFetchedAt(LocalDateTime.now());
                    rate.setSource("Frankfurter/ECB");
                    currencyRateRepository.save(rate);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching exchange rates: " + e.getMessage());
        }
    }

    public BigDecimal convert(Long tenantId, String from, String to, BigDecimal amount) {
        if (from.equals(to)) return amount;
        CurrencyRate rate = currencyRateRepository
                .findByTenantIdAndBaseAndTarget(tenantId, from, to)
                .orElseThrow(() -> new RuntimeException("No exchange rate found for " + from + " to " + to));
        return amount.multiply(rate.getRate());
    }
}
