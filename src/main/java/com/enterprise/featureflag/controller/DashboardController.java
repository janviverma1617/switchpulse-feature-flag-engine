package com.enterprise.featureflag.controller;

import com.enterprise.featureflag.entity.Tenant;
import com.enterprise.featureflag.entity.User;
import com.enterprise.featureflag.repository.AuditLogRepository;
import com.enterprise.featureflag.repository.FeatureFlagRepository;
import com.enterprise.featureflag.repository.UserRepository;
import com.enterprise.featureflag.service.FlagManagementService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final FeatureFlagRepository flagRepository;
    private final AuditLogRepository auditLogRepository;
    private final FlagManagementService flagManagementService;

    public DashboardController(UserRepository userRepository,
                               FeatureFlagRepository flagRepository,
                               AuditLogRepository auditLogRepository,
                               FlagManagementService flagManagementService) {
        this.userRepository = userRepository;
        this.flagRepository = flagRepository;
        this.auditLogRepository = auditLogRepository;
        this.flagManagementService = flagManagementService;
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping
    public String viewDashboard(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        Tenant tenant = user.getTenant();

        model.addAttribute("user", user);
        model.addAttribute("tenant", tenant);
        model.addAttribute("flags", flagRepository.findByTenantId(tenant.getId()));
        model.addAttribute("auditLogs", auditLogRepository.findTop15ByTenantIdOrderByTimestampDesc(tenant.getId()));
        return "dashboard";
    }

    @PostMapping("/flags/create")
    public String createFlag(@RequestParam String flagKey,
                             @RequestParam int rolloutPercentage,
                             @RequestParam String targetPlan,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(auth);
        try {
            flagManagementService.createFlag(flagKey, rolloutPercentage, targetPlan, user.getTenant(), user.getEmail());
            redirectAttributes.addFlashAttribute("success", "Feature flag '" + flagKey + "' created!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/flags/{id}/toggle")
    public String toggleFlag(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        flagManagementService.toggleFlag(id, user.getTenant(), user.getEmail());
        return "redirect:/dashboard";
    }

    @PostMapping("/flags/{id}/update")
    public String updateFlag(@PathVariable Long id,
                             @RequestParam int rolloutPercentage,
                             @RequestParam String targetPlan,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(auth);
        flagManagementService.updateRules(id, rolloutPercentage, targetPlan, user.getTenant(), user.getEmail());
        redirectAttributes.addFlashAttribute("success", "Flag rules updated & cache refreshed!");
        return "redirect:/dashboard";
    }

    @PostMapping("/flags/{id}/delete")
    public String deleteFlag(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(auth);
        flagManagementService.deleteFlag(id, user.getTenant(), user.getEmail());
        redirectAttributes.addFlashAttribute("success", "Feature flag deleted.");
        return "redirect:/dashboard";
    }
}