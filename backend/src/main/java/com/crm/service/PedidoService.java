/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.Pedido;
import com.crm.repository.ClienteRepository;
import com.crm.repository.PedidoRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
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
        return pedidoRepository.findByTenantId(tenantId());
    }
    
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public Pedido save(Pedido pedido) {
        Long tenantId = tenantId();
        Cliente cliente = clienteRepository.findByIdAndTenantId(pedido.getCliente().getId(), tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
        pedido.setCliente(cliente);
        pedido.setTenantId(tenantId);
        
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
        Long tenantId = tenantId();
        Pedido existingPedido = pedidoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Pedido"));
        
        Cliente cliente = clienteRepository.findByIdAndTenantId(pedido.getCliente().getId(), tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
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
        Pedido pedido = pedidoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Pedido"));
        pedidoRepository.delete(pedido);
    }
    
    public List<Pedido> findByClienteId(Long clienteId) {
        return pedidoRepository.findByTenantIdAndClienteId(tenantId(), clienteId);
    }
    
    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<Pedido> findByVendedor(String vendedor) {
        return pedidoRepository.findByTenantIdAndVendedor(tenantId(), vendedor);
    }
    
    public Pedido procesarPedido(Long id) {
        Pedido pedido = pedidoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Pedido"));
        pedido.setEstado(Pedido.EstadoPedido.PROCESANDO);
        pedido.setFechaProcesamiento(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
    
    public Pedido enviarPedido(Long id) {
        Pedido pedido = pedidoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Pedido"));
        pedido.setEstado(Pedido.EstadoPedido.ENVIADO);
        pedido.setFechaEnvio(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
    
    public Pedido entregarPedido(Long id) {
        Pedido pedido = pedidoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Pedido"));
        pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
        pedido.setFechaEntregaReal(LocalDate.now());
        return pedidoRepository.save(pedido);
    }
    
    public List<Pedido> findPedidosAtrasados() {
        return pedidoRepository.findPedidosAtrasadosByTenantId(tenantId(), LocalDate.now());
    }

    private Long tenantId() { return TenantContext.requireCurrentTenant(); }
}
