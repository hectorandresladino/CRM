/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Usuario;
import com.crm.repository.UsuarioRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RbacService {

    private final UsuarioRepository usuarioRepository;

    public enum Module {
        CLIENTES, VENTAS, LEADS, MARKETING, SOPORTE, FACTURACION,
        CONTRATOS, PRODUCTOS, REPORTES, DASHBOARD, WHATSAPP,
        INTEGRATIONS, USERS, SETTINGS, BILLING, SECURITY,
        REVENUE_AI, PORTAL_STUDIO, CUSTOMER_DATA_HUB, FLOW_ENGINE,
        ANALYTICS, FIELD_SERVICE, COMMISSIONS, FORECASTING
    }

    public enum Operation {
        CREATE, READ, UPDATE, DELETE, EXPORT, APPROVE, ASSIGN, SHARE
    }

    private static final Map<Usuario.Role, Set<Module>> ROLE_MODULE_ACCESS = new EnumMap<>(Usuario.Role.class);
    private static final Map<Usuario.Role, Set<Operation>> ROLE_OPERATIONS = new EnumMap<>(Usuario.Role.class);

    static {
        ROLE_MODULE_ACCESS.put(Usuario.Role.SUPER_ADMIN, EnumSet.allOf(Module.class));
        ROLE_MODULE_ACCESS.put(Usuario.Role.TENANT_OWNER, EnumSet.allOf(Module.class));
        ROLE_MODULE_ACCESS.put(Usuario.Role.ADMIN, EnumSet.of(
            Module.CLIENTES, Module.VENTAS, Module.LEADS, Module.MARKETING, Module.SOPORTE,
            Module.FACTURACION, Module.CONTRATOS, Module.PRODUCTOS, Module.REPORTES,
            Module.DASHBOARD, Module.WHATSAPP, Module.INTEGRATIONS, Module.USERS,
            Module.SETTINGS, Module.BILLING, Module.SECURITY, Module.REVENUE_AI,
            Module.PORTAL_STUDIO, Module.CUSTOMER_DATA_HUB, Module.FLOW_ENGINE,
            Module.ANALYTICS, Module.FIELD_SERVICE, Module.COMMISSIONS, Module.FORECASTING
        ));
        ROLE_MODULE_ACCESS.put(Usuario.Role.MANAGER, EnumSet.of(
            Module.CLIENTES, Module.VENTAS, Module.LEADS, Module.MARKETING, Module.SOPORTE,
            Module.FACTURACION, Module.CONTRATOS, Module.PRODUCTOS, Module.REPORTES,
            Module.DASHBOARD, Module.WHATSAPP, Module.REVENUE_AI, Module.ANALYTICS,
            Module.COMMISSIONS, Module.FORECASTING, Module.FIELD_SERVICE, Module.FLOW_ENGINE
        ));
        ROLE_MODULE_ACCESS.put(Usuario.Role.SALES, EnumSet.of(
            Module.CLIENTES, Module.VENTAS, Module.LEADS, Module.PRODUCTOS,
            Module.DASHBOARD, Module.WHATSAPP, Module.REPORTES, Module.FORECASTING
        ));
        ROLE_MODULE_ACCESS.put(Usuario.Role.MARKETING, EnumSet.of(
            Module.CLIENTES, Module.LEADS, Module.MARKETING, Module.PRODUCTOS,
            Module.DASHBOARD, Module.REPORTES, Module.ANALYTICS
        ));
        ROLE_MODULE_ACCESS.put(Usuario.Role.SUPPORT, EnumSet.of(
            Module.CLIENTES, Module.SOPORTE, Module.DASHBOARD, Module.WHATSAPP,
            Module.FIELD_SERVICE
        ));
        ROLE_MODULE_ACCESS.put(Usuario.Role.ACCOUNTING, EnumSet.of(
            Module.FACTURACION, Module.CONTRATOS, Module.REPORTES, Module.DASHBOARD,
            Module.BILLING, Module.COMMISSIONS
        ));

        ROLE_OPERATIONS.put(Usuario.Role.SUPER_ADMIN, EnumSet.allOf(Operation.class));
        ROLE_OPERATIONS.put(Usuario.Role.TENANT_OWNER, EnumSet.allOf(Operation.class));
        ROLE_OPERATIONS.put(Usuario.Role.ADMIN, EnumSet.allOf(Operation.class));
        ROLE_OPERATIONS.put(Usuario.Role.MANAGER, EnumSet.of(
            Operation.CREATE, Operation.READ, Operation.UPDATE, Operation.DELETE,
            Operation.EXPORT, Operation.ASSIGN, Operation.SHARE
        ));
        ROLE_OPERATIONS.put(Usuario.Role.SALES, EnumSet.of(
            Operation.CREATE, Operation.READ, Operation.UPDATE, Operation.EXPORT
        ));
        ROLE_OPERATIONS.put(Usuario.Role.MARKETING, EnumSet.of(
            Operation.CREATE, Operation.READ, Operation.UPDATE, Operation.EXPORT
        ));
        ROLE_OPERATIONS.put(Usuario.Role.SUPPORT, EnumSet.of(
            Operation.CREATE, Operation.READ, Operation.UPDATE, Operation.ASSIGN
        ));
        ROLE_OPERATIONS.put(Usuario.Role.ACCOUNTING, EnumSet.of(
            Operation.CREATE, Operation.READ, Operation.UPDATE, Operation.EXPORT, Operation.APPROVE
        ));
    }

    public boolean hasAccess(Usuario.Role role, Module module) {
        Set<Module> modules = ROLE_MODULE_ACCESS.get(role);
        return modules != null && modules.contains(module);
    }

    public boolean canOperate(Usuario.Role role, Operation operation) {
        Set<Operation> ops = ROLE_OPERATIONS.get(role);
        return ops != null && ops.contains(operation);
    }

    public boolean hasPermission(Usuario.Role role, Module module, Operation operation) {
        return hasAccess(role, module) && canOperate(role, operation);
    }

    public Map<String, Object> getRolePermissions(Usuario.Role role) {
        Map<String, Object> perms = new LinkedHashMap<>();
        perms.put("role", role.name());
        perms.put("modules", ROLE_MODULE_ACCESS.getOrDefault(role, Set.of()));
        perms.put("operations", ROLE_OPERATIONS.getOrDefault(role, Set.of()));
        return perms;
    }

    public Map<String, Object> getAllRolesPermissions() {
        Map<String, Object> all = new LinkedHashMap<>();
        for (Usuario.Role role : Usuario.Role.values()) {
            all.put(role.name(), getRolePermissions(role));
        }
        return all;
    }

    public Map<String, Object> getCurrentUserPermissions(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return getRolePermissions(user.getRol());
    }

    public List<Map<String, Object>> getTenantUsers() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        List<Usuario> users = usuarioRepository.findByTenantId(tid);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Usuario u : users) {
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("id", u.getId());
            userMap.put("username", u.getUsername());
            userMap.put("email", u.getEmail());
            userMap.put("nombre", u.getNombre());
            userMap.put("apellido", u.getApellido());
            userMap.put("rol", u.getRol().name());
            userMap.put("activo", u.getActivo());
            userMap.put("mfaEnabled", u.getMfaEnabled());
            userMap.put("permissions", getRolePermissions(u.getRol()));
            result.add(userMap);
        }
        return result;
    }
}
