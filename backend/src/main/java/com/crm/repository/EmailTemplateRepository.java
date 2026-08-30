package com.crm.repository;

import com.crm.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    List<EmailTemplate> findByTenantId(Long tenantId);
    List<EmailTemplate> findByTenantIdAndCategory(Long tenantId, String category);
    List<EmailTemplate> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
