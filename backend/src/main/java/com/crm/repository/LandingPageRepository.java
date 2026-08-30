package com.crm.repository;

import com.crm.entity.LandingPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LandingPageRepository extends JpaRepository<LandingPage, Long> {
    List<LandingPage> findByTenantId(Long tenantId);
    Optional<LandingPage> findBySlug(String slug);
    List<LandingPage> findByTenantIdAndIsPublished(Long tenantId, Boolean isPublished);
}
