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
import java.time.DayOfWeek;

@Entity
@Table(name = "booking_pages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 30;

    @Column(name = "buffer_minutes")
    private Integer bufferMinutes = 15;

    @Column(name = "min_notice_hours")
    private Integer minNoticeHours = 24;

    @Column(name = "max_advance_days")
    private Integer maxAdvanceDays = 30;

    @Column(name = "available_days", columnDefinition = "TEXT")
    private String availableDays;

    @Column(name = "start_hour")
    private Integer startHour = 9;

    @Column(name = "end_hour")
    private Integer endHour = 18;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "meeting_link_provider")
    private String meetingLinkProvider;

    @Column(name = "confirmation_mode")
    private String confirmationMode;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
