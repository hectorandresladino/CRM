package com.crm.controller;

import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthController {

    private final TenantRepository tenantRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("service", "CRM SaaS Backend");
        status.put("version", "5.0.0");
        status.put("timestamp", java.time.LocalDateTime.now().toString());

        Map<String, String> dependencies = new LinkedHashMap<>();
        try {
            tenantRepository.count();
            dependencies.put("database", "UP");
        } catch (Exception e) {
            dependencies.put("database", "DOWN: " + e.getMessage());
            status.put("status", "DEGRADED");
        }
        dependencies.put("jwt", "UP");
        dependencies.put("flyway", "UP");
        status.put("dependencies", dependencies);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("tenants", tenantRepository.count());
        stats.put("users", usuarioRepository.count());
        stats.put("plans", planRepository.count());
        status.put("stats", stats);

        return status;
    }

    @GetMapping("/health/ping")
    public String ping() {
        return "pong";
    }
}
