/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.EncuestaSatisfaccion;
import com.crm.repository.EncuestaSatisfaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EncuestaSatisfaccionService {
    
    private final EncuestaSatisfaccionRepository encuestaRepository;
    
    public List<EncuestaSatisfaccion> findAll() {
        return encuestaRepository.findAll();
    }
    
    public Optional<EncuestaSatisfaccion> findById(Long id) {
        return encuestaRepository.findById(id);
    }
    
    public EncuestaSatisfaccion save(EncuestaSatisfaccion encuesta) {
        return encuestaRepository.save(encuesta);
    }
    
    public EncuestaSatisfaccion update(Long id, EncuestaSatisfaccion encuesta) {
        encuesta.setId(id);
        return encuestaRepository.save(encuesta);
    }
    
    public void delete(Long id) {
        encuestaRepository.deleteById(id);
    }
    
    public EncuestaSatisfaccion registrarRespuesta(Long id, EncuestaSatisfaccion respuesta) {
        Optional<EncuestaSatisfaccion> encuestaOpt = encuestaRepository.findById(id);
        if (encuestaOpt.isPresent()) {
            EncuestaSatisfaccion encuesta = encuestaOpt.get();
            encuesta.setFechaRespuesta(LocalDateTime.now());
            encuesta.setEstado("RESPONDIDA");
            encuesta.setCalificacionGeneral(respuesta.getCalificacionGeneral());
            encuesta.setComentarios(respuesta.getComentarios());
            encuesta.setPregunta1(respuesta.getPregunta1());
            encuesta.setPregunta2(respuesta.getPregunta2());
            encuesta.setPregunta3(respuesta.getPregunta3());
            encuesta.setPregunta4(respuesta.getPregunta4());
            encuesta.setPregunta5(respuesta.getPregunta5());
            encuesta.setRecomendaria(respuesta.getRecomendaria());
            return encuestaRepository.save(encuesta);
        }
        throw new RuntimeException("Encuesta no encontrada");
    }
    
    public List<EncuestaSatisfaccion> findByClienteId(Long clienteId) {
        return encuestaRepository.findByClienteId(clienteId);
    }
    
    public List<EncuestaSatisfaccion> findByEstado(String estado) {
        return encuestaRepository.findByEstado(estado);
    }
}
