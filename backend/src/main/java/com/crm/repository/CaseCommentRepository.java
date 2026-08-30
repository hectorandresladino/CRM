package com.crm.repository;

import com.crm.entity.CaseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaseCommentRepository extends JpaRepository<CaseComment, Long> {
    List<CaseComment> findByTenantIdAndCaseId(Long tenantId, Long caseId);
}
