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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalesCloudAdvancedService {

    private final AccountRepository accountRepo;
    private final ContactRepository contactRepo;
    private final OpportunityCompetitorRepository competitorRepo;
    private final CalendarEventRepository calendarRepo;
    private final BookingPageRepository bookingRepo;
    private final EmailSyncLogRepository emailSyncRepo;
    private final VentaRepository ventaRepo;
    private final ClienteRepository clienteRepo;
    private final CotizacionRepository cotizacionRepo;
    private final PedidoRepository pedidoRepo;
    private final ServicioClienteRepository servicioRepo;
    private final ContratoRepository contratoRepo;
    private final FacturaRepository facturaRepo;
    private final SalesForecastRepository forecastRepo;
    private final TerritoryRepository territoryRepo;
    private final CommissionRepository commissionRepo;
    private final MetaComercialRepository metaRepo;
    private final SalesSequenceRepository sequenceRepo;
    private final AccountTeamRepository accountTeamRepo;
    private final OpportunitySplitRepository splitRepo;

    private Long tid() {
        Long t = TenantContext.getCurrentTenant();
        if (t == null) throw new RuntimeException("No tenant context");
        return t;
    }

    // === Account Management (Item 15) ===

    public Account createAccount(Account account) {
        account.setTenantId(tid());
        return accountRepo.save(account);
    }

    public List<Account> getAccounts() { return accountRepo.findByTenantId(tid()); }

    public List<Account> getAccountHierarchy(Long parentId) {
        return accountRepo.findByTenantIdAndParentAccountId(tid(), parentId);
    }

    public Account updateAccount(Long id, Account updated) {
        Account acc = accountRepo.findById(id).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        acc.setName(updated.getName());
        acc.setIndustry(updated.getIndustry());
        acc.setWebsite(updated.getWebsite());
        acc.setPhone(updated.getPhone());
        acc.setAnnualRevenue(updated.getAnnualRevenue());
        acc.setEmployeeCount(updated.getEmployeeCount());
        acc.setAccountType(updated.getAccountType());
        acc.setDescription(updated.getDescription());
        acc.setStatus(updated.getStatus());
        return accountRepo.save(acc);
    }

    // === Contact Management (Item 15) ===

    public Contact createContact(Contact contact) {
        contact.setTenantId(tid());
        return contactRepo.save(contact);
    }

    public List<Contact> getContacts() { return contactRepo.findByTenantId(tid()); }

    public List<Contact> getContactsByAccount(Long accountId) {
        return contactRepo.findByTenantIdAndAccountId(tid(), accountId);
    }

    // === Customer 360 (Item 16) ===

    public Map<String, Object> getCustomer360(Long clienteId) {
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Map<String, Object> view = new LinkedHashMap<>();
        view.put("cliente", cliente);
        view.put("ventas", ventaRepo.findByCliente(cliente));
        view.put("cotizaciones", cotizacionRepo.findByCliente(cliente));
        view.put("pedidos", pedidoRepo.findByCliente(cliente));
        view.put("servicios", servicioRepo.findByCliente(cliente));
        view.put("contratos", contratoRepo.findByCliente(cliente));
        view.put("facturas", facturaRepo.findByCliente(cliente));

        List<CalendarEvent> meetings = calendarRepo.findByTenantId(tid()).stream()
                .filter(e -> clienteId.equals(e.getContactId()) || clienteId.equals(e.getAccountId()))
                .toList();
        view.put("meetings", meetings);

        List<EmailSyncLog> emails = emailSyncRepo.findByTenantIdAndContactId(tid(), clienteId);
        view.put("emails", emails);

        return view;
    }

    public Map<String, Object> getAccount360(Long accountId) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Map<String, Object> view = new LinkedHashMap<>();
        view.put("account", account);
        view.put("contacts", contactRepo.findByTenantIdAndAccountId(tid(), accountId));
        view.put("team", accountTeamRepo.findByTenantId(tid()).stream()
                .filter(t -> accountId.equals(t.getAccountId()))
                .toList());
        view.put("meetings", calendarRepo.findByTenantId(tid()).stream()
                .filter(e -> accountId.equals(e.getAccountId()))
                .toList());
        view.put("emails", emailSyncRepo.findByTenantIdAndAccountId(tid(), accountId));
        view.put("territory", account.getTerritoryId() != null ?
                territoryRepo.findById(account.getTerritoryId()).orElse(null) : null);
        return view;
    }

    // === Opportunity Competitors (Item 17) ===

    public OpportunityCompetitor addCompetitor(OpportunityCompetitor competitor) {
        competitor.setTenantId(tid());
        return competitorRepo.save(competitor);
    }

    public List<OpportunityCompetitor> getCompetitors(Long opportunityId) {
        return competitorRepo.findByTenantIdAndOpportunityId(tid(), opportunityId);
    }

    // === Forecasting (Item 18) ===

    public List<SalesForecast> getForecasts() { return forecastRepo.findByTenantId(tid()); }

    public SalesForecast createForecast(SalesForecast forecast) {
        forecast.setTenantId(tid());
        return forecastRepo.save(forecast);
    }

    public Map<String, Object> getForecastSummary(String periodType, Integer year) {
        List<SalesForecast> forecasts = forecastRepo.findByTenantId(tid()).stream()
                .filter(f -> periodType == null || periodType.equalsIgnoreCase(f.getPeriodType()))
                .filter(f -> year == null || year.equals(f.getPeriodYear()))
                .toList();

        java.math.BigDecimal totalForecast = forecasts.stream()
                .map(SalesForecast::getForecastAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal totalClosed = forecasts.stream()
                .map(SalesForecast::getClosedAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalForecast", totalForecast);
        summary.put("totalClosed", totalClosed);
        summary.put("attainment", totalForecast.compareTo(java.math.BigDecimal.ZERO) > 0 ?
                totalClosed.divide(totalForecast, 4, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
        summary.put("count", forecasts.size());
        summary.put("forecasts", forecasts);
        return summary;
    }

    // === Territory Management (Item 19) ===

    public List<Territory> getTerritories() { return territoryRepo.findByTenantId(tid()); }

    public Territory createTerritory(Territory territory) {
        territory.setTenantId(tid());
        return territoryRepo.save(territory);
    }

    // === Goals & Quotas (Item 20) ===

    public List<MetaComercial> getGoals() { return metaRepo.findByTenantId(tid()); }

    public MetaComercial createGoal(MetaComercial goal) {
        goal.setTenantId(tid());
        return metaRepo.save(goal);
    }

    public Map<String, Object> getGoalAttainment(Integer year) {
        List<MetaComercial> goals = metaRepo.findByTenantId(tid()).stream()
                .filter(g -> year == null || year.equals(g.getAnio()))
                .toList();

        List<Map<String, Object>> results = new ArrayList<>();
        for (MetaComercial g : goals) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("id", g.getId());
            r.put("usuario", g.getUsuario());
            r.put("anio", g.getAnio());
            r.put("mes", g.getMes());
            r.put("meta", g.getMeta());
            r.put("realizado", g.getRealizado());
            r.put("cumplimiento", g.getMeta() != null && g.getMeta().compareTo(java.math.BigDecimal.ZERO) > 0 ?
                    g.getRealizado().divide(g.getMeta(), 4, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
            results.add(r);
        }
        return Map.of("goals", results, "count", results.size());
    }

    // === Commissions (Item 21) ===

    public List<Commission> getCommissions() { return commissionRepo.findByTenantId(tid()); }

    public Commission createCommission(Commission commission) {
        commission.setTenantId(tid());
        return commissionRepo.save(commission);
    }

    // === Sales Sequences (Item 22) ===

    public List<SalesSequence> getSequences() { return sequenceRepo.findByTenantId(tid()); }

    public SalesSequence createSequence(SalesSequence sequence) {
        sequence.setTenantId(tid());
        return sequenceRepo.save(sequence);
    }

    // === Calendar & Booking (Item 23) ===

    public CalendarEvent createEvent(CalendarEvent event) {
        event.setTenantId(tid());
        return calendarRepo.save(event);
    }

    public List<CalendarEvent> getEvents(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return calendarRepo.findByTenantIdAndStartTimeBetween(tid(), start, end);
        }
        return calendarRepo.findByTenantId(tid());
    }

    public BookingPage createBookingPage(BookingPage page) {
        page.setTenantId(tid());
        return bookingRepo.save(page);
    }

    public BookingPage getBookingPage(String slug) {
        return bookingRepo.findBySlug(slug).orElseThrow(() -> new RuntimeException("Pagina no encontrada"));
    }

    public List<BookingPage> getBookingPages() { return bookingRepo.findByTenantId(tid()); }

    public CalendarEvent bookMeeting(String slug, LocalDateTime startTime, String attendeeName, String attendeeEmail) {
        BookingPage page = getBookingPage(slug);
        CalendarEvent event = new CalendarEvent();
        event.setTenantId(tid());
        event.setOwnerId(page.getOwnerId());
        event.setTitle("Reunion con " + attendeeName);
        event.setStartTime(startTime);
        event.setEndTime(startTime.plusMinutes(page.getDurationMinutes()));
        event.setMeetingType("BOOKED");
        event.setAttendeeEmails(attendeeEmail);
        event.setStatus(CalendarEvent.EventStatus.SCHEDULED);
        return calendarRepo.save(event);
    }

    // === Email Sync (Item 24) ===

    public EmailSyncLog logEmail(EmailSyncLog logEntry) {
        logEntry.setTenantId(tid());
        return emailSyncRepo.save(logEntry);
    }

    public List<EmailSyncLog> getEmails() { return emailSyncRepo.findByTenantId(tid()); }

    public List<EmailSyncLog> getEmailsByContact(Long contactId) {
        return emailSyncRepo.findByTenantIdAndContactId(tid(), contactId);
    }

    public Map<String, Object> getEmailStats() {
        List<EmailSyncLog> all = emailSyncRepo.findByTenantId(tid());
        long total = all.size();
        long opened = all.stream().filter(e -> Boolean.TRUE.equals(e.getOpenedAt() != null)).count();
        long clicked = all.stream().filter(e -> Boolean.TRUE.equals(e.getClickedAt() != null)).count();
        long replied = all.stream().filter(e -> Boolean.TRUE.equals(e.getIsReplied())).count();
        long incoming = all.stream().filter(e -> Boolean.TRUE.equals(e.getIsIncoming())).count();
        long outgoing = total - incoming;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total);
        stats.put("opened", opened);
        stats.put("clicked", clicked);
        stats.put("replied", replied);
        stats.put("incoming", incoming);
        stats.put("outgoing", outgoing);
        stats.put("openRate", total > 0 ? (double) opened / total : 0);
        stats.put("clickRate", total > 0 ? (double) clicked / total : 0);
        stats.put("replyRate", total > 0 ? (double) replied / total : 0);
        return stats;
    }
}
