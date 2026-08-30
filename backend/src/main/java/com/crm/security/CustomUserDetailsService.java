/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.security;

import com.crm.entity.Usuario;
import com.crm.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByTenantIdAndUsername(TenantContext.requireCurrentTenant(), username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new CustomUserDetails(usuario);
    }

    public UserDetails loadPlatformAdminByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndTenantIdIsNull(username)
                .filter(user -> user.getRol() == Usuario.Role.SUPER_ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador de plataforma no encontrado"));
        return new CustomUserDetails(usuario);
    }
}
