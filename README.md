# ⚡ Smart Recommendation Service Management Platform

> **Capstone Project #256 • Object-Oriented Technologies using Java (Semester III)**  
> **United Nations SDG 9: Industry, Innovation & Infrastructure**  
> **Category:** AI, Data Science & Intelligent Systems (Projects 251–260)  
> **Institution:** NIET Greater Noida

---

## 📌 Problem Statement Reference (Project #256 vs. Project #23)

| Metric | Project #23 (Intelligent Course Recommendation) | Project #256 (Smart Recommendation Service Platform) |
| :--- | :--- | :--- |
| **Problem Statement** | *"Students often face difficulties selecting suitable electives and learning paths. Design a Java-based recommendation platform that suggests courses based on interests, performance, and career goals."* | *"Organizations providing recommendation-based services require scalable user and content management systems. Develop a Java-based recommendation service administration platform."* |
| **UN SDG Goal** | **SDG 4: Quality Education** | **SDG 9: Industry, Innovation & Infrastructure** |
| **System Scope** | Student course elective store & wallet | **Enterprise Recommendation Service Administration, Multi-Tenant Pipelines, Catalog Management, A/B Testing & Governance** |
| **Target Users** | Students & Course Instructors | **Platform Super Admins, Recommendation Service Directors, Content Curators, Data Analysts & API Consumers** |

---

## 🌟 Executive Overview

The **Smart Recommendation Service Management Platform** is a scalable, enterprise-grade administration and orchestration control plane for recommendation-driven organizations. Built as a self-contained, high-performance Java application with zero external framework dependencies, it provides full end-to-end management of recommendation engines, content catalogs, user cohorts, A/B model experiments, business guardrails, and telemetry.

The platform includes a **Dark-Slate Swing Desktop GUI**, **5 polymorphic recommendation engines**, **real-time taste vector gradient adaptation**, **background concurrency daemons**, and an **embedded HTTP REST API server on port 8080** serving live inference and an interactive HTML web console.

---

## 🚀 How to Run

### Method 1: Double-Click Batch File (Recommended)
Simply double-click `run.bat` in Windows Explorer.

### Method 2: Single-File Direct Execution (Java 11+)
```bash
java SmartRecommendationPlatform.java
```

### Method 3: Pre-Compiled Standalone JAR
```bash
java -jar SmartRecommendationPlatform.jar
```

### Method 4: Clean Rebuild
Run `build.bat` or execute:
```bash
javac SmartRecommendationPlatform.java
jar cfm SmartRecommendationPlatform.jar manifest.txt *.class
```

---

## 👥 Pre-Configured Administrative & Operator Profiles

Switch instantly between roles via the top-header role dropdown or use credentials for API access:

| Role | Name | Email | Permissions & Focus Area |
| :--- | :--- | :--- | :--- |
| **Platform Super Admin** | Dr. Alice Morgan | `admin@platform.com` | Global infrastructure control, system SLA, API key generation, data reset |
| **Service Director** | Darius Vance | `service.lead@platform.com` | Recommendation pipelines, hyperparameter tuning, model retraining, A/B experiments |
| **Catalog Curator** | Elena Rostova | `catalog.curator@platform.com` | Content catalog ingestion, 8-dim vector embeddings, metadata, stock status |
| **Platform Analyst** | Marcus Sterling | `analyst@platform.com` | Telemetry event logs, CTR, conversion rate analytics, CSV report exports |
| **API Client Developer** | Chloe Zhao | `api.dev@client.com` | REST API integration, endpoint testing, live sandbox inference evaluation |

---

## 🎯 Architecture & Key Features

### 1. ⚙️ Multi-Tenant Recommendation Services & Pipelines
Manage recommendation services across diverse enterprise verticals:
- **E-Commerce Dynamic Product Recommender** (`SVC-ECOMM-01`): Hybrid Ensemble for retail shopping.
- **OTT Video Streaming Feed Recommender** (`SVC-STREAM-02`): Collaborative filtering for movies & series.
- **Audio & Music Flow Recommender** (`SVC-AUDIO-03`): Contextual session recency for music tracks.
- **Digital News & Editorial Recommender** (`SVC-NEWS-04`): Content-based feature similarity for articles.
- **Enterprise Talent & Skill Matcher** (`SVC-TALENT-05`): Hybrid matching for engineering & design jobs.

*Hyperparameter Controls:* Tune Algorithm Engine, Target QPS, Cache TTL, Novelty Factor (0.0 to 1.0), and Diversity (MMR) Weight.

### 2. 🧠 Five (5) Polymorphic Recommendation Engines
1. **Collaborative Filtering Engine**: Computes user-user interaction overlap and item co-occurrence matrices.
2. **Content-Based Filtering Engine**: Calculates 8-dimensional cosine vector similarity between user taste vectors and item feature embeddings.
3. **Hybrid Ensemble Engine**: Multi-task weighted blend (Collaborative + Content + Trending) with Maximal Marginal Relevance (MMR) diversity re-ranking to prevent filter bubbles.
4. **Trending & Popularity Velocity Engine**: Time-decayed velocity score ($e^{-\lambda \Delta t}$) for cold-start exploration.
5. **Contextual Session Engine**: Recency-weighted trajectory modeling focusing on the user's last 5 interactions.

### 3. 📦 Scalable Content Catalog Management
- Omnichannel catalog supporting items across Electronics, Computing, Apparel, Sci-Fi Video, Lo-Fi Music, AI Research, and Talent Jobs.
- Full CRUD: Add Item modal with validation, real-time search with instant filtering, category & service filters, and deletion.
- Real-time tracking of item View Count, Click Count, Conversion Count, CTR (Click-Through Rate), and Stock Inventory.

### 4. 👥 User Profiles & Dynamic Taste Vectors
- User directory segmented into **Power Users**, **Casual Explorers**, **Cold-Start / New Users**, **VIP High Spenders**, and **At-Risk / Churn Profiles**.
- Visual Category Affinity visualizer with real-time progress bars for each category.
- **Dynamic Online Learning**: User preference vectors update instantly via gradient steps whenever interactions occur.

### 5. ⚡ Recommendation Studio (Interactive Sandbox / Playground)
- Live interactive workbench to simulate recommendation inference:
  - Select User Profile, Target Service, Algorithm Override, Top-K candidates (1–15), Diversity slider, and Category filter.
  - Generates ranked **Recommendation Cards** with match score percentage bar and **Explainable AI (XAI)** rationale.
  - **Live Feedback Simulator on each card**: `👁️ View`, `🖱️ Click`, `❤️ Like`, `💰 Convert`, and `❌ Dislike`.
  - Pressing any button logs the event to the telemetry stream, updates the user's taste vector, and updates platform metrics in real time!

### 6. 🧪 A/B Testing & Model Experiments
- Configure and run experiments (Variant A Control vs Variant B Challenger with custom split ratio).
- Real-time tracking of Impressions, Clicks, Conversions, CTR%, and statistical winner designation.
- Actions: Start/Pause, Create Experiment, and Rollout Winner.

### 7. 🛡️ Business Rules & Guardrails Engine
Define live business logic overriding raw algorithmic scores:
- `BOOST_CATEGORY`: Boost item scores in target category (e.g. +20% for Electronics).
- `BURY_LOW_STOCK`: Deprioritize items with inventory < 10 units (-30%).
- `MIN_RATING_FILTER`: Quality guardrail excluding items with rating < 3.8★.
- `PIN_TOP_RANK`: Guarantee priority placement for sponsored/flagship items.
- Toggle rules ON/OFF with instant effect on the Recommendation Studio.

### 8. 🔑 API Key Governance & Multi-Tenant Rate Limiting
- Issue client credentials across **Starter** (120 RPM), **Professional** (600 RPM), and **Enterprise** (3,000 RPM) tiers.
- Token bucket rate limiter tracking requests per minute, lifetime calls, and status.

### 9. 🌐 Embedded HTTP REST API Server (Port 8080)
Runs on `http://localhost:8080/` with interactive Web Console and JSON endpoints:
- `GET /` & `GET /docs` : Interactive Dark Web UI Console & documentation
- `GET /api/v1/health` : System health, uptime & JVM memory stats
- `GET /api/v1/services` : List of recommendation services
- `GET /api/v1/catalog` : Catalog items query (`?serviceId=...&category=...`)
- `GET /api/v1/users` : User profiles and taste vectors
- `GET /api/v1/recommend?serviceId=...&userId=...&k=5` : Live recommendation inference
- `POST /api/v1/events` : Real-time interaction event ingestion
- `GET /api/v1/metrics` : SLA percentiles (P50, P95, P99), QPS, cache hit ratio

---

## 🎓 Academic Curriculum & Syllabus Mapping (Semester III)

| Unit | Topic | Platform Implementation |
| :--- | :--- | :--- |
| **Unit 1** | **Encapsulation & Core OOP** | `PlatformUser`, `RecommendationService`, `ContentItem`, `UserProfile`, `InteractionEvent`, `RecommendationResult`, `ABExperiment`, `BusinessRule`, `ApiKeyCredential`. Robust constructors, state validation, and immutable IDs. |
| **Unit 2** | **Inheritance, Polymorphism & Streams** | `IRecommendationEngine` interface, `AbstractRecommendationEngine` base class, 5 concrete algorithm engines (`CollaborativeFilteringEngine`, `ContentBasedFilteringEngine`, `HybridEnsembleEngine`, `TrendingPopularityEngine`, `ContextualSessionEngine`). Java 8+ Stream pipelines (`.filter().sorted().map().collect()`). |
| **Unit 3** | **Exception Handling & Validations** | Custom exception hierarchy: `RecommendationPlatformException`, `ServiceNotFoundException`, `ItemNotFoundException`, `UserNotFoundException`, `QuotaExceededException`, `InvalidRuleException`, `ModelExecutionException`. Numerical and string validation guards. |
| **Unit 4** | **Concurrency, Networking & File I/O** | `PlatformTelemetryDaemon` background thread, thread-safe `ConcurrentHashMap`, `CopyOnWriteArrayList`, atomic counters (`AtomicLong`, `AtomicInteger`), embedded `com.sun.net.httpserver.HttpServer`, CSV exporters for catalog, events, and recommendations. |
| **Unit 5** | **Modern GUI & UX** | Dark-themed Swing desktop GUI with 9 tabs: Executive Dashboard, Services & Pipelines, Content Catalog, User Profiles, Recommendation Studio, A/B Testing, Business Rules, API Keys, and REST API Live Console. Custom renderers and canvas charts. |

---

## 📊 File Structure

```
SmartRecommendationPlatform/
├── SmartRecommendationPlatform.java  # Complete, self-contained single-file Java source
├── SmartRecommendationPlatform.jar   # Standalone executable JAR with bundled manifest
├── build.bat                         # One-click automated compilation & packaging script
├── run.bat                           # Instant execution batch script
├── README.md                         # Detailed system documentation & academic mapping
└── CP_OOT_Sample Problem Statements...pdf # Original university problem statements reference
```

---

*Engineered with precision for NIET B.Tech Computer Science / AI / DS Capstone Evaluation (Semester III).*
