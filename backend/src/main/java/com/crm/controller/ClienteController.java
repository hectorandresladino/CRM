/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Cliente;
import com.crm.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() {
        return ResponseEntity.ok(clienteService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Cliente clienteActualizado = clienteService.update(id, cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Cliente>> getClientesByEstado(@PathVariable Cliente.EstadoCliente estado) {
        return ResponseEntity.ok(clienteService.findByEstado(estado));
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarClientes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String empresa) {
        if (empresa != null) {
            return ResponseEntity.ok(clienteService.buscarPorEmpresa(empresa));
        }
        return ResponseEntity.ok(clienteService.buscarPorNombre(nombre != null ? nombre : "", 
                                                                 apellido != null ? apellido : ""));
    }
}
