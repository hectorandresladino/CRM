/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.ServicioCliente;
import com.crm.repository.ClienteRepository;
import com.crm.repository.ServicioClienteRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioClienteService {
    
    private final ServicioClienteRepository servicioClienteRepository;
    private final ClienteRepository clienteRepository;
    
    public List<ServicioCliente> findAll() {
        return servicioClienteRepository.findByTenantId(tenantId());
    }
    
    public Optional<ServicioCliente> findById(Long id) {
        return servicioClienteRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public ServicioCliente save(ServicioCliente servicio) {
        Long tenantId = tenantId();
        Cliente cliente = clienteRepository.findByIdAndTenantId(servicio.getCliente().getId(), tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
        servicio.setCliente(cliente);
        servicio.setTenantId(tenantId);
        return servicioClienteRepository.save(servicio);
    }
    
    public ServicioCliente update(Long id, ServicioCliente servicio) {
        Long tenantId = tenantId();
        ServicioCliente existingServicio = servicioClienteRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Servicio"));
        
        Cliente cliente = clienteRepository.findByIdAndTenantId(servicio.getCliente().getId(), tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
        existingServicio.setCliente(cliente);
        existingServicio.setCodigo(servicio.getCodigo());
        existingServicio.setAsunto(servicio.getAsunto());
        existingServicio.setDescripcion(servicio.getDescripcion());
        existingServicio.setTipo(servicio.getTipo());
        existingServicio.setPrioridad(servicio.getPrioridad());
        existingServicio.setCanal(servicio.getCanal());
        existingServicio.setEstado(servicio.getEstado());
        existingServicio.setAsignadoA(servicio.getAsignadoA());
        existingServicio.setResolucion(servicio.getResolucion());
        existingServicio.setNotas(servicio.getNotas());
        
        if (servicio.getAsignadoA() != null && existingServicio.getFechaAsignacion() == null) {
            existingServicio.setFechaAsignacion(LocalDateTime.now());
        }
        
        return servicioClienteRepository.save(existingServicio);
    }
    
    public void delete(Long id) {
        ServicioCliente servicio = servicioClienteRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Servicio"));
        servicioClienteRepository.delete(servicio);
    }
    
    public List<ServicioCliente> findByClienteId(Long clienteId) {
        return servicioClienteRepository.findByTenantIdAndClienteId(tenantId(), clienteId);
    }
    
    public List<ServicioCliente> findByEstado(ServicioCliente.EstadoServicio estado) {
        return servicioClienteRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<ServicioCliente> findByTipo(ServicioCliente.TipoPQRS tipo) {
        return servicioClienteRepository.findByTenantIdAndTipo(tenantId(), tipo);
    }
    
    public List<ServicioCliente> findByPrioridad(ServicioCliente.PrioridadPQRS prioridad) {
        return servicioClienteRepository.findByTenantIdAndPrioridad(tenantId(), prioridad);
    }
    
    public List<ServicioCliente> findByAsignadoA(String asignadoA) {
        return servicioClienteRepository.findByTenantIdAndAsignadoA(tenantId(), asignadoA);
    }
    
    public ServicioCliente asignarServicio(Long id, String asignadoA) {
        ServicioCliente servicio = servicioClienteRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Servicio"));
        servicio.setAsignadoA(asignadoA);
        servicio.setEstado(ServicioCliente.EstadoServicio.ASIGNADO);
        servicio.setFechaAsignacion(LocalDateTime.now());
        return servicioClienteRepository.save(servicio);
    }
    
    public ServicioCliente resolverServicio(Long id, String resolucion) {
        ServicioCliente servicio = servicioClienteRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Servicio"));
        servicio.setResolucion(resolucion);
        servicio.setEstado(ServicioCliente.EstadoServicio.RESUELTO);
        servicio.setFechaCierre(LocalDateTime.now());
        return servicioClienteRepository.save(servicio);
    }
    
    public ServicioCliente cerrarServicio(Long id) {
        ServicioCliente servicio = servicioClienteRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Servicio"));
        servicio.setEstado(ServicioCliente.EstadoServicio.CERRADO);
        servicio.setFechaCierre(LocalDateTime.now());
        return servicioClienteRepository.save(servicio);
    }
    
    public List<ServicioCliente> findUrgentesAbiertos() {
        return servicioClienteRepository.findUrgentesAbiertosByTenantId(
                tenantId(), ServicioCliente.PrioridadPQRS.URGENTE);
    }

    private Long tenantId() { return TenantContext.requireCurrentTenant(); }
}
