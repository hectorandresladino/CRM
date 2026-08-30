package com.crm.controller;

import com.crm.entity.ESignatureRequest;
import com.crm.service.ESignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/esignature")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ESignatureController {

    private final ESignatureService eSignatureService;

    @GetMapping
    public ResponseEntity<List<ESignatureRequest>> getAll() {
        return ResponseEntity.ok(eSignatureService.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<ESignatureRequest> create(@RequestBody ESignatureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eSignatureService.create(request));
    }

    @PostMapping("/sign")
    public ResponseEntity<ESignatureRequest> sign(@RequestBody Map<String, String> body) {
        try {
            String ip = body.getOrDefault("ip", "unknown");
            return ResponseEntity.ok(eSignatureService.sign(body.get("token"), ip));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        eSignatureService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
