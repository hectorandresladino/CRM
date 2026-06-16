package com.crm.controller;

import com.crm.entity.GestionDocumental;
import com.crm.service.GestionDocumentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestion-documental")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GestionDocumentalController {
    
    private final GestionDocumentalService documentoService;
    
    @GetMapping
    public List<GestionDocumental> findAll() {
        return documentoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GestionDocumental> findById(@PathVariable Long id) {
        return documentoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public GestionDocumental save(@RequestBody GestionDocumental documento) {
        return documentoService.save(documento);
    }
    
    @PutMapping("/{id}")
    public GestionDocumental update(@PathVariable Long id, @RequestBody GestionDocumental documento) {
        return documentoService.update(id, documento);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        documentoService.delete(id);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<GestionDocumental> findByClienteId(@PathVariable Long clienteId) {
        return documentoService.findByClienteId(clienteId);
    }
    
    @GetMapping("/categoria/{categoria}")
    public List<GestionDocumental> findByCategoria(@PathVariable String categoria) {
        return documentoService.findByCategoria(categoria);
    }
    
    @GetMapping("/por-vencer")
    public List<GestionDocumental> findPorVencer() {
        return documentoService.findPorVencer();
    }
}
