/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.MesaAyuda;
import com.crm.repository.MesaAyudaRepository;
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
public class MesaAyudaService {
    
    private final MesaAyudaRepository mesaAyudaRepository;
    
    public List<MesaAyuda> findAll() {
        return mesaAyudaRepository.findByTenantId(tid());
    }
    
    public Optional<MesaAyuda> findById(Long id) {
        return mesaAyudaRepository.findByTenantIdAndId(tid(), id);
    }
    
    public MesaAyuda save(MesaAyuda ticket) {
        ticket.setTenantId(tid());
        return mesaAyudaRepository.save(ticket);
    }
    
    public MesaAyuda update(Long id, MesaAyuda ticket) {
        mesaAyudaRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        ticket.setId(id);
        ticket.setTenantId(tid());
        return mesaAyudaRepository.save(ticket);
    }
    
    public void delete(Long id) {
        mesaAyudaRepository.delete(mesaAyudaRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado")));
    }
    
    public MesaAyuda asignar(Long id, String asignadoA) {
        Optional<MesaAyuda> ticketOpt = mesaAyudaRepository.findByTenantIdAndId(tid(), id);
        if (ticketOpt.isPresent()) {
            MesaAyuda ticket = ticketOpt.get();
            ticket.setAsignadoA(asignadoA);
            return mesaAyudaRepository.save(ticket);
        }
        throw new RuntimeException("Ticket no encontrado");
    }
    
    public MesaAyuda resolver(Long id, String solucion, Integer satisfaccion) {
        Optional<MesaAyuda> ticketOpt = mesaAyudaRepository.findByTenantIdAndId(tid(), id);
        if (ticketOpt.isPresent()) {
            MesaAyuda ticket = ticketOpt.get();
            ticket.setSolucion(solucion);
            ticket.setEstado("CERRADO");
            ticket.setFechaCierre(LocalDateTime.now());
            ticket.setSatisfaccionCliente(satisfaccion);
            if (ticket.getFechaCreacion() != null) {
                long minutos = java.time.Duration.between(ticket.getFechaCreacion(), LocalDateTime.now()).toMinutes();
                ticket.setTiempoResolucionMinutos((int) minutos);
            }
            return mesaAyudaRepository.save(ticket);
        }
        throw new RuntimeException("Ticket no encontrado");
    }
    
    public List<MesaAyuda> findByClienteId(Long clienteId) {
        return mesaAyudaRepository.findByTenantIdAndClienteId(tid(), clienteId);
    }
    
    public List<MesaAyuda> findByEstado(String estado) {
        return mesaAyudaRepository.findByTenantIdAndEstado(tid(), estado);
    }
    
    public List<MesaAyuda> findByAsignadoA(String asignadoA) {
        return mesaAyudaRepository.findByTenantIdAndAsignadoA(tid(), asignadoA);
    }
    
    public List<MesaAyuda> findAbiertos() {
        return mesaAyudaRepository.findByTenantIdAndEstadoNot(tid(), "CERRADO");
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
