package com.crm.controller;

import com.crm.entity.FormularioWeb;
import com.crm.service.FormularioWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/formularios-web")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormularioWebController {
    private final FormularioWebService service;

    @GetMapping
    public ResponseEntity<List<FormularioWeb>> getAll() {
        return ResponseEntity.ok(service.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<FormularioWeb> create(@RequestBody FormularioWeb form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(form));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormularioWeb> update(@PathVariable Long id, @RequestBody FormularioWeb form) {
        form.setId(id);
        return ResponseEntity.ok(service.save(form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/submit/{token}")
    public ResponseEntity<Map<String, String>> submit(@PathVariable String token, @RequestBody Map<String, Object> data) {
        service.submit(token);
        return ResponseEntity.ok(Map.of("status", "submitted"));
    }
}
