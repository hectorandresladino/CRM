package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_objects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "plural_label")
    private String pluralLabel;

    @Column(name = "icon_name")
    private String iconName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "enable_activities")
    private Boolean enableActivities = false;

    @Column(name = "enable_history")
    private Boolean enableHistory = true;

    @Column(name = "enable_reports")
    private Boolean enableReports = true;

    @Column(name = "enable_search")
    private Boolean enableSearch = true;

    @Column(name = "field_definitions")
    @Lob
    private String fieldDefinitions;

    @Column(name = "record_count")
    private Integer recordCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
