/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TenantContext - ThreadLocal tenant isolation")
class TenantContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should set and get tenant ID")
    void shouldSetAndGetTenantId() {
        TenantContext.setCurrentTenant(100L);
        assertEquals(100L, TenantContext.getCurrentTenant());
    }

    @Test
    @DisplayName("Should return null when no tenant set")
    void shouldReturnNullWhenNotSet() {
        assertNull(TenantContext.getCurrentTenant());
    }

    @Test
    @DisplayName("hasTenant should return true when set")
    void hasTenantShouldReturnTrue() {
        TenantContext.setCurrentTenant(5L);
        assertTrue(TenantContext.hasTenant());
    }

    @Test
    @DisplayName("hasTenant should return false when not set")
    void hasTenantShouldReturnFalse() {
        assertFalse(TenantContext.hasTenant());
    }

    @Test
    @DisplayName("Clear should remove tenant from context")
    void clearShouldRemoveTenant() {
        TenantContext.setCurrentTenant(42L);
        TenantContext.clear();
        assertNull(TenantContext.getCurrentTenant());
        assertFalse(TenantContext.hasTenant());
    }

    @Test
    @DisplayName("Each thread should have its own tenant context")
    void eachThreadShouldHaveOwnContext() throws InterruptedException {
        TenantContext.setCurrentTenant(1L);

        Thread t = new Thread(() -> {
            assertNull(TenantContext.getCurrentTenant());
            TenantContext.setCurrentTenant(2L);
            assertEquals(2L, TenantContext.getCurrentTenant());
        });
        t.start();
        t.join();

        assertEquals(1L, TenantContext.getCurrentTenant());
    }
}
