package com.enterprise.featureflag.controller;

import com.enterprise.featureflag.entity.Tenant;
import com.enterprise.featureflag.entity.User;
import com.enterprise.featureflag.repository.TenantRepository;
import com.enterprise.featureflag.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class AuthController {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(TenantRepository tenantRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerCompany(@RequestParam String companyName,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  Model model) {
        if (tenantRepository.existsByCompanyName(companyName)) {
            model.addAttribute("error", "Company name is already registered.");
            return "register";
        }
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use.");
            return "register";
        }

        // Generate unique tenant API Key
        String generatedApiKey = "ff_live_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        Tenant tenant = tenantRepository.save(new Tenant(companyName, generatedApiKey));

        // Create Admin user linked to this tenant
        User adminUser = new User(email, passwordEncoder.encode(password), "ADMIN", tenant);
        userRepository.save(adminUser);

        return "redirect:/login?registered";
    }

   
}