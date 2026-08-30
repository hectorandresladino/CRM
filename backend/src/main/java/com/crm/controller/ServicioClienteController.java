/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.ServicioCliente;
import com.crm.service.ServicioClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicio-cliente")
@RequiredArgsConstructor
public class ServicioClienteController {
    
    private final ServicioClienteService servicioClienteService;
    
    @GetMapping
    public ResponseEntity<List<ServicioCliente>> getAllServicios() {
        return ResponseEntity.ok(servicioClienteService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServicioCliente> getServicioById(@PathVariable Long id) {
        return servicioClienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ServicioCliente> createServicio(@RequestBody ServicioCliente servicio) {
        try {
            ServicioCliente nuevoServicio = servicioClienteService.save(servicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServicioCliente> updateServicio(@PathVariable Long id, @RequestBody ServicioCliente servicio) {
        try {
            ServicioCliente servicioActualizado = servicioClienteService.update(id, servicio);
            return ResponseEntity.ok(servicioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicio(@PathVariable Long id) {
        try {
            servicioClienteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ServicioCliente>> getServiciosByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(servicioClienteService.findByClienteId(clienteId));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ServicioCliente>> getServiciosByEstado(@PathVariable ServicioCliente.EstadoServicio estado) {
        return ResponseEntity.ok(servicioClienteService.findByEstado(estado));
    }
    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ServicioCliente>> getServiciosByTipo(@PathVariable ServicioCliente.TipoPQRS tipo) {
        return ResponseEntity.ok(servicioClienteService.findByTipo(tipo));
    }
    
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<ServicioCliente>> getServiciosByPrioridad(@PathVariable ServicioCliente.PrioridadPQRS prioridad) {
        return ResponseEntity.ok(servicioClienteService.findByPrioridad(prioridad));
    }
    
    @GetMapping("/asignado/{asignadoA}")
    public ResponseEntity<List<ServicioCliente>> getServiciosByAsignado(@PathVariable String asignadoA) {
        return ResponseEntity.ok(servicioClienteService.findByAsignadoA(asignadoA));
    }
    
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<ServicioCliente> asignarServicio(@PathVariable Long id, 
                                                           @RequestParam String asignadoA) {
        try {
            return ResponseEntity.ok(servicioClienteService.asignarServicio(id, asignadoA));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<ServicioCliente> resolverServicio(@PathVariable Long id, 
                                                           @RequestParam String resolucion) {
        try {
            return ResponseEntity.ok(servicioClienteService.resolverServicio(id, resolucion));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<ServicioCliente> cerrarServicio(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicioClienteService.cerrarServicio(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/urgentes")
    public ResponseEntity<List<ServicioCliente>> getServiciosUrgentes() {
        return ResponseEntity.ok(servicioClienteService.findUrgentesAbiertos());
    }
}
