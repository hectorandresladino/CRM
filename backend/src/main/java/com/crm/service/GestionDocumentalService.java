package com.crm.service;

import com.crm.entity.GestionDocumental;
import com.crm.repository.GestionDocumentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GestionDocumentalService {
    
    private final GestionDocumentalRepository documentoRepository;
    
    public List<GestionDocumental> findAll() {
        return documentoRepository.findAll();
    }
    
    public Optional<GestionDocumental> findById(Long id) {
        return documentoRepository.findById(id);
    }
    
    public GestionDocumental save(GestionDocumental documento) {
        documento.setFechaSubida(LocalDateTime.now());
        return documentoRepository.save(documento);
    }
    
    public GestionDocumental update(Long id, GestionDocumental documento) {
        documento.setId(id);
        return documentoRepository.save(documento);
    }
    
    public void delete(Long id) {
        documentoRepository.deleteById(id);
    }
    
    public List<GestionDocumental> findByClienteId(Long clienteId) {
        return documentoRepository.findByClienteId(clienteId);
    }
    
    public List<GestionDocumental> findByCategoria(String categoria) {
        return documentoRepository.findByCategoria(categoria);
    }
    
    public List<GestionDocumental> findPorVencer() {
        return documentoRepository.findByFechaVencimientoBefore(LocalDateTime.now().plusDays(7));
    }
}
