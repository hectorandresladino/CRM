package com.crm.controller;

import com.crm.entity.Pago;
import com.crm.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagoController {
    private final PagoService service;

    @GetMapping
    public ResponseEntity<List<Pago>> getAll() {
        return ResponseEntity.ok(service.findAll(1L));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.findByEstado(1L, estado));
    }

    @PostMapping
    public ResponseEntity<Pago> create(@RequestBody Pago pago) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(pago));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Pago> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.markAsPaid(id, body.get("transactionId")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
