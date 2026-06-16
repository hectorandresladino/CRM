package com.crm.service;

import com.crm.entity.EmailMarketing;
import com.crm.repository.EmailMarketingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailMarketingService {
    
    private final EmailMarketingRepository emailRepository;
    
    public List<EmailMarketing> findAll() {
        return emailRepository.findAll();
    }
    
    public Optional<EmailMarketing> findById(Long id) {
        return emailRepository.findById(id);
    }
    
    public EmailMarketing save(EmailMarketing email) {
        return emailRepository.save(email);
    }
    
    public EmailMarketing update(Long id, EmailMarketing email) {
        email.setId(id);
        return emailRepository.save(email);
    }
    
    public void delete(Long id) {
        emailRepository.deleteById(id);
    }
    
    public List<EmailMarketing> findByEstado(String estado) {
        return emailRepository.findByEstado(estado);
    }
    
    public List<EmailMarketing> findByTipo(String tipo) {
        return emailRepository.findByTipo(tipo);
    }
}
