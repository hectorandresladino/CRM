package com.crm.config;

import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
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
        starter.setDescription("Plan inicial para pequenas empresas");
        starter.setPriceMonthly(BigDecimal.valueOf(29));
        starter.setPriceYearly(BigDecimal.valueOf(290));
        starter.setCurrency("USD");
        starter.setMaxUsers(3);
        starter.setMaxClients(100);
        starter.setMaxStorageMb(1024L);
        starter.setMaxAutomations(5);
        starter.setHasWhatsapp(false);
        starter.setHasEmailMarketing(true);
        starter.setHasApiAccess(false);
        starter.setHasWhiteLabel(false);
        starter.setHasAiFeatures(false);
        starter.setHasAdvancedReports(false);
        starter.setHasWebhooks(false);
        starter.setTrialDays(14);
        planRepository.save(starter);

        Plan professional = new Plan();
        professional.setName("PROFESSIONAL");
        professional.setDescription("Plan profesional para empresas en crecimiento");
        professional.setPriceMonthly(BigDecimal.valueOf(79));
        professional.setPriceYearly(BigDecimal.valueOf(790));
        professional.setCurrency("USD");
        professional.setMaxUsers(10);
        professional.setMaxClients(1000);
        professional.setMaxStorageMb(10240L);
        professional.setMaxAutomations(50);
        professional.setHasWhatsapp(true);
        professional.setHasEmailMarketing(true);
        professional.setHasApiAccess(true);
        professional.setHasWhiteLabel(false);
        professional.setHasAiFeatures(true);
        professional.setHasAdvancedReports(true);
        professional.setHasWebhooks(true);
        professional.setTrialDays(14);
        planRepository.save(professional);

        Plan enterprise = new Plan();
        enterprise.setName("ENTERPRISE");
        enterprise.setDescription("Plan empresarial con todas las funciones");
        enterprise.setPriceMonthly(BigDecimal.valueOf(199));
        enterprise.setPriceYearly(BigDecimal.valueOf(1990));
        enterprise.setCurrency("USD");
        enterprise.setMaxUsers(50);
        enterprise.setMaxClients(10000);
        enterprise.setMaxStorageMb(102400L);
        enterprise.setMaxAutomations(999);
        enterprise.setHasWhatsapp(true);
        enterprise.setHasEmailMarketing(true);
        enterprise.setHasApiAccess(true);
        enterprise.setHasWhiteLabel(true);
        enterprise.setHasAiFeatures(true);
        enterprise.setHasAdvancedReports(true);
        enterprise.setHasWebhooks(true);
        enterprise.setTrialDays(30);
        planRepository.save(enterprise);

        log.info("Planes creados: STARTER, PROFESSIONAL, ENTERPRISE");
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
