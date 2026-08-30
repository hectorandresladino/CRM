package com.crm.repository;

import com.crm.entity.JourneyStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JourneyStepRepository extends JpaRepository<JourneyStep, Long> {
    List<JourneyStep> findByTenantIdAndJourneyId(Long tenantId, Long journeyId);
}
