package com.crm.repository;

import com.crm.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    List<SystemSetting> findByTenantId(Long tenantId);
    Optional<SystemSetting> findByTenantIdAndKey(Long tenantId, String key);
    List<SystemSetting> findByTenantIdAndCategory(Long tenantId, String category);
}
