package com.enterprise.featureflag.service;

import com.enterprise.featureflag.repository.FeatureFlagRepository;
import com.enterprise.featureflag.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperimentService {

    private final TenantRepository tenantRepository;
    private final FeatureFlagRepository flagRepository;

    public ExperimentService(TenantRepository tenantRepository,
                             FeatureFlagRepository flagRepository) {
        this.tenantRepository = tenantRepository;
        this.flagRepository = flagRepository;
    }

    @Transactional
    public void recordImpression(String apiKey, String flagKey, boolean enabled) {
        tenantRepository.findByApiKey(apiKey).ifPresent(tenant ->
                flagRepository.findByTenantIdAndFlagKey(tenant.getId(), flagKey).ifPresent(flag -> {
                    if (enabled) {
                        flag.setTreatmentViews(flag.getTreatmentViews() + 1);
                    } else {
                        flag.setControlViews(flag.getControlViews() + 1);
                    }
                    flagRepository.save(flag);
                })
        );
    }

    @Transactional
    public void recordConversion(String apiKey, String flagKey, boolean enabled) {
        tenantRepository.findByApiKey(apiKey).ifPresent(tenant ->
                flagRepository.findByTenantIdAndFlagKey(tenant.getId(), flagKey).ifPresent(flag -> {
                    if (enabled) {
                        flag.setTreatmentConversions(flag.getTreatmentConversions() + 1);
                    } else {
                        flag.setControlConversions(flag.getControlConversions() + 1);
                    }
                    flagRepository.save(flag);
                })
        );
    }
}