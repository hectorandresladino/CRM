package com.crm.repository;

import com.crm.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByTenantId(Long tenantId);
    Optional<ApiKey> findByKeyAndEsActivo(String key, Boolean esActivo);
}
