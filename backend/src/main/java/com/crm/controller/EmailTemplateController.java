package com.crm.controller;

import com.crm.entity.EmailTemplate;
import com.crm.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-templates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @GetMapping
    public ResponseEntity<List<EmailTemplate>> getAll() {
        return ResponseEntity.ok(emailTemplateService.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<EmailTemplate> create(@RequestBody EmailTemplate template) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailTemplateService.save(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplate> update(@PathVariable Long id, @RequestBody EmailTemplate template) {
        return ResponseEntity.ok(emailTemplateService.update(id, template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        emailTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
