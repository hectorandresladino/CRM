/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Cotizacion;
import com.crm.service.CotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {
    
    private final CotizacionService cotizacionService;
    
    @GetMapping
    public ResponseEntity<List<Cotizacion>> getAllCotizaciones() {
        return ResponseEntity.ok(cotizacionService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> getCotizacionById(@PathVariable Long id) {
        return cotizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Cotizacion> createCotizacion(@RequestBody Cotizacion cotizacion) {
        try {
            Cotizacion nuevaCotizacion = cotizacionService.save(cotizacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCotizacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cotizacion> updateCotizacion(@PathVariable Long id, @RequestBody Cotizacion cotizacion) {
        try {
            Cotizacion cotizacionActualizada = cotizacionService.update(id, cotizacion);
            return ResponseEntity.ok(cotizacionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCotizacion(@PathVariable Long id) {
        try {
            cotizacionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cotizacion>> getCotizacionesByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(cotizacionService.findByClienteId(clienteId));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Cotizacion>> getCotizacionesByEstado(@PathVariable Cotizacion.EstadoCotizacion estado) {
        return ResponseEntity.ok(cotizacionService.findByEstado(estado));
    }
    
    @GetMapping("/vendedor/{vendedor}")
    public ResponseEntity<List<Cotizacion>> getCotizacionesByVendedor(@PathVariable String vendedor) {
        return ResponseEntity.ok(cotizacionService.findByVendedor(vendedor));
    }
    
    @PatchMapping("/{id}/enviar")
    public ResponseEntity<Cotizacion> enviarCotizacion(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cotizacionService.enviarCotizacion(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Cotizacion> aprobarCotizacion(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cotizacionService.aprobarCotizacion(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/expiradas")
    public ResponseEntity<List<Cotizacion>> getCotizacionesExpiradas() {
        return ResponseEntity.ok(cotizacionService.findExpiredCotizaciones());
    }
}
