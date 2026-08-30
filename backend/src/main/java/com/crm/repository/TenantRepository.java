package com.crm.repository;

import com.crm.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySlug(String slug);
    Optional<Tenant> findByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}
