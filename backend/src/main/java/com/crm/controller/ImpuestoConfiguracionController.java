package com.crm.controller;

import com.crm.entity.ImpuestoConfiguracion;
import com.crm.service.ImpuestoConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impuestos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImpuestoConfiguracionController {
    private final ImpuestoConfiguracionService service;

    @GetMapping
    public ResponseEntity<List<ImpuestoConfiguracion>> getAll() {
        return ResponseEntity.ok(service.findAll(1L));
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<ImpuestoConfiguracion>> getByPais(@PathVariable String pais) {
        return ResponseEntity.ok(service.findByPais(1L, pais));
    }

    @PostMapping
    public ResponseEntity<ImpuestoConfiguracion> create(@RequestBody ImpuestoConfiguracion impuesto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(impuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImpuestoConfiguracion> update(@PathVariable Long id, @RequestBody ImpuestoConfiguracion impuesto) {
        impuesto.setId(id);
        return ResponseEntity.ok(service.save(impuesto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
