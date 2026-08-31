/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.Cotizacion;
import com.crm.repository.ClienteRepository;
import com.crm.repository.CotizacionRepository;
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
public class CotizacionService {
    
    private final CotizacionRepository cotizacionRepository;
    private final ClienteRepository clienteRepository;
    
    public List<Cotizacion> findAll() {
        return cotizacionRepository.findByTenantId(tenantId());
    }
    
    public Optional<Cotizacion> findById(Long id) {
        return cotizacionRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public Cotizacion save(Cotizacion cotizacion) {
        Long tenantId = tenantId();
        Cliente cliente = clienteRepository.findByIdAndTenantId(cotizacion.getCliente().getId(), tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
        cotizacion.setCliente(cliente);
        cotizacion.setTenantId(tenantId);
        
        if (cotizacion.getTotal() == null) {
            BigDecimal total = cotizacion.getSubtotal();
            if (cotizacion.getDescuento() != null) {
                total = total.subtract(cotizacion.getDescuento());
            }
            if (cotizacion.getImpuesto() != null) {
                total = total.add(cotizacion.getImpuesto());
            }
            cotizacion.setTotal(total);
        }
        
        if (cotizacion.getValidez() == null) {
            cotizacion.setValidez(LocalDate.now().plusDays(30));
        }
        
        return cotizacionRepository.save(cotizacion);
    }
    
    public Cotizacion update(Long id, Cotizacion cotizacion) {
        Long tenantId = tenantId();
        Cotizacion existingCotizacion = cotizacionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cotización"));
        
        Cliente cliente = clienteRepository.findByIdAndTenantId(cotizacion.getCliente().getId(), tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
        existingCotizacion.setCliente(cliente);
        existingCotizacion.setCodigo(cotizacion.getCodigo());
        existingCotizacion.setDescripcion(cotizacion.getDescripcion());
        existingCotizacion.setSubtotal(cotizacion.getSubtotal());
        existingCotizacion.setDescuento(cotizacion.getDescuento());
        existingCotizacion.setImpuesto(cotizacion.getImpuesto());
        existingCotizacion.setMargen(cotizacion.getMargen());
        existingCotizacion.setVendedor(cotizacion.getVendedor());
        existingCotizacion.setTerminos(cotizacion.getTerminos());
        existingCotizacion.setNotas(cotizacion.getNotas());
        existingCotizacion.setValidez(cotizacion.getValidez());
        existingCotizacion.setEstado(cotizacion.getEstado());
        
        BigDecimal total = cotizacion.getSubtotal();
        if (cotizacion.getDescuento() != null) {
            total = total.subtract(cotizacion.getDescuento());
        }
        if (cotizacion.getImpuesto() != null) {
            total = total.add(cotizacion.getImpuesto());
        }
        existingCotizacion.setTotal(total);
        
        return cotizacionRepository.save(existingCotizacion);
    }
    
    public void delete(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Cotización"));
        cotizacionRepository.delete(cotizacion);
    }
    
    public List<Cotizacion> findByClienteId(Long clienteId) {
        return cotizacionRepository.findByTenantIdAndClienteId(tenantId(), clienteId);
    }
    
    public List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado) {
        return cotizacionRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<Cotizacion> findByVendedor(String vendedor) {
        return cotizacionRepository.findByTenantIdAndVendedor(tenantId(), vendedor);
    }
    
    public Cotizacion enviarCotizacion(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Cotización"));
        cotizacion.setEstado(Cotizacion.EstadoCotizacion.ENVIADA);
        cotizacion.setFechaEnvio(LocalDateTime.now());
        return cotizacionRepository.save(cotizacion);
    }
    
    public Cotizacion aprobarCotizacion(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Cotización"));
        cotizacion.setEstado(Cotizacion.EstadoCotizacion.APROBADA);
        cotizacion.setFechaAprobacion(LocalDateTime.now());
        return cotizacionRepository.save(cotizacion);
    }
    
    public List<Cotizacion> findExpiredCotizaciones() {
        return cotizacionRepository.findExpiredCotizacionesByTenantId(tenantId(), LocalDate.now());
    }

    private Long tenantId() { return TenantContext.requireCurrentTenant(); }
}
