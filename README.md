# SwitchPulse — Multi-Tenant SaaS Feature-Flag & A/B Experimentation Engine

A lightweight, enterprise-grade feature-flagging and A/B experimentation platform built with **Java, Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, and MySQL**.

## Key Features
- **Multi-Tenant Architecture:** Isolated organization workspaces with unique API key generation (`ff_live_...`).
- **Dynamic Flag Evaluation API (`GET /switchpulse/api/evaluate`):** Supports global kill-switches, `PRO` tier targeting, and deterministic percentage rollouts (`0–100%`).
- **In-Memory Caching:** Uses `@Cacheable` and `@CacheEvict` for low-latency flag evaluations.
- **Real-Time A/B Conversion Analytics:** Tracks Control (Flag OFF) vs. Treatment (Flag ON) impressions and conversions.
- **Enterprise Audit Trail:** Logs every flag creation, toggle, and rule update with user identity and timestamp.
- **Live Interactive Playground:** Built-in simulated storefront demonstrating live UI updates and conversion tracking.

## How to Run Locally
1. Create a MySQL database named `feature_flag_db`.
2. Update `spring.datasource.username` and `spring.datasource.password` in `src/main/resources/application.properties`.
3. Run `FeatureflagApplication.java` and open `http://localhost:8080/switchpulse/register`.
