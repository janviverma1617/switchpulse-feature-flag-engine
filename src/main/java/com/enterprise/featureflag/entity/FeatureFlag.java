package com.enterprise.featureflag.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feature_flags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "flag_key"})
})
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flag_key", nullable = false)
    private String flagKey;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private int rolloutPercentage; // 0 to 100

    @Column(nullable = false)
    private String targetPlan; // "ALL" or "PRO"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    public FeatureFlag() {}

    public FeatureFlag(String flagKey, boolean enabled, int rolloutPercentage, String targetPlan, Tenant tenant) {
        this.flagKey = flagKey;
        this.enabled = enabled;
        this.rolloutPercentage = rolloutPercentage;
        this.targetPlan = targetPlan;
        this.tenant = tenant;
    }
    @Column(nullable = false)
    private int controlViews = 0;

    @Column(nullable = false)
    private int controlConversions = 0;

    @Column(nullable = false)
    private int treatmentViews = 0;

    @Column(nullable = false)
    private int treatmentConversions = 0;

    public int getControlViews() { return controlViews; }
    public void setControlViews(int controlViews) { this.controlViews = controlViews; }

    public int getControlConversions() { return controlConversions; }
    public void setControlConversions(int controlConversions) { this.controlConversions = controlConversions; }

    public int getTreatmentViews() { return treatmentViews; }
    public void setTreatmentViews(int treatmentViews) { this.treatmentViews = treatmentViews; }

    public int getTreatmentConversions() { return treatmentConversions; }
    public void setTreatmentConversions(int treatmentConversions) { this.treatmentConversions = treatmentConversions; }

    public double getControlConversionRate() {
        return controlViews == 0 ? 0.0 : Math.round((controlConversions * 1000.0) / controlViews) / 10.0;
    }

    public double getTreatmentConversionRate() {
        return treatmentViews == 0 ? 0.0 : Math.round((treatmentConversions * 1000.0) / treatmentViews) / 10.0;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlagKey() { return flagKey; }
    public void setFlagKey(String flagKey) { this.flagKey = flagKey; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getRolloutPercentage() { return rolloutPercentage; }
    public void setRolloutPercentage(int rolloutPercentage) { this.rolloutPercentage = rolloutPercentage; }

    public String getTargetPlan() { return targetPlan; }
    public void setTargetPlan(String targetPlan) { this.targetPlan = targetPlan; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
}