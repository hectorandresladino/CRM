/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.WhatsAppAIConfig;
import com.crm.entity.WhatsAppConversation;
import com.crm.repository.WhatsAppAIConfigRepository;
import com.crm.repository.WhatsAppConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class WhatsAppAIService {

    private final WhatsAppConversationRepository conversationRepository;
    private final WhatsAppAIConfigRepository configRepository;

    public WhatsAppAIConfig getConfig(Long tenantId) {
        return configRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    WhatsAppAIConfig config = new WhatsAppAIConfig();
                    config.setTenantId(tenantId);
                    config.setEnabled(true);
                    config.setAutoReply(true);
                    config.setBusinessName("Mi Empresa");
                    config.setWelcomeMessage("Hola! Bienvenido. Soy tu asistente virtual. ¿En qué puedo ayudarte?");
                    config.setFallbackMessage("No estoy seguro de entender. ¿Podrías reformular? También puedes escribir 'agente' para hablar con un humano.");
                    config.setOutOfHoursMessage("Gracias por escribir. Nuestro horario de atención es de 8am a 6pm. Te responderemos pronto.");
                    config.setSystemPrompt("Eres un asistente de ventas profesional. Ayudas a cualificar leads, responder preguntas sobre productos/servicios, y agendar demos. Sé amable, conciso y profesional.");
                    return configRepository.save(config);
                });
    }

    public WhatsAppAIConfig updateConfig(Long tenantId, WhatsAppAIConfig config) {
        WhatsAppAIConfig existing = configRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    WhatsAppAIConfig c = new WhatsAppAIConfig();
                    c.setTenantId(tenantId);
                    return c;
                });
        existing.setEnabled(config.getEnabled());
        existing.setAutoReply(config.getAutoReply());
        existing.setBusinessName(config.getBusinessName());
        existing.setWelcomeMessage(config.getWelcomeMessage());
        existing.setFallbackMessage(config.getFallbackMessage());
        existing.setHoursStart(config.getHoursStart());
        existing.setHoursEnd(config.getHoursEnd());
        existing.setOutOfHoursMessage(config.getOutOfHoursMessage());
        existing.setQualifyLeads(config.getQualifyLeads());
        existing.setTranscribeAudio(config.getTranscribeAudio());
        existing.setLanguage(config.getLanguage());
        existing.setPersonality(config.getPersonality());
        existing.setSystemPrompt(config.getSystemPrompt());
        return configRepository.save(existing);
    }

    public WhatsAppConversation processInboundMessage(Long tenantId, String phone, String contactName, String message, String messageType) {
        WhatsAppAIConfig config = getConfig(tenantId);

        WhatsAppConversation conv = new WhatsAppConversation();
        conv.setTenantId(tenantId);
        conv.setContactPhone(phone);
        conv.setContactName(contactName);
        conv.setDirection("INBOUND");
        conv.setMessage(message);
        conv.setMessageType(messageType != null ? messageType : "TEXT");
        conv.setSentAt(LocalDateTime.now());

        if (!config.getEnabled()) {
            conv.setAiHandled(false);
            conv.setStatus(WhatsAppConversation.ConversationStatus.WAITING_AGENT);
            return conversationRepository.save(conv);
        }

        if (!isWithinBusinessHours(config)) {
            conv.setAiResponse(true);
            conv.setAiIntent("OUT_OF_HOURS");
            conv.setAiConfidence(1.0);
            conv.setAiHandled(true);
            sendAutoReply(tenantId, phone, config.getOutOfHoursMessage());
            conv.setStatus(WhatsAppConversation.ConversationStatus.ACTIVE);
            return conversationRepository.save(conv);
        }

        String intent = detectIntent(message);
        String sentiment = analyzeSentiment(message);
        String reply = generateReply(message, intent, config);

        conv.setAiResponse(true);
        conv.setAiIntent(intent);
        conv.setAiConfidence(intent.equals("GENERAL") ? 0.5 : 0.85);
        conv.setAiHandled(!intent.equals("HUMAN_AGENT"));
        conv.setHumanTakenOver(intent.equals("HUMAN_AGENT"));
        conv.setSentiment(sentiment);

        if (intent.equals("HUMAN_AGENT")) {
            conv.setStatus(WhatsAppConversation.ConversationStatus.WAITING_AGENT);
        } else {
            sendAutoReply(tenantId, phone, reply);
            conv.setStatus(WhatsAppConversation.ConversationStatus.ACTIVE);
        }

        return conversationRepository.save(conv);
    }

    public WhatsAppConversation sendOutboundMessage(Long tenantId, String phone, String message, String assignedAgent) {
        WhatsAppConversation conv = new WhatsAppConversation();
        conv.setTenantId(tenantId);
        conv.setContactPhone(phone);
        conv.setDirection("OUTBOUND");
        conv.setMessage(message);
        conv.setMessageType("TEXT");
        conv.setAiResponse(false);
        conv.setAiHandled(false);
        conv.setAssignedAgent(assignedAgent);
        conv.setStatus(WhatsAppConversation.ConversationStatus.ACTIVE);
        conv.setSentAt(LocalDateTime.now());
        return conversationRepository.save(conv);
    }

    public List<WhatsAppConversation> getConversations(Long tenantId) {
        return conversationRepository.findByTenantIdOrderBySentAtDesc(tenantId);
    }

    public List<WhatsAppConversation> getConversationsByPhone(Long tenantId, String phone) {
        return conversationRepository.findByTenantIdAndContactPhone(tenantId, phone);
    }

    public List<WhatsAppConversation> getWaitingForAgent(Long tenantId) {
        return conversationRepository.findByTenantIdAndStatus(tenantId, WhatsAppConversation.ConversationStatus.WAITING_AGENT);
    }

    public WhatsAppConversation takeOver(Long conversationId, String agentName) {
        WhatsAppConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        conv.setHumanTakenOver(true);
        conv.setAiHandled(false);
        conv.setAssignedAgent(agentName);
        conv.setStatus(WhatsAppConversation.ConversationStatus.ACTIVE);
        return conversationRepository.save(conv);
    }

    public WhatsAppConversation resolve(Long conversationId) {
        WhatsAppConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        conv.setStatus(WhatsAppConversation.ConversationStatus.RESOLVED);
        return conversationRepository.save(conv);
    }

    public Map<String, Object> getStats(Long tenantId) {
        List<WhatsAppConversation> all = conversationRepository.findByTenantIdOrderBySentAtDesc(tenantId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", all.size());
        stats.put("aiHandled", all.stream().filter(c -> Boolean.TRUE.equals(c.getAiHandled())).count());
        stats.put("humanHandled", all.stream().filter(c -> Boolean.TRUE.equals(c.getHumanTakenOver())).count());
        stats.put("waitingAgent", all.stream().filter(c -> c.getStatus() == WhatsAppConversation.ConversationStatus.WAITING_AGENT).count());
        stats.put("resolved", all.stream().filter(c -> c.getStatus() == WhatsAppConversation.ConversationStatus.RESOLVED).count());
        stats.put("uniqueContacts", all.stream().map(WhatsAppConversation::getContactPhone).distinct().count());
        return stats;
    }

    private boolean isWithinBusinessHours(WhatsAppAIConfig config) {
        try {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.parse(config.getHoursStart());
            LocalTime end = LocalTime.parse(config.getHoursEnd());
            return !now.isBefore(start) && !now.isAfter(end);
        } catch (Exception e) {
            return true;
        }
    }

    private String detectIntent(String message) {
        String lower = message.toLowerCase().trim();
        if (lower.contains("agente") || lower.contains("humano") || lower.contains("operador") || lower.contains("persona")) {
            return "HUMAN_AGENT";
        }
        if (lower.contains("precio") || lower.contains("cuesta") || lower.contains("valor") || lower.contains("cuanto") || lower.contains("cotizar") || lower.contains("presupuesto")) {
            return "PRICING_INQUIRY";
        }
        if (lower.contains("hola") || lower.contains("buenos") || lower.contains("buenas") || lower.contains("saludos") || lower.contains("hey") || lower.contains("hi ")) {
            return "GREETING";
        }
        if (lower.contains("info") || lower.contains("información") || lower.contains("detalles") || lower.contains("quiero saber") || lower.contains("características") || lower.contains("features")) {
            return "INFO_REQUEST";
        }
        if (lower.contains("demo") || lower.contains("prueba") || lower.contains("agendar") || lower.contains("cita") || lower.contains("reunión") || lower.contains("calendario")) {
            return "DEMO_REQUEST";
        }
        if (lower.contains("comprar") || lower.contains("adquirir") || lower.contains("contratar") || lower.contains("suscribir") || lower.contains("pagar")) {
            return "PURCHASE_INTENT";
        }
        if (lower.contains("gracias") || lower.contains("perfecto") || lower.contains("excelente") || lower.contains("genial") || lower.contains("muy bueno")) {
            return "SATISFACTION";
        }
        if (lower.contains("problema") || lower.contains("error") || lower.contains("no funciona") || lower.contains("ayuda") || lower.contains("urgente") || lower.contains("falla")) {
            return "SUPPORT_REQUEST";
        }
        if (lower.contains("cancelar") || lower.contains("devolver") || lower.contains("reembolso") || lower.contains("queja")) {
            return "COMPLAINT";
        }
        if (lower.contains("whatsapp") || lower.contains("crm") || lower.contains("software") || lower.contains("sistema")) {
            return "PRODUCT_QUESTION";
        }
        return "GENERAL";
    }

    public String analyzeSentiment(String message) {
        String lower = message.toLowerCase().trim();
        int positive = 0, negative = 0;
        String[] positiveWords = {"bueno", "excelente", "perfecto", "gracias", "genial", "feliz", "satisfecho", "increible", "amazing", "great", "love", "perfect"};
        String[] negativeWords = {"malo", "terrible", "problema", "error", "no funciona", "frustrado", "molesto", "queja", "horrible", "bad", "awful", "hate", "broken"};
        for (String w : positiveWords) if (lower.contains(w)) positive++;
        for (String w : negativeWords) if (lower.contains(w)) negative++;
        if (positive > negative) return "POSITIVE";
        if (negative > positive) return "NEGATIVE";
        return "NEUTRAL";
    }

    private String generateReply(String message, String intent, WhatsAppAIConfig config) {
        switch (intent) {
            case "GREETING":
                return config.getWelcomeMessage();
            case "PRICING_INQUIRY":
                return "Nuestros planes empiezan desde $29/mes (Starter), $79/mes (Business), $199/mes (Enterprise) y $399/mes (Agency), todos con usuarios internos ilimitados. ¿Te gustaría recibir más detalles o agendar una demo?";
            case "INFO_REQUEST":
                return "Con gusto te comparto más información. Nuestro CRM incluye gestión de clientes, ventas, marketing, WhatsApp Business, reportes y más. ¿Qué funcionalidad te interesa más?";
            case "DEMO_REQUEST":
                return "Perfecto! Puedo agendarte una demo. ¿Qué día y hora te funciona mejor? Tenemos disponibilidad de lunes a viernes de 8am a 6pm.";
            case "PURCHASE_INTENT":
                return "Excelente! Para proceder con la compra, necesito algunos datos: nombre de tu empresa, número de usuarios y plan de interés. ¿Comenzamos?";
            case "SUPPORT_REQUEST":
                return "Lamento el inconveniente. ¿Podrías darme más detalles del problema? Si prefieres, puedo conectarte con un agente escribiendo 'agente'.";
            case "COMPLAINT":
                return "Lamento mucho la experiencia. Toma tu número de caso y te conecto con un supervisor. Escribe 'agente' para atención prioritaria.";
            case "PRODUCT_QUESTION":
                return "Nuestro CRM SaaS incluye pipeline de ventas, marketing, WhatsApp Business con IA, CPQ, firma electrónica y más. ¿Quieres una demo personalizada?";
            case "SATISFACTION":
                return "Gracias a ti! Estoy aquí para lo que necesites. ¿Hay algo más en lo que pueda ayudarte?";
            case "HUMAN_AGENT":
                return "Entiendo. Te estoy conectando con un agente humano. Un momento por favor...";
            default:
                return config.getFallbackMessage();
        }
    }

    private void sendAutoReply(Long tenantId, String phone, String message) {
        WhatsAppConversation reply = new WhatsAppConversation();
        reply.setTenantId(tenantId);
        reply.setContactPhone(phone);
        reply.setDirection("OUTBOUND");
        reply.setMessage(message);
        reply.setMessageType("TEXT");
        reply.setAiResponse(true);
        reply.setAiHandled(true);
        reply.setSentAt(LocalDateTime.now());
        reply.setStatus(WhatsAppConversation.ConversationStatus.ACTIVE);
        conversationRepository.save(reply);
    }
}
