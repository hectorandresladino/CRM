package com.crm.controller;

import com.crm.entity.CPQProduct;
import com.crm.entity.CPQQuoteItem;
import com.crm.service.CPQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cpq")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CPQController {

    private final CPQService cpqService;

    @GetMapping("/products")
    public ResponseEntity<List<CPQProduct>> getProducts() {
        return ResponseEntity.ok(cpqService.findAllProducts(1L));
    }

    @PostMapping("/products")
    public ResponseEntity<CPQProduct> createProduct(@RequestBody CPQProduct product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cpqService.saveProduct(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<CPQProduct> updateProduct(@PathVariable Long id, @RequestBody CPQProduct product) {
        product.setId(id);
        return ResponseEntity.ok(cpqService.saveProduct(product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        cpqService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quote-items/{cotizacionId}")
    public ResponseEntity<List<CPQQuoteItem>> getQuoteItems(@PathVariable Long cotizacionId) {
        return ResponseEntity.ok(cpqService.findQuoteItems(1L, cotizacionId));
    }

    @PostMapping("/quote-items")
    public ResponseEntity<CPQQuoteItem> addQuoteItem(@RequestBody CPQQuoteItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cpqService.addQuoteItem(item));
    }

    @PatchMapping("/quote-items/{id}/approve")
    public ResponseEntity<CPQQuoteItem> approveItem(@PathVariable Long id, @RequestBody String approvedBy) {
        return ResponseEntity.ok(cpqService.approveItem(id, approvedBy));
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<CPQQuoteItem>> getPendingApprovals() {
        return ResponseEntity.ok(cpqService.getPendingApprovals(1L));
    }

    @DeleteMapping("/quote-items/{id}")
    public ResponseEntity<Void> deleteQuoteItem(@PathVariable Long id) {
        cpqService.deleteQuoteItem(id);
        return ResponseEntity.noContent().build();
    }
}
