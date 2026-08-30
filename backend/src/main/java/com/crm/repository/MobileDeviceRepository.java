package com.crm.repository;

import com.crm.entity.MobileDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MobileDeviceRepository extends JpaRepository<MobileDevice, Long> {
    List<MobileDevice> findByTenantId(Long tenantId);
    List<MobileDevice> findByTenantIdAndUserId(Long tenantId, Long userId);
    Optional<MobileDevice> findByTenantIdAndDeviceUuid(Long tenantId, String deviceUuid);
    List<MobileDevice> findByTenantIdAndIsRegistered(Long tenantId, Boolean isRegistered);
}
