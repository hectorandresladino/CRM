package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnterpriseSecurityService {

    private final PermissionSetRepository permissionSetRepo;
    private final SharingRuleRepository sharingRuleRepo;
    private final UsuarioRepository usuarioRepo;

    public List<PermissionSet> getPermissionSets() {
        return permissionSetRepo.findByTenantId(TenantContext.getCurrentTenant());
    }

    public PermissionSet createPermissionSet(PermissionSet ps) {
        ps.setTenantId(TenantContext.getCurrentTenant());
        return permissionSetRepo.save(ps);
    }

    public List<SharingRule> getSharingRules(String objectName) {
        Long tid = TenantContext.getCurrentTenant();
        if (objectName != null) return sharingRuleRepo.findByTenantIdAndObjectName(tid, objectName);
        return sharingRuleRepo.findByTenantId(tid);
    }

    public SharingRule createSharingRule(SharingRule rule) {
        rule.setTenantId(TenantContext.getCurrentTenant());
        return sharingRuleRepo.save(rule);
    }

    public Map<String, Object> getUserPermissions(Long userId) {
        Usuario user = usuarioRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Map<String, Object> perms = new HashMap<>();
        perms.put("userId", userId);
        perms.put("role", user.getRol());
        perms.put("permissionSets", Collections.emptyList());
        perms.put("objectPermissions", getObjectPermissionsForRole(user.getRol().name()));
        perms.put("fieldLevelSecurity", getFieldLevelSecurity(user.getRol().name()));
        return perms;
    }

    private Map<String, String> getObjectPermissionsForRole(String role) {
        Map<String, String> perms = new HashMap<>();
        switch (role) {
            case "SUPER_ADMIN":
                perms.put("ALL", "FULL");
                break;
            case "TENANT_OWNER":
                perms.put("ALL", "FULL");
                break;
            case "ADMIN":
                perms.put("ALL", "FULL");
                break;
            case "MANAGER":
                perms.put("CLIENTES", "EDIT");
                perms.put("VENTAS", "EDIT");
                perms.put("PROSPECTOS", "EDIT");
                perms.put("REPORTES", "READ");
                perms.put("USUARIOS", "READ");
                break;
            case "SALES":
                perms.put("CLIENTES", "EDIT");
                perms.put("VENTAS", "EDIT");
                perms.put("PROSPECTOS", "EDIT");
                perms.put("COTIZACIONES", "EDIT");
                break;
            case "MARKETING":
                perms.put("CLIENTES", "READ");
                perms.put("PROSPECTOS", "EDIT");
                perms.put("CAMPANAS", "FULL");
                break;
            case "SUPPORT":
                perms.put("CLIENTES", "READ");
                perms.put("SERVICIO_CLIENTE", "FULL");
                perms.put("PQRS", "FULL");
                break;
            case "ACCOUNTING":
                perms.put("FACTURAS", "FULL");
                perms.put("PAGOS", "FULL");
                perms.put("CLIENTES", "READ");
                break;
            default:
                perms.put("DASHBOARD", "READ");
        }
        return perms;
    }

    private Map<String, String> getFieldLevelSecurity(String role) {
        Map<String, String> fls = new HashMap<>();
        if ("ACCOUNTING".equals(role) || "ADMIN".equals(role) || "TENANT_OWNER".equals(role)) {
            fls.put("Cliente.ingresos", "VISIBLE");
            fls.put("Cliente.credito", "VISIBLE");
            fls.put("Venta.comision", "VISIBLE");
        } else {
            fls.put("Cliente.ingresos", "HIDDEN");
            fls.put("Cliente.credito", "HIDDEN");
            fls.put("Venta.comision", "HIDDEN");
        }
        return fls;
    }

    public Map<String, Object> getSecurityAudit() {
        Map<String, Object> audit = new HashMap<>();
        audit.put("mfaEnabled", false);
        audit.put("passwordPolicy", Map.of(
                "minLength", 8,
                "requireUppercase", true,
                "requireNumbers", true,
                "requireSymbols", true,
                "expiryDays", 90
        ));
        audit.put("sessionTimeout", 900);
        audit.put("ipWhitelist", Collections.emptyList());
        audit.put("dataMaskingRules", Map.of(
                "Cliente.identificacion", "PARTIAL",
                "Cliente.telefono", "PARTIAL",
                "Usuario.email", "FULL"
        ));
        audit.put("encryptionAtRest", true);
        audit.put("encryptionInTransit", true);
        return audit;
    }
}
