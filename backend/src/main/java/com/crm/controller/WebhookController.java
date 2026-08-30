package com.crm.controller;

import com.crm.entity.Webhook;
import com.crm.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebhookController {
    private final WebhookService service;

    @GetMapping
    public ResponseEntity<List<Webhook>> getAll() {
        return ResponseEntity.ok(service.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<Webhook> create(@RequestBody Webhook webhook) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(webhook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Webhook> update(@PathVariable Long id, @RequestBody Webhook webhook) {
        webhook.setId(id);
        return ResponseEntity.ok(service.save(webhook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test/{id}")
    public ResponseEntity<Map<String, String>> test(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("status", "Test sent"));
    }
}
