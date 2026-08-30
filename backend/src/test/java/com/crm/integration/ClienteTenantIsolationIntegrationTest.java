/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.integration;

import com.crm.entity.Cliente;
import com.crm.repository.ClienteRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
import com.crm.service.ClienteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ClienteService.class)
class ClienteTenantIsolationIntegrationTest {

    @Autowired
    private ClienteService service;

    @Autowired
    private ClienteRepository repository;

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void tenantsCanUseSameEmailButCannotReadUpdateOrDeleteEachOthersRecords() {
        TenantContext.setCurrentTenant(101L);
        Cliente tenantA = service.save(cliente("Ana", "compartido@example.com", "DOC-1"));

        TenantContext.setCurrentTenant(202L);
        Cliente tenantB = service.save(cliente("Beatriz", "compartido@example.com", "DOC-1"));

        assertEquals(1, service.findAll().size());
        assertEquals(tenantB.getId(), service.findAll().get(0).getId());
        assertTrue(service.findById(tenantA.getId()).isEmpty());

        Cliente forgedUpdate = cliente("Intruso", "otro@example.com", "DOC-2");
        assertThrows(TenantAccessDeniedException.class,
                () -> service.update(tenantA.getId(), forgedUpdate));
        assertThrows(TenantAccessDeniedException.class,
                () -> service.delete(tenantA.getId()));

        assertTrue(repository.findByIdAndTenantId(tenantA.getId(), 101L).isPresent());
        assertTrue(repository.findByIdAndTenantId(tenantA.getId(), 202L).isEmpty());
    }

    @Test
    void createIgnoresTenantIdSuppliedByClient() {
        TenantContext.setCurrentTenant(303L);
        Cliente cliente = cliente("Carlos", "carlos@example.com", "DOC-3");
        cliente.setTenantId(999L);

        Cliente saved = service.save(cliente);

        assertEquals(303L, saved.getTenantId());
    }

    private Cliente cliente(String nombre, String email, String identificacion) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido("Prueba");
        cliente.setEmail(email);
        cliente.setIdentificacion(identificacion);
        cliente.setEstado(Cliente.EstadoCliente.ACTIVO);
        return cliente;
    }
}
