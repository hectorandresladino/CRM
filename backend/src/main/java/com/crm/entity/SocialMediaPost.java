/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_media_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialPlatform platform;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "media_urls", columnDefinition = "TEXT")
    private String mediaUrls;

    @Column(name = "hashtags", columnDefinition = "TEXT")
    private String hashtags;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "external_post_id")
    private String externalPostId;

    @Column(name = "external_url")
    private String externalUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "shares_count")
    private Integer sharesCount = 0;

    @Column(name = "impressions")
    private Integer impressions = 0;

    @Column(name = "reach")
    private Integer reach = 0;

    @Column(name = "clicks")
    private Integer clicks = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum SocialPlatform { FACEBOOK, INSTAGRAM, LINKEDIN, TWITTER, TIKTOK, YOUTUBE, PINTEREST }
    public enum PostStatus { DRAFT, SCHEDULED, PUBLISHED, FAILED, ARCHIVED }
}
