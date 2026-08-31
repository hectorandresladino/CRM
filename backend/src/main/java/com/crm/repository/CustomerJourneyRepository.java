package com.crm.repository;

import com.crm.entity.CustomerJourney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerJourneyRepository extends JpaRepository<CustomerJourney, Long> {
    List<CustomerJourney> findByTenantId(Long tenantId);
    List<CustomerJourney> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
    Optional<CustomerJourney> findByTenantIdAndId(Long tenantId, Long id);
}
