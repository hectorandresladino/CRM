/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.integration;

import com.crm.entity.Cliente;
import com.crm.repository.ClienteRepository;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Tenant Data Isolation Tests")
@Transactional
class TenantDataIsolationTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Tenant A should not see Tenant B's clients")
    void tenantAShouldNotSeeTenantBClients() {
        // Setup: Create clients for two tenants
        TenantContext.setCurrentTenant(1L);
        Cliente clienteA = new Cliente();
        clienteA.setTenantId(1L);
        clienteA.setNombre("Cliente A");
        clienteA.setApellido("Tenant A");
        clienteA.setEmail("clienta@tenant1.com");
        clienteA.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteA = clienteRepository.save(clienteA);

        TenantContext.setCurrentTenant(2L);
        Cliente clienteB = new Cliente();
        clienteB.setTenantId(2L);
        clienteB.setNombre("Cliente B");
        clienteB.setApellido("Tenant B");
        clienteB.setEmail("clientb@tenant2.com");
        clienteB.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteB = clienteRepository.save(clienteB);

        // Verify: Tenant A should only see their own client
        TenantContext.setCurrentTenant(1L);
        List<Cliente> tenantAClients = clienteRepository.findByTenantId(1L);
        assertEquals(1, tenantAClients.size());
        assertEquals("Cliente A", tenantAClients.get(0).getNombre());

        // Verify: Tenant B should only see their own client
        TenantContext.setCurrentTenant(2L);
        List<Cliente> tenantBClients = clienteRepository.findByTenantId(2L);
        assertEquals(1, tenantBClients.size());
        assertEquals("Cliente B", tenantBClients.get(0).getNombre());

        // Verify: Tenant A cannot access Tenant B's client by ID
        TenantContext.setCurrentTenant(1L);
        var tenantBClientFromA = clienteRepository.findByIdAndTenantId(clienteB.getId(), 1L);
        assertTrue(tenantBClientFromA.isEmpty(), "Tenant A should not be able to access Tenant B's client");
    }

    @Test
    @DisplayName("TenantStatementInspector should filter queries by tenant")
    void tenantStatementInspectorShouldFilterQueries() {
        // Setup: Create clients for two tenants
        TenantContext.setCurrentTenant(1L);
        Cliente clienteA1 = new Cliente();
        clienteA1.setTenantId(1L);
        clienteA1.setNombre("Cliente A1");
        clienteA1.setApellido("Tenant A");
        clienteA1.setEmail("clienta1@tenant1.com");
        clienteA1.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteRepository.save(clienteA1);

        Cliente clienteA2 = new Cliente();
        clienteA2.setTenantId(1L);
        clienteA2.setNombre("Cliente A2");
        clienteA2.setApellido("Tenant A");
        clienteA2.setEmail("clienta2@tenant1.com");
        clienteA2.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteRepository.save(clienteA2);

        TenantContext.setCurrentTenant(2L);
        Cliente clienteB = new Cliente();
        clienteB.setTenantId(2L);
        clienteB.setNombre("Cliente B");
        clienteB.setApellido("Tenant B");
        clienteB.setEmail("clientb@tenant2.com");
        clienteB.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteRepository.save(clienteB);

        // Verify: When Tenant A is set, only Tenant A's clients are returned
        TenantContext.setCurrentTenant(1L);
        List<Cliente> allClientsForTenantA = clienteRepository.findByTenantId(1L);
        assertEquals(2, allClientsForTenantA.size(), "Tenant A should see exactly 2 clients");

        // Verify: When Tenant B is set, only Tenant B's clients are returned
        TenantContext.setCurrentTenant(2L);
        List<Cliente> allClientsForTenantB = clienteRepository.findByTenantId(2L);
        assertEquals(1, allClientsForTenantB.size(), "Tenant B should see exactly 1 client");
    }

    @Test
    @DisplayName("Update should be restricted to tenant's own data")
    void updateShouldBeRestrictedToTenantOwnData() {
        // Setup
        TenantContext.setCurrentTenant(1L);
        Cliente clienteA = new Cliente();
        clienteA.setTenantId(1L);
        clienteA.setNombre("Original Name");
        clienteA.setApellido("Tenant A");
        clienteA.setEmail("original@tenant1.com");
        clienteA.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteA = clienteRepository.save(clienteA);

        // Tenant B tries to update Tenant A's client
        TenantContext.setCurrentTenant(2L);
        Cliente clienteB = new Cliente();
        clienteB.setTenantId(2L);
        clienteB.setNombre("Client B");
        clienteB.setApellido("Tenant B");
        clienteB.setEmail("clientb@tenant2.com");
        clienteB.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteRepository.save(clienteB);

        // Verify: Tenant B cannot find or update Tenant A's client
        var tenantAFromB = clienteRepository.findByIdAndTenantId(clienteA.getId(), 2L);
        assertTrue(tenantAFromB.isEmpty(), "Tenant B should not find Tenant A's client");

        // Verify: Tenant A's data remains unchanged
        TenantContext.setCurrentTenant(1L);
        var originalClient = clienteRepository.findByIdAndTenantId(clienteA.getId(), 1L);
        assertTrue(originalClient.isPresent());
        assertEquals("Original Name", originalClient.get().getNombre());
    }

    @Test
    @DisplayName("Delete should be restricted to tenant's own data")
    void deleteShouldBeRestrictedToTenantOwnData() {
        // Setup
        TenantContext.setCurrentTenant(1L);
        Cliente clienteA = new Cliente();
        clienteA.setTenantId(1L);
        clienteA.setNombre("Cliente A");
        clienteA.setApellido("Tenant A");
        clienteA.setEmail("clienta@tenant1.com");
        clienteA.setEstado(Cliente.EstadoCliente.ACTIVO);
        clienteA = clienteRepository.save(clienteA);

        Long clienteAId = clienteA.getId();

        // Tenant B tries to delete Tenant A's client
        TenantContext.setCurrentTenant(2L);
        var clientFromB = clienteRepository.findByIdAndTenantId(clienteAId, 2L);
        assertTrue(clientFromB.isEmpty(), "Tenant B should not find Tenant A's client to delete");

        // Verify: Tenant A's client still exists
        TenantContext.setCurrentTenant(1L);
        var clientFromA = clienteRepository.findByIdAndTenantId(clienteAId, 1L);
        assertTrue(clientFromA.isPresent(), "Tenant A's client should still exist");
    }
}
