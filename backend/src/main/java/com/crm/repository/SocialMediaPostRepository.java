package com.crm.repository;

import com.crm.entity.SocialMediaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SocialMediaPostRepository extends JpaRepository<SocialMediaPost, Long> {
    List<SocialMediaPost> findByTenantId(Long tenantId);
    List<SocialMediaPost> findByTenantIdAndStatus(Long tenantId, SocialMediaPost.PostStatus status);
    List<SocialMediaPost> findByTenantIdAndScheduledAtBefore(Long tenantId, LocalDateTime date);
    Optional<SocialMediaPost> findByTenantIdAndId(Long tenantId, Long id);
}
