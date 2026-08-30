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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarketingCloudAdvancedService {

    private final CampanaMarketingRepository campaignRepo;
    private final EmailMarketingRepository emailRepo;
    private final CustomerSegmentRepository segmentRepo;
    private final CustomerJourneyRepository journeyRepo;
    private final JourneyStepRepository journeyStepRepo;
    private final LandingPageRepository landingRepo;
    private final MarketingAttributionRepository attributionRepo;
    private final SocialMediaPostRepository socialRepo;
    private final ABTestRepository abTestRepo;
    private final ClienteRepository clienteRepo;

    private Long tid() {
        Long t = TenantContext.getCurrentTenant();
        if (t == null) throw new RuntimeException("No tenant context");
        return t;
    }

    // === Campaign Management (Item 35) ===

    public List<CampanaMarketing> getCampaigns() { return campaignRepo.findByTenantId(tid()); }

    public CampanaMarketing createCampaign(CampanaMarketing campaign) {
        campaign.setTenantId(tid());
        return campaignRepo.save(campaign);
    }

    public Map<String, Object> getCampaignROI(Long campaignId) {
        CampanaMarketing c = campaignRepo.findById(campaignId).orElseThrow();
        BigDecimal budget = c.getPresupuesto() != null ? c.getPresupuesto() : BigDecimal.ZERO;
        BigDecimal spent = c.getPresupuestoGastado() != null ? c.getPresupuestoGastado() : BigDecimal.ZERO;

        List<MarketingAttribution> attributions = attributionRepo.findByTenantIdAndCampaignId(tid(), campaignId);
        BigDecimal revenue = attributions.stream()
                .map(a -> a.getRevenueAttributed() != null ? a.getRevenueAttributed() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal roi = spent.compareTo(BigDecimal.ZERO) > 0
                ? revenue.subtract(spent).divide(spent, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("campaignId", c.getId());
        result.put("campaignName", c.getNombre());
        result.put("budget", budget);
        result.put("spent", spent);
        result.put("revenueAttributed", revenue);
        result.put("roi", roi);
        result.put("remainingBudget", budget.subtract(spent));
        result.put("touchpoints", attributions.size());
        return result;
    }

    // === Email Marketing (Item 36) ===

    public List<EmailMarketing> getEmails() { return emailRepo.findByTenantId(tid()); }

    public EmailMarketing createEmail(EmailMarketing email) {
        email.setTenantId(tid());
        return emailRepo.save(email);
    }

    public EmailMarketing scheduleEmail(Long id, LocalDateTime scheduledAt) {
        EmailMarketing email = emailRepo.findById(id).orElseThrow();
        email.setFechaProgramada(scheduledAt);
        email.setEstado("PROGRAMADO");
        return emailRepo.save(email);
    }

    public Map<String, Object> getEmailAnalytics() {
        List<EmailMarketing> emails = emailRepo.findByTenantId(tid());
        long total = emails.size();
        int totalSent = emails.stream().mapToInt(e -> e.getTotalEnviados() != null ? e.getTotalEnviados() : 0).sum();
        int totalOpened = emails.stream().mapToInt(e -> e.getTotalAbiertos() != null ? e.getTotalAbiertos() : 0).sum();
        int totalClicked = emails.stream().mapToInt(e -> e.getTotalClicks() != null ? e.getTotalClicks() : 0).sum();

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalCampaigns", total);
        analytics.put("totalSent", totalSent);
        analytics.put("totalOpened", totalOpened);
        analytics.put("totalClicked", totalClicked);
        analytics.put("openRate", totalSent > 0 ? (double) totalOpened / totalSent * 100 : 0);
        analytics.put("clickRate", totalSent > 0 ? (double) totalClicked / totalSent * 100 : 0);
        analytics.put("clickToOpenRate", totalOpened > 0 ? (double) totalClicked / totalOpened * 100 : 0);
        return analytics;
    }

    // === Customer Segments (Item 37) ===

    public List<CustomerSegment> getSegments() { return segmentRepo.findByTenantId(tid()); }

    public CustomerSegment createSegment(CustomerSegment segment) {
        segment.setTenantId(tid());
        return segmentRepo.save(segment);
    }

    public CustomerSegment evaluateSegment(Long segmentId) {
        CustomerSegment segment = segmentRepo.findById(segmentId).orElseThrow();
        long count = clienteRepo.findByTenantId(tid()).size();
        segment.setMemberCount((int) count);
        segment.setLastEvaluatedAt(LocalDateTime.now());
        return segmentRepo.save(segment);
    }

    // === Customer Journeys (Item 38) ===

    public List<CustomerJourney> getJourneys() { return journeyRepo.findByTenantId(tid()); }

    public CustomerJourney createJourney(CustomerJourney journey) {
        journey.setTenantId(tid());
        return journeyRepo.save(journey);
    }

    public JourneyStep addJourneyStep(JourneyStep step) {
        step.setTenantId(tid());
        return journeyStepRepo.save(step);
    }

    public List<JourneyStep> getJourneySteps(Long journeyId) {
        return journeyStepRepo.findByTenantIdAndJourneyId(tid(), journeyId);
    }

    public CustomerJourney activateJourney(Long journeyId) {
        CustomerJourney journey = journeyRepo.findById(journeyId).orElseThrow();
        journey.setStatus(CustomerJourney.Status.ACTIVE.name());
        journey.setIsActive(true);
        return journeyRepo.save(journey);
    }

    public CustomerJourney pauseJourney(Long journeyId) {
        CustomerJourney journey = journeyRepo.findById(journeyId).orElseThrow();
        journey.setStatus(CustomerJourney.Status.PAUSED.name());
        journey.setIsActive(false);
        return journeyRepo.save(journey);
    }

    public Map<String, Object> getJourneyMetrics(Long journeyId) {
        CustomerJourney j = journeyRepo.findById(journeyId).orElseThrow();
        List<JourneyStep> steps = journeyStepRepo.findByTenantIdAndJourneyId(tid(), journeyId);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("journeyId", j.getId());
        metrics.put("name", j.getName());
        metrics.put("status", j.getStatus());
        metrics.put("totalEnrolled", j.getTotalEnrolled());
        metrics.put("totalCompleted", j.getTotalCompleted());
        metrics.put("totalConverted", j.getTotalConverted());
        metrics.put("conversionRate", j.getConversionRate());
        metrics.put("stepCount", steps.size());
        metrics.put("steps", steps);
        return metrics;
    }

    // === Landing Pages (Item 39) ===

    public List<LandingPage> getLandingPages() { return landingRepo.findByTenantId(tid()); }

    public LandingPage createLandingPage(LandingPage page) {
        page.setTenantId(tid());
        return landingRepo.save(page);
    }

    public LandingPage publishLandingPage(Long id) {
        LandingPage page = landingRepo.findById(id).orElseThrow();
        page.setIsPublished(true);
        page.setPublishedAt(LocalDateTime.now());
        return landingRepo.save(page);
    }

    public LandingPage trackVisit(String slug) {
        LandingPage page = landingRepo.findBySlug(slug).orElseThrow();
        page.setTotalVisits(page.getTotalVisits() + 1);
        page.setConversionRate(page.getTotalVisits() > 0
                ? (double) page.getTotalConversions() / page.getTotalVisits() * 100 : 0.0);
        return landingRepo.save(page);
    }

    public LandingPage trackConversion(String slug) {
        LandingPage page = landingRepo.findBySlug(slug).orElseThrow();
        page.setTotalConversions(page.getTotalConversions() + 1);
        page.setConversionRate(page.getTotalVisits() > 0
                ? (double) page.getTotalConversions() / page.getTotalVisits() * 100 : 0.0);
        return landingRepo.save(page);
    }

    // === Marketing Attribution (Item 40) ===

    public MarketingAttribution createAttribution(MarketingAttribution attribution) {
        attribution.setTenantId(tid());
        return attributionRepo.save(attribution);
    }

    public Map<String, Object> getAttributionSummary(Long campaignId) {
        List<MarketingAttribution> attributions = campaignId != null
                ? attributionRepo.findByTenantIdAndCampaignId(tid(), campaignId)
                : attributionRepo.findByTenantId(tid());

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalTouchpoints", attributions.size());

        Map<String, BigDecimal> revenueByModel = new LinkedHashMap<>();
        for (MarketingAttribution.AttributionModel model : MarketingAttribution.AttributionModel.values()) {
            BigDecimal rev = attributions.stream()
                    .filter(a -> a.getModel() == model)
                    .map(a -> a.getRevenueAttributed() != null ? a.getRevenueAttributed() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueByModel.put(model.name(), rev);
        }
        summary.put("revenueByModel", revenueByModel);

        Map<String, BigDecimal> revenueByChannel = new LinkedHashMap<>();
        for (MarketingAttribution a : attributions) {
            String channel = a.getTouchpointChannel() != null ? a.getTouchpointChannel() : "UNKNOWN";
            BigDecimal rev = a.getRevenueAttributed() != null ? a.getRevenueAttributed() : BigDecimal.ZERO;
            revenueByChannel.merge(channel, rev, BigDecimal::add);
        }
        summary.put("revenueByChannel", revenueByChannel);

        BigDecimal totalRevenue = attributions.stream()
                .map(a -> a.getRevenueAttributed() != null ? a.getRevenueAttributed() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalRevenueAttributed", totalRevenue);

        return summary;
    }

    // === Social Media Management (Item 41) ===

    public List<SocialMediaPost> getSocialPosts() { return socialRepo.findByTenantId(tid()); }

    public SocialMediaPost createSocialPost(SocialMediaPost post) {
        post.setTenantId(tid());
        return socialRepo.save(post);
    }

    public SocialMediaPost scheduleSocialPost(Long id, LocalDateTime scheduledAt) {
        SocialMediaPost post = socialRepo.findById(id).orElseThrow();
        post.setScheduledAt(scheduledAt);
        post.setStatus(SocialMediaPost.PostStatus.SCHEDULED);
        return socialRepo.save(post);
    }

    public List<SocialMediaPost> getScheduledPosts() {
        return socialRepo.findByTenantIdAndStatus(tid(), SocialMediaPost.PostStatus.SCHEDULED);
    }

    public SocialMediaPost markPublished(Long id, String externalPostId, String externalUrl) {
        SocialMediaPost post = socialRepo.findById(id).orElseThrow();
        post.setStatus(SocialMediaPost.PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        post.setExternalPostId(externalPostId);
        post.setExternalUrl(externalUrl);
        return socialRepo.save(post);
    }

    public Map<String, Object> getSocialAnalytics() {
        List<SocialMediaPost> posts = socialRepo.findByTenantId(tid());
        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalPosts", posts.size());
        analytics.put("published", posts.stream().filter(p -> p.getStatus() == SocialMediaPost.PostStatus.PUBLISHED).count());
        analytics.put("scheduled", posts.stream().filter(p -> p.getStatus() == SocialMediaPost.PostStatus.SCHEDULED).count());

        int totalLikes = posts.stream().mapToInt(p -> p.getLikesCount() != null ? p.getLikesCount() : 0).sum();
        int totalComments = posts.stream().mapToInt(p -> p.getCommentsCount() != null ? p.getCommentsCount() : 0).sum();
        int totalShares = posts.stream().mapToInt(p -> p.getSharesCount() != null ? p.getSharesCount() : 0).sum();
        int totalImpressions = posts.stream().mapToInt(p -> p.getImpressions() != null ? p.getImpressions() : 0).sum();
        int totalReach = posts.stream().mapToInt(p -> p.getReach() != null ? p.getReach() : 0).sum();
        int totalClicks = posts.stream().mapToInt(p -> p.getClicks() != null ? p.getClicks() : 0).sum();

        analytics.put("totalLikes", totalLikes);
        analytics.put("totalComments", totalComments);
        analytics.put("totalShares", totalShares);
        analytics.put("totalImpressions", totalImpressions);
        analytics.put("totalReach", totalReach);
        analytics.put("totalClicks", totalClicks);
        analytics.put("engagementRate", totalImpressions > 0
                ? (double) (totalLikes + totalComments + totalShares) / totalImpressions * 100 : 0);

        Map<String, Long> byPlatform = new LinkedHashMap<>();
        for (SocialMediaPost.SocialPlatform p : SocialMediaPost.SocialPlatform.values()) {
            byPlatform.put(p.name(), posts.stream().filter(post -> post.getPlatform() == p).count());
        }
        analytics.put("postsByPlatform", byPlatform);
        return analytics;
    }

    // === A/B Testing (Item 42) ===

    public List<ABTest> getABTests() { return abTestRepo.findByTenantId(tid()); }

    public ABTest createABTest(ABTest test) {
        test.setTenantId(tid());
        return abTestRepo.save(test);
    }

    public ABTest recordVisit(Long testId, String variant) {
        ABTest test = abTestRepo.findById(testId).orElseThrow();
        if ("A".equalsIgnoreCase(variant)) {
            test.setVariantAVisits(test.getVariantAVisits() + 1);
        } else {
            test.setVariantBVisits(test.getVariantBVisits() + 1);
        }
        updateTestRates(test);
        return abTestRepo.save(test);
    }

    public ABTest recordConversion(Long testId, String variant) {
        ABTest test = abTestRepo.findById(testId).orElseThrow();
        if ("A".equalsIgnoreCase(variant)) {
            test.setVariantAConversions(test.getVariantAConversions() + 1);
        } else {
            test.setVariantBConversions(test.getVariantBConversions() + 1);
        }
        updateTestRates(test);
        return abTestRepo.save(test);
    }

    private void updateTestRates(ABTest test) {
        test.setVariantARate(test.getVariantAVisits() > 0
                ? (double) test.getVariantAConversions() / test.getVariantAVisits() * 100 : 0.0);
        test.setVariantBRate(test.getVariantBVisits() > 0
                ? (double) test.getVariantBConversions() / test.getVariantBVisits() * 100 : 0.0);
        test.setConfidenceLevel(calculateConfidence(test));
        if (test.getConfidenceLevel() >= 95.0) {
            test.setStatus(ABTest.TestStatus.COMPLETED);
            test.setEndedAt(LocalDateTime.now());
            test.setWinningVariant(test.getVariantARate() >= test.getVariantBRate() ? "A" : "B");
        }
    }

    private double calculateConfidence(ABTest test) {
        int n1 = test.getVariantAVisits();
        int n2 = test.getVariantBVisits();
        if (n1 < 30 || n2 < 30) return 0.0;
        double p1 = test.getVariantARate() / 100.0;
        double p2 = test.getVariantBRate() / 100.0;
        double pooledP = (p1 * n1 + p2 * n2) / (n1 + n2);
        double se = Math.sqrt(pooledP * (1 - pooledP) * (1.0/n1 + 1.0/n2));
        if (se == 0) return 0.0;
        double z = Math.abs(p1 - p2) / se;
        double confidence = 2 * (1 - normalCdf(z)) * 100;
        return Math.min(confidence, 99.99);
    }

    private double normalCdf(double z) {
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }

    private double erf(double x) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(x));
        double ans = 1 - t * Math.exp(-x*x - 1.26551223 +
                t * (1.00002368 + t * (0.37409196 + t * (0.09678418 +
                t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 +
                t * (1.48851587 + t * (-0.82215223 + t * 0.17087277)))))))));
        return x >= 0 ? ans : -ans;
    }

    // === Marketing Dashboard (Item 43) ===

    public Map<String, Object> getMarketingDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalCampaigns", campaignRepo.findByTenantId(tid()).size());
        dashboard.put("activeJourneys", journeyRepo.findByTenantIdAndIsActive(tid(), true).size());
        dashboard.put("totalSegments", segmentRepo.findByTenantId(tid()).size());
        dashboard.put("publishedLandingPages", landingRepo.findByTenantIdAndIsPublished(tid(), true).size());
        dashboard.put("scheduledSocialPosts", socialRepo.findByTenantIdAndStatus(tid(), SocialMediaPost.PostStatus.SCHEDULED).size());
        dashboard.put("runningABTests", abTestRepo.findByTenantIdAndStatus(tid(), ABTest.TestStatus.RUNNING).size());
        dashboard.put("emailAnalytics", getEmailAnalytics());
        dashboard.put("socialAnalytics", getSocialAnalytics());
        return dashboard;
    }

    // === Lead Scoring Integration (Item 44) ===

    public Map<String, Object> getLeadScoringSummary() {
        List<Cliente> clients = clienteRepo.findByTenantId(tid());
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalClients", clients.size());

        Map<String, Integer> scoreBuckets = new LinkedHashMap<>();
        scoreBuckets.put("HOT (80-100)", 0);
        scoreBuckets.put("WARM (50-79)", 0);
        scoreBuckets.put("COLD (0-49)", 0);

        summary.put("scoreBuckets", scoreBuckets);
        summary.put("averageScore", 0);
        return summary;
    }
}
