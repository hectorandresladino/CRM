/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    public static Long getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Returns the authenticated tenant or fails closed. Tenant-owned services
     * must use this method instead of accepting a tenant id from the request.
     */
    public static Long requireCurrentTenant() {
        Long tenantId = currentTenant.get();
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalStateException("No hay un tenant autenticado en el contexto actual");
        }
        return tenantId;
    }

    public static void setCurrentTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("El tenantId debe ser un identificador positivo");
        }
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }

    public static boolean hasTenant() {
        return currentTenant.get() != null;
    }
}
