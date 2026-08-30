package com.crm.repository;

import com.crm.entity.CaseAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaseAttachmentRepository extends JpaRepository<CaseAttachment, Long> {
    List<CaseAttachment> findByTenantIdAndCaseId(Long tenantId, Long caseId);
}
