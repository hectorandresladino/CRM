/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.SalesOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales-advanced")
@RequiredArgsConstructor
public class SalesOperationsController {

    private final SalesOperationsService service;

    // === Accounts (Item 15) ===
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() { return ResponseEntity.ok(service.getAccounts()); }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) { return ResponseEntity.ok(service.createAccount(account)); }

    @PutMapping("/accounts/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) { return ResponseEntity.ok(service.updateAccount(id, account)); }

    @GetMapping("/accounts/{parentId}/hierarchy")
    public ResponseEntity<List<Account>> getHierarchy(@PathVariable Long parentId) { return ResponseEntity.ok(service.getAccountHierarchy(parentId)); }

    @GetMapping("/accounts/{accountId}/360")
    public ResponseEntity<Map<String, Object>> getAccount360(@PathVariable Long accountId) { return ResponseEntity.ok(service.getAccount360(accountId)); }

    // === Contacts (Item 15) ===
    @GetMapping("/contacts")
    public ResponseEntity<List<Contact>> getContacts() { return ResponseEntity.ok(service.getContacts()); }

    @PostMapping("/contacts")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) { return ResponseEntity.ok(service.createContact(contact)); }

    @GetMapping("/contacts/account/{accountId}")
    public ResponseEntity<List<Contact>> getContactsByAccount(@PathVariable Long accountId) { return ResponseEntity.ok(service.getContactsByAccount(accountId)); }

    // === Customer 360 (Item 16) ===
    @GetMapping("/customer360/{clienteId}")
    public ResponseEntity<Map<String, Object>> getCustomer360(@PathVariable Long clienteId) { return ResponseEntity.ok(service.getCustomer360(clienteId)); }

    // === Competitors (Item 17) ===
    @PostMapping("/competitors")
    public ResponseEntity<OpportunityCompetitor> addCompetitor(@RequestBody OpportunityCompetitor competitor) { return ResponseEntity.ok(service.addCompetitor(competitor)); }

    @GetMapping("/competitors/{opportunityId}")
    public ResponseEntity<List<OpportunityCompetitor>> getCompetitors(@PathVariable Long opportunityId) { return ResponseEntity.ok(service.getCompetitors(opportunityId)); }

    // === Forecasting (Item 18) ===
    @GetMapping("/forecasts")
    public ResponseEntity<List<SalesForecast>> getForecasts() { return ResponseEntity.ok(service.getForecasts()); }

    @PostMapping("/forecasts")
    public ResponseEntity<SalesForecast> createForecast(@RequestBody SalesForecast forecast) { return ResponseEntity.ok(service.createForecast(forecast)); }

    @GetMapping("/forecasts/summary")
    public ResponseEntity<Map<String, Object>> getForecastSummary(@RequestParam(required = false) String periodType, @RequestParam(required = false) Integer year) { return ResponseEntity.ok(service.getForecastSummary(periodType, year)); }

    // === Territories (Item 19) ===
    @GetMapping("/territories")
    public ResponseEntity<List<Territory>> getTerritories() { return ResponseEntity.ok(service.getTerritories()); }

    @PostMapping("/territories")
    public ResponseEntity<Territory> createTerritory(@RequestBody Territory territory) { return ResponseEntity.ok(service.createTerritory(territory)); }

    // === Goals (Item 20) ===
    @GetMapping("/goals")
    public ResponseEntity<List<MetaComercial>> getGoals() { return ResponseEntity.ok(service.getGoals()); }

    @PostMapping("/goals")
    public ResponseEntity<MetaComercial> createGoal(@RequestBody MetaComercial goal) { return ResponseEntity.ok(service.createGoal(goal)); }

    @GetMapping("/goals/attainment")
    public ResponseEntity<Map<String, Object>> getGoalAttainment(@RequestParam(required = false) Integer year) { return ResponseEntity.ok(service.getGoalAttainment(year)); }

    // === Commissions (Item 21) ===
    @GetMapping("/commissions")
    public ResponseEntity<List<Commission>> getCommissions() { return ResponseEntity.ok(service.getCommissions()); }

    @PostMapping("/commissions")
    public ResponseEntity<Commission> createCommission(@RequestBody Commission commission) { return ResponseEntity.ok(service.createCommission(commission)); }

    // === Sales Sequences (Item 22) ===
    @GetMapping("/sequences")
    public ResponseEntity<List<SalesSequence>> getSequences() { return ResponseEntity.ok(service.getSequences()); }

    @PostMapping("/sequences")
    public ResponseEntity<SalesSequence> createSequence(@RequestBody SalesSequence sequence) { return ResponseEntity.ok(service.createSequence(sequence)); }

    // === Calendar & Booking (Item 23) ===
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarEvent>> getEvents(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) { return ResponseEntity.ok(service.getEvents(start, end)); }

    @PostMapping("/calendar")
    public ResponseEntity<CalendarEvent> createEvent(@RequestBody CalendarEvent event) { return ResponseEntity.ok(service.createEvent(event)); }

    @GetMapping("/booking-pages")
    public ResponseEntity<List<BookingPage>> getBookingPages() { return ResponseEntity.ok(service.getBookingPages()); }

    @PostMapping("/booking-pages")
    public ResponseEntity<BookingPage> createBookingPage(@RequestBody BookingPage page) { return ResponseEntity.ok(service.createBookingPage(page)); }

    @GetMapping("/booking/{slug}")
    public ResponseEntity<BookingPage> getBookingPage(@PathVariable String slug) { return ResponseEntity.ok(service.getBookingPage(slug)); }

    @PostMapping("/booking/{slug}/book")
    public ResponseEntity<CalendarEvent> bookMeeting(@PathVariable String slug, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.bookMeeting(slug,
            LocalDateTime.parse(body.get("startTime")),
            body.get("attendeeName"),
            body.get("attendeeEmail")));
    }

    // === Email Sync (Item 24) ===
    @GetMapping("/emails")
    public ResponseEntity<List<EmailSyncLog>> getEmails() { return ResponseEntity.ok(service.getEmails()); }

    @PostMapping("/emails")
    public ResponseEntity<EmailSyncLog> logEmail(@RequestBody EmailSyncLog log) { return ResponseEntity.ok(service.logEmail(log)); }

    @GetMapping("/emails/contact/{contactId}")
    public ResponseEntity<List<EmailSyncLog>> getEmailsByContact(@PathVariable Long contactId) { return ResponseEntity.ok(service.getEmailsByContact(contactId)); }

    @GetMapping("/emails/stats")
    public ResponseEntity<Map<String, Object>> getEmailStats() { return ResponseEntity.ok(service.getEmailStats()); }
}
