/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByTenantId(Long tenantId);
    List<CalendarEvent> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
    List<CalendarEvent> findByTenantIdAndStartTimeBetween(Long tenantId, LocalDateTime start, LocalDateTime end);
    List<CalendarEvent> findByTenantIdAndStatus(Long tenantId, CalendarEvent.EventStatus status);
}
