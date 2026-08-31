package com.crm.service;

import com.crm.entity.DataRetentionPolicy;
import com.crm.entity.SecurityScan;
import com.crm.entity.SessionRecord;
import com.crm.entity.SsoConfig;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityComplianceHardeningTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void ssoCannotBeEnabledAcrossTenants() {
        SsoConfigRepository sso = mock(SsoConfigRepository.class);
        SecurityComplianceService service = service(sso, mock(DataRetentionPolicyRepository.class),
                mock(SecurityScanRepository.class), mock(SessionRecordRepository.class));
        TenantContext.setCurrentTenant(61L);
        when(sso.findByTenantIdAndId(61L, 4L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.enableSso(4L));

        verify(sso, never()).findById(4L);
        verify(sso, never()).save(any());
    }

    @Test
    void ssoTestDoesNotClaimFakeSuccess() {
        SsoConfigRepository sso = mock(SsoConfigRepository.class);
        SecurityComplianceService service = service(sso, mock(DataRetentionPolicyRepository.class),
                mock(SecurityScanRepository.class), mock(SessionRecordRepository.class));
        TenantContext.setCurrentTenant(61L);
        SsoConfig config = new SsoConfig();
        when(sso.findByTenantIdAndId(61L, 4L)).thenReturn(Optional.of(config));

        assertThrows(UnsupportedOperationException.class, () -> service.testSsoConnection(4L));
        assertNull(config.getLastTestResult());
        verify(sso, never()).save(any());
    }

    @Test
    void retentionDoesNotSimulateDeletedRecords() {
        DataRetentionPolicyRepository retention = mock(DataRetentionPolicyRepository.class);
        SecurityComplianceService service = service(mock(SsoConfigRepository.class), retention,
                mock(SecurityScanRepository.class), mock(SessionRecordRepository.class));
        TenantContext.setCurrentTenant(61L);
        DataRetentionPolicy policy = new DataRetentionPolicy();
        when(retention.findByTenantIdAndId(61L, 5L)).thenReturn(Optional.of(policy));

        assertThrows(UnsupportedOperationException.class, () -> service.executeRetentionPolicy(5L));
        verify(retention, never()).save(any());
    }

    @Test
    void securityScanRemainsPendingUntilRealScannerRuns() {
        SecurityScanRepository scans = mock(SecurityScanRepository.class);
        SecurityComplianceService service = service(mock(SsoConfigRepository.class),
                mock(DataRetentionPolicyRepository.class), scans, mock(SessionRecordRepository.class));
        TenantContext.setCurrentTenant(61L);
        SecurityScan scan = new SecurityScan();
        scan.setReportUrl("untrusted.pdf");
        when(scans.save(scan)).thenReturn(scan);

        SecurityScan saved = service.createSecurityScan(scan);

        assertEquals(61L, saved.getTenantId());
        assertEquals(SecurityScan.ScanStatus.PENDING, saved.getStatus());
        assertNull(saved.getReportUrl());
        assertNull(saved.getCompletedAt());
        verify(scans, times(1)).save(scan);
    }

    @Test
    void sessionCannotBeEndedAcrossTenants() {
        SessionRecordRepository sessions = mock(SessionRecordRepository.class);
        SecurityComplianceService service = service(mock(SsoConfigRepository.class),
                mock(DataRetentionPolicyRepository.class), mock(SecurityScanRepository.class), sessions);
        TenantContext.setCurrentTenant(61L);
        when(sessions.findByTenantIdAndId(61L, 6L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.endSession(6L));

        verify(sessions, never()).findById(6L);
        verify(sessions, never()).save(any(SessionRecord.class));
    }

    private SecurityComplianceService service(
            SsoConfigRepository sso,
            DataRetentionPolicyRepository retention,
            SecurityScanRepository scans,
            SessionRecordRepository sessions) {
        return new SecurityComplianceService(
                sso,
                mock(PasswordPolicyRepository.class),
                retention,
                mock(GdprRequestRepository.class),
                mock(GdprConsentRepository.class),
                mock(ComplianceAuditRepository.class),
                mock(IpWhitelistRepository.class),
                scans,
                sessions,
                mock(AuditLogRepository.class)
        );
    }
}
