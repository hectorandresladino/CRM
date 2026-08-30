/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.ServicioCliente;
import com.crm.repository.ClienteRepository;
import com.crm.repository.ServicioClienteRepository;
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
        return servicioClienteRepository.findAll();
    }
    
    public Optional<ServicioCliente> findById(Long id) {
        return servicioClienteRepository.findById(id);
    }
    
    public ServicioCliente save(ServicioCliente servicio) {
        Cliente cliente = clienteRepository.findById(servicio.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        servicio.setCliente(cliente);
        return servicioClienteRepository.save(servicio);
    }
    
    public ServicioCliente update(Long id, ServicioCliente servicio) {
        ServicioCliente existingServicio = servicioClienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        Cliente cliente = clienteRepository.findById(servicio.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
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
        if (!servicioClienteRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        servicioClienteRepository.deleteById(id);
    }
    
    public List<ServicioCliente> findByClienteId(Long clienteId) {
        return servicioClienteRepository.findByClienteId(clienteId);
    }
    
    public List<ServicioCliente> findByEstado(ServicioCliente.EstadoServicio estado) {
        return servicioClienteRepository.findByEstado(estado);
    }
    
    public List<ServicioCliente> findByTipo(ServicioCliente.TipoPQRS tipo) {
        return servicioClienteRepository.findByTipo(tipo);
    }
    
    public List<ServicioCliente> findByPrioridad(ServicioCliente.PrioridadPQRS prioridad) {
        return servicioClienteRepository.findByPrioridad(prioridad);
    }
    
    public List<ServicioCliente> findByAsignadoA(String asignadoA) {
        return servicioClienteRepository.findByAsignadoA(asignadoA);
    }
    
    public ServicioCliente asignarServicio(Long id, String asignadoA) {
        ServicioCliente servicio = servicioClienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setAsignadoA(asignadoA);
        servicio.setEstado(ServicioCliente.EstadoServicio.ASIGNADO);
        servicio.setFechaAsignacion(LocalDateTime.now());
        return servicioClienteRepository.save(servicio);
    }
    
    public ServicioCliente resolverServicio(Long id, String resolucion) {
        ServicioCliente servicio = servicioClienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setResolucion(resolucion);
        servicio.setEstado(ServicioCliente.EstadoServicio.RESUELTO);
        servicio.setFechaCierre(LocalDateTime.now());
        return servicioClienteRepository.save(servicio);
    }
    
    public ServicioCliente cerrarServicio(Long id) {
        ServicioCliente servicio = servicioClienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setEstado(ServicioCliente.EstadoServicio.CERRADO);
        servicio.setFechaCierre(LocalDateTime.now());
        return servicioClienteRepository.save(servicio);
    }
    
    public List<ServicioCliente> findUrgentesAbiertos() {
        return servicioClienteRepository.findUrgentesAbiertos(ServicioCliente.PrioridadPQRS.URGENTE);
    }
}
