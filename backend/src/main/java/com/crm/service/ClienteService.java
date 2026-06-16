package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.repository.ClienteRepository;
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
        return clienteRepository.findAll();
    }
    
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Cliente save(Cliente cliente) {
        try {
            if (cliente.getEmail() != null && !cliente.getEmail().isEmpty() && clienteRepository.existsByEmail(cliente.getEmail())) {
                throw new RuntimeException("Ya existe un cliente con ese email");
            }
            if (cliente.getIdentificacion() != null && !cliente.getIdentificacion().isEmpty() && clienteRepository.existsByIdentificacion(cliente.getIdentificacion())) {
                throw new RuntimeException("Ya existe un cliente con esa identificación");
            }
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cliente: " + e.getMessage(), e);
        }
    }
    
    public Cliente update(Long id, Cliente cliente) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        if (!existingCliente.getEmail().equals(cliente.getEmail()) && 
            clienteRepository.existsByEmail(cliente.getEmail())) {
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
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }
    
    public List<Cliente> findByEstado(Cliente.EstadoCliente estado) {
        return clienteRepository.findByEstado(estado);
    }
    
    public List<Cliente> buscarPorNombre(String nombre, String apellido) {
        return clienteRepository.buscarPorNombre(nombre, apellido);
    }
    
    public List<Cliente> buscarPorEmpresa(String empresa) {
        return clienteRepository.buscarPorEmpresa(empresa);
    }
}
