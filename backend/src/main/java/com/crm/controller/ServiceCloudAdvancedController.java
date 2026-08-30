/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.ServiceCloudAdvancedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/service-advanced")
@RequiredArgsConstructor
public class ServiceCloudAdvancedController {

    private final ServiceCloudAdvancedService service;

    // === Cases (Item 25) ===
    @GetMapping("/cases")
    public ResponseEntity<List<ServicioCliente>> getCases() { return ResponseEntity.ok(service.getCases()); }

    @PostMapping("/cases")
    public ResponseEntity<ServicioCliente> createCase(@RequestBody ServicioCliente caso) { return ResponseEntity.ok(service.createCase(caso)); }

    @PutMapping("/cases/{id}/status")
    public ResponseEntity<ServicioCliente> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.updateCaseStatus(id, ServicioCliente.EstadoServicio.valueOf(body.get("status"))));
    }

    @PutMapping("/cases/{id}/assign")
    public ResponseEntity<ServicioCliente> assign(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.assignCase(id, body.get("assignedTo")));
    }

    @PostMapping("/cases/{id}/escalate")
    public ResponseEntity<ServicioCliente> escalate(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.escalateCase(id, body.get("escalatedTo")));
    }

    @GetMapping("/cases/escalatable")
    public ResponseEntity<List<ServicioCliente>> getEscalatable() { return ResponseEntity.ok(service.getEscalatableCases()); }

    // === Case Comments (Item 25) ===
    @PostMapping("/cases/{caseId}/comments")
    public ResponseEntity<CaseComment> addComment(@PathVariable Long caseId, @RequestBody CaseComment comment) {
        comment.setCaseId(caseId);
        return ResponseEntity.ok(service.addComment(comment));
    }

    @GetMapping("/cases/{caseId}/comments")
    public ResponseEntity<List<CaseComment>> getComments(@PathVariable Long caseId) { return ResponseEntity.ok(service.getComments(caseId)); }

    // === Case Attachments (Item 25) ===
    @PostMapping("/cases/{caseId}/attachments")
    public ResponseEntity<CaseAttachment> addAttachment(@PathVariable Long caseId, @RequestBody CaseAttachment attachment) {
        attachment.setCaseId(caseId);
        return ResponseEntity.ok(service.addAttachment(attachment));
    }

    @GetMapping("/cases/{caseId}/attachments")
    public ResponseEntity<List<CaseAttachment>> getAttachments(@PathVariable Long caseId) { return ResponseEntity.ok(service.getAttachments(caseId)); }

    // === Knowledge Base (Item 26) ===
    @GetMapping("/knowledge")
    public ResponseEntity<List<KnowledgeArticle>> getArticles() { return ResponseEntity.ok(service.getArticles()); }

    @PostMapping("/knowledge")
    public ResponseEntity<KnowledgeArticle> createArticle(@RequestBody KnowledgeArticle article) { return ResponseEntity.ok(service.createArticle(article)); }

    @PutMapping("/knowledge/{id}/publish")
    public ResponseEntity<KnowledgeArticle> publishArticle(@PathVariable Long id) { return ResponseEntity.ok(service.publishArticle(id)); }

    @GetMapping("/knowledge/search")
    public ResponseEntity<List<KnowledgeArticle>> searchArticles(@RequestParam String q) { return ResponseEntity.ok(service.searchArticles(q)); }

    @PostMapping("/knowledge/{id}/view")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) { service.incrementViewCount(id); return ResponseEntity.ok().build(); }

    // === SLA (Item 27) ===
    @GetMapping("/sla")
    public ResponseEntity<List<SLAConfiguracion>> getSLAs() { return ResponseEntity.ok(service.getSLAs()); }

    @PostMapping("/sla")
    public ResponseEntity<SLAConfiguracion> createSLA(@RequestBody SLAConfiguracion sla) { return ResponseEntity.ok(service.createSLA(sla)); }

    @GetMapping("/sla/check/{caseId}")
    public ResponseEntity<Map<String, Object>> checkSLA(@PathVariable Long caseId) { return ResponseEntity.ok(service.checkSLACompliance(caseId)); }

    // === Entitlements (Item 28) ===
    @GetMapping("/entitlements")
    public ResponseEntity<List<Entitlement>> getEntitlements() { return ResponseEntity.ok(service.getEntitlements()); }

    @PostMapping("/entitlements")
    public ResponseEntity<Entitlement> createEntitlement(@RequestBody Entitlement entitlement) { return ResponseEntity.ok(service.createEntitlement(entitlement)); }

    @GetMapping("/entitlements/check/{clientId}")
    public ResponseEntity<Map<String, Boolean>> checkEntitlement(@PathVariable Long clientId) {
        return ResponseEntity.ok(Map.of("hasEntitlement", service.checkEntitlement(clientId)));
    }

    // === Field Service (Item 29) ===
    @GetMapping("/field-service")
    public ResponseEntity<List<FieldServiceOrder>> getFieldServiceOrders() { return ResponseEntity.ok(service.getFieldServiceOrders()); }

    @PostMapping("/field-service")
    public ResponseEntity<FieldServiceOrder> createFieldServiceOrder(@RequestBody FieldServiceOrder order) { return ResponseEntity.ok(service.createFieldServiceOrder(order)); }

    @PutMapping("/field-service/{id}/status")
    public ResponseEntity<FieldServiceOrder> updateFieldServiceStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.updateFieldServiceStatus(id, body.get("status")));
    }

    // === CSAT (Item 30) ===
    @GetMapping("/csat")
    public ResponseEntity<List<EncuestaSatisfaccion>> getSurveys() { return ResponseEntity.ok(service.getSurveys()); }

    @PostMapping("/csat")
    public ResponseEntity<EncuestaSatisfaccion> createSurvey(@RequestBody EncuestaSatisfaccion survey) { return ResponseEntity.ok(service.createSurvey(survey)); }

    @GetMapping("/csat/summary")
    public ResponseEntity<Map<String, Object>> getCSATSummary() { return ResponseEntity.ok(service.getCSATSummary()); }

    // === Live Chat (Item 31) ===
    @PostMapping("/chat/start")
    public ResponseEntity<LiveChatSession> startChat(@RequestBody LiveChatSession session) { return ResponseEntity.ok(service.startChatSession(session)); }

    @PostMapping("/chat/{sessionId}/pickup")
    public ResponseEntity<LiveChatSession> pickUpChat(@PathVariable Long sessionId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.pickUpChat(sessionId, Long.valueOf(body.get("agentId").toString()), body.get("agentName").toString()));
    }

    @PostMapping("/chat/{sessionId}/end")
    public ResponseEntity<LiveChatSession> endChat(@PathVariable Long sessionId, @RequestBody Map<String, Object> body) {
        Integer score = body.containsKey("satisfactionScore") ? Integer.valueOf(body.get("satisfactionScore").toString()) : null;
        return ResponseEntity.ok(service.endChat(sessionId, score));
    }

    @PostMapping("/chat/{sessionId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable Long sessionId, @RequestBody ChatMessage message) {
        message.setSessionId(sessionId);
        return ResponseEntity.ok(service.sendChatMessage(message));
    }

    @GetMapping("/chat/{sessionId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) { return ResponseEntity.ok(service.getChatMessages(sessionId)); }

    @GetMapping("/chat/waiting")
    public ResponseEntity<List<LiveChatSession>> getWaitingChats() { return ResponseEntity.ok(service.getWaitingChats()); }

    @GetMapping("/chat/active")
    public ResponseEntity<List<LiveChatSession>> getActiveChats() { return ResponseEntity.ok(service.getActiveChats()); }

    // === Service Console (Item 32) ===
    @GetMapping("/console")
    public ResponseEntity<Map<String, Object>> getConsole() { return ResponseEntity.ok(service.getServiceConsole()); }

    // === Omnichannel (Item 34) ===
    @GetMapping("/omnichannel/stats")
    public ResponseEntity<Map<String, Object>> getOmnichannelStats() { return ResponseEntity.ok(service.getOmnichannelStats()); }
}
