package com.crm.controller;

import com.crm.entity.Integration;
import com.crm.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IntegrationController {

    private final IntegrationService integrationService;

    @GetMapping
    public ResponseEntity<List<Integration>> getAll() {
        return ResponseEntity.ok(integrationService.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<Integration> connect(@RequestBody Integration integration) {
        return ResponseEntity.status(HttpStatus.CREATED).body(integrationService.connect(integration));
    }

    @PatchMapping("/{id}/disconnect")
    public ResponseEntity<Integration> disconnect(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.disconnect(id));
    }

    @PatchMapping("/{id}/toggle-sync")
    public ResponseEntity<Integration> toggleSync(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.toggleSync(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        integrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
