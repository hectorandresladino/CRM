package com.crm.controller;

import com.crm.entity.Usuario;
import com.crm.repository.UsuarioRepository;
import com.crm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = authService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Intento de login - Username: " + loginRequest.getUsername());
        System.out.println("Intento de login - Password: " + loginRequest.getPassword());
        try {
            Usuario usuario = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            System.out.println("Login exitoso para: " + usuario.getUsername());
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            System.out.println("Error en login: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            return authService.findByUsername(username)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            System.out.println("Total usuarios: " + usuarios.size());
            for (Usuario u : usuarios) {
                System.out.println("Usuario: " + u.getUsername() + ", Password: " + u.getPassword() + ", Activo: " + u.getActivo());
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
