package com.crm.controller;

import com.crm.entity.GamificationBadge;
import com.crm.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping
    public ResponseEntity<List<GamificationBadge>> getAll() {
        return ResponseEntity.ok(gamificationService.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<GamificationBadge> create(@RequestBody GamificationBadge badge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gamificationService.save(badge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gamificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
