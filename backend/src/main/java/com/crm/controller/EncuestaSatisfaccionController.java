package com.crm.controller;

import com.crm.entity.EncuestaSatisfaccion;
import com.crm.service.EncuestaSatisfaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encuestas-satisfaccion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncuestaSatisfaccionController {
    
    private final EncuestaSatisfaccionService encuestaService;
    
    @GetMapping
    public List<EncuestaSatisfaccion> findAll() {
        return encuestaService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EncuestaSatisfaccion> findById(@PathVariable Long id) {
        return encuestaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public EncuestaSatisfaccion save(@RequestBody EncuestaSatisfaccion encuesta) {
        return encuestaService.save(encuesta);
    }
    
    @PutMapping("/{id}")
    public EncuestaSatisfaccion update(@PathVariable Long id, @RequestBody EncuestaSatisfaccion encuesta) {
        return encuestaService.update(id, encuesta);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        encuestaService.delete(id);
    }
    
    @PutMapping("/{id}/responder")
    public EncuestaSatisfaccion registrarRespuesta(@PathVariable Long id, @RequestBody EncuestaSatisfaccion respuesta) {
        return encuestaService.registrarRespuesta(id, respuesta);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<EncuestaSatisfaccion> findByClienteId(@PathVariable Long clienteId) {
        return encuestaService.findByClienteId(clienteId);
    }
    
    @GetMapping("/estado/{estado}")
    public List<EncuestaSatisfaccion> findByEstado(@PathVariable String estado) {
        return encuestaService.findByEstado(estado);
    }
}
