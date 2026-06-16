package com.crm.config;

import com.crm.entity.Usuario;
import com.crm.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UsuarioRepository usuarioRepository;
    
    @Override
    public void run(String... args) {
        System.out.println("Iniciando DataInitializer...");
        System.out.println("Total usuarios en base de datos: " + usuarioRepository.count());
        
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            System.out.println("Creando usuario administrador...");
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setEmail("admin@crm.com");
            admin.setNombre("Administrador");
            admin.setRol("ADMIN");
            admin.setActivo(true);
            Usuario saved = usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado: admin / admin123");
            System.out.println("ID del usuario creado: " + saved.getId());
        } else {
            System.out.println("El usuario administrador ya existe");
            usuarioRepository.findByUsername("admin").ifPresent(u -> {
                System.out.println("Usuario encontrado: " + u.getUsername());
                System.out.println("Contraseña: " + u.getPassword());
                System.out.println("Activo: " + u.getActivo());
            });
        }
    }
}
