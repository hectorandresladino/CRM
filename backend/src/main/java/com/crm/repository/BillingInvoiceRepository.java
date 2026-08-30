package com.crm.repository;

import com.crm.entity.BillingInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingInvoiceRepository extends JpaRepository<BillingInvoice, Long> {

    List<BillingInvoice> findByTenantIdOrderByIssueDateDesc(Long tenantId);
}
