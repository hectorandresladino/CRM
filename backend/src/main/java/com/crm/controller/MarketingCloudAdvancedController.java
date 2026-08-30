/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.MarketingCloudAdvancedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/marketing-advanced")
@RequiredArgsConstructor
public class MarketingCloudAdvancedController {

    private final MarketingCloudAdvancedService service;

    // === Campaigns (Item 35) ===
    @GetMapping("/campaigns")
    public ResponseEntity<List<CampanaMarketing>> getCampaigns() { return ResponseEntity.ok(service.getCampaigns()); }

    @PostMapping("/campaigns")
    public ResponseEntity<CampanaMarketing> createCampaign(@RequestBody CampanaMarketing campaign) { return ResponseEntity.ok(service.createCampaign(campaign)); }

    @GetMapping("/campaigns/{id}/roi")
    public ResponseEntity<Map<String, Object>> getCampaignROI(@PathVariable Long id) { return ResponseEntity.ok(service.getCampaignROI(id)); }

    // === Email Marketing (Item 36) ===
    @GetMapping("/emails")
    public ResponseEntity<List<EmailMarketing>> getEmails() { return ResponseEntity.ok(service.getEmails()); }

    @PostMapping("/emails")
    public ResponseEntity<EmailMarketing> createEmail(@RequestBody EmailMarketing email) { return ResponseEntity.ok(service.createEmail(email)); }

    @PutMapping("/emails/{id}/schedule")
    public ResponseEntity<EmailMarketing> scheduleEmail(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.scheduleEmail(id, LocalDateTime.parse(body.get("scheduledAt"))));
    }

    @GetMapping("/emails/analytics")
    public ResponseEntity<Map<String, Object>> getEmailAnalytics() { return ResponseEntity.ok(service.getEmailAnalytics()); }

    // === Segments (Item 37) ===
    @GetMapping("/segments")
    public ResponseEntity<List<CustomerSegment>> getSegments() { return ResponseEntity.ok(service.getSegments()); }

    @PostMapping("/segments")
    public ResponseEntity<CustomerSegment> createSegment(@RequestBody CustomerSegment segment) { return ResponseEntity.ok(service.createSegment(segment)); }

    @PostMapping("/segments/{id}/evaluate")
    public ResponseEntity<CustomerSegment> evaluateSegment(@PathVariable Long id) { return ResponseEntity.ok(service.evaluateSegment(id)); }

    // === Journeys (Item 38) ===
    @GetMapping("/journeys")
    public ResponseEntity<List<CustomerJourney>> getJourneys() { return ResponseEntity.ok(service.getJourneys()); }

    @PostMapping("/journeys")
    public ResponseEntity<CustomerJourney> createJourney(@RequestBody CustomerJourney journey) { return ResponseEntity.ok(service.createJourney(journey)); }

    @PostMapping("/journeys/{id}/activate")
    public ResponseEntity<CustomerJourney> activateJourney(@PathVariable Long id) { return ResponseEntity.ok(service.activateJourney(id)); }

    @PostMapping("/journeys/{id}/pause")
    public ResponseEntity<CustomerJourney> pauseJourney(@PathVariable Long id) { return ResponseEntity.ok(service.pauseJourney(id)); }

    @GetMapping("/journeys/{id}/metrics")
    public ResponseEntity<Map<String, Object>> getJourneyMetrics(@PathVariable Long id) { return ResponseEntity.ok(service.getJourneyMetrics(id)); }

    @PostMapping("/journeys/{journeyId}/steps")
    public ResponseEntity<JourneyStep> addJourneyStep(@PathVariable Long journeyId, @RequestBody JourneyStep step) {
        step.setJourneyId(journeyId);
        return ResponseEntity.ok(service.addJourneyStep(step));
    }

    @GetMapping("/journeys/{journeyId}/steps")
    public ResponseEntity<List<JourneyStep>> getJourneySteps(@PathVariable Long journeyId) { return ResponseEntity.ok(service.getJourneySteps(journeyId)); }

    // === Landing Pages (Item 39) ===
    @GetMapping("/landing-pages")
    public ResponseEntity<List<LandingPage>> getLandingPages() { return ResponseEntity.ok(service.getLandingPages()); }

    @PostMapping("/landing-pages")
    public ResponseEntity<LandingPage> createLandingPage(@RequestBody LandingPage page) { return ResponseEntity.ok(service.createLandingPage(page)); }

    @PostMapping("/landing-pages/{id}/publish")
    public ResponseEntity<LandingPage> publishLandingPage(@PathVariable Long id) { return ResponseEntity.ok(service.publishLandingPage(id)); }

    @PostMapping("/landing-pages/{slug}/visit")
    public ResponseEntity<LandingPage> trackVisit(@PathVariable String slug) { return ResponseEntity.ok(service.trackVisit(slug)); }

    @PostMapping("/landing-pages/{slug}/convert")
    public ResponseEntity<LandingPage> trackConversion(@PathVariable String slug) { return ResponseEntity.ok(service.trackConversion(slug)); }

    // === Attribution (Item 40) ===
    @PostMapping("/attribution")
    public ResponseEntity<MarketingAttribution> createAttribution(@RequestBody MarketingAttribution attribution) { return ResponseEntity.ok(service.createAttribution(attribution)); }

    @GetMapping("/attribution/summary")
    public ResponseEntity<Map<String, Object>> getAttributionSummary(@RequestParam(required = false) Long campaignId) { return ResponseEntity.ok(service.getAttributionSummary(campaignId)); }

    // === Social Media (Item 41) ===
    @GetMapping("/social")
    public ResponseEntity<List<SocialMediaPost>> getSocialPosts() { return ResponseEntity.ok(service.getSocialPosts()); }

    @PostMapping("/social")
    public ResponseEntity<SocialMediaPost> createSocialPost(@RequestBody SocialMediaPost post) { return ResponseEntity.ok(service.createSocialPost(post)); }

    @PutMapping("/social/{id}/schedule")
    public ResponseEntity<SocialMediaPost> scheduleSocialPost(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.scheduleSocialPost(id, LocalDateTime.parse(body.get("scheduledAt"))));
    }

    @GetMapping("/social/scheduled")
    public ResponseEntity<List<SocialMediaPost>> getScheduledPosts() { return ResponseEntity.ok(service.getScheduledPosts()); }

    @PostMapping("/social/{id}/published")
    public ResponseEntity<SocialMediaPost> markPublished(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.markPublished(id, body.get("externalPostId"), body.get("externalUrl")));
    }

    @GetMapping("/social/analytics")
    public ResponseEntity<Map<String, Object>> getSocialAnalytics() { return ResponseEntity.ok(service.getSocialAnalytics()); }

    // === A/B Testing (Item 42) ===
    @GetMapping("/ab-tests")
    public ResponseEntity<List<ABTest>> getABTests() { return ResponseEntity.ok(service.getABTests()); }

    @PostMapping("/ab-tests")
    public ResponseEntity<ABTest> createABTest(@RequestBody ABTest test) { return ResponseEntity.ok(service.createABTest(test)); }

    @PostMapping("/ab-tests/{id}/visit")
    public ResponseEntity<ABTest> recordVisit(@PathVariable Long id, @RequestParam String variant) { return ResponseEntity.ok(service.recordVisit(id, variant)); }

    @PostMapping("/ab-tests/{id}/convert")
    public ResponseEntity<ABTest> recordConversion(@PathVariable Long id, @RequestParam String variant) { return ResponseEntity.ok(service.recordConversion(id, variant)); }

    // === Dashboard (Item 43) ===
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() { return ResponseEntity.ok(service.getMarketingDashboard()); }

    // === Lead Scoring (Item 44) ===
    @GetMapping("/lead-scoring/summary")
    public ResponseEntity<Map<String, Object>> getLeadScoringSummary() { return ResponseEntity.ok(service.getLeadScoringSummary()); }
}
