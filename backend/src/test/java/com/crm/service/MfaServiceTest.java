package com.crm.service;

import com.crm.entity.Usuario;
import com.crm.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MfaServiceTest {

    @Test
    void recoveryCodeIsSingleUse() {
        UsuarioRepository users = mock(UsuarioRepository.class);
        MfaService service = new MfaService(users);
        Usuario user = new Usuario();
        user.setUsername("owner");
        user.setMfaEnabled(true);
        user.setMfaSecret("JBSWY3DPEHPK3PXP");
        user.setMfaRecoveryCodes("123456789,987654321");

        assertTrue(service.verifyLoginCode(user, "123456789"));
        assertEquals("987654321", user.getMfaRecoveryCodes());
        assertFalse(service.verifyLoginCode(user, "123456789"));
        verify(users).save(user);
    }
}
