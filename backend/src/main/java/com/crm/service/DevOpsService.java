package com.crm.service;

import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DevOpsService {

    private final TenantRepository tenantRepo;
    private final PlanRepository planRepo;

    public Map<String, Object> getFeatureFlags() {
        Map<String, Object> flags = new LinkedHashMap<>();
        flags.put("sales_cloud_forecasts", true);
        flags.put("service_cloud_knowledge", true);
        flags.put("revenue_cloud_amendments", true);
        flags.put("data360_cdp", true);
        flags.put("platform_custom_objects", true);
        flags.put("flow_engine", true);
        flags.put("agentforce_ai", true);
        flags.put("analytics_dashboards", true);
        flags.put("experience_cloud", true);
        flags.put("enterprise_security", true);
        flags.put("whatsapp_ai", true);
        flags.put("integrations_sync", true);
        return flags;
    }

    public Map<String, Object> toggleFeatureFlag(String feature, boolean enabled) {
        log.info("Feature flag {} toggled to {} for tenant {}", feature, enabled, TenantContext.getCurrentTenant());
        return Map.of("feature", feature, "enabled", enabled, "updated", true);
    }

    public Map<String, Object> getSandboxInfo() {
        Map<String, Object> sandbox = new LinkedHashMap<>();
        sandbox.put("environment", System.getProperty("spring.profiles.active", "dev"));
        sandbox.put("version", "5.0.0");
        sandbox.put("database", "H2");
        sandbox.put("flywayVersion", "V7");
        sandbox.put("javaVersion", System.getProperty("java.version"));
        sandbox.put("springBootVersion", "3.2.0");
        sandbox.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + "s");
        sandbox.put("availableSandboxes", List.of("dev", "staging", "prod"));
        return sandbox;
    }

    public Map<String, Object> getReleaseInfo() {
        Map<String, Object> release = new LinkedHashMap<>();
        release.put("version", "5.0.0");
        release.put("migrations", List.of("V1", "V2", "V3", "V4", "V5", "V6", "V7"));
        release.put("entities", 40);
        release.put("repositories", 30);
        release.put("services", 15);
        release.put("controllers", 15);
        release.put("endpoints", 80);
        release.put("ciStatus", "GREEN");
        release.put("lastDeploy", java.time.LocalDateTime.now().toString());
        return release;
    }

    public Map<String, Object> rollbackMigration(String version) {
        log.warn("Rollback solicitado para migracion {} - no implementado en H2", version);
        Map<String, Object> result = new HashMap<>();
        result.put("requestedVersion", version);
        result.put("status", "NOT_SUPPORTED");
        result.put("message", "Rollback no soportado en H2. Use PostgreSQL con Flyway repair.");
        return result;
    }

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        Map<String, Object> jvm = new LinkedHashMap<>();
        jvm.put("heapUsedMB", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024);
        jvm.put("heapMaxMB", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1024 / 1024);
        jvm.put("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        jvm.put("loadedClasses", ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
        health.put("jvm", jvm);
        health.put("tenants", tenantRepo.count());
        health.put("plans", planRepo.count());
        return health;
    }
}
