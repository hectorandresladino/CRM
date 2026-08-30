package com.crm.service;

import com.crm.entity.CampoPersonalizado;
import com.crm.repository.CampoPersonalizadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CampoPersonalizadoService {
    private final CampoPersonalizadoRepository repository;

    public List<CampoPersonalizado> findByEntidad(Long tenantId, String entidad) {
        return repository.findByTenantIdAndEntidadOrderByOrden(tenantId, entidad);
    }

    public CampoPersonalizado save(CampoPersonalizado campo) {
        return repository.save(campo);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
