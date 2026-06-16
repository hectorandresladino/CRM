package com.crm.controller;

import com.crm.entity.Venta;
import com.crm.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {
    
    private final VentaService ventaService;
    
    @GetMapping
    public ResponseEntity<List<Venta>> getAllVentas() {
        return ResponseEntity.ok(ventaService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        return ventaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Venta> createVenta(@RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.save(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Venta> updateVenta(@PathVariable Long id, @RequestBody Venta venta) {
        try {
            Venta ventaActualizada = ventaService.update(id, venta);
            return ResponseEntity.ok(ventaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        try {
            ventaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> getVentasByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.findByClienteId(clienteId));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Venta>> getVentasByEstado(@PathVariable Venta.EstadoVenta estado) {
        return ResponseEntity.ok(ventaService.findByEstado(estado));
    }
    
    @GetMapping("/vendedor/{vendedor}")
    public ResponseEntity<List<Venta>> getVentasByVendedor(@PathVariable String vendedor) {
        return ResponseEntity.ok(ventaService.findByVendedor(vendedor));
    }
    
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Venta> cerrarVenta(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ventaService.cerrarVenta(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/total-cerradas")
    public ResponseEntity<Double> getTotalVentasCerradas() {
        return ResponseEntity.ok(ventaService.getTotalVentasCerradas());
    }
}
