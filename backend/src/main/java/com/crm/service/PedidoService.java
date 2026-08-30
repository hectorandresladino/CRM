/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.Pedido;
import com.crm.repository.ClienteRepository;
import com.crm.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }
    
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }
    
    public Pedido save(Pedido pedido) {
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        pedido.setCliente(cliente);
        
        if (pedido.getTotal() == null) {
            BigDecimal total = pedido.getSubtotal();
            if (pedido.getDescuento() != null) {
                total = total.subtract(pedido.getDescuento());
            }
            if (pedido.getImpuesto() != null) {
                total = total.add(pedido.getImpuesto());
            }
            if (pedido.getCostoEnvio() != null) {
                total = total.add(pedido.getCostoEnvio());
            }
            pedido.setTotal(total);
        }
        
        if (pedido.getFechaEntregaEstimada() == null) {
            pedido.setFechaEntregaEstimada(LocalDate.now().plusDays(7));
        }
        
        return pedidoRepository.save(pedido);
    }
    
    public Pedido update(Long id, Pedido pedido) {
        Pedido existingPedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        existingPedido.setCliente(cliente);
        existingPedido.setCodigo(pedido.getCodigo());
        existingPedido.setDescripcion(pedido.getDescripcion());
        existingPedido.setSubtotal(pedido.getSubtotal());
        existingPedido.setDescuento(pedido.getDescuento());
        existingPedido.setImpuesto(pedido.getImpuesto());
        existingPedido.setCostoEnvio(pedido.getCostoEnvio());
        existingPedido.setDireccionEnvio(pedido.getDireccionEnvio());
        existingPedido.setCiudadEnvio(pedido.getCiudadEnvio());
        existingPedido.setPaisEnvio(pedido.getPaisEnvio());
        existingPedido.setCodigoPostalEnvio(pedido.getCodigoPostalEnvio());
        existingPedido.setFechaEntregaEstimada(pedido.getFechaEntregaEstimada());
        existingPedido.setFechaEntregaReal(pedido.getFechaEntregaReal());
        existingPedido.setVendedor(pedido.getVendedor());
        existingPedido.setNotas(pedido.getNotas());
        existingPedido.setNotasEnvio(pedido.getNotasEnvio());
        existingPedido.setEstado(pedido.getEstado());
        existingPedido.setMetodoEnvio(pedido.getMetodoEnvio());
        
        BigDecimal total = pedido.getSubtotal();
        if (pedido.getDescuento() != null) {
            total = total.subtract(pedido.getDescuento());
        }
        if (pedido.getImpuesto() != null) {
            total = total.add(pedido.getImpuesto());
        }
        if (pedido.getCostoEnvio() != null) {
            total = total.add(pedido.getCostoEnvio());
        }
        existingPedido.setTotal(total);
        
        return pedidoRepository.save(existingPedido);
    }
    
    public void delete(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado");
        }
        pedidoRepository.deleteById(id);
    }
    
    public List<Pedido> findByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }
    
    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }
    
    public List<Pedido> findByVendedor(String vendedor) {
        return pedidoRepository.findByVendedor(vendedor);
    }
    
    public Pedido procesarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(Pedido.EstadoPedido.PROCESANDO);
        pedido.setFechaProcesamiento(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
    
    public Pedido enviarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(Pedido.EstadoPedido.ENVIADO);
        pedido.setFechaEnvio(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
    
    public Pedido entregarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
        pedido.setFechaEntregaReal(LocalDate.now());
        return pedidoRepository.save(pedido);
    }
    
    public List<Pedido> findPedidosAtrasados() {
        return pedidoRepository.findPedidosAtrasados(LocalDate.now());
    }
}
