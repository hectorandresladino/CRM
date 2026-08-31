/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.CPQProduct;
import com.crm.entity.CPQQuoteItem;
import com.crm.repository.CPQProductRepository;
import com.crm.repository.CPQQuoteItemRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CPQService {

    private final CPQProductRepository productRepository;
    private final CPQQuoteItemRepository quoteItemRepository;

    public List<CPQProduct> findAllProducts(Long tenantId) {
        return productRepository.findByTenantIdAndIsActive(tid(), true);
    }

    public CPQProduct saveProduct(CPQProduct product) {
        product.setTenantId(tid());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.delete(productRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado")));
    }

    public List<CPQQuoteItem> findQuoteItems(Long tenantId, Long cotizacionId) {
        return quoteItemRepository.findByTenantIdAndCotizacionId(tid(), cotizacionId);
    }

    public CPQQuoteItem addQuoteItem(CPQQuoteItem item) {
        Long tenantId = tid();
        CPQProduct product = productRepository.findByTenantIdAndId(tenantId, item.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        item.setTenantId(tenantId);

        if (item.getUnitPrice() == null) {
            item.setUnitPrice(product.getBasePrice());
        }

        if (item.getDiscountPct() == null) {
            item.setDiscountPct(BigDecimal.ZERO);
        }

        BigDecimal discountAmount = item.getUnitPrice()
                .multiply(item.getDiscountPct())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal netPrice = item.getUnitPrice().subtract(discountAmount);
        item.setLineTotal(netPrice.multiply(BigDecimal.valueOf(item.getQuantity())));

        if (product.getMaxDiscountPct() != null && item.getDiscountPct().compareTo(product.getMaxDiscountPct()) > 0) {
            item.setApprovalRequired(true);
        } else {
            item.setApprovalRequired(false);
        }

        if (product.getMinMarginPct() != null && product.getCostPrice() != null) {
            BigDecimal margin = netPrice.subtract(product.getCostPrice())
                    .divide(item.getUnitPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            if (margin.compareTo(product.getMinMarginPct()) < 0) {
                item.setApprovalRequired(true);
            }
        }

        return quoteItemRepository.save(item);
    }

    public CPQQuoteItem approveItem(Long id, String approvedBy) {
        CPQQuoteItem item = quoteItemRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        item.setApprovedBy(approvedBy);
        item.setApprovedAt(LocalDateTime.now());
        return quoteItemRepository.save(item);
    }

    public List<CPQQuoteItem> getPendingApprovals(Long tenantId) {
        return quoteItemRepository.findByTenantIdAndApprovalRequired(tid(), true)
                .stream()
                .filter(i -> i.getApprovedBy() == null)
                .toList();
    }

    public void deleteQuoteItem(Long id) {
        quoteItemRepository.delete(quoteItemRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
