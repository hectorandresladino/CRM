package com.crm.repository;

import com.crm.entity.IpWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IpWhitelistRepository extends JpaRepository<IpWhitelist, Long> {
    List<IpWhitelist> findByTenantId(Long tenantId);
    List<IpWhitelist> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
