/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.integration;

import com.crm.security.TenantContext;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Multi-Tenant Isolation Tests")
class TenantIsolationTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Tenant A context should not leak to Tenant B")
    void tenantAContextShouldNotLeakToTenantB() {
        TenantContext.setCurrentTenant(100L);
        assertEquals(100L, TenantContext.getCurrentTenant());

        TenantContext.setCurrentTenant(200L);
        assertEquals(200L, TenantContext.getCurrentTenant());
        assertNotEquals(100L, TenantContext.getCurrentTenant());
    }

    @Test
    @DisplayName("Clearing tenant context should prevent data access")
    void clearingContextShouldPreventAccess() {
        TenantContext.setCurrentTenant(50L);
        assertTrue(TenantContext.hasTenant());

        TenantContext.clear();
        assertFalse(TenantContext.hasTenant());
        assertNull(TenantContext.getCurrentTenant());
    }

    @Test
    @DisplayName("Tenant IDs should be isolated per thread")
    void tenantIdsShouldBeIsolatedPerThread() throws InterruptedException {
        TenantContext.setCurrentTenant(1L);

        Thread t1 = new Thread(() -> {
            TenantContext.setCurrentTenant(2L);
            assertEquals(2L, TenantContext.getCurrentTenant());
            TenantContext.clear();
        });

        Thread t2 = new Thread(() -> {
            TenantContext.setCurrentTenant(3L);
            assertEquals(3L, TenantContext.getCurrentTenant());
            TenantContext.clear();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(1L, TenantContext.getCurrentTenant());
    }

    @Test
    @DisplayName("Concurrent tenant contexts should not interfere")
    void concurrentTenantsShouldNotInterfere() throws InterruptedException {
        Thread[] threads = new Thread[10];
        boolean[] results = new boolean[10];

        for (int i = 0; i < 10; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                long tenantId = 1000L + idx;
                TenantContext.setCurrentTenant(tenantId);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                results[idx] = (TenantContext.getCurrentTenant() == tenantId);
                TenantContext.clear();
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        for (int i = 0; i < 10; i++) {
            assertTrue(results[i], "Thread " + i + " should have isolated tenant context");
        }
    }
}
