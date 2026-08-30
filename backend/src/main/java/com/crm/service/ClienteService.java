/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.repository.ClienteRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    public List<Cliente> findAll() {
        return clienteRepository.findByTenantId(tenantId());
    }
    
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public Cliente save(Cliente cliente) {
        try {
            Long tenantId = tenantId();
            cliente.setTenantId(tenantId);
            if (cliente.getEmail() != null && !cliente.getEmail().isEmpty() && clienteRepository.existsByTenantIdAndEmail(tenantId, cliente.getEmail())) {
                throw new RuntimeException("Ya existe un cliente con ese email");
            }
            if (cliente.getIdentificacion() != null && !cliente.getIdentificacion().isEmpty() && clienteRepository.existsByTenantIdAndIdentificacion(tenantId, cliente.getIdentificacion())) {
                throw new RuntimeException("Ya existe un cliente con esa identificaciÃ³n");
            }
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cliente: " + e.getMessage(), e);
        }
    }
    
    public Cliente update(Long id, Cliente cliente) {
        Long tenantId = tenantId();
        Cliente existingCliente = clienteRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        
        if (!existingCliente.getEmail().equals(cliente.getEmail()) && 
            clienteRepository.existsByTenantIdAndEmail(tenantId, cliente.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con ese email");
        }
        
        existingCliente.setNombre(cliente.getNombre());
        existingCliente.setApellido(cliente.getApellido());
        existingCliente.setEmail(cliente.getEmail());
        existingCliente.setTelefono(cliente.getTelefono());
        existingCliente.setCelular(cliente.getCelular());
        existingCliente.setDireccion(cliente.getDireccion());
        existingCliente.setCiudad(cliente.getCiudad());
        existingCliente.setPais(cliente.getPais());
        existingCliente.setCodigoPostal(cliente.getCodigoPostal());
        existingCliente.setIdentificacion(cliente.getIdentificacion());
        existingCliente.setTipoIdentificacion(cliente.getTipoIdentificacion());
        existingCliente.setEmpresa(cliente.getEmpresa());
        existingCliente.setCargo(cliente.getCargo());
        existingCliente.setSector(cliente.getSector());
        existingCliente.setNotas(cliente.getNotas());
        existingCliente.setEstado(cliente.getEstado());
        
        return clienteRepository.save(existingCliente);
    }
    
    public void delete(Long id) {
        Cliente cliente = clienteRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        clienteRepository.delete(cliente);
    }
    
    public List<Cliente> findByEstado(Cliente.EstadoCliente estado) {
        return clienteRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<Cliente> buscarPorNombre(String nombre, String apellido) {
        Long tenantId = tenantId();
        return clienteRepository.findByTenantIdAndNombreContainingIgnoreCaseOrTenantIdAndApellidoContainingIgnoreCase(
                tenantId, nombre, tenantId, apellido);
    }
    
    public List<Cliente> buscarPorEmpresa(String empresa) {
        return clienteRepository.findByTenantIdAndEmpresaContainingIgnoreCase(tenantId(), empresa);
    }

    private Long tenantId() {
        return TenantContext.requireCurrentTenant();
    }
}
