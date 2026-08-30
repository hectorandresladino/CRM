/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.MesaAyuda;
import com.crm.repository.MesaAyudaRepository;
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
        return mesaAyudaRepository.findAll();
    }
    
    public Optional<MesaAyuda> findById(Long id) {
        return mesaAyudaRepository.findById(id);
    }
    
    public MesaAyuda save(MesaAyuda ticket) {
        return mesaAyudaRepository.save(ticket);
    }
    
    public MesaAyuda update(Long id, MesaAyuda ticket) {
        ticket.setId(id);
        return mesaAyudaRepository.save(ticket);
    }
    
    public void delete(Long id) {
        mesaAyudaRepository.deleteById(id);
    }
    
    public MesaAyuda asignar(Long id, String asignadoA) {
        Optional<MesaAyuda> ticketOpt = mesaAyudaRepository.findById(id);
        if (ticketOpt.isPresent()) {
            MesaAyuda ticket = ticketOpt.get();
            ticket.setAsignadoA(asignadoA);
            return mesaAyudaRepository.save(ticket);
        }
        throw new RuntimeException("Ticket no encontrado");
    }
    
    public MesaAyuda resolver(Long id, String solucion, Integer satisfaccion) {
        Optional<MesaAyuda> ticketOpt = mesaAyudaRepository.findById(id);
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
        return mesaAyudaRepository.findByClienteId(clienteId);
    }
    
    public List<MesaAyuda> findByEstado(String estado) {
        return mesaAyudaRepository.findByEstado(estado);
    }
    
    public List<MesaAyuda> findByAsignadoA(String asignadoA) {
        return mesaAyudaRepository.findByAsignadoA(asignadoA);
    }
    
    public List<MesaAyuda> findAbiertos() {
        return mesaAyudaRepository.findByEstadoNot("CERRADO");
    }
}
