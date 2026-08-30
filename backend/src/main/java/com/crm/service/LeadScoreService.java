/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.LeadScore;
import com.crm.entity.Prospecto;
import com.crm.repository.LeadScoreRepository;
import com.crm.repository.ProspectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeadScoreService {

    private final LeadScoreRepository leadScoreRepository;
    private final ProspectoRepository prospectoRepository;

    public List<LeadScore> findAll(Long tenantId) {
        return leadScoreRepository.findByTenantIdOrderByScoreDesc(tenantId);
    }

    public LeadScore scoreProspecto(Long tenantId, Long prospectoId) {
        Prospecto prospecto = prospectoRepository.findById(prospectoId)
                .orElseThrow(() -> new RuntimeException("Prospecto no encontrado"));

        int score = 0;
        StringBuilder factors = new StringBuilder();

        if (prospecto.getEmail() != null && !prospecto.getEmail().isEmpty()) {
            score += 10;
            factors.append("Email verificado (+10); ");
        }
        if (prospecto.getTelefono() != null || prospecto.getCelular() != null) {
            score += 10;
            factors.append("TelÃ©fono (+10); ");
        }
        if (prospecto.getEmpresa() != null && !prospecto.getEmpresa().isEmpty()) {
            score += 15;
            factors.append("Empresa B2B (+15); ");
        }
        if (prospecto.getCargo() != null && !prospecto.getCargo().isEmpty()) {
            score += 10;
            factors.append("Cargo definido (+10); ");
        }
        if (prospecto.getSector() != null && !prospecto.getSector().isEmpty()) {
            score += 5;
            factors.append("Sector definido (+5); ");
        }
        if (prospecto.getEstado() == Prospecto.EstadoProspecto.CALIFICADO) {
            score += 20;
            factors.append("Calificado (+20); ");
        }
        if (prospecto.getEstado() == Prospecto.EstadoProspecto.NEGOCIACION) {
            score += 30;
            factors.append("En negociaciÃ³n (+30); ");
        }
        if (prospecto.getEstado() == Prospecto.EstadoProspecto.PROPUESTA) {
            score += 25;
            factors.append("Propuesta enviada (+25); ");
        }
        if (prospecto.getPrioridad() == Prospecto.PrioridadProspecto.ALTA) {
            score += 15;
            factors.append("Prioridad alta (+15); ");
        }
        if (prospecto.getPrioridad() == Prospecto.PrioridadProspecto.URGENTE) {
            score += 25;
            factors.append("Prioridad urgente (+25); ");
        }

        score = Math.min(score, 100);
        String grade = score >= 80 ? "A" : score >= 60 ? "B" : score >= 40 ? "C" : score >= 20 ? "D" : "F";

        LeadScore leadScore = leadScoreRepository
                .findByTenantIdAndProspectoId(tenantId, prospectoId)
                .orElse(new LeadScore());
        leadScore.setTenantId(tenantId);
        leadScore.setProspectoId(prospectoId);
        leadScore.setScore(score);
        leadScore.setGrade(grade);
        leadScore.setFactors(factors.toString());

        return leadScoreRepository.save(leadScore);
    }

    public void delete(Long id) {
        leadScoreRepository.deleteById(id);
    }
}
