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

    public static void setCurrentTenant(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }

    public static boolean hasTenant() {
        return currentTenant.get() != null;
    }
}
