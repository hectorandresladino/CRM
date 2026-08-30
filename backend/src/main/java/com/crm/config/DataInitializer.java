/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.config;

import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final TenantRepository tenantRepository;
    private final UsuarioRepository usuarioRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initPlans();
        initSuperAdmin();
    }

    private void initPlans() {
        if (planRepository.count() > 0) return;

        Plan starter = new Plan();
        starter.setName("STARTER");
        starter.setDescription("Plan inicial para pequenas empresas - usuarios ilimitados");
        starter.setPriceMonthly(BigDecimal.valueOf(29));
        starter.setPriceYearly(BigDecimal.valueOf(290));
        starter.setCurrency("USD");
        starter.setMaxUsers(null);
        starter.setMaxContacts(500);
        starter.setMaxClients(500);
        starter.setMaxStorageMb(2048L);
        starter.setMaxAutomations(10);
        starter.setMaxWhatsappMessages(100);
        starter.setMaxEmails(1000);
        starter.setMaxSms(50);
        starter.setMaxApiCalls(1000);
        starter.setMaxAiPredictions(50);
        starter.setMaxSubAccounts(0);
        starter.setIsAgencyPlan(false);
        starter.setHasWhatsapp(false);
        starter.setHasEmailMarketing(true);
        starter.setHasApiAccess(false);
        starter.setHasWhiteLabel(false);
        starter.setHasAiFeatures(false);
        starter.setHasAdvancedReports(false);
        starter.setHasWebhooks(false);
        starter.setTrialDays(14);
        planRepository.save(starter);

        Plan business = new Plan();
        business.setName("BUSINESS");
        business.setDescription("Plan business para empresas en crecimiento - usuarios ilimitados");
        business.setPriceMonthly(BigDecimal.valueOf(79));
        business.setPriceYearly(BigDecimal.valueOf(790));
        business.setCurrency("USD");
        business.setMaxUsers(null);
        business.setMaxContacts(5000);
        business.setMaxClients(5000);
        business.setMaxStorageMb(20480L);
        business.setMaxAutomations(100);
        business.setMaxWhatsappMessages(1000);
        business.setMaxEmails(10000);
        business.setMaxSms(500);
        business.setMaxApiCalls(10000);
        business.setMaxAiPredictions(500);
        business.setMaxSubAccounts(0);
        business.setIsAgencyPlan(false);
        business.setHasWhatsapp(true);
        business.setHasEmailMarketing(true);
        business.setHasApiAccess(true);
        business.setHasWhiteLabel(false);
        business.setHasAiFeatures(true);
        business.setHasAdvancedReports(true);
        business.setHasWebhooks(true);
        business.setTrialDays(14);
        planRepository.save(business);

        Plan enterprise = new Plan();
        enterprise.setName("ENTERPRISE");
        enterprise.setDescription("Plan empresarial con todas las funciones - usuarios ilimitados");
        enterprise.setPriceMonthly(BigDecimal.valueOf(199));
        enterprise.setPriceYearly(BigDecimal.valueOf(1990));
        enterprise.setCurrency("USD");
        enterprise.setMaxUsers(null);
        enterprise.setMaxContacts(50000);
        enterprise.setMaxClients(50000);
        enterprise.setMaxStorageMb(204800L);
        enterprise.setMaxAutomations(999);
        enterprise.setMaxWhatsappMessages(10000);
        enterprise.setMaxEmails(100000);
        enterprise.setMaxSms(5000);
        enterprise.setMaxApiCalls(100000);
        enterprise.setMaxAiPredictions(5000);
        enterprise.setMaxSubAccounts(0);
        enterprise.setIsAgencyPlan(false);
        enterprise.setHasWhatsapp(true);
        enterprise.setHasEmailMarketing(true);
        enterprise.setHasApiAccess(true);
        enterprise.setHasWhiteLabel(true);
        enterprise.setHasAiFeatures(true);
        enterprise.setHasAdvancedReports(true);
        enterprise.setHasWebhooks(true);
        enterprise.setTrialDays(30);
        planRepository.save(enterprise);

        Plan agency = new Plan();
        agency.setName("AGENCY");
        agency.setDescription("Plan para agencias con subcuentas y white-label - usuarios ilimitados");
        agency.setPriceMonthly(BigDecimal.valueOf(399));
        agency.setPriceYearly(BigDecimal.valueOf(3990));
        agency.setCurrency("USD");
        agency.setMaxUsers(null);
        agency.setMaxContacts(100000);
        agency.setMaxClients(100000);
        agency.setMaxStorageMb(512000L);
        agency.setMaxAutomations(999);
        agency.setMaxWhatsappMessages(50000);
        agency.setMaxEmails(500000);
        agency.setMaxSms(25000);
        agency.setMaxApiCalls(500000);
        agency.setMaxAiPredictions(25000);
        agency.setMaxSubAccounts(50);
        agency.setIsAgencyPlan(true);
        agency.setHasWhatsapp(true);
        agency.setHasEmailMarketing(true);
        agency.setHasApiAccess(true);
        agency.setHasWhiteLabel(true);
        agency.setHasAiFeatures(true);
        agency.setHasAdvancedReports(true);
        agency.setHasWebhooks(true);
        agency.setTrialDays(30);
        planRepository.save(agency);

        log.info("Planes creados: STARTER, BUSINESS, ENTERPRISE, AGENCY");
    }

    private void initSuperAdmin() {
        if (usuarioRepository.existsByUsername("superadmin")) return;

        Usuario superAdmin = new Usuario();
        superAdmin.setUsername("superadmin");
        superAdmin.setPassword(passwordEncoder.encode("SuperAdmin123!"));
        superAdmin.setEmail("superadmin@crm-saas.com");
        superAdmin.setNombre("Super Administrador");
        superAdmin.setRol(Usuario.Role.SUPER_ADMIN);
        superAdmin.setActivo(true);
        superAdmin.setEmailVerified(true);
        usuarioRepository.save(superAdmin);

        log.info("SuperAdmin creado: superadmin / SuperAdmin123!");
    }
}
