/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TenantStatementInspector - SQL-level tenant filtering")
class TenantStatementInspectorTest {

    private final TenantStatementInspector inspector = new TenantStatementInspector();

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should not modify SQL when no tenant context")
    void shouldNotModifyWhenNoTenant() {
        TenantContext.clear();
        String sql = "SELECT * FROM clientes";
        assertEquals(sql, inspector.inspect(sql));
    }

    @Test
    @DisplayName("Should add WHERE tenant_id to SELECT without WHERE")
    void shouldAddWhereToSelect() {
        TenantContext.setCurrentTenant(10L);
        String sql = "SELECT * FROM clientes";
        String result = inspector.inspect(sql);
        assertTrue(result.contains("tenant_id = 10"));
    }

    @Test
    @DisplayName("Should add AND tenant_id to SELECT with WHERE")
    void shouldAddAndToSelectWithWhere() {
        TenantContext.setCurrentTenant(5L);
        String sql = "SELECT * FROM clientes WHERE nombre = 'Test'";
        String result = inspector.inspect(sql);
        assertTrue(result.contains("tenant_id = 5"));
        assertTrue(result.contains("nombre = 'Test'"));
    }

    @Test
    @DisplayName("Should add tenant filter to UPDATE")
    void shouldAddTenantFilterToUpdate() {
        TenantContext.setCurrentTenant(3L);
        String sql = "UPDATE clientes SET nombre = 'Updated' WHERE id = 1";
        String result = inspector.inspect(sql);
        assertTrue(result.contains("tenant_id = 3"));
    }

    @Test
    @DisplayName("Should add tenant filter to DELETE")
    void shouldAddTenantFilterToDelete() {
        TenantContext.setCurrentTenant(7L);
        String sql = "DELETE FROM clientes WHERE id = 1";
        String result = inspector.inspect(sql);
        assertTrue(result.contains("tenant_id = 7"));
    }

    @Test
    @DisplayName("Should not modify INSERT statements")
    void shouldNotModifyInsert() {
        TenantContext.setCurrentTenant(1L);
        String sql = "INSERT INTO audit_logs (action) VALUES ('test')";
        assertEquals(sql, inspector.inspect(sql));
    }

    @Test
    @DisplayName("Should not modify SQL that already has tenant_id")
    void shouldNotModifyIfTenantIdPresent() {
        TenantContext.setCurrentTenant(1L);
        String sql = "SELECT * FROM clientes WHERE tenant_id = 5";
        assertEquals(sql, inspector.inspect(sql));
    }

    @Test
    @DisplayName("Should handle null SQL gracefully")
    void shouldHandleNullSql() {
        assertNull(inspector.inspect(null));
    }

    @Test
    @DisplayName("Should handle empty SQL gracefully")
    void shouldHandleEmptySql() {
        assertEquals("", inspector.inspect(""));
    }

    @Test
    @DisplayName("Should add WHERE before GROUP BY")
    void shouldAddWhereBeforeGroupBy() {
        TenantContext.setCurrentTenant(2L);
        String sql = "SELECT COUNT(*) FROM clientes GROUP BY estado";
        String result = inspector.inspect(sql);
        assertTrue(result.contains("WHERE tenant_id = 2"));
        assertTrue(result.contains("GROUP BY"));
    }

    @Test
    @DisplayName("Should add WHERE before ORDER BY")
    void shouldAddWhereBeforeOrderBy() {
        TenantContext.setCurrentTenant(2L);
        String sql = "SELECT * FROM clientes ORDER BY nombre";
        String result = inspector.inspect(sql);
        assertTrue(result.contains("WHERE tenant_id = 2"));
        assertTrue(result.contains("ORDER BY"));
    }
}
