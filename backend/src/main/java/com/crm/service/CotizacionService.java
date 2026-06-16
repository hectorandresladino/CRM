package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.Cotizacion;
import com.crm.repository.ClienteRepository;
import com.crm.repository.CotizacionRepository;
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
        return cotizacionRepository.findAll();
    }
    
    public Optional<Cotizacion> findById(Long id) {
        return cotizacionRepository.findById(id);
    }
    
    public Cotizacion save(Cotizacion cotizacion) {
        Cliente cliente = clienteRepository.findById(cotizacion.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        cotizacion.setCliente(cliente);
        
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
        Cotizacion existingCotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        
        Cliente cliente = clienteRepository.findById(cotizacion.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
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
        if (!cotizacionRepository.existsById(id)) {
            throw new RuntimeException("Cotización no encontrada");
        }
        cotizacionRepository.deleteById(id);
    }
    
    public List<Cotizacion> findByClienteId(Long clienteId) {
        return cotizacionRepository.findByClienteId(clienteId);
    }
    
    public List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado) {
        return cotizacionRepository.findByEstado(estado);
    }
    
    public List<Cotizacion> findByVendedor(String vendedor) {
        return cotizacionRepository.findByVendedor(vendedor);
    }
    
    public Cotizacion enviarCotizacion(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        cotizacion.setEstado(Cotizacion.EstadoCotizacion.ENVIADA);
        cotizacion.setFechaEnvio(LocalDateTime.now());
        return cotizacionRepository.save(cotizacion);
    }
    
    public Cotizacion aprobarCotizacion(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        cotizacion.setEstado(Cotizacion.EstadoCotizacion.APROBADA);
        cotizacion.setFechaAprobacion(LocalDateTime.now());
        return cotizacionRepository.save(cotizacion);
    }
    
    public List<Cotizacion> findExpiredCotizaciones() {
        return cotizacionRepository.findExpiredCotizaciones(LocalDate.now());
    }
}
