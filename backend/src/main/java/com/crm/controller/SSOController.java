package com.crm.controller;

import com.crm.entity.SSOConfiguration;
import com.crm.service.SSOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sso")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SSOController {

    private final SSOService ssoService;

    @GetMapping
    public ResponseEntity<List<SSOConfiguration>> getAll() {
        return ResponseEntity.ok(ssoService.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<SSOConfiguration> create(@RequestBody SSOConfiguration config) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ssoService.save(config));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SSOConfiguration> update(@PathVariable Long id, @RequestBody SSOConfiguration config) {
        config.setId(id);
        return ResponseEntity.ok(ssoService.save(config));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ssoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<SSOConfiguration> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ssoService.toggleActive(id));
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<SSOConfiguration> sync(@PathVariable Long id) {
        return ResponseEntity.ok(ssoService.sync(id));
    }
}
