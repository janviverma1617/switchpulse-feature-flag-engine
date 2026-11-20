package com.enterprise.featureflag.service;

import com.enterprise.featureflag.entity.FeatureFlag;
import com.enterprise.featureflag.entity.Tenant;
import com.enterprise.featureflag.repository.FeatureFlagRepository;
import com.enterprise.featureflag.repository.TenantRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FlagEvaluationService {

    private final TenantRepository tenantRepository;
    private final FeatureFlagRepository featureFlagRepository;

    public FlagEvaluationService(TenantRepository tenantRepository,
                                 FeatureFlagRepository featureFlagRepository) {
        this.tenantRepository = tenantRepository;
        this.featureFlagRepository = featureFlagRepository;
    }

    @Cacheable(value = "evaluations", key = "#apiKey + ':' + #flagKey + ':' + #userId + ':' + #plan")
    public Map<String, Object> evaluate(String apiKey, String flagKey, String userId, String plan) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("flag", flagKey);
        response.put("userId", userId);

        Optional<Tenant> tenantOpt = tenantRepository.findByApiKey(apiKey);
        if (tenantOpt.isEmpty()) {
            response.put("enabled", false);
            response.put("reason", "INVALID_API_KEY");
            return response;
        }

        Optional<FeatureFlag> flagOpt = featureFlagRepository
                .findByTenantIdAndFlagKey(tenantOpt.get().getId(), flagKey);

        if (flagOpt.isEmpty()) {
            response.put("enabled", false);
            response.put("reason", "FLAG_NOT_FOUND");
            return response;
        }

        FeatureFlag flag = flagOpt.get();

        // Rule 1: Master Kill-Switch
        if (!flag.isEnabled()) {
            response.put("enabled", false);
            response.put("reason", "GLOBAL_KILL_SWITCH_OFF");
            return response;
        }

        // Rule 2: VIP / Plan Targeting
        if ("PRO".equalsIgnoreCase(flag.getTargetPlan())) {
            boolean isPro = "PRO".equalsIgnoreCase(plan);
            response.put("enabled", isPro);
            response.put("reason", isPro ? "PRO_PLAN_MATCH" : "REQUIRES_PRO_PLAN");
            return response;
        }

        // Rule 3: Deterministic Percentage Rollout (0 - 99 bucket)
        int bucket = Math.abs((flagKey + ":" + userId).hashCode()) % 100;
        boolean inRollout = bucket < flag.getRolloutPercentage();

        response.put("enabled", inRollout);
        response.put("reason", inRollout
                ? "ROLLOUT_MATCH (bucket " + bucket + " < " + flag.getRolloutPercentage() + "%)"
                : "ROLLOUT_EXCLUDED (bucket " + bucket + " >= " + flag.getRolloutPercentage() + "%)");
        return response;
    }
}