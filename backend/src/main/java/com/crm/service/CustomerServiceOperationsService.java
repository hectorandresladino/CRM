/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceOperationsService {

    private final ServicioClienteRepository casoRepo;
    private final CaseCommentRepository commentRepo;
    private final CaseAttachmentRepository attachmentRepo;
    private final KnowledgeArticleRepository knowledgeRepo;
    private final SLAConfiguracionRepository slaRepo;
    private final EntitlementRepository entitlementRepo;
    private final FieldServiceOrderRepository fieldServiceRepo;
    private final ServiceMilestoneRepository milestoneRepo;
    private final EncuestaSatisfaccionRepository surveyRepo;
    private final LiveChatSessionRepository chatSessionRepo;
    private final ChatMessageRepository chatMessageRepo;

    private Long tid() {
        return TenantContext.requireCurrentTenant();
    }

    // === Case Management (Item 25) ===

    public List<ServicioCliente> getCases() { return casoRepo.findByTenantId(tid()); }

    public ServicioCliente createCase(ServicioCliente caso) {
        caso.setTenantId(tid());
        String code = "CASE-" + System.currentTimeMillis();
        caso.setCodigo(code);
        return casoRepo.save(caso);
    }

    public ServicioCliente updateCaseStatus(Long id, ServicioCliente.EstadoServicio newStatus) {
        ServicioCliente caso = findCase(id);
        caso.setEstado(newStatus);
        if (newStatus == ServicioCliente.EstadoServicio.RESUELTO || newStatus == ServicioCliente.EstadoServicio.CERRADO) {
            caso.setFechaCierre(LocalDateTime.now());
        }
        return casoRepo.save(caso);
    }

    public ServicioCliente assignCase(Long id, String assignedTo) {
        ServicioCliente caso = findCase(id);
        caso.setAsignadoA(assignedTo);
        caso.setEstado(ServicioCliente.EstadoServicio.ASIGNADO);
        caso.setFechaAsignacion(LocalDateTime.now());
        return casoRepo.save(caso);
    }

    // === Case Comments (Item 25) ===

    public CaseComment addComment(CaseComment comment) {
        findCase(comment.getCaseId());
        comment.setTenantId(tid());
        return commentRepo.save(comment);
    }

    public List<CaseComment> getComments(Long caseId) {
        findCase(caseId);
        return commentRepo.findByTenantIdAndCaseId(tid(), caseId);
    }

    // === Case Attachments (Item 25) ===

    public CaseAttachment addAttachment(CaseAttachment attachment) {
        findCase(attachment.getCaseId());
        attachment.setTenantId(tid());
        return attachmentRepo.save(attachment);
    }

    public List<CaseAttachment> getAttachments(Long caseId) {
        findCase(caseId);
        return attachmentRepo.findByTenantIdAndCaseId(tid(), caseId);
    }

    // === Knowledge Base (Item 26) ===

    public List<KnowledgeArticle> getArticles() { return knowledgeRepo.findByTenantId(tid()); }

    public KnowledgeArticle createArticle(KnowledgeArticle article) {
        article.setTenantId(tid());
        return knowledgeRepo.save(article);
    }

    public KnowledgeArticle publishArticle(Long id) {
        KnowledgeArticle article = knowledgeRepo.findByTenantIdAndId(tid(), id).orElseThrow();
        article.setStatus(KnowledgeArticle.Status.PUBLISHED.name());
        article.setPublishedAt(LocalDateTime.now());
        return knowledgeRepo.save(article);
    }

    public List<KnowledgeArticle> searchArticles(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return knowledgeRepo.findByTenantId(tid()).stream()
                .filter(a -> "PUBLISHED".equals(a.getStatus()))
                .filter(a -> (a.getTitle() != null && a.getTitle().toLowerCase(Locale.ROOT).contains(normalized))
                        || (a.getSummary() != null && a.getSummary().toLowerCase(Locale.ROOT).contains(normalized))
                        || (a.getCategory() != null && a.getCategory().toLowerCase(Locale.ROOT).contains(normalized)))
                .toList();
    }

    public void incrementViewCount(Long articleId) {
        KnowledgeArticle article = knowledgeRepo.findByTenantIdAndId(tid(), articleId).orElseThrow();
        article.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
        knowledgeRepo.save(article);
    }

    // === SLA Management (Item 27) ===

    public List<SLAConfiguracion> getSLAs() { return slaRepo.findByTenantId(tid()); }

    public SLAConfiguracion createSLA(SLAConfiguracion sla) {
        sla.setTenantId(tid());
        return slaRepo.save(sla);
    }

    public Map<String, Object> checkSLACompliance(Long caseId) {
        ServicioCliente caso = findCase(caseId);
        List<SLAConfiguracion> slas = slaRepo.findByTenantId(tid()).stream()
                .filter(s -> s.getCategoria() == null || s.getCategoria().equalsIgnoreCase(caso.getTipo().name()))
                .filter(s -> s.getPrioridad() == null || s.getPrioridad().equalsIgnoreCase(caso.getPrioridad().name()))
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("caseId", caseId);
        result.put("casePriority", caso.getPrioridad());

        List<Map<String, Object>> compliance = new ArrayList<>();
        for (SLAConfiguracion sla : slas) {
            Map<String, Object> c = new LinkedHashMap<>();
            c.put("slaName", sla.getNombre());
            c.put("responseHours", sla.getTiempoRespuestaHoras());
            c.put("resolutionHours", sla.getTiempoResolucionHoras());

            long responseElapsed = ChronoUnit.HOURS.between(caso.getFechaCreacion(),
                    caso.getFechaRespuesta() != null ? caso.getFechaRespuesta() : LocalDateTime.now());
            long resolutionElapsed = ChronoUnit.HOURS.between(caso.getFechaCreacion(),
                    caso.getFechaCierre() != null ? caso.getFechaCierre() : LocalDateTime.now());

            c.put("responseElapsed", responseElapsed);
            c.put("resolutionElapsed", resolutionElapsed);
            c.put("responseCompliant", sla.getTiempoRespuestaHoras() == null || responseElapsed <= sla.getTiempoRespuestaHoras());
            c.put("resolutionCompliant", sla.getTiempoResolucionHoras() == null || resolutionElapsed <= sla.getTiempoResolucionHoras());
            compliance.add(c);
        }
        result.put("slaCompliance", compliance);
        return result;
    }

    // === Entitlements (Item 28) ===

    public List<Entitlement> getEntitlements() { return entitlementRepo.findByTenantId(tid()); }

    public Entitlement createEntitlement(Entitlement entitlement) {
        entitlement.setTenantId(tid());
        return entitlementRepo.save(entitlement);
    }

    public boolean checkEntitlement(Long clientId) {
        return entitlementRepo.findByTenantId(tid()).stream()
                .anyMatch(e -> e.getClientId().equals(clientId) && "ACTIVE".equals(e.getStatus())
                        && (e.getCasesRemaining() == null || e.getCasesRemaining() > 0));
    }

    // === Field Service (Item 29) ===

    public List<FieldServiceOrder> getFieldServiceOrders() { return fieldServiceRepo.findByTenantId(tid()); }

    public FieldServiceOrder createFieldServiceOrder(FieldServiceOrder order) {
        order.setTenantId(tid());
        return fieldServiceRepo.save(order);
    }

    public FieldServiceOrder updateFieldServiceStatus(Long id, String status) {
        FieldServiceOrder order = fieldServiceRepo.findByTenantIdAndId(tid(), id).orElseThrow();
        order.setStatus(status);
        if ("COMPLETED".equals(status)) {
            order.setCompletedAt(LocalDateTime.now());
        }
        return fieldServiceRepo.save(order);
    }

    public List<ServiceMilestone> getMilestones(Long orderId) {
        return milestoneRepo.findByTenantId(tid()).stream()
                .filter(m -> orderId.equals(m.getCaseId()))
                .toList();
    }

    // === CSAT Surveys (Item 30) ===

    public List<EncuestaSatisfaccion> getSurveys() { return surveyRepo.findByTenantId(tid()); }

    public EncuestaSatisfaccion createSurvey(EncuestaSatisfaccion survey) {
        survey.setTenantId(tid());
        return surveyRepo.save(survey);
    }

    public Map<String, Object> getCSATSummary() {
        List<EncuestaSatisfaccion> surveys = surveyRepo.findByTenantId(tid());
        long total = surveys.size();
        double avgScore = surveys.stream()
                .filter(s -> s.getCalificacionGeneral() != null)
                .mapToInt(EncuestaSatisfaccion::getCalificacionGeneral)
                .average().orElse(0);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalSurveys", total);
        summary.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
        summary.put("csatPercentage", total > 0 ? (surveys.stream().filter(s -> s.getCalificacionGeneral() != null && s.getCalificacionGeneral() >= 4).count() * 100.0 / total) : 0);

        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int score = i;
            distribution.put(score, surveys.stream().filter(s -> s.getCalificacionGeneral() != null && s.getCalificacionGeneral() == score).count());
        }
        summary.put("distribution", distribution);
        return summary;
    }

    // === Live Chat (Item 31) ===

    public LiveChatSession startChatSession(LiveChatSession session) {
        session.setTenantId(tid());
        session.setStatus(LiveChatSession.ChatStatus.WAITING);
        return chatSessionRepo.save(session);
    }

    public LiveChatSession pickUpChat(Long sessionId, Long agentId, String agentName) {
        LiveChatSession session = findChat(sessionId);
        session.setAssignedAgentId(agentId);
        session.setAssignedAgentName(agentName);
        session.setStatus(LiveChatSession.ChatStatus.ACTIVE);
        session.setPickedUpAt(LocalDateTime.now());
        if (session.getStartedAt() != null) {
            session.setWaitTimeSeconds((int) ChronoUnit.SECONDS.between(session.getStartedAt(), session.getPickedUpAt()));
        }
        return chatSessionRepo.save(session);
    }

    public LiveChatSession endChat(Long sessionId, Integer satisfactionScore) {
        LiveChatSession session = findChat(sessionId);
        session.setStatus(LiveChatSession.ChatStatus.ENDED);
        session.setEndedAt(LocalDateTime.now());
        session.setSatisfactionScore(satisfactionScore);
        if (session.getPickedUpAt() != null) {
            session.setDurationSeconds((int) ChronoUnit.SECONDS.between(session.getPickedUpAt(), session.getEndedAt()));
        }
        List<ChatMessage> msgs = chatMessageRepo.findByTenantIdAndSessionId(tid(), sessionId);
        StringBuilder transcript = new StringBuilder();
        for (ChatMessage m : msgs) {
            transcript.append("[").append(m.getSentAt()).append("] ")
                    .append(m.getSenderType()).append(": ").append(m.getContent()).append("\n");
        }
        session.setTranscript(transcript.toString());
        return chatSessionRepo.save(session);
    }

    public ChatMessage sendChatMessage(ChatMessage message) {
        findChat(message.getSessionId());
        message.setTenantId(tid());
        return chatMessageRepo.save(message);
    }

    public List<ChatMessage> getChatMessages(Long sessionId) {
        findChat(sessionId);
        return chatMessageRepo.findByTenantIdAndSessionId(tid(), sessionId);
    }

    public List<LiveChatSession> getWaitingChats() {
        return chatSessionRepo.findByTenantIdAndStatus(tid(), LiveChatSession.ChatStatus.WAITING);
    }

    public List<LiveChatSession> getActiveChats() {
        return chatSessionRepo.findByTenantIdAndStatus(tid(), LiveChatSession.ChatStatus.ACTIVE);
    }

    // === Service Console / Dashboard (Item 32) ===

    public Map<String, Object> getServiceConsole() {
        List<ServicioCliente> cases = casoRepo.findByTenantId(tid());
        Map<String, Object> console = new LinkedHashMap<>();

        console.put("totalCases", cases.size());
        console.put("openCases", cases.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.ABIERTO).count());
        console.put("assignedCases", cases.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.ASIGNADO).count());
        console.put("inProgress", cases.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.EN_PROCESO).count());
        console.put("resolved", cases.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.RESUELTO).count());
        console.put("closed", cases.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.CERRADO).count());

        console.put("urgentCases", cases.stream().filter(c -> c.getPrioridad() == ServicioCliente.PrioridadPQRS.URGENTE
                || c.getPrioridad() == ServicioCliente.PrioridadPQRS.CRITICA).count());

        console.put("waitingChats", getWaitingChats().size());
        console.put("activeChats", getActiveChats().size());

        console.put("knowledgeArticles", knowledgeRepo.findByTenantId(tid()).stream()
                .filter(a -> "PUBLISHED".equals(a.getStatus())).count());

        console.put("fieldServiceOpen", fieldServiceRepo.findByTenantId(tid()).stream()
                .filter(f -> !"COMPLETED".equals(f.getStatus()) && !"CANCELLED".equals(f.getStatus())).count());

        return console;
    }

    // === Case Escalation (Item 33) ===

    public List<ServicioCliente> getEscalatableCases() {
        List<ServicioCliente> cases = casoRepo.findByTenantId(tid());
        List<ServicioCliente> escalatable = new ArrayList<>();
        for (ServicioCliente c : cases) {
            if (c.getEstado() != ServicioCliente.EstadoServicio.CERRADO
                    && c.getEstado() != ServicioCliente.EstadoServicio.RESUELTO) {
                List<SLAConfiguracion> slas = slaRepo.findByTenantId(tid()).stream()
                        .filter(s -> s.getCategoria() == null || s.getCategoria().equalsIgnoreCase(c.getTipo().name()))
                        .filter(s -> s.getPrioridad() == null || s.getPrioridad().equalsIgnoreCase(c.getPrioridad().name()))
                        .toList();
                for (SLAConfiguracion sla : slas) {
                    if (sla.getHorasDesdeEscalar() != null) {
                        long elapsed = ChronoUnit.HOURS.between(c.getFechaCreacion(), LocalDateTime.now());
                        if (elapsed > sla.getHorasDesdeEscalar()) {
                            escalatable.add(c);
                            break;
                        }
                    }
                }
            }
        }
        return escalatable;
    }

    public ServicioCliente escalateCase(Long caseId, String escalatedTo) {
        ServicioCliente caso = findCase(caseId);
        caso.setAsignadoA(escalatedTo);
        caso.setEstado(ServicioCliente.EstadoServicio.EN_PROCESO);
        caso.setNotas((caso.getNotas() != null ? caso.getNotas() + "\n" : "") + "[ESCALATED to " + escalatedTo + " at " + LocalDateTime.now() + "]");
        return casoRepo.save(caso);
    }

    // === Omnichannel (Item 34) ===

    public Map<String, Object> getOmnichannelStats() {
        List<ServicioCliente> cases = casoRepo.findByTenantId(tid());
        Map<String, Object> stats = new LinkedHashMap<>();

        Map<String, Long> byChannel = new LinkedHashMap<>();
        for (ServicioCliente.CanalPQRS channel : ServicioCliente.CanalPQRS.values()) {
            byChannel.put(channel.name(), cases.stream().filter(c -> c.getCanal() == channel).count());
        }
        stats.put("casesByChannel", byChannel);

        Map<String, Long> byType = new LinkedHashMap<>();
        for (ServicioCliente.TipoPQRS type : ServicioCliente.TipoPQRS.values()) {
            byType.put(type.name(), cases.stream().filter(c -> c.getTipo() == type).count());
        }
        stats.put("casesByType", byType);

        stats.put("totalChats", chatSessionRepo.findByTenantId(tid()).size());
        stats.put("activeChats", getActiveChats().size());
        stats.put("abandonedChats", chatSessionRepo.findByTenantIdAndStatus(tid(), LiveChatSession.ChatStatus.ABANDONED).size());

        double avgWaitTime = chatSessionRepo.findByTenantId(tid()).stream()
                .filter(s -> s.getWaitTimeSeconds() != null)
                .mapToInt(LiveChatSession::getWaitTimeSeconds)
                .average().orElse(0);
        stats.put("avgWaitTimeSeconds", Math.round(avgWaitTime));

        double avgChatDuration = chatSessionRepo.findByTenantId(tid()).stream()
                .filter(s -> s.getDurationSeconds() != null)
                .mapToInt(LiveChatSession::getDurationSeconds)
                .average().orElse(0);
        stats.put("avgChatDurationSeconds", Math.round(avgChatDuration));

        return stats;
    }

    private ServicioCliente findCase(Long id) {
        return casoRepo.findByIdAndTenantId(id, tid())
                .orElseThrow(() -> new RuntimeException("Caso no encontrado"));
    }

    private LiveChatSession findChat(Long id) {
        return chatSessionRepo.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Sesion de chat no encontrada"));
    }
}
