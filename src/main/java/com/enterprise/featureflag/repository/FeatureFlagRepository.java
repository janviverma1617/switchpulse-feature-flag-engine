package com.enterprise.featureflag.repository;

import com.enterprise.featureflag.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    List<FeatureFlag> findByTenantId(Long tenantId);
    Optional<FeatureFlag> findByTenantIdAndFlagKey(Long tenantId, String flagKey);
}