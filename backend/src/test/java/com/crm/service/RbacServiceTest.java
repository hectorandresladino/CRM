/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Usuario;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RBAC Service - Role-based access control")
class RbacServiceTest {

    private RbacService rbacService;

    @BeforeEach
    void setUp() {
        rbacService = new RbacService(null);
    }

    @Test
    @DisplayName("SUPER_ADMIN should have access to all modules")
    void superAdminShouldAccessAllModules() {
        for (RbacService.Module module : RbacService.Module.values()) {
            assertTrue(rbacService.hasAccess(Usuario.Role.SUPER_ADMIN, module),
                "SUPER_ADMIN should access " + module);
        }
    }

    @Test
    @DisplayName("TENANT_OWNER should have access to all modules")
    void tenantOwnerShouldAccessAllModules() {
        for (RbacService.Module module : RbacService.Module.values()) {
            assertTrue(rbacService.hasAccess(Usuario.Role.TENANT_OWNER, module),
                "TENANT_OWNER should access " + module);
        }
    }

    @Test
    @DisplayName("SALES should access CLIENTES but not BILLING")
    void salesShouldAccessClientesButNotBilling() {
        assertTrue(rbacService.hasAccess(Usuario.Role.SALES, RbacService.Module.CLIENTES));
        assertFalse(rbacService.hasAccess(Usuario.Role.SALES, RbacService.Module.BILLING));
    }

    @Test
    @DisplayName("SALES should not be able to DELETE")
    void salesShouldNotDelete() {
        assertFalse(rbacService.canOperate(Usuario.Role.SALES, RbacService.Operation.DELETE));
    }

    @Test
    @DisplayName("SUPPORT should access SOPORTE but not VENTAS")
    void supportShouldAccessSoporteButNotVentas() {
        assertTrue(rbacService.hasAccess(Usuario.Role.SUPPORT, RbacService.Module.SOPORTE));
        assertFalse(rbacService.hasAccess(Usuario.Role.SUPPORT, RbacService.Module.VENTAS));
    }

    @Test
    @DisplayName("ACCOUNTING should access BILLING and COMMISSIONS")
    void accountingShouldAccessBilling() {
        assertTrue(rbacService.hasAccess(Usuario.Role.ACCOUNTING, RbacService.Module.BILLING));
        assertTrue(rbacService.hasAccess(Usuario.Role.ACCOUNTING, RbacService.Module.COMMISSIONS));
    }

    @Test
    @DisplayName("ACCOUNTING should be able to APPROVE")
    void accountingShouldApprove() {
        assertTrue(rbacService.canOperate(Usuario.Role.ACCOUNTING, RbacService.Operation.APPROVE));
    }

    @Test
    @DisplayName("MANAGER should access FLOW_ENGINE but SALES should not")
    void managerShouldAccessFlowEngineButSalesNot() {
        assertTrue(rbacService.hasAccess(Usuario.Role.MANAGER, RbacService.Module.FLOW_ENGINE));
        assertFalse(rbacService.hasAccess(Usuario.Role.SALES, RbacService.Module.FLOW_ENGINE));
    }

    @Test
    @DisplayName("MARKETING should not access SECURITY")
    void marketingShouldNotAccessSecurity() {
        assertFalse(rbacService.hasAccess(Usuario.Role.MARKETING, RbacService.Module.SECURITY));
    }

    @Test
    @DisplayName("hasPermission should combine module and operation checks")
    void hasPermissionShouldCombineBoth() {
        assertTrue(rbacService.hasPermission(Usuario.Role.ADMIN, RbacService.Module.CLIENTES, RbacService.Operation.CREATE));
        assertFalse(rbacService.hasPermission(Usuario.Role.SALES, RbacService.Module.CLIENTES, RbacService.Operation.DELETE));
    }

    @Test
    @DisplayName("All 8 roles should have defined permissions")
    void allRolesShouldHavePermissions() {
        for (Usuario.Role role : Usuario.Role.values()) {
            assertNotNull(rbacService.getRolePermissions(role));
        }
    }
}
