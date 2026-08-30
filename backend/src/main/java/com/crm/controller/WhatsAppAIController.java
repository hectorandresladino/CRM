/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.WhatsAppAIConfig;
import com.crm.entity.WhatsAppConversation;
import com.crm.security.TenantContext;
import com.crm.service.WhatsAppAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp-ai")
@RequiredArgsConstructor
public class WhatsAppAIController {

    private final WhatsAppAIService whatsappAIService;

    @GetMapping("/config")
    public ResponseEntity<WhatsAppAIConfig> getConfig() {
        return ResponseEntity.ok(whatsappAIService.getConfig(getCurrentTenantId()));
    }

    @PutMapping("/config")
    public ResponseEntity<WhatsAppAIConfig> updateConfig(@RequestBody WhatsAppAIConfig config) {
        return ResponseEntity.ok(whatsappAIService.updateConfig(getCurrentTenantId(), config));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> webhook(@RequestBody Map<String, Object> body) {
        String phone = body.get("from") != null ? body.get("from").toString() : "";
        String message = body.get("message") != null ? body.get("message").toString() : "";
        String contactName = body.get("name") != null ? body.get("name").toString() : "";
        String messageType = body.get("type") != null ? body.get("type").toString() : "TEXT";
        whatsappAIService.processInboundMessage(getCurrentTenantId(), phone, contactName, message, messageType);
        return ResponseEntity.ok(Map.of("status", "processed"));
    }

    @PostMapping("/send")
    public ResponseEntity<WhatsAppConversation> send(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(whatsappAIService.sendOutboundMessage(
                getCurrentTenantId(), body.get("phone"), body.get("message"), body.get("agent")));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<WhatsAppConversation>> getConversations() {
        return ResponseEntity.ok(whatsappAIService.getConversations(getCurrentTenantId()));
    }

    @GetMapping("/conversations/{phone}")
    public ResponseEntity<List<WhatsAppConversation>> getConversationByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(whatsappAIService.getConversationsByPhone(getCurrentTenantId(), phone));
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<WhatsAppConversation>> getWaiting() {
        return ResponseEntity.ok(whatsappAIService.getWaitingForAgent(getCurrentTenantId()));
    }

    @PatchMapping("/conversations/{id}/takeover")
    public ResponseEntity<WhatsAppConversation> takeOver(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(whatsappAIService.takeOver(id, body.get("agent")));
    }

    @PatchMapping("/conversations/{id}/resolve")
    public ResponseEntity<WhatsAppConversation> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(whatsappAIService.resolve(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(whatsappAIService.getStats(getCurrentTenantId()));
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}
