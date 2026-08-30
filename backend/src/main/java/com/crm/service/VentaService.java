/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.Venta;
import com.crm.repository.ClienteRepository;
import com.crm.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }
    
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }
    
    public Venta save(Venta venta) {
        Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        venta.setCliente(cliente);
        
        if (venta.getTotal() == null) {
            BigDecimal total = venta.getMonto();
            if (venta.getDescuento() != null) {
                total = total.subtract(venta.getDescuento());
            }
            if (venta.getImpuesto() != null) {
                total = total.add(venta.getImpuesto());
            }
            venta.setTotal(total);
        }
        
        return ventaRepository.save(venta);
    }
    
    public Venta update(Long id, Venta venta) {
        Venta existingVenta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        existingVenta.setCliente(cliente);
        existingVenta.setCodigo(venta.getCodigo());
        existingVenta.setDescripcion(venta.getDescripcion());
        existingVenta.setMonto(venta.getMonto());
        existingVenta.setDescuento(venta.getDescuento());
        existingVenta.setImpuesto(venta.getImpuesto());
        existingVenta.setComision(venta.getComision());
        existingVenta.setVendedor(venta.getVendedor());
        existingVenta.setNotas(venta.getNotas());
        existingVenta.setEstado(venta.getEstado());
        existingVenta.setMetodoPago(venta.getMetodoPago());
        
        BigDecimal total = venta.getMonto();
        if (venta.getDescuento() != null) {
            total = total.subtract(venta.getDescuento());
        }
        if (venta.getImpuesto() != null) {
            total = total.add(venta.getImpuesto());
        }
        existingVenta.setTotal(total);
        
        if (venta.getEstado() == Venta.EstadoVenta.CERRADA && existingVenta.getFechaCierre() == null) {
            existingVenta.setFechaCierre(LocalDateTime.now());
        }
        
        return ventaRepository.save(existingVenta);
    }
    
    public void delete(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }
    
    public List<Venta> findByClienteId(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }
    
    public List<Venta> findByEstado(Venta.EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }
    
    public List<Venta> findByVendedor(String vendedor) {
        return ventaRepository.findByVendedor(vendedor);
    }
    
    public Venta cerrarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado(Venta.EstadoVenta.CERRADA);
        venta.setFechaCierre(LocalDateTime.now());
        return ventaRepository.save(venta);
    }
    
    public Double getTotalVentasCerradas() {
        return ventaRepository.sumTotalVentasCerradas();
    }
}
