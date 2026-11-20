package com.enterprise.featureflag.repository;

import com.enterprise.featureflag.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByApiKey(String apiKey);
    boolean existsByCompanyName(String companyName);
}