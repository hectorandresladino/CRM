package com.crm.service;

import com.crm.entity.Contrato;
import com.crm.repository.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratoService {
    
    private final ContratoRepository contratoRepository;
    
    public List<Contrato> findAll() {
        return contratoRepository.findAll();
    }
    
    public Optional<Contrato> findById(Long id) {
        return contratoRepository.findById(id);
    }
    
    public Contrato save(Contrato contrato) {
        return contratoRepository.save(contrato);
    }
    
    public Contrato update(Long id, Contrato contrato) {
        contrato.setId(id);
        return contratoRepository.save(contrato);
    }
    
    public void delete(Long id) {
        contratoRepository.deleteById(id);
    }
    
    public List<Contrato> findByClienteId(Long clienteId) {
        return contratoRepository.findByClienteId(clienteId);
    }
    
    public List<Contrato> findByEstado(String estado) {
        return contratoRepository.findByEstado(estado);
    }
    
    public List<Contrato> findPorVencer() {
        return contratoRepository.findByFechaFinBefore(LocalDate.now().plusDays(30));
    }
}
