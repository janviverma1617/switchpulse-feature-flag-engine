package com.enterprise.featureflag.controller;

import com.enterprise.featureflag.service.ExperimentService;
import com.enterprise.featureflag.service.FlagEvaluationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EvaluationRestController {

    private final FlagEvaluationService evaluationService;
    private final ExperimentService experimentService;

    public EvaluationRestController(FlagEvaluationService evaluationService,
                                    ExperimentService experimentService) {
        this.evaluationService = evaluationService;
        this.experimentService = experimentService;
    }

    @GetMapping("/evaluate")
    public Map<String, Object> evaluateFlag(
            @RequestParam String apiKey,
            @RequestParam String flag,
            @RequestParam(defaultValue = "guest_user") String userId,
            @RequestParam(defaultValue = "FREE") String plan) {

        Map<String, Object> result = evaluationService.evaluate(apiKey, flag, userId, plan);
        String reason = String.valueOf(result.get("reason"));

        if (!"INVALID_API_KEY".equals(reason) && !"FLAG_NOT_FOUND".equals(reason)) {
            boolean enabled = Boolean.TRUE.equals(result.get("enabled"));
            experimentService.recordImpression(apiKey, flag, enabled);
        }
        return result;
    }

    @PostMapping("/track-conversion")
    public Map<String, String> trackConversion(
            @RequestParam String apiKey,
            @RequestParam String flag,
            @RequestParam boolean enabled) {

        experimentService.recordConversion(apiKey, flag, enabled);
        return Map.of("status", "CONVERSION_RECORDED", "flag", flag, "variant", enabled ? "TREATMENT_ON" : "CONTROL_OFF");
    }
}