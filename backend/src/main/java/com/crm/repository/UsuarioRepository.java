/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    List<Usuario> findAllByUsername(String username);
    Optional<Usuario> findByTenantIdAndUsername(Long tenantId, String username);
    Optional<Usuario> findByUsernameAndTenantIdIsNull(String username);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAllByEmail(String email);
    Optional<Usuario> findByTenantIdAndEmail(Long tenantId, String email);
    Optional<Usuario> findByPasswordResetToken(String token);
    Optional<Usuario> findByEmailVerificationToken(String token);
    Optional<Usuario> findByRefreshToken(String refreshToken);
    List<Usuario> findByTenantId(Long tenantId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByTenantIdAndUsername(Long tenantId, String username);
    boolean existsByTenantIdAndEmail(Long tenantId, String email);
    long countByTenantId(Long tenantId);
}
