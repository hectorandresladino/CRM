package com.crm.service;

import com.crm.entity.FormularioWeb;
import com.crm.repository.FormularioWebRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FormularioWebService {
    private final FormularioWebRepository repository;

    public List<FormularioWeb> findAll(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    public FormularioWeb save(FormularioWeb form) {
        if (form.getEmbedToken() == null || form.getEmbedToken().isEmpty()) {
            form.setEmbedToken(UUID.randomUUID().toString().replace("-", ""));
        }
        return repository.save(form);
    }

    public FormularioWeb submit(String token) {
        FormularioWeb form = repository.findByEmbedToken(token)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado"));
        form.setTotalEnvios(form.getTotalEnvios() + 1);
        return repository.save(form);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
