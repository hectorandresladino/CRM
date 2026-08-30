package com.crm.repository;

import com.crm.entity.MarketingAttribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MarketingAttributionRepository extends JpaRepository<MarketingAttribution, Long> {
    List<MarketingAttribution> findByTenantId(Long tenantId);
    List<MarketingAttribution> findByTenantIdAndCampaignId(Long tenantId, Long campaignId);
    List<MarketingAttribution> findByTenantIdAndClientId(Long tenantId, Long clientId);
}
