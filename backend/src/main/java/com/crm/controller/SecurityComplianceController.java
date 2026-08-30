/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.SecurityComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security-compliance")
@RequiredArgsConstructor
public class SecurityComplianceController {

    private final SecurityComplianceService service;

    // === SSO (Item 75) ===
    @GetMapping("/sso")
    public ResponseEntity<List<SsoConfig>> getSsoConfigs() { return ResponseEntity.ok(service.getSsoConfigs()); }

    @PostMapping("/sso")
    public ResponseEntity<SsoConfig> createSsoConfig(@RequestBody SsoConfig config) { return ResponseEntity.ok(service.createSsoConfig(config)); }

    @PostMapping("/sso/{id}/enable")
    public ResponseEntity<SsoConfig> enableSso(@PathVariable Long id) { return ResponseEntity.ok(service.enableSso(id)); }

    @PostMapping("/sso/{id}/disable")
    public ResponseEntity<SsoConfig> disableSso(@PathVariable Long id) { return ResponseEntity.ok(service.disableSso(id)); }

    @PostMapping("/sso/{id}/test")
    public ResponseEntity<SsoConfig> testSso(@PathVariable Long id) { return ResponseEntity.ok(service.testSsoConnection(id)); }

    // === Password Policy (Item 76) ===
    @GetMapping("/password-policy")
    public ResponseEntity<PasswordPolicy> getPasswordPolicy() { return ResponseEntity.ok(service.getPasswordPolicy()); }

    @PutMapping("/password-policy")
    public ResponseEntity<PasswordPolicy> updatePasswordPolicy(@RequestBody PasswordPolicy policy) { return ResponseEntity.ok(service.updatePasswordPolicy(policy)); }

    @PostMapping("/password-policy/validate")
    public ResponseEntity<Map<String, Object>> validatePassword(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.validatePassword(body.get("password")));
    }

    // === Data Retention (Item 77) ===
    @GetMapping("/retention-policies")
    public ResponseEntity<List<DataRetentionPolicy>> getRetentionPolicies() { return ResponseEntity.ok(service.getRetentionPolicies()); }

    @PostMapping("/retention-policies")
    public ResponseEntity<DataRetentionPolicy> createRetentionPolicy(@RequestBody DataRetentionPolicy policy) { return ResponseEntity.ok(service.createRetentionPolicy(policy)); }

    @PostMapping("/retention-policies/{id}/execute")
    public ResponseEntity<DataRetentionPolicy> executeRetention(@PathVariable Long id) { return ResponseEntity.ok(service.executeRetentionPolicy(id)); }

    // === GDPR Requests (Item 78) ===
    @GetMapping("/gdpr/requests")
    public ResponseEntity<List<GdprRequest>> getGdprRequests() { return ResponseEntity.ok(service.getGdprRequests()); }

    @PostMapping("/gdpr/requests")
    public ResponseEntity<GdprRequest> createGdprRequest(@RequestBody GdprRequest request) { return ResponseEntity.ok(service.createGdprRequest(request)); }

    @PutMapping("/gdpr/requests/{id}/status")
    public ResponseEntity<GdprRequest> updateGdprStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        GdprRequest.RequestStatus status = GdprRequest.RequestStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(service.updateGdprRequestStatus(id, status, body.get("responseData")));
    }

    @GetMapping("/gdpr/stats")
    public ResponseEntity<Map<String, Object>> getGdprStats() { return ResponseEntity.ok(service.getGdprStats()); }

    @GetMapping("/gdpr/consents")
    public ResponseEntity<List<GdprConsent>> getGdprConsents() { return ResponseEntity.ok(service.getGdprConsents()); }

    @PostMapping("/gdpr/consents")
    public ResponseEntity<GdprConsent> grantConsent(@RequestBody GdprConsent consent) { return ResponseEntity.ok(service.grantConsent(consent)); }

    @PostMapping("/gdpr/consents/{id}/withdraw")
    public ResponseEntity<GdprConsent> withdrawConsent(@PathVariable Long id) { return ResponseEntity.ok(service.withdrawConsent(id)); }

    // === Compliance Audits (Item 79) ===
    @GetMapping("/audits")
    public ResponseEntity<List<ComplianceAudit>> getAudits() { return ResponseEntity.ok(service.getComplianceAudits()); }

    @PostMapping("/audits")
    public ResponseEntity<ComplianceAudit> createAudit(@RequestBody ComplianceAudit audit) { return ResponseEntity.ok(service.createComplianceAudit(audit)); }

    @PutMapping("/audits/{id}/complete")
    public ResponseEntity<ComplianceAudit> completeAudit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ComplianceAudit.AuditResult result = ComplianceAudit.AuditResult.valueOf((String) body.get("result"));
        Double score = body.get("score") != null ? ((Number) body.get("score")).doubleValue() : null;
        return ResponseEntity.ok(service.completeAudit(id, result, (String) body.get("findings"), (String) body.get("recommendations"), score));
    }

    @GetMapping("/audits/summary")
    public ResponseEntity<Map<String, Object>> getComplianceSummary() { return ResponseEntity.ok(service.getComplianceSummary()); }

    // === IP Whitelist (Item 80) ===
    @GetMapping("/ip-whitelist")
    public ResponseEntity<List<IpWhitelist>> getIpWhitelist() { return ResponseEntity.ok(service.getIpWhitelist()); }

    @PostMapping("/ip-whitelist")
    public ResponseEntity<IpWhitelist> addIp(@RequestBody IpWhitelist entry) { return ResponseEntity.ok(service.addIpToWhitelist(entry)); }

    @PostMapping("/ip-whitelist/{id}/toggle")
    public ResponseEntity<IpWhitelist> toggleIp(@PathVariable Long id) { return ResponseEntity.ok(service.toggleIpWhitelist(id)); }

    @GetMapping("/ip-whitelist/check")
    public ResponseEntity<Map<String, Boolean>> checkIp(@RequestParam String ip) {
        return ResponseEntity.ok(Map.of("allowed", service.isIpAllowed(ip)));
    }

    // === Security Scans (Item 81) ===
    @GetMapping("/scans")
    public ResponseEntity<List<SecurityScan>> getScans() { return ResponseEntity.ok(service.getSecurityScans()); }

    @PostMapping("/scans")
    public ResponseEntity<SecurityScan> createScan(@RequestBody SecurityScan scan) { return ResponseEntity.ok(service.createSecurityScan(scan)); }

    @GetMapping("/scans/summary")
    public ResponseEntity<Map<String, Object>> getScanSummary() { return ResponseEntity.ok(service.getSecurityScanSummary()); }

    // === Session Management (Item 82) ===
    @GetMapping("/sessions/active")
    public ResponseEntity<List<SessionRecord>> getActiveSessions() { return ResponseEntity.ok(service.getActiveSessions()); }

    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<SessionRecord>> getUserSessions(@PathVariable Long userId) { return ResponseEntity.ok(service.getUserSessions(userId)); }

    @PostMapping("/sessions")
    public ResponseEntity<SessionRecord> recordSession(@RequestBody SessionRecord session) { return ResponseEntity.ok(service.recordSession(session)); }

    @PostMapping("/sessions/{id}/end")
    public ResponseEntity<SessionRecord> endSession(@PathVariable Long id) { return ResponseEntity.ok(service.endSession(id)); }

    @PostMapping("/sessions/user/{userId}/end-all")
    public ResponseEntity<Void> endAllUserSessions(@PathVariable Long userId) { service.endAllUserSessions(userId); return ResponseEntity.ok().build(); }

    @PostMapping("/sessions/{id}/activity")
    public ResponseEntity<SessionRecord> updateActivity(@PathVariable Long id) { return ResponseEntity.ok(service.updateLastActivity(id)); }

    // === Security Dashboard (Item 83) ===
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() { return ResponseEntity.ok(service.getSecurityDashboard()); }

    // === Security Audit Trail (Item 84) ===
    @GetMapping("/audit-trail")
    public ResponseEntity<Map<String, Object>> getAuditTrail() { return ResponseEntity.ok(service.getSecurityAuditTrail()); }
}
