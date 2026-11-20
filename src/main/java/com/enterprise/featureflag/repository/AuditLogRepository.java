package com.enterprise.featureflag.repository;

import com.enterprise.featureflag.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop15ByTenantIdOrderByTimestampDesc(Long tenantId);
}