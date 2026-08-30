/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

/**
 * Deliberately does not reveal whether a record exists in another tenant.
 */
public class TenantAccessDeniedException extends RuntimeException {
    public TenantAccessDeniedException(String resourceName) {
        super(resourceName + " no encontrado");
    }
}
