package com.enterprise.featureflag.service;

import com.enterprise.featureflag.entity.AuditLog;
import com.enterprise.featureflag.entity.FeatureFlag;
import com.enterprise.featureflag.entity.Tenant;
import com.enterprise.featureflag.repository.AuditLogRepository;
import com.enterprise.featureflag.repository.FeatureFlagRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlagManagementService {

    private final FeatureFlagRepository flagRepository;
    private final AuditLogRepository auditLogRepository;

    public FlagManagementService(FeatureFlagRepository flagRepository,
                                 AuditLogRepository auditLogRepository) {
        this.flagRepository = flagRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    @CacheEvict(value = "evaluations", allEntries = true)
    public void createFlag(String flagKey, int rolloutPercentage, String targetPlan, Tenant tenant, String adminEmail) {
        String cleanKey = flagKey.trim().toLowerCase().replaceAll("\\s+", "_");
        if (flagRepository.findByTenantIdAndFlagKey(tenant.getId(), cleanKey).isPresent()) {
            throw new IllegalArgumentException("Flag '" + cleanKey + "' already exists for this workspace.");
        }

        FeatureFlag flag = new FeatureFlag(cleanKey, true, rolloutPercentage, targetPlan, tenant);
        flagRepository.save(flag);

        auditLogRepository.save(new AuditLog(
                cleanKey,
                "CREATED flag (Enabled: true, Rollout: " + rolloutPercentage + "%, Plan: " + targetPlan + ")",
                adminEmail,
                tenant
        ));
    }

    @Transactional
    @CacheEvict(value = "evaluations", allEntries = true)
    public void toggleFlag(Long flagId, Tenant tenant, String adminEmail) {
        FeatureFlag flag = flagRepository.findById(flagId).orElseThrow();
        if (!flag.getTenant().getId().equals(tenant.getId())) {
            throw new SecurityException("Unauthorized tenant access");
        }

        flag.setEnabled(!flag.isEnabled());
        flagRepository.save(flag);

        auditLogRepository.save(new AuditLog(
                flag.getFlagKey(),
                "TOGGLED master switch to " + (flag.isEnabled() ? "ON" : "OFF"),
                adminEmail,
                tenant
        ));
    }

    @Transactional
    @CacheEvict(value = "evaluations", allEntries = true)
    public void updateRules(Long flagId, int rolloutPercentage, String targetPlan, Tenant tenant, String adminEmail) {
        FeatureFlag flag = flagRepository.findById(flagId).orElseThrow();
        if (!flag.getTenant().getId().equals(tenant.getId())) {
            throw new SecurityException("Unauthorized tenant access");
        }

        flag.setRolloutPercentage(rolloutPercentage);
        flag.setTargetPlan(targetPlan);
        flagRepository.save(flag);

        auditLogRepository.save(new AuditLog(
                flag.getFlagKey(),
                "UPDATED rules -> Rollout: " + rolloutPercentage + "%, Plan: " + targetPlan,
                adminEmail,
                tenant
        ));
    }

    @Transactional
    @CacheEvict(value = "evaluations", allEntries = true)
    public void deleteFlag(Long flagId, Tenant tenant, String adminEmail) {
        FeatureFlag flag = flagRepository.findById(flagId).orElseThrow();
        if (!flag.getTenant().getId().equals(tenant.getId())) {
            throw new SecurityException("Unauthorized tenant access");
        }

        String key = flag.getFlagKey();
        flagRepository.delete(flag);

        auditLogRepository.save(new AuditLog(
                key,
                "DELETED feature flag",
                adminEmail,
                tenant
        ));
    }
}