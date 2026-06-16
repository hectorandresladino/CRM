package com.crm.repository;

import com.crm.entity.CampanaMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampanaMarketingRepository extends JpaRepository<CampanaMarketing, Long> {
    
    List<CampanaMarketing> findByEstado(String estado);
    List<CampanaMarketing> findByTipo(String tipo);
    List<CampanaMarketing> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    Optional<CampanaMarketing> findByNombre(String nombre);
    List<CampanaMarketing> findBySegmento(String segmento);
}
