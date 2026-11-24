// ====================================================================================================
// SMART RECOMMENDATION SERVICE MANAGEMENT PLATFORM
// Capstone Project #256 | Object-Oriented Technologies (Semester III)
// United Nations SDG 9: Industry, Innovation & Infrastructure
// ====================================================================================================
// Architectural & Curriculum Alignment:
// ✔ Unit 1: OOP Core - Encapsulation, Enums, Constructors, Immutable IDs, State Management
// ✔ Unit 2: Advanced OOP - Polymorphism, Strategy Pattern, Interfaces, Abstract Classes, Java Streams
// ✔ Unit 3: Robustness - Custom Hierarchical Exceptions, String Formatting, Comprehensive Validations
// ✔ Unit 4: Systems & Concurrency - Thread Daemons, Atomic Counters, ConcurrentHashMap, Embedded HTTP Server, CSV I/O
// ✔ Unit 5: Modern UI/UX - Sleek Dark Swing GUI, Multi-Domain Pipelines, Live Recommendation Studio,
//           A/B Testing, Business Rules Guardrails, Client API Key Governance, Real-Time Telemetry
// ====================================================================================================

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;
import com.sun.net.httpserver.*;

// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 3: CUSTOM EXCEPTION HIERARCHY
// ════════════════════════════════════════════════════════════════════════════════════════════════

class RecommendationPlatformException extends Exception {
    public RecommendationPlatformException(String message) {
        super(message);
    }
    public RecommendationPlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ServiceNotFoundException extends RecommendationPlatformException {
    public ServiceNotFoundException(String serviceId) {
        super("Recommendation Service not found with ID: " + serviceId);
    }
}

class ItemNotFoundException extends RecommendationPlatformException {
    public ItemNotFoundException(String itemId) {
        super("Catalog Item not found with ID: " + itemId);
    }
}

class UserNotFoundException extends RecommendationPlatformException {
    public UserNotFoundException(String userId) {
        super("User Profile not found with ID: " + userId);
    }
}

class QuotaExceededException extends RecommendationPlatformException {
    public QuotaExceededException(String keyId, int limit) {
        super("API Rate Limit Quota exceeded for Key [" + keyId + "]. Limit is " + limit + " RPM.");
    }
}

class InvalidRuleException extends RecommendationPlatformException {
    public InvalidRuleException(String msg) {
        super("Invalid Business Rule configuration: " + msg);
    }
}

class ModelExecutionException extends RecommendationPlatformException {
    public ModelExecutionException(String msg, Throwable cause) {
        super("Recommendation Model execution failed: " + msg, cause);
    }
}

// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 1 & 2: CORE PLATFORM ENUMS
// ════════════════════════════════════════════════════════════════════════════════════════════════

enum ServiceDomain {
    ECOMMERCE("E-Commerce Retail", "[EC]"),
    STREAMING_VIDEO("Video & OTT Streaming", "[VID]"),
    STREAMING_MUSIC("Audio & Music Feeds", "[AUD]"),
    NEWS_PUBLISHING("Digital News & Articles", "[NEWS]"),
    JOB_PORTAL("Talent & Job Matching", "[JOB]"),
    FINTECH("FinTech & Wealth Advisory", "[FIN]");

    private final String displayName;
    private final String icon;

    ServiceDomain(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    @Override public String toString() { return displayName; }
}

enum AlgorithmType {
    COLLABORATIVE_FILTERING("Collaborative Filtering (Matrix / Co-Occurrence)"),
    CONTENT_BASED("Content-Based (Feature Cosine Similarity)"),
    HYBRID_ENSEMBLE("Hybrid Ensemble (Weighted Blend & Diversity MMR)"),
    TRENDING_POPULARITY("Trending & Popularity (Time-Decayed Fallback)"),
    CONTEXTUAL_SESSION("Contextual Session (Recent Trajectory Decay)");

    private final String label;
    AlgorithmType(String label) { this.label = label; }
    public String getLabel() { return label; }
    @Override public String toString() { return label; }
}

enum ServiceStatus {
    ACTIVE("Operational", new Color(16, 185, 129)),
    DEGRADED("High Latency", new Color(245, 158, 11)),
    MAINTENANCE("Maintenance", new Color(59, 130, 246)),
    OFFLINE("Offline", new Color(239, 68, 68));

    private final String label;
    private final Color color;
    ServiceStatus(String label, Color color) { this.label = label; this.color = color; }
    public String getLabel() { return label; }
    public Color getColor() { return color; }
}

enum ItemStatus {
    ACTIVE("Active", new Color(16, 185, 129)),
    LOW_STOCK("Low Stock", new Color(245, 158, 11)),
    INACTIVE("Archived", new Color(156, 163, 175)),
    FLAGGED("Flagged", new Color(239, 68, 68));

    private final String label;
    private final Color color;
    ItemStatus(String label, Color color) { this.label = label; this.color = color; }
    public String getLabel() { return label; }
    public Color getColor() { return color; }
}

enum UserSegment {
    POWER_USER("Power User (High Affinity)", new Color(139, 92, 246)),
    CASUAL_EXPLORER("Casual Explorer", new Color(59, 130, 246)),
    NEW_ONBOARDING("Cold-Start / New User", new Color(16, 185, 129)),
    HIGH_SPENDER("VIP / High Value", new Color(245, 158, 11)),
    CHURN_RISK("At-Risk / Inactive", new Color(239, 68, 68));

    private final String label;
    private final Color badgeColor;
    UserSegment(String label, Color badgeColor) { this.label = label; this.badgeColor = badgeColor; }
    public String getLabel() { return label; }
    public Color getBadgeColor() { return badgeColor; }
    @Override public String toString() { return label; }
}

enum EventType {
    VIEW("View", 1.0, "[V]"),
    CLICK("Click", 2.0, "[C]"),
    BOOKMARK("Bookmark", 3.0, "[B]"),
    LIKE("Like", 3.5, "[L]"),
    ADD_TO_CART("Add to Cart", 4.0, "[+]"),
    PURCHASE("Purchase / Convert", 5.0, "[$]"),
    DISLIKE("Dismiss / Dislike", -2.5, "[X]"),
    SHARE("Social Share", 4.5, "[S]");

    private final String label;
    private final double weight;
    private final String icon;

    EventType(String label, double weight, String icon) {
        this.label = label;
        this.weight = weight;
        this.icon = icon;
    }

    public String getLabel() { return label; }
    public double getWeight() { return weight; }
    public String getIcon() { return icon; }
    @Override public String toString() { return label; }
}

enum ExperimentStatus {
    DRAFT, RUNNING, PAUSED, CONCLUDED
}

enum RuleType {
    BOOST_CATEGORY("Boost Category"),
    BURY_LOW_STOCK("Bury Low Inventory"),
    EXCLUDE_ITEM("Exclude Item / Status"),
    PIN_TOP_RANK("Pin to Top Rank"),
    MIN_RATING_FILTER("Minimum Rating Guardrail");

    private final String description;
    RuleType(String desc) { this.description = desc; }
    public String getDescription() { return description; }
    @Override public String toString() { return description; }
}

enum ClientTier {
    STARTER("Starter (120 RPM)", 120),
    PROFESSIONAL("Professional (600 RPM)", 600),
    ENTERPRISE("Enterprise (3,000 RPM)", 3000);

    private final String label;
    private final int rateLimit;
    ClientTier(String label, int rateLimit) { this.label = label; this.rateLimit = rateLimit; }
    public String getLabel() { return label; }
    public int getRateLimit() { return rateLimit; }
    @Override public String toString() { return label; }
}

enum AdminRole {
    PLATFORM_ADMIN("Super Administrator", "Full Platform Control, Infrastructure, Keys & Security"),
    SERVICE_MANAGER("Recommendation Director", "Pipelines, Hyperparameters & A/B Experiments"),
    CONTENT_CURATOR("Catalog Curator", "Content Ingestion, Metadata & Category Rules"),
    DATA_ANALYST("Platform Analyst", "Metrics, Telemetry, CTR & Conversion Auditing"),
    API_CONSUMER("API Client Developer", "Integration Testing, Docs & Sandbox Evaluation");

    private final String title;
    private final String permissions;
    AdminRole(String title, String permissions) { this.title = title; this.permissions = permissions; }
    public String getTitle() { return title; }
    public String getPermissions() { return permissions; }
}

// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 1: DOMAIN ENTITIES & ROLES (ENCAPSULATION & INHERITANCE)
// ════════════════════════════════════════════════════════════════════════════════════════════════

abstract class PlatformUser {
    protected final String userId;
    protected String name;
    protected String email;
    protected AdminRole role;
    protected boolean active;
    protected LocalDateTime lastLogin;

    public PlatformUser(String userId, String name, String email, AdminRole role) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.active = true;
        this.lastLogin = LocalDateTime.now();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public AdminRole getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void recordLogin() { this.lastLogin = LocalDateTime.now(); }

    public abstract String getRoleBadge();
}

class SuperAdminUser extends PlatformUser {
    private final String securityClearance;

    public SuperAdminUser(String userId, String name, String email, String clearance) {
        super(userId, name, email, AdminRole.PLATFORM_ADMIN);
        this.securityClearance = clearance;
    }

    public String getSecurityClearance() { return securityClearance; }

    @Override
    public String getRoleBadge() {
        return "Role: Super Admin (Global)";
    }
}

class ServiceManagerUser extends PlatformUser {
    private String primaryDomain;

    public ServiceManagerUser(String userId, String name, String email, String domain) {
        super(userId, name, email, AdminRole.SERVICE_MANAGER);
        this.primaryDomain = domain;
    }

    public String getPrimaryDomain() { return primaryDomain; }
    public void setPrimaryDomain(String domain) { this.primaryDomain = domain; }

    @Override
    public String getRoleBadge() {
        return "SERVICE_LEAD [" + primaryDomain + "]";
    }
}

class ContentCuratorUser extends PlatformUser {
    private String assignedCatalog;

    public ContentCuratorUser(String userId, String name, String email, String catalog) {
        super(userId, name, email, AdminRole.CONTENT_CURATOR);
        this.assignedCatalog = catalog;
    }

    public String getAssignedCatalog() { return assignedCatalog; }

    @Override
    public String getRoleBadge() {
        return "CURATOR [" + assignedCatalog + "]";
    }
}

class DataAnalystUser extends PlatformUser {
    private String analyticsSquad;

    public DataAnalystUser(String userId, String name, String email, String squad) {
        super(userId, name, email, AdminRole.DATA_ANALYST);
        this.analyticsSquad = squad;
    }

    public String getAnalyticsSquad() { return analyticsSquad; }

    @Override
    public String getRoleBadge() {
        return "ANALYST [" + analyticsSquad + "]";
    }
}

class ApiConsumerUser extends PlatformUser {
    private String clientId;

    public ApiConsumerUser(String userId, String name, String email, String clientId) {
        super(userId, name, email, AdminRole.API_CONSUMER);
        this.clientId = clientId;
    }

    public String getClientId() { return clientId; }

    @Override
    public String getRoleBadge() {
        return "API_CLIENT [" + clientId + "]";
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// RECOMMENDATION SERVICE DEFINITION & METRICS (UNIT 1)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class RecommendationService {
    private final String serviceId;
    private String name;
    private ServiceDomain domain;
    private AlgorithmType algorithmType;
    private String modelVersion;
    private ServiceStatus status;
    private int targetQps;
    private double avgLatencyMs;
    private int cacheTtlSeconds;
    private double noveltyFactor;   // 0.0 to 1.0 (penalize popular items)
    private double diversityWeight; // 0.0 to 1.0 (MMR re-ranking)
    private final AtomicLong totalRequests;
    private final AtomicLong totalConversions;
    private LocalDateTime lastUpdated;

    public RecommendationService(String serviceId, String name, ServiceDomain domain,
                                 AlgorithmType algorithmType, String modelVersion,
                                 int targetQps, int cacheTtlSeconds,
                                 double noveltyFactor, double diversityWeight) {
        this.serviceId = Objects.requireNonNull(serviceId, "Service ID required");
        this.name = Objects.requireNonNull(name, "Name required");
        this.domain = Objects.requireNonNull(domain, "Domain required");
        this.algorithmType = Objects.requireNonNull(algorithmType, "Algorithm required");
        this.modelVersion = modelVersion != null ? modelVersion : "v1.0.0";
        this.status = ServiceStatus.ACTIVE;
        this.targetQps = Math.max(1, targetQps);
        this.avgLatencyMs = 12.5; // Baseline latency in ms
        this.cacheTtlSeconds = Math.max(0, cacheTtlSeconds);
        this.noveltyFactor = Math.min(1.0, Math.max(0.0, noveltyFactor));
        this.diversityWeight = Math.min(1.0, Math.max(0.0, diversityWeight));
        this.totalRequests = new AtomicLong(0);
        this.totalConversions = new AtomicLong(0);
        this.lastUpdated = LocalDateTime.now();
    }

    public String getServiceId() { return serviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; this.lastUpdated = LocalDateTime.now(); }
    public ServiceDomain getDomain() { return domain; }
    public void setDomain(ServiceDomain domain) { this.domain = domain; }
    public AlgorithmType getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(AlgorithmType algo) { this.algorithmType = algo; this.lastUpdated = LocalDateTime.now(); }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String v) { this.modelVersion = v; this.lastUpdated = LocalDateTime.now(); }
    public ServiceStatus getStatus() { return status; }
    public void setStatus(ServiceStatus status) { this.status = status; }
    public int getTargetQps() { return targetQps; }
    public void setTargetQps(int qps) { this.targetQps = qps; }
    public double getAvgLatencyMs() { return Math.round(avgLatencyMs * 10.0) / 10.0; }
    public int getCacheTtlSeconds() { return cacheTtlSeconds; }
    public void setCacheTtlSeconds(int ttl) { this.cacheTtlSeconds = ttl; }
    public double getNoveltyFactor() { return noveltyFactor; }
    public void setNoveltyFactor(double nf) { this.noveltyFactor = Math.min(1.0, Math.max(0.0, nf)); }
    public double getDiversityWeight() { return diversityWeight; }
    public void setDiversityWeight(double dw) { this.diversityWeight = Math.min(1.0, Math.max(0.0, dw)); }
    public long getTotalRequests() { return totalRequests.get(); }
    public long getTotalConversions() { return totalConversions.get(); }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public synchronized void recordRequest(double latencyMs, boolean converted) {
        totalRequests.incrementAndGet();
        if (converted) {
            totalConversions.incrementAndGet();
        }
        // Exponential moving average for latency
        this.avgLatencyMs = (this.avgLatencyMs * 0.9) + (latencyMs * 0.1);
        if (this.avgLatencyMs > 45.0) {
            this.status = ServiceStatus.DEGRADED;
        } else if (this.status == ServiceStatus.DEGRADED) {
            this.status = ServiceStatus.ACTIVE;
        }
    }

    public double getConversionRate() {
        long reqs = totalRequests.get();
        if (reqs == 0) return 0.0;
        return Math.round(((double) totalConversions.get() / reqs) * 1000.0) / 10.0;
    }

    @Override
    public String toString() {
        return serviceId + " - " + name;
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// CONTENT CATALOG ITEM (UNIT 1)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class ContentItem {
    private final String itemId;
    private String serviceId;
    private String title;
    private String category;
    private ServiceDomain domain;
    private final List<String> tags;
    private double price;
    private double rating;
    private int inventory;
    private final double[] featureVector; // 8-dimensional normalized embedding
    private ItemStatus status;
    private final AtomicInteger viewCount;
    private final AtomicInteger clickCount;
    private final AtomicInteger conversionCount;
    private final LocalDateTime createdDate;

    public ContentItem(String itemId, String serviceId, String title, String category,
                       ServiceDomain domain, List<String> tags, double price, double rating,
                       int inventory, double[] featureVector) {
        this.itemId = Objects.requireNonNull(itemId, "Item ID required");
        this.serviceId = Objects.requireNonNull(serviceId, "Service ID required");
        this.title = Objects.requireNonNull(title, "Title required");
        this.category = Objects.requireNonNull(category, "Category required");
        this.domain = Objects.requireNonNull(domain, "Domain required");
        this.tags = new CopyOnWriteArrayList<>(tags != null ? tags : Collections.emptyList());
        this.price = Math.max(0.0, price);
        this.rating = Math.min(5.0, Math.max(1.0, rating));
        this.inventory = Math.max(0, inventory);
        this.featureVector = featureVector != null && featureVector.length == 8 ?
                featureVector : new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        this.status = inventory > 0 ? ItemStatus.ACTIVE : ItemStatus.LOW_STOCK;
        this.viewCount = new AtomicInteger(10 + (int)(Math.random() * 40));
        this.clickCount = new AtomicInteger(2 + (int)(Math.random() * 10));
        this.conversionCount = new AtomicInteger(1 + (int)(Math.random() * 4));
        this.createdDate = LocalDateTime.now().minusDays((long)(Math.random() * 30));
    }

    public String getItemId() { return itemId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String sId) { this.serviceId = sId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public ServiceDomain getDomain() { return domain; }
    public void setDomain(ServiceDomain domain) { this.domain = domain; }
    public List<String> getTags() { return tags; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getRating() { return Math.round(rating * 10.0) / 10.0; }
    public void setRating(double r) { this.rating = Math.min(5.0, Math.max(1.0, r)); }
    public int getInventory() { return inventory; }
    public void setInventory(int inv) {
        this.inventory = Math.max(0, inv);
        if (this.inventory == 0 && this.status == ItemStatus.ACTIVE) {
            this.status = ItemStatus.LOW_STOCK;
        }
    }
    public double[] getFeatureVector() { return featureVector; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public int getViewCount() { return viewCount.get(); }
    public int getClickCount() { return clickCount.get(); }
    public int getConversionCount() { return conversionCount.get(); }
    public LocalDateTime getCreatedDate() { return createdDate; }

    public void incrementViews() { viewCount.incrementAndGet(); }
    public void incrementClicks() { clickCount.incrementAndGet(); }
    public void incrementConversions() { conversionCount.incrementAndGet(); }

    public double getCTR() {
        int v = viewCount.get();
        if (v == 0) return 0.0;
        return Math.round(((double) clickCount.get() / v) * 1000.0) / 10.0;
    }

    public double getConversionRate() {
        int c = clickCount.get();
        if (c == 0) return 0.0;
        return Math.round(((double) conversionCount.get() / c) * 1000.0) / 10.0;
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// USER PROFILE & DYNAMIC TASTE VECTOR (UNIT 1)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class UserProfile {
    private final String userId;
    private String name;
    private String email;
    private UserSegment segment;
    private final Map<String, Double> categoryAffinities; // e.g. "Electronics" -> 0.85
    private final double[] preferenceVector; // 8-dimensional user taste representation
    private final AtomicInteger totalInteractions;
    private final LocalDateTime registeredDate;
    private boolean active;

    public UserProfile(String userId, String name, String email, UserSegment segment) {
        this.userId = Objects.requireNonNull(userId, "User ID required");
        this.name = Objects.requireNonNull(name, "Name required");
        this.email = Objects.requireNonNull(email, "Email required");
        this.segment = Objects.requireNonNull(segment, "Segment required");
        this.categoryAffinities = new ConcurrentHashMap<>();
        this.preferenceVector = new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        this.totalInteractions = new AtomicInteger(0);
        this.registeredDate = LocalDateTime.now().minusDays(15 + (int)(Math.random() * 45));
        this.active = true;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public UserSegment getSegment() { return segment; }
    public void setSegment(UserSegment segment) { this.segment = segment; }
    public Map<String, Double> getCategoryAffinities() { return Collections.unmodifiableMap(categoryAffinities); }
    public double[] getPreferenceVector() { return preferenceVector; }
    public int getTotalInteractions() { return totalInteractions.get(); }
    public LocalDateTime getRegisteredDate() { return registeredDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }

    public double getAffinity(String category) {
        return categoryAffinities.getOrDefault(category, 0.2);
    }

    public synchronized void recordInteraction(String category, double[] itemVector, double weight) {
        totalInteractions.incrementAndGet();
        double current = categoryAffinities.getOrDefault(category, 0.2);
        double delta = weight > 0 ? (0.1 * (weight / 5.0)) : -0.15;
        double updated = Math.min(1.0, Math.max(0.05, current + delta));
        categoryAffinities.put(category, Math.round(updated * 100.0) / 100.0);

        // Update preference vector with gradient step
        if (itemVector != null && itemVector.length == 8) {
            double alpha = 0.08 * (weight / 5.0);
            for (int i = 0; i < 8; i++) {
                preferenceVector[i] = Math.min(1.0, Math.max(0.0,
                        (1.0 - alpha) * preferenceVector[i] + alpha * itemVector[i]));
            }
        }
    }

    public void setInitialAffinity(String category, double affinity) {
        categoryAffinities.put(category, Math.min(1.0, Math.max(0.0, affinity)));
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// INTERACTION EVENT TELEMETRY (UNIT 1 & 4)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class InteractionEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String itemId;
    private final String serviceId;
    private final EventType eventType;
    private final double weight;
    private final int dwellTimeMs;
    private final String metadata;

    public InteractionEvent(String eventId, String userId, String itemId, String serviceId,
                            EventType eventType, int dwellTimeMs, String metadata) {
        this.eventId = eventId != null ? eventId : "EVT-" + UUID.randomUUID().toString().substring(0, 8);
        this.timestamp = LocalDateTime.now();
        this.userId = Objects.requireNonNull(userId, "User ID required");
        this.itemId = Objects.requireNonNull(itemId, "Item ID required");
        this.serviceId = Objects.requireNonNull(serviceId, "Service ID required");
        this.eventType = Objects.requireNonNull(eventType, "Event type required");
        this.weight = eventType.getWeight();
        this.dwellTimeMs = Math.max(0, dwellTimeMs);
        this.metadata = metadata != null ? metadata : "";
    }

    public String getEventId() { return eventId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public String getServiceId() { return serviceId; }
    public EventType getEventType() { return eventType; }
    public double getWeight() { return weight; }
    public int getDwellTimeMs() { return dwellTimeMs; }
    public String getMetadata() { return metadata; }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// RECOMMENDATION RESULT & EXPLAINABILITY (UNIT 1)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class RecommendationResult {
    private final ContentItem item;
    private final double score;       // 0.0 to 1.0 (converted to % for GUI)
    private final int rank;
    private final String reason;      // Explainable AI (XAI) rationale
    private final AlgorithmType algorithmUsed;
    private final List<String> appliedRules;

    public RecommendationResult(ContentItem item, double score, int rank, String reason,
                                AlgorithmType algorithmUsed, List<String> appliedRules) {
        this.item = Objects.requireNonNull(item, "Item required");
        this.score = Math.min(1.0, Math.max(0.0, score));
        this.rank = rank;
        this.reason = reason != null ? reason : "Ranked by AI personalization engine.";
        this.algorithmUsed = algorithmUsed;
        this.appliedRules = appliedRules != null ? new ArrayList<>(appliedRules) : Collections.emptyList();
    }

    public ContentItem getItem() { return item; }
    public double getScore() { return score; }
    public int getMatchPercentage() { return (int) Math.round(score * 100.0); }
    public int getRank() { return rank; }
    public String getReason() { return reason; }
    public AlgorithmType getAlgorithmUsed() { return algorithmUsed; }
    public List<String> getAppliedRules() { return appliedRules; }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// A/B TESTING & EXPERIMENTS (UNIT 1 & 4)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class ABExperiment {
    private final String experimentId;
    private String name;
    private String serviceId;
    private AlgorithmType variantA;
    private AlgorithmType variantB;
    private double splitRatio; // 0.0 to 100.0 (e.g. 50.0 for 50/50 split)
    private final AtomicLong impressionsA = new AtomicLong(0);
    private final AtomicLong clicksA = new AtomicLong(0);
    private final AtomicLong conversionsA = new AtomicLong(0);
    private final AtomicLong impressionsB = new AtomicLong(0);
    private final AtomicLong clicksB = new AtomicLong(0);
    private final AtomicLong conversionsB = new AtomicLong(0);
    private ExperimentStatus status;
    private String winner;

    public ABExperiment(String experimentId, String name, String serviceId,
                        AlgorithmType variantA, AlgorithmType variantB, double splitRatio) {
        this.experimentId = experimentId;
        this.name = name;
        this.serviceId = serviceId;
        this.variantA = variantA;
        this.variantB = variantB;
        this.splitRatio = splitRatio;
        this.status = ExperimentStatus.RUNNING;
        this.winner = "IN_PROGRESS";
    }

    public String getExperimentId() { return experimentId; }
    public String getName() { return name; }
    public String getServiceId() { return serviceId; }
    public AlgorithmType getVariantA() { return variantA; }
    public AlgorithmType getVariantB() { return variantB; }
    public double getSplitRatio() { return splitRatio; }
    public ExperimentStatus getStatus() { return status; }
    public void setStatus(ExperimentStatus s) { this.status = s; }
    public String getWinner() { return winner; }
    public void setWinner(String w) { this.winner = w; }

    public long getImpressionsA() { return impressionsA.get(); }
    public long getClicksA() { return clicksA.get(); }
    public long getConversionsA() { return conversionsA.get(); }
    public long getImpressionsB() { return impressionsB.get(); }
    public long getClicksB() { return clicksB.get(); }
    public long getConversionsB() { return conversionsB.get(); }

    public void recordImpressionA() { impressionsA.incrementAndGet(); }
    public void recordClickA() { clicksA.incrementAndGet(); }
    public void recordConversionA() { conversionsA.incrementAndGet(); }
    public void recordImpressionB() { impressionsB.incrementAndGet(); }
    public void recordClickB() { clicksB.incrementAndGet(); }
    public void recordConversionB() { conversionsB.incrementAndGet(); }

    public double getCTRA() {
        long imp = impressionsA.get();
        return imp == 0 ? 0.0 : Math.round(((double) clicksA.get() / imp) * 1000.0) / 10.0;
    }

    public double getCTRB() {
        long imp = impressionsB.get();
        return imp == 0 ? 0.0 : Math.round(((double) clicksB.get() / imp) * 1000.0) / 10.0;
    }

    public double getConvRateA() {
        long c = clicksA.get();
        return c == 0 ? 0.0 : Math.round(((double) conversionsA.get() / c) * 1000.0) / 10.0;
    }

    public double getConvRateB() {
        long c = clicksB.get();
        return c == 0 ? 0.0 : Math.round(((double) conversionsB.get() / c) * 1000.0) / 10.0;
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// BUSINESS RULES & GUARDRAILS (UNIT 1 & 2)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class BusinessRule {
    private final String ruleId;
    private String serviceId;
    private String ruleName;
    private RuleType ruleType;
    private String target; // Category name, Item ID, or threshold
    private double adjustmentFactor; // e.g. +0.25 (boost) or -0.40 (bury)
    private boolean enabled;
    private int priority;

    public BusinessRule(String ruleId, String serviceId, String ruleName,
                        RuleType ruleType, String target, double adjustmentFactor, int priority) {
        this.ruleId = ruleId;
        this.serviceId = serviceId;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.target = target;
        this.adjustmentFactor = adjustmentFactor;
        this.enabled = true;
        this.priority = priority;
    }

    public String getRuleId() { return ruleId; }
    public String getServiceId() { return serviceId; }
    public String getRuleName() { return ruleName; }
    public RuleType getRuleType() { return ruleType; }
    public String getTarget() { return target; }
    public double getAdjustmentFactor() { return adjustmentFactor; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getPriority() { return priority; }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// API KEY CREDENTIALS & RATE LIMITING (UNIT 1 & 4)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class ApiKeyCredential {
    private final String keyId;
    private final String clientName;
    private final String apiKey;
    private final ClientTier tier;
    private final int rateLimitRpm;
    private final AtomicInteger currentMinuteCalls = new AtomicInteger(0);
    private final AtomicLong totalCalls = new AtomicLong(0);
    private boolean active;
    private final LocalDateTime createdDate;
    private volatile int currentMinuteBucket;

    public ApiKeyCredential(String keyId, String clientName, ClientTier tier) {
        this.keyId = keyId;
        this.clientName = clientName;
        this.tier = tier;
        this.rateLimitRpm = tier.getRateLimit();
        this.apiKey = "rec_live_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        this.active = true;
        this.createdDate = LocalDateTime.now();
        this.currentMinuteBucket = LocalDateTime.now().getMinute();
    }

    public String getKeyId() { return keyId; }
    public String getClientName() { return clientName; }
    public String getApiKey() { return apiKey; }
    public String getMaskedKey() {
        return apiKey.substring(0, 11) + "••••••••" + apiKey.substring(apiKey.length() - 4);
    }
    public ClientTier getTier() { return tier; }
    public int getRateLimitRpm() { return rateLimitRpm; }
    public long getTotalCalls() { return totalCalls.get(); }
    public int getCurrentMinuteCalls() { return currentMinuteCalls.get(); }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }
    public LocalDateTime getCreatedDate() { return createdDate; }

    public synchronized boolean checkAndConsumeQuota() {
        int nowMinute = LocalDateTime.now().getMinute();
        if (nowMinute != currentMinuteBucket) {
            currentMinuteBucket = nowMinute;
            currentMinuteCalls.set(0);
        }
        if (currentMinuteCalls.get() >= rateLimitRpm) {
            return false;
        }
        currentMinuteCalls.incrementAndGet();
        totalCalls.incrementAndGet();
        return true;
    }
}


// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 2: RECOMMENDATION ALGORITHM STRATEGIES (POLYMORPHISM, INTERFACES & STREAMS)
// ════════════════════════════════════════════════════════════════════════════════════════════════

interface IRecommendationEngine {
    AlgorithmType getAlgorithmType();
    List<RecommendationResult> generateRecommendations(
            UserProfile user,
            RecommendationService service,
            List<ContentItem> catalog,
            List<InteractionEvent> allEvents,
            List<BusinessRule> rules,
            int k,
            double diversityWeight,
            double noveltyWeight) throws ModelExecutionException;
}

abstract class AbstractRecommendationEngine implements IRecommendationEngine {

    protected double calculateCosineSimilarity(double[] v1, double[] v2) {
        if (v1 == null || v2 == null || v1.length != v2.length) return 0.5;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            normA += v1[i] * v1[i];
            normB += v2[i] * v2[i];
        }
        if (normA == 0.0 || normB == 0.0) return 0.5;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    protected List<RecommendationResult> applyBusinessRules(
            List<RecommendationResult> candidates,
            List<BusinessRule> rules,
            String targetServiceId) {
        if (rules == null || rules.isEmpty()) return candidates;

        List<BusinessRule> activeRules = rules.stream()
                .filter(BusinessRule::isEnabled)
                .filter(r -> r.getServiceId().equals("*") || r.getServiceId().equals(targetServiceId))
                .sorted(Comparator.comparingInt(BusinessRule::getPriority).reversed())
                .collect(Collectors.toList());

        List<RecommendationResult> modified = new ArrayList<>();

        for (RecommendationResult res : candidates) {
            ContentItem item = res.getItem();
            double adjustedScore = res.getScore();
            List<String> triggeredRules = new ArrayList<>(res.getAppliedRules());
            boolean excluded = false;

            for (BusinessRule rule : activeRules) {
                switch (rule.getRuleType()) {
                    case BOOST_CATEGORY:
                        if (item.getCategory().equalsIgnoreCase(rule.getTarget())) {
                            adjustedScore = Math.min(1.0, adjustedScore + rule.getAdjustmentFactor());
                            triggeredRules.add("Boosted (+" + (int)(rule.getAdjustmentFactor() * 100) + "% " + rule.getTarget() + ")");
                        }
                        break;
                    case BURY_LOW_STOCK:
                        if (item.getInventory() < 10) {
                            adjustedScore = Math.max(0.05, adjustedScore + rule.getAdjustmentFactor()); // adjustment is negative
                            triggeredRules.add("Bury Low Inventory (" + item.getInventory() + " left)");
                        }
                        break;
                    case EXCLUDE_ITEM:
                        if (item.getItemId().equalsIgnoreCase(rule.getTarget()) ||
                            item.getStatus().name().equalsIgnoreCase(rule.getTarget())) {
                            excluded = true;
                        }
                        break;
                    case MIN_RATING_FILTER:
                        try {
                            double minR = Double.parseDouble(rule.getTarget());
                            if (item.getRating() < minR) {
                                excluded = true;
                            }
                        } catch (NumberFormatException ignored) {}
                        break;
                    case PIN_TOP_RANK:
                        if (item.getItemId().equalsIgnoreCase(rule.getTarget())) {
                            adjustedScore = 1.0;
                            triggeredRules.add("Pinned (Top Priority Rule)");
                        }
                        break;
                }
                if (excluded) break;
            }

            if (!excluded) {
                modified.add(new RecommendationResult(
                        item, adjustedScore, 0, res.getReason(), res.getAlgorithmUsed(), triggeredRules));
            }
        }

        return modified;
    }

    protected List<RecommendationResult> applyDiversityReranking(
            List<RecommendationResult> ranked,
            double diversityWeight,
            int k) {
        if (diversityWeight <= 0.05 || ranked.size() <= 1) {
            return ranked.stream().limit(k).collect(Collectors.toList());
        }

        // Maximal Marginal Relevance (MMR) approximation to avoid category/item monotony
        List<RecommendationResult> selected = new ArrayList<>();
        List<RecommendationResult> pool = new ArrayList<>(ranked);
        Set<String> selectedCategories = new HashSet<>();

        while (!pool.isEmpty() && selected.size() < k) {
            RecommendationResult bestCandidate = null;
            double bestMmrScore = -Double.MAX_VALUE;

            for (RecommendationResult cand : pool) {
                double relevance = cand.getScore();
                // Diversity penalty if category is already selected
                double redundancy = selectedCategories.contains(cand.getItem().getCategory()) ? 0.4 : 0.0;
                double mmr = (1.0 - diversityWeight) * relevance - (diversityWeight * redundancy);

                if (mmr > bestMmrScore) {
                    bestMmrScore = mmr;
                    bestCandidate = cand;
                }
            }

            if (bestCandidate != null) {
                selected.add(bestCandidate);
                selectedCategories.add(bestCandidate.getItem().getCategory());
                pool.remove(bestCandidate);
            } else {
                break;
            }
        }

        return selected;
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// CONCRETE ALGORITHMS (UNIT 2)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class CollaborativeFilteringEngine extends AbstractRecommendationEngine {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.COLLABORATIVE_FILTERING;
    }

    @Override
    public List<RecommendationResult> generateRecommendations(
            UserProfile user,
            RecommendationService service,
            List<ContentItem> catalog,
            List<InteractionEvent> allEvents,
            List<BusinessRule> rules,
            int k,
            double diversityWeight,
            double noveltyWeight) throws ModelExecutionException {
        try {
            // Find items the user already interacted with
            Set<String> userInteractedItems = allEvents.stream()
                    .filter(e -> e.getUserId().equals(user.getUserId()))
                    .map(InteractionEvent::getItemId)
                    .collect(Collectors.toSet());

            // User-User Collaborative Filtering matrix: find co-occurring peers
            Map<String, Map<String, Double>> peerInteractions = new HashMap<>();
            for (InteractionEvent e : allEvents) {
                if (e.getServiceId().equals(service.getServiceId())) {
                    peerInteractions
                            .computeIfAbsent(e.getUserId(), u -> new HashMap<>())
                            .merge(e.getItemId(), e.getWeight(), Double::sum);
                }
            }

            Map<String, Double> myInteractions = peerInteractions.getOrDefault(user.getUserId(), Collections.emptyMap());

            // Compute peer similarity weights
            Map<String, Double> peerSimilarity = new HashMap<>();
            for (Map.Entry<String, Map<String, Double>> entry : peerInteractions.entrySet()) {
                String peerId = entry.getKey();
                if (peerId.equals(user.getUserId())) continue;

                Map<String, Double> peerItems = entry.getValue();
                Set<String> common = new HashSet<>(myInteractions.keySet());
                common.retainAll(peerItems.keySet());

                if (!common.isEmpty()) {
                    double overlapScore = common.size() / (double) Math.max(1, myInteractions.size());
                    peerSimilarity.put(peerId, overlapScore);
                }
            }

            // Score candidate items from similar peers
            Map<String, Double> itemScores = new HashMap<>();
            Map<String, Integer> peerCounts = new HashMap<>();

            for (Map.Entry<String, Double> peerSim : peerSimilarity.entrySet()) {
                Map<String, Double> items = peerInteractions.get(peerSim.getKey());
                if (items != null) {
                    for (Map.Entry<String, Double> it : items.entrySet()) {
                        if (!userInteractedItems.contains(it.getKey())) {
                            itemScores.merge(it.getKey(), it.getValue() * peerSim.getValue(), Double::sum);
                            peerCounts.merge(it.getKey(), 1, Integer::sum);
                        }
                    }
                }
            }

            Map<String, ContentItem> catalogMap = catalog.stream()
                    .filter(i -> i.getDomain() == service.getDomain() && i.getStatus() == ItemStatus.ACTIVE)
                    .collect(Collectors.toMap(ContentItem::getItemId, i -> i, (a, b) -> a));

            List<RecommendationResult> candidates = new ArrayList<>();

            for (Map.Entry<String, Double> entry : itemScores.entrySet()) {
                ContentItem item = catalogMap.get(entry.getKey());
                if (item != null) {
                    double rawScore = Math.min(0.98, Math.max(0.40, entry.getValue() / 10.0));
                    int peers = peerCounts.getOrDefault(entry.getKey(), 1);
                    String reason = "Interacted with by " + peers + " lookalike peers in your segment";
                    candidates.add(new RecommendationResult(
                            item, rawScore, 0, reason, getAlgorithmType(), Collections.emptyList()));
                }
            }

            // Fallback for cold-start if collaborative signals are sparse
            if (candidates.size() < k) {
                Set<String> existing = candidates.stream().map(c -> c.getItem().getItemId()).collect(Collectors.toSet());
                existing.addAll(userInteractedItems);

                catalog.stream()
                        .filter(i -> i.getDomain() == service.getDomain() && i.getStatus() == ItemStatus.ACTIVE)
                        .filter(i -> !existing.contains(i.getItemId()))
                        .sorted(Comparator.comparingDouble(ContentItem::getRating).reversed())
                        .limit(k - candidates.size())
                        .forEach(it -> {
                            double score = Math.min(0.85, (it.getRating() / 5.0) * 0.85);
                            candidates.add(new RecommendationResult(
                                    it, score, 0, "High peer satisfaction rating (" + it.getRating() + " / 5.0)",
                                    getAlgorithmType(), Collections.emptyList()));
                        });
            }

            // Apply Business Rules
            List<RecommendationResult> ruled = applyBusinessRules(candidates, rules, service.getServiceId());

            // Apply Diversity Re-ranking
            List<RecommendationResult> diverse = applyDiversityReranking(ruled, diversityWeight, k);

            // Sort and assign final ranks
            diverse.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            List<RecommendationResult> finalResult = new ArrayList<>();
            for (int r = 0; r < diverse.size(); r++) {
                RecommendationResult res = diverse.get(r);
                finalResult.add(new RecommendationResult(
                        res.getItem(), res.getScore(), r + 1, res.getReason(),
                        res.getAlgorithmUsed(), res.getAppliedRules()));
            }
            return finalResult;
        } catch (Exception ex) {
            throw new ModelExecutionException("Collaborative Filtering pipeline failed", ex);
        }
    }
}

class ContentBasedFilteringEngine extends AbstractRecommendationEngine {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.CONTENT_BASED;
    }

    @Override
    public List<RecommendationResult> generateRecommendations(
            UserProfile user,
            RecommendationService service,
            List<ContentItem> catalog,
            List<InteractionEvent> allEvents,
            List<BusinessRule> rules,
            int k,
            double diversityWeight,
            double noveltyWeight) throws ModelExecutionException {
        try {
            Set<String> userInteractedItems = allEvents.stream()
                    .filter(e -> e.getUserId().equals(user.getUserId()))
                    .map(InteractionEvent::getItemId)
                    .collect(Collectors.toSet());

            List<RecommendationResult> candidates = new ArrayList<>();

            for (ContentItem item : catalog) {
                if (item.getDomain() != service.getDomain() || item.getStatus() != ItemStatus.ACTIVE) continue;
                if (userInteractedItems.contains(item.getItemId())) continue;

                // Feature vector cosine similarity
                double vectorSim = calculateCosineSimilarity(user.getPreferenceVector(), item.getFeatureVector());

                // Category affinity boost
                double catAffinity = user.getAffinity(item.getCategory());

                // Novelty adjustment (if novelty weight > 0, downweight ultra-popular items)
                double popularityPenalty = noveltyWeight * Math.min(0.25, (item.getViewCount() / 300.0) * 0.25);

                double finalScore = Math.min(0.99, Math.max(0.20,
                        (0.60 * vectorSim) + (0.40 * catAffinity) - popularityPenalty));

                String reason = "Matches your strong affinity in " + item.getCategory() +
                        " (" + (int)(catAffinity * 100) + "% affinity alignment)";

                candidates.add(new RecommendationResult(
                        item, finalScore, 0, reason, getAlgorithmType(), Collections.emptyList()));
            }

            candidates.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

            // Apply Business Rules
            List<RecommendationResult> ruled = applyBusinessRules(candidates, rules, service.getServiceId());

            // Diversity MMR
            List<RecommendationResult> diverse = applyDiversityReranking(ruled, diversityWeight, k);

            diverse.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            List<RecommendationResult> finalResult = new ArrayList<>();
            for (int r = 0; r < diverse.size(); r++) {
                RecommendationResult res = diverse.get(r);
                finalResult.add(new RecommendationResult(
                        res.getItem(), res.getScore(), r + 1, res.getReason(),
                        res.getAlgorithmUsed(), res.getAppliedRules()));
            }
            return finalResult;
        } catch (Exception ex) {
            throw new ModelExecutionException("Content-Based filtering execution failed", ex);
        }
    }
}

class HybridEnsembleEngine extends AbstractRecommendationEngine {
    private final CollaborativeFilteringEngine cf = new CollaborativeFilteringEngine();
    private final ContentBasedFilteringEngine cb = new ContentBasedFilteringEngine();

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.HYBRID_ENSEMBLE;
    }

    @Override
    public List<RecommendationResult> generateRecommendations(
            UserProfile user,
            RecommendationService service,
            List<ContentItem> catalog,
            List<InteractionEvent> allEvents,
            List<BusinessRule> rules,
            int k,
            double diversityWeight,
            double noveltyWeight) throws ModelExecutionException {
        try {
            // Run sub-models
            List<RecommendationResult> cfResults = cf.generateRecommendations(
                    user, service, catalog, allEvents, Collections.emptyList(), k * 2, 0.0, noveltyWeight);
            List<RecommendationResult> cbResults = cb.generateRecommendations(
                    user, service, catalog, allEvents, Collections.emptyList(), k * 2, 0.0, noveltyWeight);

            Map<String, Double> blendedScores = new HashMap<>();
            Map<String, ContentItem> itemMap = new HashMap<>();
            Map<String, String> reasons = new HashMap<>();

            for (RecommendationResult r : cfResults) {
                blendedScores.put(r.getItem().getItemId(), r.getScore() * 0.50);
                itemMap.put(r.getItem().getItemId(), r.getItem());
                reasons.put(r.getItem().getItemId(), "Hybrid Blend: " + r.getReason());
            }

            for (RecommendationResult r : cbResults) {
                String id = r.getItem().getItemId();
                double cbContribution = r.getScore() * 0.50;
                blendedScores.merge(id, cbContribution, Double::sum);
                itemMap.put(id, r.getItem());
                if (!reasons.containsKey(id)) {
                    reasons.put(id, "Hybrid Blend: " + r.getReason());
                } else {
                    reasons.put(id, "Multi-signal alignment (Peer behavioral data + profile vector match)");
                }
            }

            List<RecommendationResult> candidates = new ArrayList<>();
            for (Map.Entry<String, Double> entry : blendedScores.entrySet()) {
                ContentItem item = itemMap.get(entry.getKey());
                if (item != null) {
                    double finalScore = Math.min(0.99, Math.max(0.35, entry.getValue()));
                    candidates.add(new RecommendationResult(
                            item, finalScore, 0, reasons.get(entry.getKey()),
                            getAlgorithmType(), Collections.emptyList()));
                }
            }

            candidates.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

            // Apply Business Rules
            List<RecommendationResult> ruled = applyBusinessRules(candidates, rules, service.getServiceId());

            // Apply Diversity Re-ranking
            List<RecommendationResult> diverse = applyDiversityReranking(ruled, diversityWeight, k);

            diverse.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            List<RecommendationResult> finalResult = new ArrayList<>();
            for (int r = 0; r < diverse.size(); r++) {
                RecommendationResult res = diverse.get(r);
                finalResult.add(new RecommendationResult(
                        res.getItem(), res.getScore(), r + 1, res.getReason(),
                        res.getAlgorithmUsed(), res.getAppliedRules()));
            }
            return finalResult;
        } catch (Exception ex) {
            throw new ModelExecutionException("Hybrid Ensemble pipeline failed", ex);
        }
    }
}

class TrendingPopularityEngine extends AbstractRecommendationEngine {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.TRENDING_POPULARITY;
    }

    @Override
    public List<RecommendationResult> generateRecommendations(
            UserProfile user,
            RecommendationService service,
            List<ContentItem> catalog,
            List<InteractionEvent> allEvents,
            List<BusinessRule> rules,
            int k,
            double diversityWeight,
            double noveltyWeight) throws ModelExecutionException {
        try {
            // Count recent interactions per item
            Map<String, Double> trendingVelocity = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();

            for (InteractionEvent e : allEvents) {
                if (e.getServiceId().equals(service.getServiceId())) {
                    // Time decay factor
                    long hoursAgo = Duration.between(e.getTimestamp(), now).toHours();
                    double decay = 1.0 / (1.0 + (0.05 * hoursAgo));
                    trendingVelocity.merge(e.getItemId(), e.getWeight() * decay, Double::sum);
                }
            }

            List<RecommendationResult> candidates = catalog.stream()
                    .filter(i -> i.getDomain() == service.getDomain() && i.getStatus() == ItemStatus.ACTIVE)
                    .map(it -> {
                        double velocity = trendingVelocity.getOrDefault(it.getItemId(), 0.0);
                        double popularityScore = Math.min(0.97, Math.max(0.40,
                                (it.getRating() / 5.0) * 0.4 + Math.min(0.6, (velocity + it.getClickCount()) / 40.0)));
                        String reason = "Trending across platform with " + it.getViewCount() + " views and " + it.getRating() + "/5.0 rating";
                        return new RecommendationResult(
                                it, popularityScore, 0, reason, getAlgorithmType(), Collections.emptyList());
                    })
                    .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                    .collect(Collectors.toList());

            List<RecommendationResult> ruled = applyBusinessRules(candidates, rules, service.getServiceId());
            List<RecommendationResult> diverse = applyDiversityReranking(ruled, diversityWeight, k);

            diverse.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            List<RecommendationResult> finalResult = new ArrayList<>();
            for (int r = 0; r < diverse.size(); r++) {
                RecommendationResult res = diverse.get(r);
                finalResult.add(new RecommendationResult(
                        res.getItem(), res.getScore(), r + 1, res.getReason(),
                        res.getAlgorithmUsed(), res.getAppliedRules()));
            }
            return finalResult;
        } catch (Exception ex) {
            throw new ModelExecutionException("Trending Popularity execution failed", ex);
        }
    }
}

class ContextualSessionEngine extends AbstractRecommendationEngine {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.CONTEXTUAL_SESSION;
    }

    @Override
    public List<RecommendationResult> generateRecommendations(
            UserProfile user,
            RecommendationService service,
            List<ContentItem> catalog,
            List<InteractionEvent> allEvents,
            List<BusinessRule> rules,
            int k,
            double diversityWeight,
            double noveltyWeight) throws ModelExecutionException {
        try {
            // Find user's last 5 interactions
            List<InteractionEvent> userRecentEvents = allEvents.stream()
                    .filter(e -> e.getUserId().equals(user.getUserId()))
                    .sorted(Comparator.comparing(InteractionEvent::getTimestamp).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            if (userRecentEvents.isEmpty()) {
                // Fallback to trending
                return new TrendingPopularityEngine().generateRecommendations(
                        user, service, catalog, allEvents, rules, k, diversityWeight, noveltyWeight);
            }

            InteractionEvent lastEvent = userRecentEvents.get(0);
            Map<String, ContentItem> catalogMap = catalog.stream()
                    .collect(Collectors.toMap(ContentItem::getItemId, i -> i, (a, b) -> a));

            ContentItem lastItem = catalogMap.get(lastEvent.getItemId());
            String lastItemCategory = lastItem != null ? lastItem.getCategory() : "Trending";
            double[] lastVector = lastItem != null ? lastItem.getFeatureVector() : user.getPreferenceVector();

            Set<String> excludedIds = userRecentEvents.stream()
                    .map(InteractionEvent::getItemId)
                    .collect(Collectors.toSet());

            List<RecommendationResult> candidates = new ArrayList<>();

            for (ContentItem item : catalog) {
                if (item.getDomain() != service.getDomain() || item.getStatus() != ItemStatus.ACTIVE) continue;
                if (excludedIds.contains(item.getItemId())) continue;

                double similarity = calculateCosineSimilarity(lastVector, item.getFeatureVector());
                boolean sameCategory = item.getCategory().equalsIgnoreCase(lastItemCategory);
                double catBoost = sameCategory ? 0.25 : 0.0;
                double finalScore = Math.min(0.98, Math.max(0.30, (0.75 * similarity) + catBoost));

                String reason = "Contextual flow: Follows your recent " + lastEvent.getEventType().getLabel() +
                        (lastItem != null ? " on '" + lastItem.getTitle() + "'" : "");

                candidates.add(new RecommendationResult(
                        item, finalScore, 0, reason, getAlgorithmType(), Collections.emptyList()));
            }

            candidates.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

            List<RecommendationResult> ruled = applyBusinessRules(candidates, rules, service.getServiceId());
            List<RecommendationResult> diverse = applyDiversityReranking(ruled, diversityWeight, k);

            diverse.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            List<RecommendationResult> finalResult = new ArrayList<>();
            for (int r = 0; r < diverse.size(); r++) {
                RecommendationResult res = diverse.get(r);
                finalResult.add(new RecommendationResult(
                        res.getItem(), res.getScore(), r + 1, res.getReason(),
                        res.getAlgorithmUsed(), res.getAppliedRules()));
            }
            return finalResult;
        } catch (Exception ex) {
            throw new ModelExecutionException("Contextual Session engine failed", ex);
        }
    }
}

class RecommendationEngineFactory {
    private static final Map<AlgorithmType, IRecommendationEngine> registry = new EnumMap<>(AlgorithmType.class);

    static {
        registry.put(AlgorithmType.COLLABORATIVE_FILTERING, new CollaborativeFilteringEngine());
        registry.put(AlgorithmType.CONTENT_BASED, new ContentBasedFilteringEngine());
        registry.put(AlgorithmType.HYBRID_ENSEMBLE, new HybridEnsembleEngine());
        registry.put(AlgorithmType.TRENDING_POPULARITY, new TrendingPopularityEngine());
        registry.put(AlgorithmType.CONTEXTUAL_SESSION, new ContextualSessionEngine());
    }

    public static IRecommendationEngine getEngine(AlgorithmType type) {
        return registry.getOrDefault(type, registry.get(AlgorithmType.HYBRID_ENSEMBLE));
    }
}


// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 4: THREAD-SAFE CONCURRENCY, REPOSITORIES & DATA STORE (UNIT 4)
// ════════════════════════════════════════════════════════════════════════════════════════════════

class PlatformDataStore {
    public static final PlatformDataStore INSTANCE = new PlatformDataStore();

    // In-memory thread-safe collections
    private final Map<String, RecommendationService> services = new ConcurrentHashMap<>();
    private final Map<String, ContentItem> catalog = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> users = new ConcurrentHashMap<>();
    private final List<InteractionEvent> events = new CopyOnWriteArrayList<>();
    private final Map<String, ABExperiment> experiments = new ConcurrentHashMap<>();
    private final Map<String, BusinessRule> rules = new ConcurrentHashMap<>();
    private final Map<String, ApiKeyCredential> apiKeys = new ConcurrentHashMap<>();
    private final Map<String, PlatformUser> adminUsers = Collections.synchronizedMap(new LinkedHashMap<>());
    private final List<String> liveTelemetryLogs = new CopyOnWriteArrayList<>();

    // Real-time SLA & telemetry stats
    private final AtomicLong totalPlatformQueries = new AtomicLong(14850);
    private final AtomicLong totalPlatformConversions = new AtomicLong(2140);
    private volatile double currentQps = 24.8;
    private volatile double p50LatencyMs = 8.4;
    private volatile double p95LatencyMs = 18.2;
    private volatile double p99LatencyMs = 28.5;
    private volatile double cacheHitRatio = 0.884; // 88.4%
    private volatile double catalogCoverageRatio = 0.942; // 94.2%

    private PlatformUser activeUser;

    private PlatformDataStore() {
        seedInitialPlatformData();
    }

    private void seedInitialPlatformData() {
        // ── 1. ADMIN GOVERNANCE ACCOUNTS ──────────────────────────────────────────────
        SuperAdminUser admin = new SuperAdminUser(
                "ADM-001", "Dr. Alice Morgan", "admin@platform.com", "Tier-1 Global Security Clearance");
        ServiceManagerUser manager = new ServiceManagerUser(
                "MGR-002", "Darius Vance", "service.lead@platform.com", "E-Commerce & Media Recommenders");
        ContentCuratorUser curator = new ContentCuratorUser(
                "CUR-003", "Elena Rostova", "catalog.curator@platform.com", "Omnichannel Catalog Ingestion");
        DataAnalystUser analyst = new DataAnalystUser(
                "ANA-004", "Marcus Sterling", "analyst@platform.com", "Algorithmic Precision & Telemetry Squad");
        ApiConsumerUser clientDev = new ApiConsumerUser(
                "DEV-005", "Chloe Zhao", "api.dev@client.com", "Storefront-NextJS-Production");

        adminUsers.put(admin.getUserId(), admin);
        adminUsers.put(manager.getUserId(), manager);
        adminUsers.put(curator.getUserId(), curator);
        adminUsers.put(analyst.getUserId(), analyst);
        adminUsers.put(clientDev.getUserId(), clientDev);

        this.activeUser = admin; // Default to Super Admin

        // ── 2. RECOMMENDATION SERVICES & PIPELINES ────────────────────────────────────
        registerService(new RecommendationService(
                "SVC-ECOMM-01", "E-Commerce Dynamic Product Recommender",
                ServiceDomain.ECOMMERCE, AlgorithmType.HYBRID_ENSEMBLE, "v3.2.0-prod",
                150, 300, 0.15, 0.25));

        registerService(new RecommendationService(
                "SVC-STREAM-02", "Netflix-Grade OTT Video Feed Recommender",
                ServiceDomain.STREAMING_VIDEO, AlgorithmType.COLLABORATIVE_FILTERING, "v2.8.4-prod",
                300, 180, 0.20, 0.35));

        registerService(new RecommendationService(
                "SVC-AUDIO-03", "Spotify-Style Music Discovery & Flow",
                ServiceDomain.STREAMING_MUSIC, AlgorithmType.CONTEXTUAL_SESSION, "v4.1.0-prod",
                500, 120, 0.30, 0.40));

        registerService(new RecommendationService(
                "SVC-NEWS-04", "Smart Article Discovery & Editorial Engine",
                ServiceDomain.NEWS_PUBLISHING, AlgorithmType.CONTENT_BASED, "v1.9.2-prod",
                100, 600, 0.25, 0.20));

        registerService(new RecommendationService(
                "SVC-TALENT-05", "Enterprise Talent & Skill Matcher",
                ServiceDomain.JOB_PORTAL, AlgorithmType.HYBRID_ENSEMBLE, "v2.0.1-prod",
                80, 900, 0.10, 0.15));

        // ── 3. CONTENT CATALOG ITEMS ─────────────────────────────────────────────────
        // E-Commerce
        addCatalogItem(new ContentItem("ITEM-EC-101", "SVC-ECOMM-01", "Sony WH-1000XM5 Wireless Headphones",
                "Electronics", ServiceDomain.ECOMMERCE, Arrays.asList("Audio", "Noise-Cancelling", "Wireless", "Premium"),
                349.99, 4.8, 45, new double[]{0.9, 0.8, 0.3, 0.2, 0.7, 0.9, 0.4, 0.6}));

        addCatalogItem(new ContentItem("ITEM-EC-102", "SVC-ECOMM-01", "Apple MacBook Pro M3 16-inch",
                "Computing", ServiceDomain.ECOMMERCE, Arrays.asList("Laptop", "Apple Silicon", "Developer", "High-End"),
                2499.00, 4.9, 22, new double[]{0.95, 0.9, 0.2, 0.1, 0.8, 0.95, 0.5, 0.8}));

        addCatalogItem(new ContentItem("ITEM-EC-103", "SVC-ECOMM-01", "Logitech MX Master 3S Wireless Mouse",
                "Electronics", ServiceDomain.ECOMMERCE, Arrays.asList("Ergonomic", "Productivity", "Bluetooth"),
                99.99, 4.7, 85, new double[]{0.8, 0.7, 0.4, 0.3, 0.6, 0.7, 0.3, 0.5}));

        addCatalogItem(new ContentItem("ITEM-EC-104", "SVC-ECOMM-01", "Keychron Q1 Pro Mechanical Keyboard",
                "Computing", ServiceDomain.ECOMMERCE, Arrays.asList("Mechanical", "Custom", "Wireless", "Typing"),
                199.00, 4.8, 30, new double[]{0.85, 0.75, 0.3, 0.2, 0.65, 0.8, 0.4, 0.6}));

        addCatalogItem(new ContentItem("ITEM-EC-105", "SVC-ECOMM-01", "Patagonia Torrentshell 3L Rain Jacket",
                "Apparel", ServiceDomain.ECOMMERCE, Arrays.asList("Outdoor", "Waterproof", "Sustainable", "Fashion"),
                179.00, 4.6, 40, new double[]{0.2, 0.3, 0.9, 0.8, 0.3, 0.2, 0.8, 0.4}));

        addCatalogItem(new ContentItem("ITEM-EC-106", "SVC-ECOMM-01", "Aer Travel Pack 3 Ultra Backpack",
                "Travel", ServiceDomain.ECOMMERCE, Arrays.asList("Travel", "Backpack", "Waterproof", "EDC"),
                269.00, 4.9, 15, new double[]{0.5, 0.6, 0.8, 0.7, 0.5, 0.6, 0.7, 0.6}));

        addCatalogItem(new ContentItem("ITEM-EC-107", "SVC-ECOMM-01", "Ember Temperature Control Smart Mug 2",
                "Home & Kitchen", ServiceDomain.ECOMMERCE, Arrays.asList("Smart Home", "Coffee", "Gadget", "Ceramic"),
                149.95, 4.4, 60, new double[]{0.7, 0.5, 0.5, 0.4, 0.6, 0.65, 0.5, 0.5}));

        addCatalogItem(new ContentItem("ITEM-EC-108", "SVC-ECOMM-01", "Sony Alpha 7 IV Full-Frame Camera",
                "Electronics", ServiceDomain.ECOMMERCE, Arrays.asList("Photography", "4K Video", "Full-Frame", "Creator"),
                2498.00, 4.8, 8, new double[]{0.92, 0.85, 0.4, 0.3, 0.75, 0.9, 0.6, 0.7}));

        // Streaming Video
        addCatalogItem(new ContentItem("ITEM-VID-201", "SVC-STREAM-02", "Interstellar: Beyond Horizons",
                "Sci-Fi", ServiceDomain.STREAMING_VIDEO, Arrays.asList("Space", "Nolan", "Physics", "Time-Travel"),
                0.0, 4.9, 9999, new double[]{0.9, 0.95, 0.2, 0.1, 0.8, 0.9, 0.3, 0.7}));

        addCatalogItem(new ContentItem("ITEM-VID-202", "SVC-STREAM-02", "The Cyberpunk Chronicles",
                "Sci-Fi", ServiceDomain.STREAMING_VIDEO, Arrays.asList("Dystopian", "AI", "Futuristic", "Action"),
                0.0, 4.7, 9999, new double[]{0.85, 0.9, 0.3, 0.2, 0.75, 0.85, 0.4, 0.6}));

        addCatalogItem(new ContentItem("ITEM-VID-203", "SVC-STREAM-02", "Planet Earth III: Hidden Realms",
                "Documentary", ServiceDomain.STREAMING_VIDEO, Arrays.asList("Nature", "4K HDR", "Wildlife", "Attenborough"),
                0.0, 4.95, 9999, new double[]{0.3, 0.4, 0.9, 0.85, 0.4, 0.3, 0.9, 0.5}));

        addCatalogItem(new ContentItem("ITEM-VID-204", "SVC-STREAM-02", "Succession: Corporate Machinations",
                "Drama", ServiceDomain.STREAMING_VIDEO, Arrays.asList("Drama", "Business", "Satire", "Emmy-Winner"),
                0.0, 4.8, 9999, new double[]{0.4, 0.5, 0.6, 0.7, 0.6, 0.5, 0.5, 0.6}));

        addCatalogItem(new ContentItem("ITEM-VID-205", "SVC-STREAM-02", "Oppenheimer: The Atomic Dawn",
                "Biopic", ServiceDomain.STREAMING_VIDEO, Arrays.asList("History", "Drama", "Science", "War"),
                0.0, 4.85, 9999, new double[]{0.8, 0.85, 0.5, 0.4, 0.7, 0.8, 0.6, 0.7}));

        // Streaming Music
        addCatalogItem(new ContentItem("ITEM-MUS-301", "SVC-AUDIO-03", "Midnight Chillout Lo-Fi Beats",
                "Lo-Fi", ServiceDomain.STREAMING_MUSIC, Arrays.asList("Focus", "Study", "Relax", "Instrumental"),
                0.0, 4.8, 9999, new double[]{0.4, 0.3, 0.7, 0.8, 0.9, 0.4, 0.6, 0.8}));

        addCatalogItem(new ContentItem("ITEM-MUS-302", "SVC-AUDIO-03", "Synthwave 2084: Neon Dreams",
                "Electronic", ServiceDomain.STREAMING_MUSIC, Arrays.asList("Retro", "Synth", "Drive", "Energy"),
                0.0, 4.7, 9999, new double[]{0.8, 0.7, 0.4, 0.3, 0.8, 0.8, 0.4, 0.7}));

        addCatalogItem(new ContentItem("ITEM-MUS-303", "SVC-AUDIO-03", "Deep Focus: Ambient Soundscapes",
                "Ambient", ServiceDomain.STREAMING_MUSIC, Arrays.asList("Brainwaves", "Minimal", "Calm", "Coding"),
                0.0, 4.9, 9999, new double[]{0.5, 0.4, 0.8, 0.9, 0.95, 0.5, 0.7, 0.85}));

        addCatalogItem(new ContentItem("ITEM-MUS-304", "SVC-AUDIO-03", "Acoustic Sunsets & Indie Ballads",
                "Indie Acoustic", ServiceDomain.STREAMING_MUSIC, Arrays.asList("Guitar", "Vocal", "Warm", "Folk"),
                0.0, 4.6, 9999, new double[]{0.3, 0.2, 0.8, 0.7, 0.7, 0.3, 0.8, 0.6}));

        // News & Publishing
        addCatalogItem(new ContentItem("ITEM-NW-401", "SVC-NEWS-04", "Generative AI & Autonomous Agent Architectures",
                "Artificial Intelligence", ServiceDomain.NEWS_PUBLISHING, Arrays.asList("LLM", "Deep Learning", "Tech", "Research"),
                0.0, 4.9, 9999, new double[]{0.95, 0.95, 0.2, 0.1, 0.6, 0.95, 0.3, 0.8}));

        addCatalogItem(new ContentItem("ITEM-NW-402", "SVC-NEWS-04", "Quantum Computing Breakthroughs in Cryptography",
                "Deep Tech", ServiceDomain.NEWS_PUBLISHING, Arrays.asList("Quantum", "Security", "Physics", "Qubits"),
                0.0, 4.75, 9999, new double[]{0.9, 0.9, 0.3, 0.2, 0.5, 0.9, 0.4, 0.75}));

        addCatalogItem(new ContentItem("ITEM-NW-403", "SVC-NEWS-04", "Global Energy Transition & Renewable Grids",
                "Climate & Energy", ServiceDomain.NEWS_PUBLISHING, Arrays.asList("Solar", "Batteries", "Policy", "SDG-7"),
                0.0, 4.65, 9999, new double[]{0.5, 0.6, 0.8, 0.7, 0.4, 0.5, 0.8, 0.6}));

        addCatalogItem(new ContentItem("ITEM-NW-404", "SVC-NEWS-04", "Monetary Policy & Decentralized Finance 2026",
                "Economics", ServiceDomain.NEWS_PUBLISHING, Arrays.asList("Macro", "Inflation", "Crypto", "Markets"),
                0.0, 4.7, 9999, new double[]{0.6, 0.7, 0.5, 0.4, 0.5, 0.6, 0.5, 0.65}));

        // Job Portal
        addCatalogItem(new ContentItem("ITEM-JB-501", "SVC-TALENT-05", "Principal AI Platform Architect",
                "Engineering", ServiceDomain.JOB_PORTAL, Arrays.asList("Distributed Systems", "Kubernetes", "PyTorch", "Java"),
                185000.0, 4.9, 3, new double[]{0.95, 0.95, 0.2, 0.1, 0.7, 0.95, 0.4, 0.85}));

        addCatalogItem(new ContentItem("ITEM-JB-502", "SVC-TALENT-05", "Senior Recommendation Systems Engineer",
                "Machine Learning", ServiceDomain.JOB_PORTAL, Arrays.asList("RecSys", "Collaborative Filtering", "Vector Search", "Go"),
                165000.0, 4.85, 5, new double[]{0.92, 0.9, 0.3, 0.2, 0.75, 0.92, 0.4, 0.8}));

        addCatalogItem(new ContentItem("ITEM-JB-503", "SVC-TALENT-05", "Staff Product Designer (Design Systems)",
                "Design", ServiceDomain.JOB_PORTAL, Arrays.asList("Figma", "UI/UX", "Accessibility", "Design Tokens"),
                145000.0, 4.7, 2, new double[]{0.4, 0.3, 0.8, 0.9, 0.5, 0.4, 0.85, 0.6}));

        // ── 4. USER PROFILES ─────────────────────────────────────────────────────────
        UserProfile u1 = new UserProfile("USR-101", "Dr. Aris Vance", "aris.vance@tech.org", UserSegment.POWER_USER);
        u1.setInitialAffinity("Electronics", 0.92);
        u1.setInitialAffinity("Computing", 0.88);
        u1.setInitialAffinity("Artificial Intelligence", 0.95);
        u1.setInitialAffinity("Sci-Fi", 0.85);

        UserProfile u2 = new UserProfile("USR-102", "Elena Rostova", "elena.r@designhub.io", UserSegment.CASUAL_EXPLORER);
        u2.setInitialAffinity("Design", 0.90);
        u2.setInitialAffinity("Indie Acoustic", 0.85);
        u2.setInitialAffinity("Travel", 0.75);

        UserProfile u3 = new UserProfile("USR-103", "Maya Lin", "maya.lin@newuser.com", UserSegment.NEW_ONBOARDING);
        // Cold-start user: low initial affinities

        UserProfile u4 = new UserProfile("USR-104", "Devon Thorne", "devon.thorne@fund.com", UserSegment.HIGH_SPENDER);
        u4.setInitialAffinity("Electronics", 0.95);
        u4.setInitialAffinity("Computing", 0.92);
        u4.setInitialAffinity("Economics", 0.85);

        UserProfile u5 = new UserProfile("USR-105", "Kavita Iyer", "kavita.iyer@health.in", UserSegment.CHURN_RISK);
        u5.setInitialAffinity("Documentary", 0.80);
        u5.setInitialAffinity("Lo-Fi", 0.70);

        UserProfile u6 = new UserProfile("USR-106", "Tariq Mansoor", "tariq.m@streamer.net", UserSegment.CASUAL_EXPLORER);
        u6.setInitialAffinity("Sci-Fi", 0.88);
        u6.setInitialAffinity("Electronic", 0.82);

        UserProfile u7 = new UserProfile("USR-107", "Zoe Chen", "zoe.chen@ai-labs.ca", UserSegment.POWER_USER);
        u7.setInitialAffinity("Engineering", 0.95);
        u7.setInitialAffinity("Machine Learning", 0.94);
        u7.setInitialAffinity("Artificial Intelligence", 0.96);

        UserProfile u8 = new UserProfile("USR-108", "Marcus Bell", "marcus.bell@corp.co", UserSegment.HIGH_SPENDER);
        u8.setInitialAffinity("Apparel", 0.85);
        u8.setInitialAffinity("Home & Kitchen", 0.80);

        users.put(u1.getUserId(), u1);
        users.put(u2.getUserId(), u2);
        users.put(u3.getUserId(), u3);
        users.put(u4.getUserId(), u4);
        users.put(u5.getUserId(), u5);
        users.put(u6.getUserId(), u6);
        users.put(u7.getUserId(), u7);
        users.put(u8.getUserId(), u8);

        // ── 5. SEED INTERACTION EVENTS ───────────────────────────────────────────────
        seedInteractionEvents();

        // ── 6. A/B TESTING EXPERIMENTS ───────────────────────────────────────────────
        ABExperiment exp1 = new ABExperiment(
                "EXP-101", "Hybrid Ensemble vs Matrix Factorization", "SVC-ECOMM-01",
                AlgorithmType.HYBRID_ENSEMBLE, AlgorithmType.COLLABORATIVE_FILTERING, 50.0);
        for (int i = 0; i < 480; i++) exp1.recordImpressionA();
        for (int i = 0; i < 68; i++) exp1.recordClickA();
        for (int i = 0; i < 18; i++) exp1.recordConversionA();
        for (int i = 0; i < 475; i++) exp1.recordImpressionB();
        for (int i = 0; i < 51; i++) exp1.recordClickB();
        for (int i = 0; i < 11; i++) exp1.recordConversionB();
        exp1.setWinner("Variant A (+33% CTR Stat-Sig p<0.01)");
        experiments.put(exp1.getExperimentId(), exp1);

        ABExperiment exp2 = new ABExperiment(
                "EXP-102", "Contextual Recency vs Popularity Fallback", "SVC-AUDIO-03",
                AlgorithmType.CONTEXTUAL_SESSION, AlgorithmType.TRENDING_POPULARITY, 50.0);
        for (int i = 0; i < 320; i++) exp2.recordImpressionA();
        for (int i = 0; i < 52; i++) exp2.recordClickA();
        for (int i = 0; i < 315; i++) exp2.recordImpressionB();
        for (int i = 0; i < 38; i++) exp2.recordClickB();
        experiments.put(exp2.getExperimentId(), exp2);

        // ── 7. BUSINESS RULES & GUARDRAILS ───────────────────────────────────────────
        addRule(new BusinessRule("RULE-01", "SVC-ECOMM-01", "Boost Electronics Category (+20%)",
                RuleType.BOOST_CATEGORY, "Electronics", 0.20, 10));

        addRule(new BusinessRule("RULE-02", "SVC-ECOMM-01", "Bury Items with Stock < 10 Units",
                RuleType.BURY_LOW_STOCK, "10", -0.30, 8));

        addRule(new BusinessRule("RULE-03", "*", "Enforce Quality Guardrail (Min 3.8 Rating)",
                RuleType.MIN_RATING_FILTER, "3.8", 0.0, 9));

        addRule(new BusinessRule("RULE-04", "SVC-STREAM-02", "Boost Sci-Fi & Action (+25%)",
                RuleType.BOOST_CATEGORY, "Sci-Fi", 0.25, 7));

        addRule(new BusinessRule("RULE-05", "SVC-ECOMM-01", "Pin Flagship Noise-Cancelling Headphones",
                RuleType.PIN_TOP_RANK, "ITEM-EC-101", 1.0, 5));

        // ── 8. API CLIENT CREDENTIALS ────────────────────────────────────────────────
        apiKeys.put("KEY-WEB-01", new ApiKeyCredential("KEY-WEB-01", "Storefront Production Next.js", ClientTier.ENTERPRISE));
        apiKeys.put("KEY-IOS-02", new ApiKeyCredential("KEY-IOS-02", "iOS Mobile Client App", ClientTier.PROFESSIONAL));
        apiKeys.put("KEY-AND-03", new ApiKeyCredential("KEY-AND-03", "Android Mobile Client App", ClientTier.PROFESSIONAL));
        apiKeys.put("KEY-PIPE-04", new ApiKeyCredential("KEY-PIPE-04", "Snowflake Analytics Ingestion", ClientTier.ENTERPRISE));

        logTelemetry("Platform initialized with 5 Recommendation Services, 23 Catalog Items, 8 User Profiles.");
    }

    private void seedInteractionEvents() {
        String[] usersArr = {"USR-101", "USR-102", "USR-104", "USR-106", "USR-107", "USR-108"};
        String[] ecommItems = {"ITEM-EC-101", "ITEM-EC-102", "ITEM-EC-103", "ITEM-EC-104", "ITEM-EC-106"};
        String[] videoItems = {"ITEM-VID-201", "ITEM-VID-202", "ITEM-VID-204", "ITEM-VID-205"};
        String[] musicItems = {"ITEM-MUS-301", "ITEM-MUS-302", "ITEM-MUS-303"};
        String[] newsItems = {"ITEM-NW-401", "ITEM-NW-402", "ITEM-NW-404"};

        // Generate realistic co-occurrence events
        for (String u : usersArr) {
            // E-Commerce events
            for (String item : ecommItems) {
                if (Math.random() > 0.3) {
                    logEventInternal(new InteractionEvent(null, u, item, "SVC-ECOMM-01", EventType.VIEW, 4500, "Web"));
                    if (Math.random() > 0.4) {
                        logEventInternal(new InteractionEvent(null, u, item, "SVC-ECOMM-01", EventType.CLICK, 12000, "Web"));
                        if (Math.random() > 0.6) {
                            logEventInternal(new InteractionEvent(null, u, item, "SVC-ECOMM-01", EventType.PURCHASE, 0, "Web"));
                        }
                    }
                }
            }
            // Video events
            for (String vid : videoItems) {
                if (Math.random() > 0.4) {
                    logEventInternal(new InteractionEvent(null, u, vid, "SVC-STREAM-02", EventType.VIEW, 60000, "OTT"));
                    if (Math.random() > 0.5) {
                        logEventInternal(new InteractionEvent(null, u, vid, "SVC-STREAM-02", EventType.LIKE, 0, "OTT"));
                    }
                }
            }
            // Music events
            for (String mus : musicItems) {
                if (Math.random() > 0.3) {
                    logEventInternal(new InteractionEvent(null, u, mus, "SVC-AUDIO-03", EventType.CLICK, 180000, "App"));
                    logEventInternal(new InteractionEvent(null, u, mus, "SVC-AUDIO-03", EventType.LIKE, 0, "App"));
                }
            }
            // News events
            for (String nw : newsItems) {
                if (Math.random() > 0.5) {
                    logEventInternal(new InteractionEvent(null, u, nw, "SVC-NEWS-04", EventType.CLICK, 45000, "Feed"));
                    logEventInternal(new InteractionEvent(null, u, nw, "SVC-NEWS-04", EventType.BOOKMARK, 0, "Feed"));
                }
            }
        }
    }

    private void logEventInternal(InteractionEvent e) {
        events.add(e);
        ContentItem item = catalog.get(e.getItemId());
        if (item != null) {
            switch (e.getEventType()) {
                case VIEW: item.incrementViews(); break;
                case CLICK: item.incrementClicks(); break;
                case PURCHASE: item.incrementConversions(); break;
                default: break;
            }
        }
        UserProfile user = users.get(e.getUserId());
        if (user != null && item != null) {
            user.recordInteraction(item.getCategory(), item.getFeatureVector(), e.getWeight());
        }
        RecommendationService svc = services.get(e.getServiceId());
        if (svc != null) {
            svc.recordRequest(8.0 + Math.random() * 8.0, e.getEventType() == EventType.PURCHASE);
        }
    }

    public synchronized void resetToDefaultData() {
        services.clear();
        catalog.clear();
        users.clear();
        events.clear();
        experiments.clear();
        rules.clear();
        apiKeys.clear();
        adminUsers.clear();
        liveTelemetryLogs.clear();
        totalPlatformQueries.set(14850);
        totalPlatformConversions.set(2140);
        seedInitialPlatformData();
        logTelemetry("Platform demo dataset restored to baseline state.");
    }

    public synchronized void logEvent(InteractionEvent e) {
        logEventInternal(e);
        logTelemetry("Event ingested: [" + e.getEventType() + "] by " + e.getUserId() +
                " on " + e.getItemId() + " (" + e.getServiceId() + ")");
    }

    public void logTelemetry(String msg) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = "[" + timestamp + "] " + msg;
        liveTelemetryLogs.add(0, entry);
        if (liveTelemetryLogs.size() > 200) {
            liveTelemetryLogs.remove(liveTelemetryLogs.size() - 1);
        }
    }

    // ── GETTERS & REGISTRY METHODS ───────────────────────────────────────────────

    public Map<String, RecommendationService> getServices() { return Collections.unmodifiableMap(services); }
    public RecommendationService getService(String id) { return services.get(id); }
    public void registerService(RecommendationService s) {
        services.put(s.getServiceId(), s);
        logTelemetry("Registered Recommendation Service: " + s.getName() + " [" + s.getServiceId() + "]");
    }

    public Map<String, ContentItem> getCatalog() { return Collections.unmodifiableMap(catalog); }
    public ContentItem getCatalogItem(String id) { return catalog.get(id); }
    public void addCatalogItem(ContentItem item) {
        catalog.put(item.getItemId(), item);
    }
    public void removeCatalogItem(String id) {
        catalog.remove(id);
        logTelemetry("Removed catalog item: " + id);
    }

    public Map<String, UserProfile> getUsers() { return Collections.unmodifiableMap(users); }
    public UserProfile getUser(String id) { return users.get(id); }
    public void addUser(UserProfile u) {
        users.put(u.getUserId(), u);
        logTelemetry("Registered user profile: " + u.getName() + " [" + u.getUserId() + "]");
    }

    public List<InteractionEvent> getEvents() { return Collections.unmodifiableList(events); }
    public Map<String, ABExperiment> getExperiments() { return Collections.unmodifiableMap(experiments); }
    public void addExperiment(ABExperiment exp) {
        experiments.put(exp.getExperimentId(), exp);
        logTelemetry("Created A/B Experiment: " + exp.getName());
    }

    public Map<String, BusinessRule> getRules() { return Collections.unmodifiableMap(rules); }
    public void addRule(BusinessRule r) {
        rules.put(r.getRuleId(), r);
        logTelemetry("Configured business guardrail: " + r.getRuleName());
    }
    public void toggleRule(String ruleId) {
        BusinessRule r = rules.get(ruleId);
        if (r != null) {
            r.setEnabled(!r.isEnabled());
            logTelemetry("Rule " + ruleId + " status toggled to: " + (r.isEnabled() ? "ACTIVE" : "DISABLED"));
        }
    }
    public void removeRule(String ruleId) {
        rules.remove(ruleId);
        logTelemetry("Deleted business rule: " + ruleId);
    }

    public Map<String, ApiKeyCredential> getApiKeys() { return Collections.unmodifiableMap(apiKeys); }
    public ApiKeyCredential generateApiKey(String clientName, ClientTier tier) {
        String keyId = "KEY-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ApiKeyCredential cred = new ApiKeyCredential(keyId, clientName, tier);
        apiKeys.put(keyId, cred);
        logTelemetry("Generated new API Key for: " + clientName + " (" + tier.name() + ")");
        return cred;
    }

    public Map<String, PlatformUser> getAdminUsers() { return Collections.unmodifiableMap(adminUsers); }
    public PlatformUser getActiveUser() { return activeUser; }
    public void setActiveUser(PlatformUser user) {
        this.activeUser = user;
        logTelemetry("Active session switched to: " + user.getName() + " [" + user.getRole().name() + "]");
    }

    public List<String> getLiveTelemetryLogs() { return Collections.unmodifiableList(liveTelemetryLogs); }

    // ── RECOMMENDATION PIPELINE INVOCATION ────────────────────────────────────────

    public List<RecommendationResult> executeRecommendationPipeline(
            String userId,
            String serviceId,
            AlgorithmType algorithmOverride,
            int k,
            Double diversityOverride,
            String categoryFilter) throws RecommendationPlatformException {

        UserProfile user = users.get(userId);
        if (user == null) throw new UserNotFoundException(userId);

        RecommendationService service = services.get(serviceId);
        if (service == null) throw new ServiceNotFoundException(serviceId);

        long startTime = System.nanoTime();

        AlgorithmType algoToUse = algorithmOverride != null ? algorithmOverride : service.getAlgorithmType();
        IRecommendationEngine engine = RecommendationEngineFactory.getEngine(algoToUse);

        double divWeight = diversityOverride != null ? diversityOverride : service.getDiversityWeight();

        List<ContentItem> candidateCatalog = new ArrayList<>(catalog.values());
        if (categoryFilter != null && !categoryFilter.trim().isEmpty() && !categoryFilter.equalsIgnoreCase("ALL") && !categoryFilter.equalsIgnoreCase("All Categories") && !categoryFilter.toLowerCase().startsWith("all")) {
            candidateCatalog = candidateCatalog.stream()
                    .filter(i -> i.getCategory().equalsIgnoreCase(categoryFilter))
                    .collect(Collectors.toList());
        }

        List<BusinessRule> activeRules = new ArrayList<>(rules.values());

        List<RecommendationResult> results = engine.generateRecommendations(
                user, service, candidateCatalog, events, activeRules, k, divWeight, service.getNoveltyFactor());

        long elapsedNanos = System.nanoTime() - startTime;
        double latencyMs = elapsedNanos / 1_000_000.0;

        service.recordRequest(latencyMs, false);
        totalPlatformQueries.incrementAndGet();

        logTelemetry("Generated " + results.size() + " recommendations for " + user.getName() +
                " via " + algoToUse.getLabel() + " (Latency: " + (Math.round(latencyMs * 10.0) / 10.0) + "ms)");

        return results;
    }

    // ── TELEMETRY & SLA COUNTERS ──────────────────────────────────────────────────

    public long getTotalQueries() { return totalPlatformQueries.get(); }
    public long getTotalConversions() { return totalPlatformConversions.get(); }
    public double getCurrentQps() { return Math.round(currentQps * 10.0) / 10.0; }
    public double getP50Latency() { return p50LatencyMs; }
    public double getP95Latency() { return p95LatencyMs; }
    public double getP99Latency() { return p99LatencyMs; }
    public double getCacheHitRatio() { return cacheHitRatio; }
    public double getCatalogCoverage() { return catalogCoverageRatio; }

    public void updateMetricsPulse(double qpsDelta, double p50, double p95, double p99) {
        this.currentQps = Math.max(12.0, Math.min(180.0, this.currentQps + qpsDelta));
        this.p50LatencyMs = p50;
        this.p95LatencyMs = p95;
        this.p99LatencyMs = p99;
    }

    // ── CSV EXPORT UTILITIES (UNIT 4) ─────────────────────────────────────────────

    public void exportCatalogToCSV(File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ItemID,ServiceId,Title,Category,Domain,Price,Rating,Inventory,Status,Views,Clicks,Conversions,CTR_Percent");
            for (ContentItem item : catalog.values()) {
                pw.printf("%s,%s,\"%s\",%s,%s,%.2f,%.1f,%d,%s,%d,%d,%d,%.1f%%%n",
                        item.getItemId(), item.getServiceId(), item.getTitle().replace("\"", "\"\""),
                        item.getCategory(), item.getDomain().name(), item.getPrice(), item.getRating(),
                        item.getInventory(), item.getStatus().name(), item.getViewCount(),
                        item.getClickCount(), item.getConversionCount(), item.getCTR());
            }
        }
    }

    public void exportEventsToCSV(File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("EventID,Timestamp,UserID,ItemID,ServiceID,EventType,Weight,DwellTimeMs,Metadata");
            for (InteractionEvent e : events) {
                pw.printf("%s,%s,%s,%s,%s,%s,%.1f,%d,\"%s\"%n",
                        e.getEventId(), e.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        e.getUserId(), e.getItemId(), e.getServiceId(), e.getEventType().name(),
                        e.getWeight(), e.getDwellTimeMs(), e.getMetadata().replace("\"", "\"\""));
            }
        }
    }

    public void exportRecommendationsToCSV(File file, List<RecommendationResult> recs) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Rank,ItemID,Title,Category,ScorePercent,Algorithm,Reason,AppliedRules");
            for (RecommendationResult r : recs) {
                pw.printf("%d,%s,\"%s\",%s,%d%%,%s,\"%s\",\"%s\"%n",
                        r.getRank(), r.getItem().getItemId(), r.getItem().getTitle().replace("\"", "\"\""),
                        r.getItem().getCategory(), r.getMatchPercentage(), r.getAlgorithmUsed().name(),
                        r.getReason().replace("\"", "\"\""), String.join("; ", r.getAppliedRules()).replace("\"", "\"\""));
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────────────────
// BACKGROUND DAEMON THREADS (UNIT 4)
// ──────────────────────────────────────────────────────────────────────────────────────────────────

class PlatformTelemetryDaemon extends Thread {
    private volatile boolean running = true;

    public PlatformTelemetryDaemon() {
        setDaemon(true);
        setName("Platform-Telemetry-Daemon");
        setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(3000);
                PlatformDataStore ds = PlatformDataStore.INSTANCE;

                // Jitter realistic metrics
                double delta = (Math.random() - 0.48) * 3.5;
                double p50 = 7.5 + (Math.random() * 2.0);
                double p95 = 16.0 + (Math.random() * 4.5);
                double p99 = 26.0 + (Math.random() * 6.0);
                ds.updateMetricsPulse(delta, p50, p95, p99);

                // Occasionally simulate realistic background synthetic traffic
                if (Math.random() > 0.65) {
                    List<String> uKeys = new ArrayList<>(ds.getUsers().keySet());
                    List<String> cKeys = new ArrayList<>(ds.getCatalog().keySet());
                    if (!uKeys.isEmpty() && !cKeys.isEmpty()) {
                        String randUser = uKeys.get((int)(Math.random() * uKeys.size()));
                        String randItem = cKeys.get((int)(Math.random() * cKeys.size()));
                        ContentItem item = ds.getCatalogItem(randItem);
                        if (item != null) {
                            EventType[] types = {EventType.VIEW, EventType.CLICK, EventType.LIKE};
                            EventType randType = types[(int)(Math.random() * types.length)];
                            ds.logEvent(new InteractionEvent(
                                    null, randUser, randItem, item.getServiceId(), randType, 3000, "SimulatedPulse"));
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ignored) {}
        }
    }

    public void shutdown() {
        this.running = false;
        interrupt();
    }
}


// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 4: EMBEDDED REST API SERVER & WEB CONSOLE (NETWORKING & HTTP - UNIT 4)
// ════════════════════════════════════════════════════════════════════════════════════════════════

class PlatformWebServer {
    private HttpServer server;
    private final int port;
    private volatile boolean running = false;

    public PlatformWebServer(int port) {
        this.port = port;
    }

    public synchronized void start() {
        if (running) return;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Web UI and Docs
            server.createContext("/", new WebConsoleHandler());
            server.createContext("/docs", new WebConsoleHandler());

            // REST Endpoints
            server.createContext("/api/v1/health", new HealthHandler());
            server.createContext("/api/v1/services", new ServicesHandler());
            server.createContext("/api/v1/catalog", new CatalogHandler());
            server.createContext("/api/v1/users", new UsersHandler());
            server.createContext("/api/v1/recommend", new RecommendationHandler());
            server.createContext("/api/v1/events", new EventsHandler());
            server.createContext("/api/v1/metrics", new MetricsHandler());

            server.setExecutor(Executors.newFixedThreadPool(6));
            server.start();
            running = true;
            PlatformDataStore.INSTANCE.logTelemetry("Embedded REST API Server listening on port " + port);
        } catch (IOException e) {
            PlatformDataStore.INSTANCE.logTelemetry("Failed to start HTTP server on port " + port + ": " + e.getMessage());
        }
    }

    public synchronized void stop() {
        if (server != null && running) {
            server.stop(1);
            running = false;
            PlatformDataStore.INSTANCE.logTelemetry("REST API Server stopped.");
        }
    }

    public boolean isRunning() { return running; }
    public int getPort() { return port; }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // JSON UTILITIES
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-API-Key");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendHtmlResponse(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] bytes = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.trim().isEmpty()) return params;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                } catch (Exception ignored) {}
            } else if (kv.length == 1) {
                params.put(kv[0], "");
            }
        }
        return params;
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // REST API HANDLERS
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Runtime rt = Runtime.getRuntime();
            long freeMem = rt.freeMemory() / (1024 * 1024);
            long totalMem = rt.totalMemory() / (1024 * 1024);
            long usedMem = totalMem - freeMem;

            String json = "{\n" +
                    "  \"status\": \"UP\",\n" +
                    "  \"service\": \"Smart Recommendation Service Management Platform\",\n" +
                    "  \"sdg\": \"SDG 9: Industry, Innovation & Infrastructure\",\n" +
                    "  \"version\": \"2.4.0-enterprise\",\n" +
                    "  \"timestamp\": \"" + LocalDateTime.now() + "\",\n" +
                    "  \"jvm\": {\n" +
                    "    \"usedMemoryMB\": " + usedMem + ",\n" +
                    "    \"totalMemoryMB\": " + totalMem + ",\n" +
                    "    \"activeThreads\": " + Thread.activeCount() + "\n" +
                    "  }\n" +
                    "}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class ServicesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            PlatformDataStore ds = PlatformDataStore.INSTANCE;
            StringBuilder sb = new StringBuilder("[\n");
            int count = 0;
            for (RecommendationService s : ds.getServices().values()) {
                if (count > 0) sb.append(",\n");
                sb.append("  {\n")
                        .append("    \"serviceId\": \"").append(s.getServiceId()).append("\",\n")
                        .append("    \"name\": \"").append(escapeJson(s.getName())).append("\",\n")
                        .append("    \"domain\": \"").append(s.getDomain().name()).append("\",\n")
                        .append("    \"algorithmType\": \"").append(s.getAlgorithmType().name()).append("\",\n")
                        .append("    \"modelVersion\": \"").append(s.getModelVersion()).append("\",\n")
                        .append("    \"status\": \"").append(s.getStatus().name()).append("\",\n")
                        .append("    \"targetQps\": ").append(s.getTargetQps()).append(",\n")
                        .append("    \"avgLatencyMs\": ").append(s.getAvgLatencyMs()).append(",\n")
                        .append("    \"totalRequests\": ").append(s.getTotalRequests()).append(",\n")
                        .append("    \"conversionRate\": ").append(s.getConversionRate()).append("\n")
                        .append("  }");
                count++;
            }
            sb.append("\n]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class CatalogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            PlatformDataStore ds = PlatformDataStore.INSTANCE;
            Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());
            String serviceIdFilter = query.get("serviceId");
            String categoryFilter = query.get("category");

            Stream<ContentItem> stream = ds.getCatalog().values().stream();
            if (serviceIdFilter != null && !serviceIdFilter.isEmpty()) {
                stream = stream.filter(i -> i.getServiceId().equalsIgnoreCase(serviceIdFilter));
            }
            if (categoryFilter != null && !categoryFilter.trim().isEmpty() && !categoryFilter.equalsIgnoreCase("ALL") && !categoryFilter.equalsIgnoreCase("All Categories") && !categoryFilter.toLowerCase().startsWith("all")) {
                stream = stream.filter(i -> i.getCategory().equalsIgnoreCase(categoryFilter));
            }

            List<ContentItem> items = stream.collect(Collectors.toList());
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < items.size(); i++) {
                ContentItem item = items.get(i);
                if (i > 0) sb.append(",\n");
                sb.append("  {\n")
                        .append("    \"itemId\": \"").append(item.getItemId()).append("\",\n")
                        .append("    \"serviceId\": \"").append(item.getServiceId()).append("\",\n")
                        .append("    \"title\": \"").append(escapeJson(item.getTitle())).append("\",\n")
                        .append("    \"category\": \"").append(escapeJson(item.getCategory())).append("\",\n")
                        .append("    \"domain\": \"").append(item.getDomain().name()).append("\",\n")
                        .append("    \"price\": ").append(item.getPrice()).append(",\n")
                        .append("    \"rating\": ").append(item.getRating()).append(",\n")
                        .append("    \"inventory\": ").append(item.getInventory()).append(",\n")
                        .append("    \"status\": \"").append(item.getStatus().name()).append("\",\n")
                        .append("    \"viewCount\": ").append(item.getViewCount()).append(",\n")
                        .append("    \"clickCount\": ").append(item.getClickCount()).append(",\n")
                        .append("    \"ctrPercent\": ").append(item.getCTR()).append("\n")
                        .append("  }");
            }
            sb.append("\n]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            PlatformDataStore ds = PlatformDataStore.INSTANCE;
            StringBuilder sb = new StringBuilder("[\n");
            int count = 0;
            for (UserProfile u : ds.getUsers().values()) {
                if (count > 0) sb.append(",\n");
                sb.append("  {\n")
                        .append("    \"userId\": \"").append(u.getUserId()).append("\",\n")
                        .append("    \"name\": \"").append(escapeJson(u.getName())).append("\",\n")
                        .append("    \"email\": \"").append(escapeJson(u.getEmail())).append("\",\n")
                        .append("    \"segment\": \"").append(u.getSegment().name()).append("\",\n")
                        .append("    \"totalInteractions\": ").append(u.getTotalInteractions()).append(",\n")
                        .append("    \"affinities\": ").append(u.getCategoryAffinities().toString()).append("\n")
                        .append("  }");
                count++;
            }
            sb.append("\n]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class RecommendationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());
            String userId = query.getOrDefault("userId", "USR-101");
            String serviceId = query.getOrDefault("serviceId", "SVC-ECOMM-01");
            int k = 5;
            try {
                if (query.containsKey("k")) k = Math.max(1, Integer.parseInt(query.get("k")));
            } catch (Exception ignored) {}

            String algoStr = query.get("strategy");
            AlgorithmType algo = null;
            if (algoStr != null) {
                try { algo = AlgorithmType.valueOf(algoStr.toUpperCase()); } catch (Exception ignored) {}
            }

            try {
                List<RecommendationResult> recs = PlatformDataStore.INSTANCE.executeRecommendationPipeline(
                        userId, serviceId, algo, k, null, null);

                StringBuilder sb = new StringBuilder("{\n");
                sb.append("  \"userId\": \"").append(userId).append("\",\n");
                sb.append("  \"serviceId\": \"").append(serviceId).append("\",\n");
                sb.append("  \"generatedAt\": \"").append(LocalDateTime.now()).append("\",\n");
                sb.append("  \"count\": ").append(recs.size()).append(",\n");
                sb.append("  \"recommendations\": [\n");

                for (int i = 0; i < recs.size(); i++) {
                    RecommendationResult r = recs.get(i);
                    if (i > 0) sb.append(",\n");
                    sb.append("    {\n")
                            .append("      \"rank\": ").append(r.getRank()).append(",\n")
                            .append("      \"itemId\": \"").append(r.getItem().getItemId()).append("\",\n")
                            .append("      \"title\": \"").append(escapeJson(r.getItem().getTitle())).append("\",\n")
                            .append("      \"category\": \"").append(escapeJson(r.getItem().getCategory())).append("\",\n")
                            .append("      \"price\": ").append(r.getItem().getPrice()).append(",\n")
                            .append("      \"rating\": ").append(r.getItem().getRating()).append(",\n")
                            .append("      \"scorePercent\": ").append(r.getMatchPercentage()).append(",\n")
                            .append("      \"algorithm\": \"").append(r.getAlgorithmUsed().name()).append("\",\n")
                            .append("      \"reason\": \"").append(escapeJson(r.getReason())).append("\"\n")
                            .append("    }");
                }
                sb.append("\n  ]\n}");
                sendJsonResponse(exchange, 200, sb.toString());
            } catch (Exception ex) {
                String err = "{\"error\": \"" + escapeJson(ex.getMessage()) + "\"}";
                sendJsonResponse(exchange, 400, err);
            }
        }
    }

    static class EventsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));

                    // Simple lightweight parameter parser
                    String userId = extractJsonField(body, "userId", "USR-101");
                    String itemId = extractJsonField(body, "itemId", "ITEM-EC-101");
                    String serviceId = extractJsonField(body, "serviceId", "SVC-ECOMM-01");
                    String typeStr = extractJsonField(body, "eventType", "CLICK");

                    EventType type = EventType.CLICK;
                    try { type = EventType.valueOf(typeStr.toUpperCase()); } catch (Exception ignored) {}

                    InteractionEvent event = new InteractionEvent(null, userId, itemId, serviceId, type, 5000, "API-Ingest");
                    PlatformDataStore.INSTANCE.logEvent(event);

                    String res = "{\"status\": \"INGESTED\", \"eventId\": \"" + event.getEventId() + "\"}";
                    sendJsonResponse(exchange, 201, res);
                } catch (Exception ex) {
                    sendJsonResponse(exchange, 400, "{\"error\": \"Invalid request payload: " + escapeJson(ex.getMessage()) + "\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"error\": \"Method not allowed. Use POST.\"}");
            }
        }

        private String extractJsonField(String json, String field, String def) {
            String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) return m.group(1);
            return def;
        }
    }

    static class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            PlatformDataStore ds = PlatformDataStore.INSTANCE;
            String json = "{\n" +
                    "  \"totalServices\": " + ds.getServices().size() + ",\n" +
                    "  \"totalCatalogItems\": " + ds.getCatalog().size() + ",\n" +
                    "  \"totalUsers\": " + ds.getUsers().size() + ",\n" +
                    "  \"totalEventsIngested\": " + ds.getEvents().size() + ",\n" +
                    "  \"totalRecommendationsServed\": " + ds.getTotalQueries() + ",\n" +
                    "  \"currentQps\": " + ds.getCurrentQps() + ",\n" +
                    "  \"sla\": {\n" +
                    "    \"p50LatencyMs\": " + ds.getP50Latency() + ",\n" +
                    "    \"p95LatencyMs\": " + ds.getP95Latency() + ",\n" +
                    "    \"p99LatencyMs\": " + ds.getP99Latency() + ",\n" +
                    "    \"cacheHitRatio\": " + ds.getCacheHitRatio() + ",\n" +
                    "    \"catalogCoverageRatio\": " + ds.getCatalogCoverage() + "\n" +
                    "  }\n" +
                    "}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class WebConsoleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            PlatformDataStore ds = PlatformDataStore.INSTANCE;
            String html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <title>Smart Recommendation Service Management Platform | REST Console</title>\n" +
                    "  <style>\n" +
                    "    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }\n" +
                    "    body { background-color: #0F172A; color: #E2E8F0; padding: 30px; line-height: 1.6; }\n" +
                    "    .header { border-bottom: 1px solid #1E293B; padding-bottom: 20px; margin-bottom: 30px; }\n" +
                    "    h1 { color: #818CF8; font-size: 26px; display: flex; align-items: center; gap: 10px; }\n" +
                    "    .badge { background: #4338CA; color: #EEF2FF; font-size: 12px; padding: 4px 10px; border-radius: 9999px; font-weight: bold; }\n" +
                    "    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 30px; }\n" +
                    "    .kpi { background: #1E293B; padding: 20px; border-radius: 12px; border: 1px solid #334155; }\n" +
                    "    .kpi-title { font-size: 13px; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; }\n" +
                    "    .kpi-val { font-size: 28px; font-weight: bold; color: #F8FAFC; margin-top: 5px; }\n" +
                    "    .section { background: #1E293B; padding: 25px; border-radius: 12px; border: 1px solid #334155; margin-bottom: 25px; }\n" +
                    "    h2 { font-size: 18px; color: #38BDF8; margin-bottom: 15px; border-bottom: 1px solid #334155; padding-bottom: 8px; }\n" +
                    "    .endpoint { margin-bottom: 15px; background: #0F172A; padding: 12px 16px; border-radius: 8px; border: 1px solid #334155; display: flex; justify-content: space-between; align-items: center; }\n" +
                    "    .method { font-weight: bold; padding: 3px 8px; border-radius: 4px; font-size: 12px; margin-right: 10px; }\n" +
                    "    .get { background: #065F46; color: #34D399; }\n" +
                    "    .post { background: #1E40AF; color: #60A5FA; }\n" +
                    "    .url { font-family: monospace; color: #F1F5F9; font-size: 14px; text-decoration: none; }\n" +
                    "    .url:hover { text-decoration: underline; color: #38BDF8; }\n" +
                    "    .desc { color: #94A3B8; font-size: 13px; }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <div class=\"header\">\n" +
                    "    <h1>⚡ Smart Recommendation Service Management Platform <span class=\"badge\">SDG 9</span></h1>\n" +
                    "    <p style=\"color: #94A3B8; margin-top: 6px;\">Enterprise REST API & Live Telemetry Gateway | Capstone Project #256</p>\n" +
                    "  </div>\n" +
                    "  <div class=\"grid\">\n" +
                    "    <div class=\"kpi\"><div class=\"kpi-title\">Active Services</div><div class=\"kpi-val\">" + ds.getServices().size() + "</div></div>\n" +
                    "    <div class=\"kpi\"><div class=\"kpi-title\">Catalog Items</div><div class=\"kpi-val\">" + ds.getCatalog().size() + "</div></div>\n" +
                    "    <div class=\"kpi\"><div class=\"kpi-title\">Registered Profiles</div><div class=\"kpi-val\">" + ds.getUsers().size() + "</div></div>\n" +
                    "    <div class=\"kpi\"><div class=\"kpi-title\">Current QPS</div><div class=\"kpi-val\">" + ds.getCurrentQps() + "</div></div>\n" +
                    "    <div class=\"kpi\"><div class=\"kpi-title\">P95 Latency</div><div class=\"kpi-val\">" + ds.getP95Latency() + " ms</div></div>\n" +
                    "  </div>\n" +
                    "  <div class=\"section\">\n" +
                    "    <h2>🌐 Production REST Endpoints (Click to Test)</h2>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method get\">GET</span><a class=\"url\" href=\"/api/v1/health\" target=\"_blank\">/api/v1/health</a></div><div class=\"desc\">System Health & JVM Status</div></div>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method get\">GET</span><a class=\"url\" href=\"/api/v1/services\" target=\"_blank\">/api/v1/services</a></div><div class=\"desc\">All Recommendation Services & Pipelines</div></div>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method get\">GET</span><a class=\"url\" href=\"/api/v1/catalog\" target=\"_blank\">/api/v1/catalog</a></div><div class=\"desc\">Content Catalog Items (Query: ?serviceId=...&category=...)</div></div>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method get\">GET</span><a class=\"url\" href=\"/api/v1/users\" target=\"_blank\">/api/v1/users</a></div><div class=\"desc\">User Profiles, Segments & Taste Vectors</div></div>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method get\">GET</span><a class=\"url\" href=\"/api/v1/recommend?serviceId=SVC-ECOMM-01&userId=USR-101&k=5\" target=\"_blank\">/api/v1/recommend?serviceId=SVC-ECOMM-01&userId=USR-101&k=5</a></div><div class=\"desc\">Live AI Recommendation Pipeline Inference</div></div>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method post\">POST</span><span class=\"url\">/api/v1/events</span></div><div class=\"desc\">Ingest Interaction Event (Payload: JSON with userId, itemId, serviceId, eventType)</div></div>\n" +
                    "    <div class=\"endpoint\"><div><span class=\"method get\">GET</span><a class=\"url\" href=\"/api/v1/metrics\" target=\"_blank\">/api/v1/metrics</a></div><div class=\"desc\">Platform Telemetry & Algorithmic SLA Stats</div></div>\n" +
                    "  </div>\n" +
                    "</body>\n" +
                    "</html>";
            sendHtmlResponse(exchange, 200, html);
        }
    }
}


// ════════════════════════════════════════════════════════════════════════════════════════════════
// UNIT 5: MODERN SWING DESKTOP GUI & MANAGEMENT WORKBENCH (SIDEBAR + CARDLAYOUT ARCHITECTURE)
// ════════════════════════════════════════════════════════════════════════════════════════════════

public class SmartRecommendationPlatform extends JFrame {

    // Theme Color Palette (Tailwind Slate + Indigo)
    public static final Color SIDEBAR_BG   = new Color(11, 17, 32);    // #0B1120
    public static final Color BG_DARK      = new Color(15, 23, 42);    // #0F172A
    public static final Color PANEL_BG     = new Color(30, 41, 59);    // #1E293B
    public static final Color PANEL_ALT    = new Color(24, 34, 52);    // #182234
    public static final Color CARD_BG      = new Color(51, 65, 85);    // #334155
    public static final Color CARD_HOVER   = new Color(71, 85, 105);   // #475569
    public static final Color TEXT_MAIN    = new Color(248, 250, 252); // #F8FAFC
    public static final Color TEXT_MUTED   = new Color(148, 163, 184); // #94A3B8
    public static final Color ACCENT       = new Color(99, 102, 241);  // #6366F1
    public static final Color ACCENT_SOFT  = new Color(49, 46, 129);   // #312E81
    public static final Color SUCCESS      = new Color(16, 185, 129);  // #10B981
    public static final Color WARNING      = new Color(245, 158, 11);  // #F59E0B
    public static final Color DANGER       = new Color(239, 68, 68);   // #EF4444
    public static final Color CYAN         = new Color(14, 165, 233);  // #0EA5E9
    public static final Color BORDER_COLOR = new Color(51, 65, 85);    // #334155

    private final PlatformDataStore dataStore = PlatformDataStore.INSTANCE;
    private final PlatformWebServer webServer = new PlatformWebServer(8080);
    private final PlatformTelemetryDaemon telemetryDaemon = new PlatformTelemetryDaemon();

    // Navigation & Layout
    private CardLayout contentCardLayout;
    private JPanel contentDeck;
    private final List<SidebarNavButton> navButtons = new ArrayList<>();
    private JLabel topSectionTitle;
    private JLabel topSectionSubtitle;

    // Header & Sidebar Status Components
    private JComboBox<String> userSwitcherCombo;
    private JLabel userRoleBadgeLabel;
    private JLabel qpsStatusLabel;
    private JLabel serverStatusLabel;

    // Dashboard Live Labels
    private JLabel kpiServicesVal;
    private JLabel kpiCatalogVal;
    private JLabel kpiUsersVal;
    private JLabel kpiQueriesVal;
    private JLabel slaP50Val;
    private JLabel slaP95Val;
    private JLabel slaCacheVal;
    private JLabel slaCovVal;
    private EventDistributionCanvas chartCanvas;

    // Tab Panels
    private JPanel dashboardPanel;
    private JPanel servicesPanel;
    private JPanel catalogPanel;
    private JPanel usersPanel;
    private JPanel studioPanel;
    private JPanel experimentsPanel;
    private JPanel rulesPanel;
    private JPanel apiKeysPanel;
    private JPanel apiServerPanel;

    // Table Models & Tables
    private DefaultTableModel servicesTableModel;
    private DefaultTableModel catalogTableModel;
    private DefaultTableModel usersTableModel;
    private DefaultTableModel telemetryTableModel;
    private DefaultTableModel experimentsTableModel;
    private DefaultTableModel rulesTableModel;
    private DefaultTableModel apiKeysTableModel;
    private JTable usersTable;
    private Runnable catalogFilterUpdater;

    // Studio Components
    private JComboBox<String> studioUserCombo;
    private JComboBox<String> studioServiceCombo;
    private JComboBox<String> studioAlgoCombo;
    private JComboBox<String> studioCategoryCombo;
    private JSlider studioKSlider;
    private JSlider studioDiversitySlider;
    private JLabel studioKValueLabel;
    private JLabel studioDivValueLabel;
    private JPanel studioResultsContainer;
    private JLabel studioExecutionInfoLabel;
    private List<RecommendationResult> currentStudioRecommendations = new ArrayList<>();

    // User Affinities Side Panel
    private JPanel userAffinitiesPanel;

    public SmartRecommendationPlatform() {
        super("Smart Recommendation Service Management Platform - Capstone #256 (UN SDG 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1380, Math.max(1100, (int) (screenSize.width * 0.92)));
        int height = Math.min(860, Math.max(700, (int) (screenSize.height * 0.88)));
        setSize(width, height);
        setMinimumSize(new Dimension(1060, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        configureThemeDefaults();
        buildUI();

        telemetryDaemon.start();
        webServer.start();

        javax.swing.Timer refreshTimer = new javax.swing.Timer(3000, e -> refreshLiveTelemetryData());
        refreshTimer.start();
    }

    private void configureThemeDefaults() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_MAIN);
        UIManager.put("Label.foreground", TEXT_MAIN);
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("TableHeader.cellBorder", BorderFactory.createEmptyBorder(8, 10, 8, 10));
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ToolTip.background", CARD_BG);
        UIManager.put("ToolTip.foreground", TEXT_MAIN);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Build all 9 section views first
        buildDashboardTab();
        buildServicesTab();
        buildCatalogTab();
        buildUsersTab();
        buildStudioTab();
        buildExperimentsTab();
        buildRulesTab();
        buildApiKeysTab();
        buildApiServerTab();

        contentCardLayout = new CardLayout();
        contentDeck = new JPanel(contentCardLayout);
        contentDeck.setBackground(BG_DARK);

        contentDeck.add(dashboardPanel, "DASHBOARD");
        contentDeck.add(servicesPanel, "SERVICES");
        contentDeck.add(catalogPanel, "CATALOG");
        contentDeck.add(usersPanel, "USERS");
        contentDeck.add(studioPanel, "STUDIO");
        contentDeck.add(experimentsPanel, "EXPERIMENTS");
        contentDeck.add(rulesPanel, "RULES");
        contentDeck.add(apiKeysPanel, "APIKEYS");
        contentDeck.add(apiServerPanel, "APISERVER");

        JPanel mainRight = new JPanel(new BorderLayout());
        mainRight.setBackground(BG_DARK);
        mainRight.add(createTopBar(), BorderLayout.NORTH);
        mainRight.add(contentDeck, BorderLayout.CENTER);
        mainRight.add(createFooterPanel(), BorderLayout.SOUTH);

        add(createSidebar(), BorderLayout.WEST);
        add(mainRight, BorderLayout.CENTER);

        switchView(0, "DASHBOARD", "Executive Dashboard",
                "Real-time infrastructure SLAs, event ingestion telemetry & algorithmic health");
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // LEFT SIDEBAR & SLIM TOP BAR (ZERO OVERLAP)
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // 1. Brand Header
        JPanel brandPanel = new JPanel(new BorderLayout(10, 4));
        brandPanel.setBackground(SIDEBAR_BG);
        brandPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(16, 16, 16, 14)));

        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setPreferredSize(new Dimension(36, 36));
        logoBox.setBackground(ACCENT);
        JLabel logoIcon = new JLabel(new VectorIcon(VectorIcon.Kind.BOLT, 20, Color.WHITE));
        logoBox.add(logoIcon, BorderLayout.CENTER);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 2));
        brandText.setOpaque(false);
        JLabel appTitle = new JLabel("SmartRec Admin");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appTitle.setForeground(TEXT_MAIN);

        JLabel appTag = new JLabel("Project #256 | UN SDG 9");
        appTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        appTag.setForeground(new Color(165, 180, 252));

        brandText.add(appTitle);
        brandText.add(appTag);

        brandPanel.add(logoBox, BorderLayout.WEST);
        brandPanel.add(brandText, BorderLayout.CENTER);

        // 2. Navigation Links
        JPanel navList = new JPanel();
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setBackground(SIDEBAR_BG);
        navList.setBorder(new EmptyBorder(12, 10, 12, 10));

        JLabel navGroup1 = new JLabel("  PLATFORM OVERVIEW");
        navGroup1.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navGroup1.setForeground(TEXT_MUTED);
        navGroup1.setAlignmentX(Component.LEFT_ALIGNMENT);
        navList.add(navGroup1);
        navList.add(Box.createVerticalStrut(6));

        addNavItem(navList, 0, "DASHBOARD", "Executive Dashboard",
                "Real-time infrastructure SLAs, event ingestion telemetry & algorithmic health", VectorIcon.Kind.DASHBOARD);
        addNavItem(navList, 1, "SERVICES", "Services & Pipelines",
                "Manage multi-tenant recommendation services, active algorithms & model tuning", VectorIcon.Kind.SERVICES);
        addNavItem(navList, 2, "CATALOG", "Content Catalog",
                "Omnichannel item repository, metadata indexing, inventory & CTR performance", VectorIcon.Kind.CATALOG);
        addNavItem(navList, 3, "USERS", "User Profiles & Segments",
                "Audience cohort segmentation & dynamic 8-D preference taste vectors", VectorIcon.Kind.USERS);

        navList.add(Box.createVerticalStrut(12));
        JLabel navGroup2 = new JLabel("  AI STUDIO & GOVERNANCE");
        navGroup2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navGroup2.setForeground(TEXT_MUTED);
        navGroup2.setAlignmentX(Component.LEFT_ALIGNMENT);
        navList.add(navGroup2);
        navList.add(Box.createVerticalStrut(6));

        addNavItem(navList, 4, "STUDIO", "Recommendation Studio",
                "Live interactive sandbox: generate ranked predictions with XAI & live feedback", VectorIcon.Kind.STUDIO);
        addNavItem(navList, 5, "EXPERIMENTS", "A/B Testing",
                "Compare control vs challenger algorithms with live CTR & conversion telemetry", VectorIcon.Kind.EXPERIMENT);
        addNavItem(navList, 6, "RULES", "Business Guardrails",
                "Configure boost, bury, pin & quality filter rules applied on top of AI scores", VectorIcon.Kind.SHIELD);
        addNavItem(navList, 7, "APIKEYS", "API Keys & Clients",
                "Client credential issuance, tier quotas & token-bucket rate limiting", VectorIcon.Kind.KEY);
        addNavItem(navList, 8, "APISERVER", "REST API Console",
                "Embedded HTTP server endpoints (Port 8080) & live JSON request tester", VectorIcon.Kind.GLOBE);

        // 3. Bottom Role Switcher Card
        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
        rolePanel.setBackground(PANEL_ALT);
        rolePanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(12, 14, 14, 14)));

        JLabel roleTitle = new JLabel("ACTIVE OPERATOR SESSION");
        roleTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleTitle.setForeground(TEXT_MUTED);
        roleTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        userSwitcherCombo = new JComboBox<>();
        styleComboBox(userSwitcherCombo);
        userSwitcherCombo.setPrototypeDisplayValue("Dr. Alice Morgan (ADMIN)");
        userSwitcherCombo.setMaximumSize(new Dimension(216, 30));
        userSwitcherCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (PlatformUser u : dataStore.getAdminUsers().values()) {
            userSwitcherCombo.addItem(u.getName() + " (" + u.getRole().name() + ")");
        }

        userRoleBadgeLabel = new JLabel(dataStore.getActiveUser().getRoleBadge());
        userRoleBadgeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRoleBadgeLabel.setForeground(new Color(165, 180, 252));
        userRoleBadgeLabel.setMaximumSize(new Dimension(216, 20));
        userRoleBadgeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userSwitcherCombo.addActionListener(e -> {
            int idx = userSwitcherCombo.getSelectedIndex();
            if (idx >= 0) {
                List<PlatformUser> list = new ArrayList<>(dataStore.getAdminUsers().values());
                PlatformUser selected = list.get(idx);
                dataStore.setActiveUser(selected);
                userRoleBadgeLabel.setText(selected.getRoleBadge());
                switch (selected.getRole()) {
                    case CONTENT_CURATOR: navButtons.get(2).doClick(); break;
                    case SERVICE_MANAGER: navButtons.get(1).doClick(); break;
                    case DATA_ANALYST: navButtons.get(0).doClick(); break;
                    case API_CONSUMER: navButtons.get(8).doClick(); break;
                    default: break;
                }
            }
        });

        rolePanel.add(roleTitle);
        rolePanel.add(Box.createVerticalStrut(6));
        rolePanel.add(userSwitcherCombo);
        rolePanel.add(Box.createVerticalStrut(6));
        rolePanel.add(userRoleBadgeLabel);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(createBorderlessScrollPane(navList, SIDEBAR_BG), BorderLayout.CENTER);
        sidebar.add(rolePanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNavItem(JPanel container, int idx, String cardKey, String title, String subtitle, VectorIcon.Kind iconKind) {
        SidebarNavButton btn = new SidebarNavButton(title, iconKind);
        btn.addActionListener(e -> switchView(idx, cardKey, title, subtitle));
        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(3));
    }

    private void switchView(int index, String cardKey, String title, String subtitle) {
        for (int i = 0; i < navButtons.size(); i++) {
            navButtons.get(i).setActive(i == index);
        }
        topSectionTitle.setText(title);
        topSectionSubtitle.setText(subtitle);
        contentCardLayout.show(contentDeck, cardKey);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setBackground(PANEL_BG);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(8, 20, 8, 20)));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 1));
        left.setOpaque(false);

        topSectionTitle = new JLabel("Executive Dashboard");
        topSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topSectionTitle.setForeground(TEXT_MAIN);

        topSectionSubtitle = new JLabel("Real-time infrastructure SLAs, event ingestion telemetry & algorithmic health");
        topSectionSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        topSectionSubtitle.setForeground(TEXT_MUTED);

        left.add(topSectionTitle);
        left.add(topSectionSubtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        right.setOpaque(false);

        serverStatusLabel = createBadge("HTTP :8080 Online", SUCCESS);
        serverStatusLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        serverStatusLabel.setToolTipText("Click to open REST Web Console at http://localhost:8080/");
        serverStatusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try { Desktop.getDesktop().browse(new URI("http://localhost:8080/")); } catch (Exception ignored) {}
            }
        });

        qpsStatusLabel = createBadge("QPS: " + dataStore.getCurrentQps(), CYAN);

        JButton refreshBtn = createIconButton("Refresh", VectorIcon.Kind.REFRESH, CARD_BG, TEXT_MAIN);
        refreshBtn.addActionListener(e -> refreshAllTables());

        JButton resetBtn = createIconButton("Reset Demo", VectorIcon.Kind.BOLT, CARD_BG, TEXT_MAIN);
        resetBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Restore all recommendation services, catalog items, rules & telemetry to initial demo state?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                dataStore.resetToDefaultData();
                refreshAllTables();
                if (studioResultsContainer != null) {
                    studioResultsContainer.removeAll();
                    studioResultsContainer.revalidate();
                    studioResultsContainer.repaint();
                }
                JOptionPane.showMessageDialog(this, "Demo dataset cleanly restored!", "Data Reset", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        right.add(serverStatusLabel);
        right.add(qpsStatusLabel);
        right.add(refreshBtn);
        right.add(resetBtn);

        topBar.add(left, BorderLayout.CENTER);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PANEL_BG);
        footer.setPreferredSize(new Dimension(0, 28));
        footer.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(4, 20, 4, 20)));

        JLabel left = new JLabel("OOT Java  |  Capstone #256  |  UN SDG 9: Industry, Innovation & Infrastructure");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        left.setForeground(TEXT_MUTED);

        JLabel right = new JLabel("REST API :8080  |  Telemetry Daemon Active  ");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(TEXT_MUTED);

        footer.add(left, BorderLayout.CENTER);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 1: EXECUTIVE DASHBOARD
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildDashboardTab() {
        dashboardPanel = new JPanel(new BorderLayout(14, 14));
        dashboardPanel.setBackground(BG_DARK);
        dashboardPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Top 4 KPI Cards with Live Value Labels
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiRow.setOpaque(false);
        kpiRow.setPreferredSize(new Dimension(0, 88));

        kpiServicesVal = new JLabel(String.valueOf(dataStore.getServices().size()));
        kpiCatalogVal = new JLabel(String.valueOf(dataStore.getCatalog().size()));
        kpiUsersVal = new JLabel(String.valueOf(dataStore.getUsers().size()));
        kpiQueriesVal = new JLabel(String.valueOf(dataStore.getTotalQueries()));

        kpiRow.add(createKpiCard("Active Services", kpiServicesVal, "Recommendation Pipelines", VectorIcon.Kind.SERVICES, ACCENT));
        kpiRow.add(createKpiCard("Catalog Items", kpiCatalogVal, "Omnichannel Repository", VectorIcon.Kind.CATALOG, CYAN));
        kpiRow.add(createKpiCard("User Profiles", kpiUsersVal, "Audience Segments", VectorIcon.Kind.USERS, SUCCESS));
        kpiRow.add(createKpiCard("Queries Served", kpiQueriesVal, "Total AI Inferences", VectorIcon.Kind.BOLT, WARNING));

        // Center Split: Left SLA + Chart, Right Live Telemetry Feed
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        centerGrid.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(PANEL_BG);
        leftPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel slaWrapper = new JPanel(new BorderLayout(0, 8));
        slaWrapper.setOpaque(false);

        JLabel slaHeader = new JLabel("Infrastructure SLA & Algorithmic Performance");
        slaHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        slaHeader.setForeground(TEXT_MAIN);

        JPanel metricsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        metricsGrid.setOpaque(false);
        metricsGrid.setPreferredSize(new Dimension(0, 118));

        slaP50Val = new JLabel(dataStore.getP50Latency() + " ms");
        slaP95Val = new JLabel(dataStore.getP95Latency() + " ms");
        slaCacheVal = new JLabel((int) (dataStore.getCacheHitRatio() * 100) + "%");
        slaCovVal = new JLabel((int) (dataStore.getCatalogCoverage() * 100) + "%");

        metricsGrid.add(createSlaMetricTile("P50 Latency", slaP50Val, "Target: <15ms", SUCCESS));
        metricsGrid.add(createSlaMetricTile("P95 Latency", slaP95Val, "Target: <30ms", SUCCESS));
        metricsGrid.add(createSlaMetricTile("Cache Hit Ratio", slaCacheVal, "In-Memory Feature Tier", ACCENT));
        metricsGrid.add(createSlaMetricTile("Catalog Coverage", slaCovVal, "Long-Tail Reach", CYAN));

        slaWrapper.add(slaHeader, BorderLayout.NORTH);
        slaWrapper.add(metricsGrid, BorderLayout.CENTER);

        chartCanvas = new EventDistributionCanvas();
        chartCanvas.setMinimumSize(new Dimension(280, 180));

        leftPanel.add(slaWrapper, BorderLayout.NORTH);
        leftPanel.add(chartCanvas, BorderLayout.CENTER);

        // Right Panel: Real-Time Telemetry Event Stream
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(PANEL_BG);
        rightPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel feedHeaderPanel = new JPanel(new BorderLayout());
        feedHeaderPanel.setOpaque(false);
        JLabel feedTitle = new JLabel("Real-Time Telemetry & Event Stream");
        feedTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedTitle.setForeground(TEXT_MAIN);

        JButton exportLogsBtn = createIconButton("Export CSV", VectorIcon.Kind.COPY, CARD_BG, TEXT_MAIN);
        exportLogsBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("Platform_Telemetry_Events.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    dataStore.exportEventsToCSV(chooser.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Telemetry stream exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        feedHeaderPanel.add(feedTitle, BorderLayout.WEST);
        feedHeaderPanel.add(exportLogsBtn, BorderLayout.EAST);

        String[] cols = {"Time", "User", "Item", "Service", "Event", "Weight"};
        telemetryTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable telemetryTable = createStyledTable(telemetryTableModel);
        telemetryTable.getColumnModel().getColumn(4).setCellRenderer(new EventBadgeRenderer());
        setColumnWidths(telemetryTable, 70, 75, 95, 100, 115, 55);

        JScrollPane scroll = createStyledScrollPane(telemetryTable);

        rightPanel.add(feedHeaderPanel, BorderLayout.NORTH);
        rightPanel.add(scroll, BorderLayout.CENTER);

        centerGrid.add(leftPanel);
        centerGrid.add(rightPanel);

        dashboardPanel.add(kpiRow, BorderLayout.NORTH);
        dashboardPanel.add(centerGrid, BorderLayout.CENTER);

        refreshTelemetryTable();
    }

    private JPanel createKpiCard(String title, JLabel valLabel, String subtitle, VectorIcon.Kind iconKind, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(PANEL_BG);
        card.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(12, 16, 12, 16)));

        JPanel textSide = new JPanel(new GridLayout(3, 1, 0, 2));
        textSide.setOpaque(false);

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLabel.setForeground(TEXT_MAIN);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(TEXT_MUTED);

        textSide.add(t);
        textSide.add(valLabel);
        textSide.add(s);

        JLabel ic = new JLabel(new VectorIcon(iconKind, 28, accentColor));
        card.add(textSide, BorderLayout.CENTER);
        card.add(ic, BorderLayout.EAST);
        return card;
    }

    private JPanel createSlaMetricTile(String title, JLabel valLabel, String target, Color color) {
        JPanel p = new JPanel(new GridLayout(3, 1, 0, 2));
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.setForeground(TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        valLabel.setForeground(color);

        JLabel sub = new JLabel(target);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXT_MUTED);

        p.add(t);
        p.add(valLabel);
        p.add(sub);
        return p;
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 2: RECOMMENDATION SERVICES & PIPELINES
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildServicesTab() {
        servicesPanel = new JPanel(new BorderLayout(14, 14));
        servicesPanel.setBackground(BG_DARK);
        servicesPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton addServiceBtn = createIconButton("Register Service", VectorIcon.Kind.PLUS, ACCENT, TEXT_MAIN);
        JButton toggleStatusBtn = createIconButton("Toggle Status", VectorIcon.Kind.BOLT, CARD_BG, TEXT_MAIN);
        JButton retrainBtn = createIconButton("Trigger Model Retraining", VectorIcon.Kind.REFRESH, CYAN, TEXT_MAIN);
        JButton editHyperparamsBtn = createIconButton("Tune Hyperparameters", VectorIcon.Kind.SERVICES, CARD_BG, TEXT_MAIN);

        toolbar.add(addServiceBtn);
        toolbar.add(toggleStatusBtn);
        toolbar.add(retrainBtn);
        toolbar.add(editHyperparamsBtn);

        String[] cols = {"Service ID", "Service Name", "Domain", "Active Algorithm", "Version", "Status", "Target QPS", "Latency", "Requests", "Conv. Rate"};
        servicesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(servicesTableModel);
        table.getColumnModel().getColumn(5).setCellRenderer(new ServiceStatusBadgeRenderer());
        setColumnWidths(table, 100, 230, 140, 160, 90, 95, 75, 75, 75, 80);

        JScrollPane scroll = createStyledScrollPane(table);

        JPanel drawer = new JPanel(new GridLayout(1, 4, 12, 0));
        drawer.setBackground(PANEL_BG);
        drawer.setPreferredSize(new Dimension(0, 96));
        drawer.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(12, 14, 12, 14)));

        drawer.add(createParamExplanation("Collaborative Filtering", "Leverages peer co-occurrence & interaction overlap matrices. Ideal for warm users."));
        drawer.add(createParamExplanation("Content-Based Cosine", "Calculates vector embeddings similarity between user taste vectors and catalog features."));
        drawer.add(createParamExplanation("Hybrid Ensemble", "Weighted multi-task blend with Maximal Marginal Relevance (MMR) diversity penalty."));
        drawer.add(createParamExplanation("Cold-Start Strategy", "Time-decayed trending velocity fallback prevents recommendation starvation."));

        addServiceBtn.addActionListener(e -> showRegisterServiceDialog());

        toggleStatusBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String sid = (String) servicesTableModel.getValueAt(row, 0);
                RecommendationService s = dataStore.getService(sid);
                if (s != null) {
                    s.setStatus(s.getStatus() == ServiceStatus.ACTIVE ? ServiceStatus.MAINTENANCE : ServiceStatus.ACTIVE);
                    refreshServicesTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a service from the table.", "Select Service", JOptionPane.WARNING_MESSAGE);
            }
        });

        retrainBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String sid = (String) servicesTableModel.getValueAt(row, 0);
                RecommendationService s = dataStore.getService(sid);
                if (s != null) {
                    s.setModelVersion("v" + (int)(Math.random() * 5 + 3) + "." + (int)(Math.random() * 9) + ".0-prod");
                    dataStore.logTelemetry("Retrained offline matrix embeddings for pipeline: " + s.getName());
                    refreshServicesTable();
                    JOptionPane.showMessageDialog(this, "Model retraining completed!\nActive deployment: " + s.getModelVersion(), "Retrained", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a service to retrain.", "Select Service", JOptionPane.WARNING_MESSAGE);
            }
        });

        editHyperparamsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String sid = (String) servicesTableModel.getValueAt(row, 0);
                showHyperparameterDialog(dataStore.getService(sid));
            } else {
                JOptionPane.showMessageDialog(this, "Select a service to configure.", "Select Service", JOptionPane.WARNING_MESSAGE);
            }
        });

        servicesPanel.add(toolbar, BorderLayout.NORTH);
        servicesPanel.add(scroll, BorderLayout.CENTER);
        servicesPanel.add(drawer, BorderLayout.SOUTH);

        refreshServicesTable();
    }

    private JPanel createParamExplanation(String title, String desc) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(new Color(165, 180, 252));

        JTextArea a = new JTextArea(desc);
        a.setWrapStyleWord(true);
        a.setLineWrap(true);
        a.setEditable(false);
        a.setOpaque(false);
        a.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        a.setForeground(TEXT_MUTED);

        p.add(t, BorderLayout.NORTH);
        p.add(a, BorderLayout.CENTER);
        return p;
    }

    private void showRegisterServiceDialog() {
        JDialog dialog = new JDialog(this, "Register New Recommendation Service", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField("SVC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        JTextField nameField = new JTextField();
        JComboBox<ServiceDomain> domainCombo = new JComboBox<>(ServiceDomain.values());
        JComboBox<AlgorithmType> algoCombo = new JComboBox<>(AlgorithmType.values());
        JTextField qpsField = new JTextField("150");
        JTextField ttlField = new JTextField("300");

        styleInputField(idField);
        styleInputField(nameField);
        styleComboBox(domainCombo);
        styleComboBox(algoCombo);
        styleInputField(qpsField);
        styleInputField(ttlField);

        addGridRow(form, gbc, 0, "Service ID:", idField);
        addGridRow(form, gbc, 1, "Service Name:", nameField);
        addGridRow(form, gbc, 2, "Domain:", domainCombo);
        addGridRow(form, gbc, 3, "Algorithm Engine:", algoCombo);
        addGridRow(form, gbc, 4, "Target QPS:", qpsField);
        addGridRow(form, gbc, 5, "Cache TTL (sec):", ttlField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton saveBtn = createStyledButton("Register Service", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter service name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int qps = 100, ttl = 300;
            try {
                qps = Integer.parseInt(qpsField.getText().trim());
                ttl = Integer.parseInt(ttlField.getText().trim());
            } catch (Exception ignored) {}

            RecommendationService svc = new RecommendationService(
                    idField.getText().trim(), name, (ServiceDomain) domainCombo.getSelectedItem(),
                    (AlgorithmType) algoCombo.getSelectedItem(), "v1.0.0-prod", qps, ttl, 0.2, 0.3);
            dataStore.registerService(svc);
            refreshAllTables();
            dialog.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showHyperparameterDialog(RecommendationService svc) {
        if (svc == null) return;
        JDialog dialog = new JDialog(this, "Tune Hyperparameters: " + svc.getName(), true);
        dialog.setSize(490, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<AlgorithmType> algoCombo = new JComboBox<>(AlgorithmType.values());
        algoCombo.setSelectedItem(svc.getAlgorithmType());
        styleComboBox(algoCombo);

        JSlider divSlider = createStyledSlider(0, 100, (int) (svc.getDiversityWeight() * 100));
        JSlider novSlider = createStyledSlider(0, 100, (int) (svc.getNoveltyFactor() * 100));

        JTextField qpsField = new JTextField(String.valueOf(svc.getTargetQps()));
        JTextField ttlField = new JTextField(String.valueOf(svc.getCacheTtlSeconds()));
        styleInputField(qpsField);
        styleInputField(ttlField);

        addGridRow(form, gbc, 0, "Active Algorithm:", algoCombo);
        addGridRow(form, gbc, 1, "Diversity (MMR) %:", divSlider);
        addGridRow(form, gbc, 2, "Novelty Penalty %:", novSlider);
        addGridRow(form, gbc, 3, "Target QPS:", qpsField);
        addGridRow(form, gbc, 4, "Cache TTL (Seconds):", ttlField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton saveBtn = createStyledButton("Save Parameters", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            svc.setAlgorithmType((AlgorithmType) algoCombo.getSelectedItem());
            svc.setDiversityWeight(divSlider.getValue() / 100.0);
            svc.setNoveltyFactor(novSlider.getValue() / 100.0);
            try {
                svc.setTargetQps(Integer.parseInt(qpsField.getText().trim()));
                svc.setCacheTtlSeconds(Integer.parseInt(ttlField.getText().trim()));
            } catch (Exception ignored) {}

            dataStore.logTelemetry("Updated hyperparameters for pipeline: " + svc.getServiceId());
            refreshServicesTable();
            dialog.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 3: CONTENT CATALOG MANAGEMENT
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildCatalogTab() {
        catalogPanel = new JPanel(new BorderLayout(14, 14));
        catalogPanel.setBackground(BG_DARK);
        catalogPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Responsive Toolbar: Top Search/Filter bar + Actions neatly aligned with zero overlap
        JPanel toolbar = new JPanel(new BorderLayout(12, 8));
        toolbar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        searchPanel.setOpaque(false);

        JLabel searchIcon = new JLabel(new VectorIcon(VectorIcon.Kind.SEARCH, 15, TEXT_MUTED));
        JTextField searchField = new JTextField(14);
        searchField.setToolTipText("Search by title, item ID or tag");
        styleInputField(searchField);

        JComboBox<String> serviceFilterCombo = new JComboBox<>();
        styleComboBox(serviceFilterCombo);
        serviceFilterCombo.setPrototypeDisplayValue("SVC-ECOMM-01");
        serviceFilterCombo.addItem("All Services");
        for (RecommendationService s : dataStore.getServices().values()) {
            serviceFilterCombo.addItem(s.getServiceId());
        }

        JComboBox<String> categoryFilterCombo = new JComboBox<>();
        styleComboBox(categoryFilterCombo);
        categoryFilterCombo.setPrototypeDisplayValue("Artificial Intelligence");
        categoryFilterCombo.addItem("All Categories");
        Set<String> categories = dataStore.getCatalog().values().stream()
                .map(ContentItem::getCategory).collect(Collectors.toCollection(TreeSet::new));
        for (String c : categories) categoryFilterCombo.addItem(c);

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(createFormLabel("Service:"));
        searchPanel.add(serviceFilterCombo);
        searchPanel.add(createFormLabel("Category:"));
        searchPanel.add(categoryFilterCombo);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        actionsPanel.setOpaque(false);

        JButton addItemBtn = createIconButton("Add Item", VectorIcon.Kind.PLUS, ACCENT, TEXT_MAIN);
        JButton deleteItemBtn = createIconButton("Delete", VectorIcon.Kind.TRASH, DANGER, TEXT_MAIN);
        JButton exportCatalogBtn = createIconButton("Export CSV", VectorIcon.Kind.COPY, CARD_BG, TEXT_MAIN);

        actionsPanel.add(addItemBtn);
        actionsPanel.add(deleteItemBtn);
        actionsPanel.add(exportCatalogBtn);

        toolbar.add(searchPanel, BorderLayout.CENTER);
        toolbar.add(actionsPanel, BorderLayout.EAST);

        // Catalog Table
        String[] cols = {"Item ID", "Service", "Title", "Category", "Domain", "Price", "Rating", "Stock", "Status", "Views", "Clicks", "CTR"};
        catalogTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(catalogTableModel);
        table.getColumnModel().getColumn(8).setCellRenderer(new ItemStatusBadgeRenderer());
        setColumnWidths(table, 90, 95, 220, 110, 115, 75, 65, 60, 85, 60, 60, 65);

        JScrollPane scroll = createStyledScrollPane(table);

        catalogFilterUpdater = () -> {
            String q = searchField.getText().trim().toLowerCase();
            String sFilter = (String) serviceFilterCombo.getSelectedItem();
            String cFilter = (String) categoryFilterCombo.getSelectedItem();
            if (sFilter == null) sFilter = "All Services";
            if (cFilter == null) cFilter = "All Categories";

            catalogTableModel.setRowCount(0);
            for (ContentItem item : dataStore.getCatalog().values()) {
                boolean matchText = q.isEmpty() || item.getTitle().toLowerCase().contains(q) ||
                        item.getItemId().toLowerCase().contains(q) || item.getTags().stream().anyMatch(t -> t.toLowerCase().contains(q));
                boolean matchService = sFilter.equals("All Services") || item.getServiceId().equalsIgnoreCase(sFilter);
                boolean matchCategory = cFilter.equals("All Categories") || item.getCategory().equalsIgnoreCase(cFilter);

                if (matchText && matchService && matchCategory) {
                    catalogTableModel.addRow(new Object[]{
                            item.getItemId(), item.getServiceId(), item.getTitle(), item.getCategory(),
                            item.getDomain().name(), item.getPrice() > 0 ? String.format("$%.2f", item.getPrice()) : "Free",
                            item.getRating() + " / 5", item.getInventory(), item.getStatus(),
                            item.getViewCount(), item.getClickCount(), item.getCTR() + "%"
                    });
                }
            }
        };

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { catalogFilterUpdater.run(); }
        });
        serviceFilterCombo.addActionListener(e -> catalogFilterUpdater.run());
        categoryFilterCombo.addActionListener(e -> catalogFilterUpdater.run());

        addItemBtn.addActionListener(e -> showAddCatalogItemDialog());

        deleteItemBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) catalogTableModel.getValueAt(row, 0);
                int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove item [" + id + "]?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    dataStore.removeCatalogItem(id);
                    refreshAllTables();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select an item to delete.", "Select Item", JOptionPane.WARNING_MESSAGE);
            }
        });

        exportCatalogBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("Platform_Content_Catalog.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    dataStore.exportCatalogToCSV(chooser.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Catalog exported successfully!", "Exported", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        catalogPanel.add(toolbar, BorderLayout.NORTH);
        catalogPanel.add(scroll, BorderLayout.CENTER);

        refreshCatalogTable();
    }

    private void showAddCatalogItemDialog() {
        JDialog dialog = new JDialog(this, "Add New Catalog Item", true);
        dialog.setSize(470, 440);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField("ITEM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        JTextField titleField = new JTextField();
        JTextField categoryField = new JTextField();
        JComboBox<RecommendationService> serviceCombo = new JComboBox<>();
        for (RecommendationService s : dataStore.getServices().values()) serviceCombo.addItem(s);

        JTextField priceField = new JTextField("99.99");
        JTextField ratingField = new JTextField("4.8");
        JTextField stockField = new JTextField("50");

        styleInputField(idField);
        styleInputField(titleField);
        styleInputField(categoryField);
        styleComboBox(serviceCombo);
        styleInputField(priceField);
        styleInputField(ratingField);
        styleInputField(stockField);

        addGridRow(form, gbc, 0, "Item ID:", idField);
        addGridRow(form, gbc, 1, "Title:", titleField);
        addGridRow(form, gbc, 2, "Target Service:", serviceCombo);
        addGridRow(form, gbc, 3, "Category:", categoryField);
        addGridRow(form, gbc, 4, "Price ($):", priceField);
        addGridRow(form, gbc, 5, "Initial Rating (1-5):", ratingField);
        addGridRow(form, gbc, 6, "Stock Inventory:", stockField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton saveBtn = createStyledButton("Save Item", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String cat = categoryField.getText().trim();
            if (title.isEmpty() || cat.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and Category are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double price = 0.0, rating = 4.5;
            int stock = 10;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                rating = Double.parseDouble(ratingField.getText().trim());
                stock = Integer.parseInt(stockField.getText().trim());
            } catch (Exception ignored) {}

            RecommendationService selSvc = (RecommendationService) serviceCombo.getSelectedItem();
            ContentItem item = new ContentItem(
                    idField.getText().trim(), selSvc.getServiceId(), title, cat,
                    selSvc.getDomain(), Arrays.asList("New", cat), price, rating, stock, null);

            dataStore.addCatalogItem(item);
            refreshAllTables();
            dialog.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 4: USER PROFILES & SEGMENTS
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildUsersTab() {
        usersPanel = new JPanel(new BorderLayout(14, 14));
        usersPanel.setBackground(BG_DARK);
        usersPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.58);
        splitPane.setBackground(BG_DARK);
        splitPane.setBorder(null);
        splitPane.setDividerSize(6);

        // Left Users Directory
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(PANEL_BG);
        leftPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(14, 14, 14, 14)));

        JPanel userToolbar = new JPanel(new BorderLayout());
        userToolbar.setOpaque(false);
        JLabel userListTitle = new JLabel("User Directory & Audience Segments");
        userListTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userListTitle.setForeground(TEXT_MAIN);

        JButton addUserBtn = createIconButton("Add Profile", VectorIcon.Kind.PLUS, ACCENT, TEXT_MAIN);
        addUserBtn.addActionListener(e -> showAddUserDialog());
        userToolbar.add(userListTitle, BorderLayout.WEST);
        userToolbar.add(addUserBtn, BorderLayout.EAST);

        String[] cols = {"User ID", "Name", "Email", "Segment", "Events", "Status"};
        usersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = createStyledTable(usersTableModel);
        usersTable.getColumnModel().getColumn(3).setCellRenderer(new UserSegmentBadgeRenderer());
        setColumnWidths(usersTable, 75, 130, 170, 165, 65, 70);

        JScrollPane userScroll = createStyledScrollPane(usersTable);

        leftPanel.add(userToolbar, BorderLayout.NORTH);
        leftPanel.add(userScroll, BorderLayout.CENTER);

        // Right Detail Panel: Taste Vector Affinities
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(PANEL_BG);
        rightPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel detailTitle = new JLabel("Dynamic Taste Vector & Category Affinities");
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailTitle.setForeground(TEXT_MAIN);

        userAffinitiesPanel = new JPanel();
        userAffinitiesPanel.setLayout(new BoxLayout(userAffinitiesPanel, BoxLayout.Y_AXIS));
        userAffinitiesPanel.setOpaque(false);

        JScrollPane affinityScroll = createStyledScrollPane(userAffinitiesPanel);

        rightPanel.add(detailTitle, BorderLayout.NORTH);
        rightPanel.add(affinityScroll, BorderLayout.CENTER);

        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = usersTable.getSelectedRow();
            if (row >= 0) {
                String uid = (String) usersTableModel.getValueAt(row, 0);
                UserProfile u = dataStore.getUser(uid);
                if (u != null) displayUserAffinities(u);
            }
        });

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        usersPanel.add(splitPane, BorderLayout.CENTER);

        refreshUsersTable();
        if (usersTable.getRowCount() > 0) {
            usersTable.setRowSelectionInterval(0, 0);
        }
    }

    private void displayUserAffinities(UserProfile u) {
        userAffinitiesPanel.removeAll();

        JPanel userCard = new JPanel(new GridLayout(3, 1, 0, 4));
        userCard.setBackground(CARD_BG);
        userCard.setBorder(new EmptyBorder(10, 14, 10, 14));
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(u.getName() + " (" + u.getUserId() + ")");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(TEXT_MAIN);

        JLabel segLbl = new JLabel("Segment: " + u.getSegment().getLabel());
        segLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        segLbl.setForeground(u.getSegment().getBadgeColor());

        JLabel actLbl = new JLabel("Total Lifetime Interactions: " + u.getTotalInteractions());
        actLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        actLbl.setForeground(TEXT_MUTED);

        userCard.add(nameLbl);
        userCard.add(segLbl);
        userCard.add(actLbl);

        userAffinitiesPanel.add(userCard);
        userAffinitiesPanel.add(Box.createVerticalStrut(14));

        JLabel affHeader = new JLabel("CATEGORY AFFINITY SCORES (ONLINE GRADIENT VECTOR)");
        affHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        affHeader.setForeground(TEXT_MUTED);
        affHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        userAffinitiesPanel.add(affHeader);
        userAffinitiesPanel.add(Box.createVerticalStrut(8));

        Map<String, Double> affs = u.getCategoryAffinities();
        if (affs.isEmpty()) {
            JLabel emptyLbl = new JLabel("No interaction history yet (Cold-Start Profile).");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            emptyLbl.setForeground(TEXT_MUTED);
            emptyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            userAffinitiesPanel.add(emptyLbl);
        } else {
            for (Map.Entry<String, Double> entry : affs.entrySet()) {
                JPanel barPanel = new JPanel(new BorderLayout(10, 0));
                barPanel.setOpaque(false);
                barPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
                barPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel catName = new JLabel(entry.getKey());
                catName.setPreferredSize(new Dimension(135, 22));
                catName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                catName.setForeground(TEXT_MAIN);

                JProgressBar bar = new JProgressBar(0, 100);
                bar.setValue((int) (entry.getValue() * 100));
                bar.setStringPainted(true);
                bar.setString((int) (entry.getValue() * 100) + "% Match");
                bar.setForeground(ACCENT);
                bar.setBackground(CARD_BG);
                bar.setBorderPainted(false);

                barPanel.add(catName, BorderLayout.WEST);
                barPanel.add(bar, BorderLayout.CENTER);

                userAffinitiesPanel.add(barPanel);
                userAffinitiesPanel.add(Box.createVerticalStrut(6));
            }
        }

        userAffinitiesPanel.add(Box.createVerticalGlue());
        userAffinitiesPanel.revalidate();
        userAffinitiesPanel.repaint();
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Create User Profile", true);
        dialog.setSize(430, 320);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField("USR-" + (100 + dataStore.getUsers().size() + 1));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<UserSegment> segCombo = new JComboBox<>(UserSegment.values());

        styleInputField(idField);
        styleInputField(nameField);
        styleInputField(emailField);
        styleComboBox(segCombo);

        addGridRow(form, gbc, 0, "User ID:", idField);
        addGridRow(form, gbc, 1, "Full Name:", nameField);
        addGridRow(form, gbc, 2, "Email:", emailField);
        addGridRow(form, gbc, 3, "Cohort Segment:", segCombo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton saveBtn = createStyledButton("Register Profile", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Email required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            UserProfile up = new UserProfile(idField.getText().trim(), name, email, (UserSegment) segCombo.getSelectedItem());
            dataStore.addUser(up);
            refreshAllTables();
            dialog.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 5: RECOMMENDATION STUDIO (INTERACTIVE SANDBOX - CLEAN GRIDBAG CONTROLS & NON-OVERLAPPING CARDS)
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildStudioTab() {
        studioPanel = new JPanel(new BorderLayout(14, 14));
        studioPanel.setBackground(BG_DARK);
        studioPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Top Control Console with GridBagLayout (Zero Overlaps!)
        JPanel controlsCard = new JPanel(new BorderLayout(0, 12));
        controlsCard.setBackground(PANEL_BG);
        controlsCard.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(14, 18, 14, 18)));

        JPanel row1 = new JPanel(new GridLayout(1, 4, 14, 0));
        row1.setOpaque(false);

        studioUserCombo = new JComboBox<>();
        studioServiceCombo = new JComboBox<>();
        studioAlgoCombo = new JComboBox<>();
        studioCategoryCombo = new JComboBox<>();

        styleComboBox(studioUserCombo);
        styleComboBox(studioServiceCombo);
        styleComboBox(studioAlgoCombo);
        styleComboBox(studioCategoryCombo);

        studioUserCombo.setPrototypeDisplayValue("USR-101 - Dr. Aris Vance [POWER]");
        studioServiceCombo.setPrototypeDisplayValue("SVC-ECOMM-01 - E-Commerce");
        studioAlgoCombo.setPrototypeDisplayValue("Hybrid Ensemble (Weighted)");
        studioCategoryCombo.setPrototypeDisplayValue("Artificial Intelligence");

        studioAlgoCombo.addItem("Default Service Engine");
        for (AlgorithmType a : AlgorithmType.values()) studioAlgoCombo.addItem(a.getLabel());

        row1.add(createLabeledControl("Target User Profile:", studioUserCombo));
        row1.add(createLabeledControl("Recommendation Service:", studioServiceCombo));
        row1.add(createLabeledControl("Algorithm Override:", studioAlgoCombo));
        row1.add(createLabeledControl("Category Filter:", studioCategoryCombo));

        // Row 2: Sliders on left, Action Buttons on right
        JPanel row2 = new JPanel(new BorderLayout(16, 0));
        row2.setOpaque(false);

        JPanel slidersGroup = new JPanel(new GridLayout(1, 2, 20, 0));
        slidersGroup.setOpaque(false);

        studioKSlider = createStyledSlider(1, 15, 5);
        studioKValueLabel = new JLabel("Top-K Candidates: 5");
        studioKValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studioKValueLabel.setForeground(TEXT_MUTED);
        studioKSlider.addChangeListener(e -> studioKValueLabel.setText("Top-K Candidates: " + studioKSlider.getValue()));

        studioDiversitySlider = createStyledSlider(0, 100, 25);
        studioDivValueLabel = new JLabel("Diversity (MMR) Weight: 25%");
        studioDivValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studioDivValueLabel.setForeground(TEXT_MUTED);
        studioDiversitySlider.addChangeListener(e -> studioDivValueLabel.setText("Diversity (MMR) Weight: " + studioDiversitySlider.getValue() + "%"));

        JPanel kPanel = new JPanel(new BorderLayout(0, 2));
        kPanel.setOpaque(false);
        kPanel.add(studioKValueLabel, BorderLayout.NORTH);
        kPanel.add(studioKSlider, BorderLayout.CENTER);

        JPanel divPanel = new JPanel(new BorderLayout(0, 2));
        divPanel.setOpaque(false);
        divPanel.add(studioDivValueLabel, BorderLayout.NORTH);
        divPanel.add(studioDiversitySlider, BorderLayout.CENTER);

        slidersGroup.add(kPanel);
        slidersGroup.add(divPanel);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        actionRow.setOpaque(false);

        JButton exportRecsBtn = createIconButton("Export CSV", VectorIcon.Kind.COPY, CARD_BG, TEXT_MAIN);
        JButton runPipelineBtn = createIconButton("Run Recommendation Pipeline", VectorIcon.Kind.PLAY, ACCENT, TEXT_MAIN);
        runPipelineBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        actionRow.add(exportRecsBtn);
        actionRow.add(runPipelineBtn);

        row2.add(slidersGroup, BorderLayout.CENTER);
        row2.add(actionRow, BorderLayout.EAST);

        controlsCard.add(row1, BorderLayout.NORTH);
        controlsCard.add(row2, BorderLayout.CENTER);

        // Results Header Banner
        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setOpaque(false);
        resultsHeader.setBorder(new EmptyBorder(4, 2, 4, 2));

        studioExecutionInfoLabel = new JLabel("Select a user and service, then click 'Run Recommendation Pipeline' to test live AI ranking & explainability.");
        studioExecutionInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studioExecutionInfoLabel.setForeground(TEXT_MUTED);
        resultsHeader.add(studioExecutionInfoLabel, BorderLayout.CENTER);

        // Results Cards Container
        studioResultsContainer = new JPanel();
        studioResultsContainer.setLayout(new BoxLayout(studioResultsContainer, BoxLayout.Y_AXIS));
        studioResultsContainer.setBackground(BG_DARK);

        JScrollPane resultsScroll = createStyledScrollPane(studioResultsContainer);
        resultsScroll.getViewport().setBackground(BG_DARK);
        resultsScroll.setBorder(BorderFactory.createEmptyBorder());

        populateStudioDropdowns();

        runPipelineBtn.addActionListener(e -> executeStudioPipeline());

        exportRecsBtn.addActionListener(e -> {
            if (currentStudioRecommendations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No recommendations to export yet. Run the pipeline first.", "Empty", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("Recommendations_Inference.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    dataStore.exportRecommendationsToCSV(chooser.getSelectedFile(), currentStudioRecommendations);
                    JOptionPane.showMessageDialog(this, "Recommendations exported successfully!", "Exported", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        studioPanel.add(controlsCard, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(4, 6));
        centerWrapper.setOpaque(false);
        centerWrapper.add(resultsHeader, BorderLayout.NORTH);
        centerWrapper.add(resultsScroll, BorderLayout.CENTER);

        studioPanel.add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createLabeledControl(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void populateStudioDropdowns() {
        if (studioUserCombo == null) return;
        int prevUser = studioUserCombo.getSelectedIndex();
        int prevSvc = studioServiceCombo.getSelectedIndex();

        studioUserCombo.removeAllItems();
        for (UserProfile u : dataStore.getUsers().values()) {
            studioUserCombo.addItem(u.getUserId() + " - " + u.getName() + " [" + u.getSegment().name() + "]");
        }
        if (prevUser >= 0 && prevUser < studioUserCombo.getItemCount()) studioUserCombo.setSelectedIndex(prevUser);

        studioServiceCombo.removeAllItems();
        for (RecommendationService s : dataStore.getServices().values()) {
            studioServiceCombo.addItem(s.getServiceId() + " - " + s.getName());
        }
        if (prevSvc >= 0 && prevSvc < studioServiceCombo.getItemCount()) studioServiceCombo.setSelectedIndex(prevSvc);

        studioCategoryCombo.removeAllItems();
        studioCategoryCombo.addItem("All Categories");
        Set<String> cats = dataStore.getCatalog().values().stream()
                .map(ContentItem::getCategory).collect(Collectors.toCollection(TreeSet::new));
        for (String c : cats) studioCategoryCombo.addItem(c);
    }

    private void executeStudioPipeline() {
        int userIdx = studioUserCombo.getSelectedIndex();
        int svcIdx = studioServiceCombo.getSelectedIndex();
        if (userIdx < 0 || svcIdx < 0) return;

        List<UserProfile> userList = new ArrayList<>(dataStore.getUsers().values());
        List<RecommendationService> svcList = new ArrayList<>(dataStore.getServices().values());

        UserProfile selectedUser = userList.get(userIdx);
        RecommendationService selectedSvc = svcList.get(svcIdx);

        AlgorithmType algoOverride = null;
        if (studioAlgoCombo.getSelectedIndex() > 0) {
            algoOverride = AlgorithmType.values()[studioAlgoCombo.getSelectedIndex() - 1];
        }

        int k = studioKSlider.getValue();
        double diversity = studioDiversitySlider.getValue() / 100.0;
        String categoryFilter = (String) studioCategoryCombo.getSelectedItem();

        try {
            long t0 = System.nanoTime();
            currentStudioRecommendations = dataStore.executeRecommendationPipeline(
                    selectedUser.getUserId(), selectedSvc.getServiceId(),
                    algoOverride, k, diversity, categoryFilter);
            double elapsedMs = (System.nanoTime() - t0) / 1_000_000.0;

            studioExecutionInfoLabel.setText(String.format(
                    "Generated %d recommendations for %s  |  Engine: %s  |  Latency: %.2f ms",
                    currentStudioRecommendations.size(), selectedUser.getName(),
                    (algoOverride != null ? algoOverride.name() : selectedSvc.getAlgorithmType().name()),
                    elapsedMs));
            studioExecutionInfoLabel.setForeground(TEXT_MAIN);

            renderRecommendationCards(selectedUser, selectedSvc);
            refreshLiveTelemetryData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Pipeline Execution Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderRecommendationCards(UserProfile user, RecommendationService svc) {
        studioResultsContainer.removeAll();

        if (currentStudioRecommendations.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyPanel.setOpaque(false);
            JLabel empty = new JLabel("No items in this service domain match the selected category filter.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(TEXT_MUTED);
            emptyPanel.add(empty);
            studioResultsContainer.add(emptyPanel);
        } else {
            for (RecommendationResult res : currentStudioRecommendations) {
                JPanel card = createRecommendationCard(res, user, svc);
                studioResultsContainer.add(card);
                studioResultsContainer.add(Box.createVerticalStrut(10));
            }
        }

        studioResultsContainer.add(Box.createVerticalGlue());
        studioResultsContainer.revalidate();
        studioResultsContainer.repaint();
    }

    private JPanel createRecommendationCard(RecommendationResult res, UserProfile user, RecommendationService svc) {
        ContentItem item = res.getItem();

        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(PANEL_BG);
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 16, 12, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 126));
        card.setPreferredSize(new Dimension(800, 126));
        card.setMinimumSize(new Dimension(500, 114));

        // Left Rank Badge
        JPanel rankCol = new JPanel(new BorderLayout());
        rankCol.setOpaque(false);
        rankCol.setPreferredSize(new Dimension(44, 60));

        JLabel rankBadge = new JLabel("#" + res.getRank(), SwingConstants.CENTER);
        rankBadge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rankBadge.setForeground(res.getRank() <= 3 ? ACCENT : TEXT_MUTED);
        rankCol.add(rankBadge, BorderLayout.CENTER);

        // Center Content (3 clean rows)
        JPanel centerCol = new JPanel(new GridLayout(3, 1, 0, 5));
        centerCol.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        JLabel titleLbl = new JLabel(item.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(TEXT_MAIN);

        JLabel catPill = createBadge(item.getCategory(), CARD_BG);
        JLabel priceLbl = new JLabel(item.getPrice() > 0 ? String.format("$%.2f", item.getPrice()) : "FREE");
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        priceLbl.setForeground(SUCCESS);

        titleRow.add(titleLbl);
        titleRow.add(catPill);
        titleRow.add(priceLbl);

        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        scoreRow.setOpaque(false);

        JProgressBar scoreBar = new JProgressBar(0, 100);
        scoreBar.setValue(res.getMatchPercentage());
        scoreBar.setStringPainted(true);
        scoreBar.setString(res.getMatchPercentage() + "% Match");
        scoreBar.setPreferredSize(new Dimension(115, 20));
        scoreBar.setForeground(ACCENT);
        scoreBar.setBackground(CARD_BG);
        scoreBar.setBorderPainted(false);

        JLabel xaiReason = new JLabel("Why: " + res.getReason());
        xaiReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        xaiReason.setForeground(TEXT_MUTED);

        scoreRow.add(scoreBar);
        scoreRow.add(xaiReason);

        JPanel rulesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rulesRow.setOpaque(false);

        if (!res.getAppliedRules().isEmpty()) {
            for (String r : res.getAppliedRules()) {
                rulesRow.add(createBadge("RULE: " + r, ACCENT_SOFT));
            }
        }
        JLabel meta = new JLabel("Rating: " + item.getRating() + " / 5.0  |  Stock: " + item.getInventory() + "  |  CTR: " + item.getCTR() + "%");
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        meta.setForeground(TEXT_MUTED);
        rulesRow.add(meta);

        centerCol.add(titleRow);
        centerCol.add(scoreRow);
        centerCol.add(rulesRow);

        // Right Actions: Compact 2-Row Grid (Zero horizontal overflow!)
        JPanel actionCol = new JPanel(new GridLayout(2, 3, 5, 5));
        actionCol.setOpaque(false);
        actionCol.setPreferredSize(new Dimension(245, 64));

        JButton viewBtn = createCompactActionButton("View", VectorIcon.Kind.EYE, CARD_BG, TEXT_MAIN);
        JButton clickBtn = createCompactActionButton("Click", VectorIcon.Kind.PLAY, CARD_BG, TEXT_MAIN);
        JButton likeBtn = createCompactActionButton("Like", VectorIcon.Kind.HEART, new Color(190, 24, 93), Color.WHITE);
        JButton buyBtn = createCompactActionButton("Buy", VectorIcon.Kind.CART, SUCCESS, Color.WHITE);
        JButton dislikeBtn = createCompactActionButton("Skip", VectorIcon.Kind.CLOSE, DANGER, Color.WHITE);

        viewBtn.addActionListener(e -> recordStudioFeedbackNonBlocking(user, item, svc, EventType.VIEW, card));
        clickBtn.addActionListener(e -> recordStudioFeedbackNonBlocking(user, item, svc, EventType.CLICK, card));
        likeBtn.addActionListener(e -> recordStudioFeedbackNonBlocking(user, item, svc, EventType.LIKE, card));
        buyBtn.addActionListener(e -> recordStudioFeedbackNonBlocking(user, item, svc, EventType.PURCHASE, card));
        dislikeBtn.addActionListener(e -> recordStudioFeedbackNonBlocking(user, item, svc, EventType.DISLIKE, card));

        actionCol.add(viewBtn);
        actionCol.add(clickBtn);
        actionCol.add(likeBtn);
        actionCol.add(buyBtn);
        actionCol.add(dislikeBtn);
        actionCol.add(new JLabel(""));

        JPanel actionHolder = new JPanel(new GridBagLayout());
        actionHolder.setOpaque(false);
        actionHolder.add(actionCol);

        card.add(rankCol, BorderLayout.WEST);
        card.add(centerCol, BorderLayout.CENTER);
        card.add(actionHolder, BorderLayout.EAST);
        return card;
    }

    private void recordStudioFeedbackNonBlocking(UserProfile user, ContentItem item, RecommendationService svc, EventType type, JPanel card) {
        InteractionEvent evt = new InteractionEvent(null, user.getUserId(), item.getItemId(), svc.getServiceId(), type, 4000, "StudioSandbox");
        dataStore.logEvent(evt);
        refreshLiveTelemetryData();

        studioExecutionInfoLabel.setText("Recorded [" + type.getLabel() + "] for " + user.getName() +
                " on '" + item.getTitle() + "'  |  Taste vector updated (weight: " + type.getWeight() + ")");
        studioExecutionInfoLabel.setForeground(SUCCESS);

        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(SUCCESS, 2),
                new EmptyBorder(11, 15, 11, 15)));

        javax.swing.Timer timer = new javax.swing.Timer(1200, evt2 -> card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 16, 12, 16))));
        timer.setRepeats(false);
        timer.start();

        if (usersTable != null && usersTable.getSelectedRow() >= 0) {
            String selUid = (String) usersTableModel.getValueAt(usersTable.getSelectedRow(), 0);
            if (selUid.equals(user.getUserId())) {
                displayUserAffinities(user);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 6: A/B TESTING & EXPERIMENTS
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildExperimentsTab() {
        experimentsPanel = new JPanel(new BorderLayout(14, 14));
        experimentsPanel.setBackground(BG_DARK);
        experimentsPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton createExpBtn = createIconButton("Create A/B Experiment", VectorIcon.Kind.PLUS, ACCENT, TEXT_MAIN);
        JButton simTrafficBtn = createIconButton("Simulate 100 Impressions", VectorIcon.Kind.BOLT, CYAN, TEXT_MAIN);
        JButton startPauseBtn = createIconButton("Toggle Run / Pause", VectorIcon.Kind.PLAY, CARD_BG, TEXT_MAIN);
        JButton concludeBtn = createIconButton("Conclude & Rollout", VectorIcon.Kind.CHECK, SUCCESS, Color.WHITE);

        toolbar.add(createExpBtn);
        toolbar.add(simTrafficBtn);
        toolbar.add(startPauseBtn);
        toolbar.add(concludeBtn);

        String[] cols = {"Exp ID", "Experiment Name", "Service", "Variant A", "Variant B", "Split", "Impr A", "CTR A", "Impr B", "CTR B", "Status / Winner"};
        experimentsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(experimentsTableModel);
        setColumnWidths(table, 75, 220, 100, 140, 140, 60, 65, 65, 65, 65, 190);
        JScrollPane scroll = createStyledScrollPane(table);

        createExpBtn.addActionListener(e -> showCreateExperimentDialog());

        simTrafficBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) experimentsTableModel.getValueAt(row, 0);
                ABExperiment exp = dataStore.getExperiments().get(id);
                if (exp != null && exp.getStatus() == ExperimentStatus.RUNNING) {
                    for (int i = 0; i < 50; i++) {
                        exp.recordImpressionA();
                        if (Math.random() < 0.16) exp.recordClickA();
                        exp.recordImpressionB();
                        if (Math.random() < 0.11) exp.recordClickB();
                    }
                    double ctrA = exp.getCTRA();
                    double ctrB = exp.getCTRB();
                    exp.setWinner(ctrA >= ctrB ? "Variant A leading (" + ctrA + "% vs " + ctrB + "%)" : "Variant B leading (" + ctrB + "% vs " + ctrA + "%)");
                    refreshExperimentsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Experiment must be RUNNING to simulate traffic.", "Paused", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select an experiment row first.", "Select Experiment", JOptionPane.WARNING_MESSAGE);
            }
        });

        startPauseBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) experimentsTableModel.getValueAt(row, 0);
                ABExperiment exp = dataStore.getExperiments().get(id);
                if (exp != null) {
                    exp.setStatus(exp.getStatus() == ExperimentStatus.RUNNING ? ExperimentStatus.PAUSED : ExperimentStatus.RUNNING);
                    refreshExperimentsTable();
                }
            }
        });

        concludeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) experimentsTableModel.getValueAt(row, 0);
                ABExperiment exp = dataStore.getExperiments().get(id);
                if (exp != null) {
                    exp.setStatus(ExperimentStatus.CONCLUDED);
                    refreshExperimentsTable();
                    JOptionPane.showMessageDialog(this, "Experiment concluded! Rolled out: " + exp.getWinner(), "Winner Rolled Out", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        experimentsPanel.add(toolbar, BorderLayout.NORTH);
        experimentsPanel.add(scroll, BorderLayout.CENTER);

        refreshExperimentsTable();
    }

    private void showCreateExperimentDialog() {
        JDialog dialog = new JDialog(this, "Launch New A/B Testing Experiment", true);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField("EXP-" + (100 + dataStore.getExperiments().size() + 1));
        JTextField nameField = new JTextField();
        JComboBox<RecommendationService> svcCombo = new JComboBox<>();
        for (RecommendationService s : dataStore.getServices().values()) svcCombo.addItem(s);

        JComboBox<AlgorithmType> varACombo = new JComboBox<>(AlgorithmType.values());
        JComboBox<AlgorithmType> varBCombo = new JComboBox<>(AlgorithmType.values());
        varBCombo.setSelectedIndex(1);

        styleInputField(idField);
        styleInputField(nameField);
        styleComboBox(svcCombo);
        styleComboBox(varACombo);
        styleComboBox(varBCombo);

        addGridRow(form, gbc, 0, "Experiment ID:", idField);
        addGridRow(form, gbc, 1, "Name:", nameField);
        addGridRow(form, gbc, 2, "Service Pipeline:", svcCombo);
        addGridRow(form, gbc, 3, "Variant A (Control):", varACombo);
        addGridRow(form, gbc, 4, "Variant B (Challenger):", varBCombo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton saveBtn = createStyledButton("Launch Experiment", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;
            RecommendationService s = (RecommendationService) svcCombo.getSelectedItem();
            ABExperiment exp = new ABExperiment(
                    idField.getText().trim(), name, s.getServiceId(),
                    (AlgorithmType) varACombo.getSelectedItem(),
                    (AlgorithmType) varBCombo.getSelectedItem(), 50.0);
            dataStore.addExperiment(exp);
            refreshExperimentsTable();
            dialog.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 7: BUSINESS RULES & GUARDRAILS
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildRulesTab() {
        rulesPanel = new JPanel(new BorderLayout(14, 14));
        rulesPanel.setBackground(BG_DARK);
        rulesPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton addRuleBtn = createIconButton("Create Business Rule", VectorIcon.Kind.PLUS, ACCENT, TEXT_MAIN);
        JButton toggleRuleBtn = createIconButton("Toggle Enable / Disable", VectorIcon.Kind.BOLT, CARD_BG, TEXT_MAIN);
        JButton deleteRuleBtn = createIconButton("Delete Rule", VectorIcon.Kind.TRASH, DANGER, TEXT_MAIN);

        toolbar.add(addRuleBtn);
        toolbar.add(toggleRuleBtn);
        toolbar.add(deleteRuleBtn);

        String[] cols = {"Rule ID", "Service Scope", "Rule Name", "Rule Type", "Target Entity", "Adjustment", "Priority", "Status"};
        rulesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(rulesTableModel);
        setColumnWidths(table, 85, 110, 260, 155, 115, 95, 70, 90);
        JScrollPane scroll = createStyledScrollPane(table);

        addRuleBtn.addActionListener(e -> showCreateRuleDialog());

        toggleRuleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) rulesTableModel.getValueAt(row, 0);
                dataStore.toggleRule(id);
                refreshRulesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Select a rule to toggle.", "Select Rule", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteRuleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) rulesTableModel.getValueAt(row, 0);
                dataStore.removeRule(id);
                refreshRulesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Select a rule to delete.", "Select Rule", JOptionPane.WARNING_MESSAGE);
            }
        });

        rulesPanel.add(toolbar, BorderLayout.NORTH);
        rulesPanel.add(scroll, BorderLayout.CENTER);

        refreshRulesTable();
    }

    private void showCreateRuleDialog() {
        JDialog dialog = new JDialog(this, "Define Business Rule & Guardrail", true);
        dialog.setSize(490, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField("RULE-0" + (dataStore.getRules().size() + 1));
        JTextField nameField = new JTextField();
        JComboBox<String> scopeCombo = new JComboBox<>();
        scopeCombo.addItem("* (All Services)");
        for (RecommendationService s : dataStore.getServices().values()) scopeCombo.addItem(s.getServiceId());

        JComboBox<RuleType> typeCombo = new JComboBox<>(RuleType.values());
        JTextField targetField = new JTextField();
        JTextField factorField = new JTextField("0.20");

        styleInputField(idField);
        styleInputField(nameField);
        styleComboBox(scopeCombo);
        styleComboBox(typeCombo);
        styleInputField(targetField);
        styleInputField(factorField);

        addGridRow(form, gbc, 0, "Rule ID:", idField);
        addGridRow(form, gbc, 1, "Rule Name:", nameField);
        addGridRow(form, gbc, 2, "Service Scope:", scopeCombo);
        addGridRow(form, gbc, 3, "Rule Type:", typeCombo);
        addGridRow(form, gbc, 4, "Target (Category/Item/Rating):", targetField);
        addGridRow(form, gbc, 5, "Score Adjustment Factor:", factorField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton saveBtn = createStyledButton("Save Rule", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String target = targetField.getText().trim();
            if (name.isEmpty() || target.isEmpty()) return;

            double factor = 0.20;
            try { factor = Double.parseDouble(factorField.getText().trim()); } catch (Exception ignored) {}

            String scope = (String) scopeCombo.getSelectedItem();
            if (scope.contains(" ")) scope = "*";

            BusinessRule rule = new BusinessRule(
                    idField.getText().trim(), scope, name,
                    (RuleType) typeCombo.getSelectedItem(), target, factor, 8);

            dataStore.addRule(rule);
            refreshRulesTable();
            dialog.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 8: API KEYS & CLIENT APPS
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildApiKeysTab() {
        apiKeysPanel = new JPanel(new BorderLayout(14, 14));
        apiKeysPanel.setBackground(BG_DARK);
        apiKeysPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton genKeyBtn = createIconButton("Generate New API Key", VectorIcon.Kind.PLUS, ACCENT, TEXT_MAIN);
        JButton copyKeyBtn = createIconButton("Copy API Key", VectorIcon.Kind.COPY, CARD_BG, TEXT_MAIN);
        JButton revokeKeyBtn = createIconButton("Revoke Key", VectorIcon.Kind.CLOSE, DANGER, Color.WHITE);

        toolbar.add(genKeyBtn);
        toolbar.add(copyKeyBtn);
        toolbar.add(revokeKeyBtn);

        String[] cols = {"Key ID", "Client Application", "API Key (Masked)", "Tier", "Rate Limit (RPM)", "Min Calls", "Total Calls", "Status"};
        apiKeysTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(apiKeysTableModel);
        setColumnWidths(table, 95, 230, 195, 110, 110, 80, 80, 75);
        JScrollPane scroll = createStyledScrollPane(table);

        genKeyBtn.addActionListener(e -> showGenerateKeyDialog());

        copyKeyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) apiKeysTableModel.getValueAt(row, 0);
                ApiKeyCredential cred = dataStore.getApiKeys().get(id);
                if (cred != null) {
                    java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(cred.getApiKey());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
                    JOptionPane.showMessageDialog(this, "API Key copied to clipboard:\n" + cred.getApiKey(), "Copied", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a key row to copy.", "Select Key", JOptionPane.WARNING_MESSAGE);
            }
        });

        revokeKeyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) apiKeysTableModel.getValueAt(row, 0);
                ApiKeyCredential cred = dataStore.getApiKeys().get(id);
                if (cred != null) {
                    cred.setActive(false);
                    refreshApiKeysTable();
                }
            }
        });

        apiKeysPanel.add(toolbar, BorderLayout.NORTH);
        apiKeysPanel.add(scroll, BorderLayout.CENTER);

        refreshApiKeysTable();
    }

    private void showGenerateKeyDialog() {
        JDialog dialog = new JDialog(this, "Generate Client API Key", true);
        dialog.setSize(430, 260);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField();
        JComboBox<ClientTier> tierCombo = new JComboBox<>(ClientTier.values());
        styleInputField(nameField);
        styleComboBox(tierCombo);

        addGridRow(form, gbc, 0, "Client App Name:", nameField);
        addGridRow(form, gbc, 1, "Quota Tier:", tierCombo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);
        JButton cancelBtn = createStyledButton("Cancel", CARD_BG, TEXT_MAIN);
        JButton createBtn = createStyledButton("Generate Key", ACCENT, TEXT_MAIN);

        cancelBtn.addActionListener(e -> dialog.dispose());
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;
            ApiKeyCredential cred = dataStore.generateApiKey(name, (ClientTier) tierCombo.getSelectedItem());
            refreshApiKeysTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Key Generated!\n" + cred.getApiKey(), "API Key", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRow.add(cancelBtn);
        btnRow.add(createBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // TAB 9: REST API SERVER & DOCUMENTATION
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void buildApiServerTab() {
        apiServerPanel = new JPanel(new BorderLayout(14, 14));
        apiServerPanel.setBackground(BG_DARK);
        apiServerPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel statusCard = new JPanel(new BorderLayout(12, 0));
        statusCard.setBackground(PANEL_BG);
        statusCard.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(12, 18, 12, 18)));

        JLabel srvTitle = new JLabel("Embedded HTTP REST API Server (Listening on http://localhost:" + webServer.getPort() + ")");
        srvTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        srvTitle.setForeground(TEXT_MAIN);

        JButton openBrowserBtn = createIconButton("Open Web Console in Browser", VectorIcon.Kind.GLOBE, ACCENT, TEXT_MAIN);
        openBrowserBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:" + webServer.getPort()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not open browser: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        statusCard.add(srvTitle, BorderLayout.CENTER);
        statusCard.add(openBrowserBtn, BorderLayout.EAST);

        // Interactive In-App Test Console
        JPanel testBench = new JPanel(new BorderLayout(10, 12));
        testBench.setBackground(PANEL_BG);
        testBench.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel testControls = new JPanel(new GridBagLayout());
        testControls.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> endpointCombo = new JComboBox<>(new String[]{
                "/api/v1/health",
                "/api/v1/services",
                "/api/v1/catalog",
                "/api/v1/users",
                "/api/v1/recommend?serviceId=SVC-ECOMM-01&userId=USR-101&k=5",
                "/api/v1/metrics"
        });
        endpointCombo.setFont(new Font("Consolas", Font.PLAIN, 12));
        styleComboBox(endpointCombo);

        JButton sendBtn = createIconButton("Send GET", VectorIcon.Kind.PLAY, ACCENT, TEXT_MAIN);
        JLabel statusPill = createBadge("READY", CARD_BG);
        JButton copyRespBtn = createIconButton("Copy JSON", VectorIcon.Kind.COPY, CARD_BG, TEXT_MAIN);
        JButton copyCurlBtn = createIconButton("Copy cURL", VectorIcon.Kind.KEY, CARD_BG, TEXT_MAIN);

        gbc.gridx = 0; gbc.weightx = 0.0;
        testControls.add(createFormLabel("Endpoint:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        testControls.add(endpointCombo, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0;
        testControls.add(sendBtn, gbc);
        gbc.gridx = 3;
        testControls.add(statusPill, gbc);
        gbc.gridx = 4;
        testControls.add(copyRespBtn, gbc);
        gbc.gridx = 5;
        testControls.add(copyCurlBtn, gbc);

        JTextArea responseArea = new JTextArea();
        responseArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        responseArea.setBackground(BG_DARK);
        responseArea.setForeground(new Color(56, 189, 248));
        responseArea.setEditable(false);
        responseArea.setText("Select an API endpoint above and click 'Send GET' to inspect the live JSON payload.");
        responseArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane respScroll = createStyledScrollPane(responseArea);

        copyRespBtn.addActionListener(e -> {
            String txt = responseArea.getText();
            java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(txt);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
            statusPill.setText("COPIED JSON");
            statusPill.setBackground(CYAN);
        });

        copyCurlBtn.addActionListener(e -> {
            String ep = (String) endpointCombo.getSelectedItem();
            String curl = "curl -s \"http://localhost:" + webServer.getPort() + ep + "\"";
            java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(curl);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
            statusPill.setText("COPIED CURL");
            statusPill.setBackground(CYAN);
        });

        sendBtn.addActionListener(e -> {
            String ep = (String) endpointCombo.getSelectedItem();
            statusPill.setText("SENDING...");
            statusPill.setBackground(WARNING);
            new Thread(() -> {
                try {
                    long t0 = System.currentTimeMillis();
                    URL url = URI.create("http://localhost:" + webServer.getPort() + ep).toURL();
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(2000);
                    con.setReadTimeout(3000);

                    int code = con.getResponseCode();
                    long ms = System.currentTimeMillis() - t0;
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            code >= 400 ? con.getErrorStream() : con.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine).append("\n");
                    }
                    in.close();

                    SwingUtilities.invokeLater(() -> {
                        statusPill.setText(code + " OK (" + ms + "ms)");
                        statusPill.setBackground(code < 400 ? SUCCESS : DANGER);
                        responseArea.setText(content.toString());
                        responseArea.setCaretPosition(0);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusPill.setText("FAILED");
                        statusPill.setBackground(DANGER);
                        responseArea.setText("Error calling endpoint: " + ex.getMessage());
                    });
                }
            }).start();
        });

        testBench.add(testControls, BorderLayout.NORTH);
        testBench.add(respScroll, BorderLayout.CENTER);

        apiServerPanel.add(statusCard, BorderLayout.NORTH);
        apiServerPanel.add(testBench, BorderLayout.CENTER);
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // REFRESH & SYNCHRONIZATION HELPERS
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private void refreshAllTables() {
        refreshServicesTable();
        refreshCatalogTable();
        refreshUsersTable();
        refreshTelemetryTable();
        refreshExperimentsTable();
        refreshRulesTable();
        refreshApiKeysTable();
        populateStudioDropdowns();
        refreshLiveTelemetryData();
    }

    private void refreshServicesTable() {
        if (servicesTableModel == null) return;
        servicesTableModel.setRowCount(0);
        for (RecommendationService s : dataStore.getServices().values()) {
            servicesTableModel.addRow(new Object[]{
                    s.getServiceId(), s.getName(), s.getDomain().getDisplayName(),
                    s.getAlgorithmType().name(), s.getModelVersion(), s.getStatus(),
                    s.getTargetQps(), s.getAvgLatencyMs() + " ms", s.getTotalRequests(), s.getConversionRate() + "%"
            });
        }
    }

    private void refreshCatalogTable() {
        if (catalogFilterUpdater != null) {
            catalogFilterUpdater.run();
            return;
        }
        if (catalogTableModel == null) return;
        catalogTableModel.setRowCount(0);
        for (ContentItem i : dataStore.getCatalog().values()) {
            catalogTableModel.addRow(new Object[]{
                    i.getItemId(), i.getServiceId(), i.getTitle(), i.getCategory(),
                    i.getDomain().name(), i.getPrice() > 0 ? String.format("$%.2f", i.getPrice()) : "Free",
                    i.getRating() + " / 5", i.getInventory(), i.getStatus(), i.getViewCount(), i.getClickCount(), i.getCTR() + "%"
            });
        }
    }

    private void refreshUsersTable() {
        if (usersTableModel == null) return;
        int prevRow = usersTable != null ? usersTable.getSelectedRow() : -1;
        usersTableModel.setRowCount(0);
        for (UserProfile u : dataStore.getUsers().values()) {
            usersTableModel.addRow(new Object[]{
                    u.getUserId(), u.getName(), u.getEmail(), u.getSegment(),
                    u.getTotalInteractions(), u.isActive() ? "Active" : "Suspended"
            });
        }
        if (usersTable != null && prevRow >= 0 && prevRow < usersTable.getRowCount()) {
            usersTable.setRowSelectionInterval(prevRow, prevRow);
        }
    }

    private void refreshTelemetryTable() {
        if (telemetryTableModel == null) return;
        telemetryTableModel.setRowCount(0);
        List<InteractionEvent> evts = dataStore.getEvents();
        int start = Math.max(0, evts.size() - 25);
        for (int i = evts.size() - 1; i >= start; i--) {
            InteractionEvent e = evts.get(i);
            telemetryTableModel.addRow(new Object[]{
                    e.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    e.getUserId(), e.getItemId(), e.getServiceId(), e.getEventType(), e.getWeight()
            });
        }
    }

    private void refreshExperimentsTable() {
        if (experimentsTableModel == null) return;
        experimentsTableModel.setRowCount(0);
        for (ABExperiment exp : dataStore.getExperiments().values()) {
            experimentsTableModel.addRow(new Object[]{
                    exp.getExperimentId(), exp.getName(), exp.getServiceId(),
                    exp.getVariantA().name(), exp.getVariantB().name(),
                    (int) exp.getSplitRatio() + "/" + (100 - (int) exp.getSplitRatio()),
                    exp.getImpressionsA(), exp.getCTRA() + "%",
                    exp.getImpressionsB(), exp.getCTRB() + "%",
                    exp.getStatus().name() + ": " + exp.getWinner()
            });
        }
    }

    private void refreshRulesTable() {
        if (rulesTableModel == null) return;
        rulesTableModel.setRowCount(0);
        for (BusinessRule r : dataStore.getRules().values()) {
            rulesTableModel.addRow(new Object[]{
                    r.getRuleId(), r.getServiceId(), r.getRuleName(), r.getRuleType().name(),
                    r.getTarget(), (r.getAdjustmentFactor() > 0 ? "+" : "") + r.getAdjustmentFactor(),
                    r.getPriority(), r.isEnabled() ? "ACTIVE" : "DISABLED"
            });
        }
    }

    private void refreshApiKeysTable() {
        if (apiKeysTableModel == null) return;
        apiKeysTableModel.setRowCount(0);
        for (ApiKeyCredential k : dataStore.getApiKeys().values()) {
            apiKeysTableModel.addRow(new Object[]{
                    k.getKeyId(), k.getClientName(), k.getMaskedKey(), k.getTier().name(),
                    k.getRateLimitRpm(), k.getCurrentMinuteCalls(), k.getTotalCalls(), k.isActive() ? "ACTIVE" : "REVOKED"
            });
        }
    }

    private void refreshLiveTelemetryData() {
        if (qpsStatusLabel != null) qpsStatusLabel.setText("QPS: " + dataStore.getCurrentQps());
        if (kpiServicesVal != null) kpiServicesVal.setText(String.valueOf(dataStore.getServices().size()));
        if (kpiCatalogVal != null) kpiCatalogVal.setText(String.valueOf(dataStore.getCatalog().size()));
        if (kpiUsersVal != null) kpiUsersVal.setText(String.valueOf(dataStore.getUsers().size()));
        if (kpiQueriesVal != null) kpiQueriesVal.setText(String.valueOf(dataStore.getTotalQueries()));
        if (slaP50Val != null) slaP50Val.setText(String.format("%.1f ms", dataStore.getP50Latency()));
        if (slaP95Val != null) slaP95Val.setText(String.format("%.1f ms", dataStore.getP95Latency()));
        if (slaCacheVal != null) slaCacheVal.setText((int) (dataStore.getCacheHitRatio() * 100) + "%");
        if (slaCovVal != null) slaCovVal.setText((int) (dataStore.getCatalogCoverage() * 100) + "%");

        refreshTelemetryTable();
        if (chartCanvas != null) chartCanvas.repaint();
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // CUSTOM DARK WIDGETS, VECTOR ICONS & RENDERERS (ZERO EMOJI GLYPH BOXES)
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    private JLabel createBadge(String text, Color bg) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(bg);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        return badge;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                new EmptyBorder(6, 13, 6, 13)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createIconButton(String text, VectorIcon.Kind kind, Color bg, Color fg) {
        JButton btn = createStyledButton(text, bg, fg);
        btn.setIcon(new VectorIcon(kind, 14, fg));
        btn.setIconTextGap(7);
        return btn;
    }

    private JButton createCompactActionButton(String text, VectorIcon.Kind kind, Color bg, Color fg) {
        JButton btn = new JButton(text, new VectorIcon(kind, 12, fg));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setIconTextGap(5);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMargin(new Insets(2, 6, 2, 6));
        btn.setBorder(BorderFactory.createLineBorder(bg.brighter(), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleInputField(JTextField f) {
        f.setBackground(CARD_BG);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(TEXT_MAIN);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)));
    }

    private JSlider createStyledSlider(int min, int max, int val) {
        JSlider s = new JSlider(min, max, val);
        s.setOpaque(false);
        s.setForeground(TEXT_MUTED);
        s.setBackground(PANEL_BG);
        return s;
    }

    private <T> void styleComboBox(JComboBox<T> combo) {
        combo.setBackground(CARD_BG);
        combo.setForeground(TEXT_MAIN);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        combo.setUI(new DarkComboBoxUI());
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBackground(isSelected ? ACCENT : PANEL_BG);
                l.setForeground(TEXT_MAIN);
                l.setBorder(new EmptyBorder(5, 8, 5, 8));
                return l;
            }
        });
    }

    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(createFormLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }

    private JScrollPane createStyledScrollPane(JComponent view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.getViewport().setBackground(PANEL_BG);
        scroll.setBackground(PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        JPanel corner = new JPanel();
        corner.setBackground(CARD_BG);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        return scroll;
    }

    private JScrollPane createBorderlessScrollPane(JComponent view, Color bg) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.getViewport().setBackground(bg);
        scroll.setBackground(bg);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(CARD_BG);
        table.setSelectionForeground(TEXT_MAIN);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new DarkTableCellRenderer());

        JTableHeader header = table.getTableHeader();
        header.setBackground(CARD_BG);
        header.setForeground(TEXT_MAIN);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DarkHeaderRenderer());

        return table;
    }

    private void setColumnWidths(JTable table, int... widths) {
        TableColumnModel cm = table.getColumnModel();
        for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    // Sidebar Navigation Button
    static class SidebarNavButton extends JButton {
        private boolean active = false;
        private boolean hovered = false;
        private final VectorIcon.Kind iconKind;

        public SidebarNavButton(String title, VectorIcon.Kind iconKind) {
            super(title);
            this.iconKind = iconKind;
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_MUTED);
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(12);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(225, 38));
            setPreferredSize(new Dimension(225, 38));
            setBorder(new EmptyBorder(0, 14, 0, 10));
            setIcon(new VectorIcon(iconKind, 16, TEXT_MUTED));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public void setActive(boolean a) {
            this.active = a;
            setFont(new Font("Segoe UI", a ? Font.BOLD : Font.PLAIN, 13));
            setForeground(a ? TEXT_MAIN : TEXT_MUTED);
            setIcon(new VectorIcon(iconKind, 16, a ? ACCENT : TEXT_MUTED));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 1, getWidth(), getHeight() - 2, 8, 8);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
            } else if (hovered) {
                g2.setColor(new Color(30, 41, 59, 140));
                g2.fillRoundRect(0, 1, getWidth(), getHeight() - 2, 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Crisp Anti-Aliased Vector Icons (Eliminates all Unicode Emoji Boxes!)
    static class VectorIcon implements Icon {
        enum Kind { BOLT, DASHBOARD, SERVICES, CATALOG, USERS, STUDIO, EXPERIMENT, SHIELD, KEY, GLOBE, PLUS, TRASH, COPY, PLAY, REFRESH, CHECK, EYE, HEART, CART, CLOSE, SEARCH }

        private final Kind kind;
        private final int size;
        private final Color color;

        public VectorIcon(Kind kind, int size, Color color) {
            this.kind = kind;
            this.size = size;
            this.color = color;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            float s = size;
            g2.setStroke(new BasicStroke(Math.max(1.4f, s / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (kind) {
                case BOLT:
                    Polygon bolt = new Polygon(
                            new int[]{(int)(s*0.58), (int)(s*0.22), (int)(s*0.52), (int)(s*0.42), (int)(s*0.78), (int)(s*0.48)},
                            new int[]{(int)(s*0.08), (int)(s*0.55), (int)(s*0.55), (int)(s*0.92), (int)(s*0.45), (int)(s*0.45)}, 6);
                    g2.fill(bolt);
                    break;
                case DASHBOARD:
                    g2.drawRoundRect(1, 1, (int)(s*0.4), (int)(s*0.4), 2, 2);
                    g2.drawRoundRect((int)(s*0.55), 1, (int)(s*0.4), (int)(s*0.4), 2, 2);
                    g2.drawRoundRect(1, (int)(s*0.55), (int)(s*0.4), (int)(s*0.4), 2, 2);
                    g2.drawRoundRect((int)(s*0.55), (int)(s*0.55), (int)(s*0.4), (int)(s*0.4), 2, 2);
                    break;
                case SERVICES:
                    g2.drawLine(2, (int)(s*0.25), (int)(s-2), (int)(s*0.25));
                    g2.drawLine(2, (int)(s*0.50), (int)(s-2), (int)(s*0.50));
                    g2.drawLine(2, (int)(s*0.75), (int)(s-2), (int)(s*0.75));
                    g2.fillOval((int)(s*0.25), (int)(s*0.15), (int)(s*0.2), (int)(s*0.2));
                    g2.fillOval((int)(s*0.60), (int)(s*0.40), (int)(s*0.2), (int)(s*0.2));
                    g2.fillOval((int)(s*0.35), (int)(s*0.65), (int)(s*0.2), (int)(s*0.2));
                    break;
                case CATALOG:
                    g2.drawRoundRect(2, 2, (int)(s-4), (int)(s-4), 3, 3);
                    g2.drawLine(2, (int)(s*0.4), (int)(s-2), (int)(s*0.4));
                    g2.drawLine((int)(s*0.35), (int)(s*0.65), (int)(s*0.65), (int)(s*0.65));
                    break;
                case USERS:
                    g2.drawOval((int)(s*0.3), (int)(s*0.12), (int)(s*0.4), (int)(s*0.4));
                    g2.drawArc((int)(s*0.15), (int)(s*0.58), (int)(s*0.7), (int)(s*0.5), 0, 180);
                    break;
                case STUDIO:
                case PLAY:
                    Polygon tri = new Polygon(
                            new int[]{(int)(s*0.25), (int)(s*0.82), (int)(s*0.25)},
                            new int[]{(int)(s*0.15), (int)(s*0.50), (int)(s*0.85)}, 3);
                    g2.fill(tri);
                    break;
                case EXPERIMENT:
                    g2.drawLine((int)(s*0.35), 2, (int)(s*0.65), 2);
                    g2.drawLine((int)(s*0.4), 2, (int)(s*0.4), (int)(s*0.4));
                    g2.drawLine((int)(s*0.6), 2, (int)(s*0.6), (int)(s*0.4));
                    g2.drawPolygon(new int[]{(int)(s*0.4), (int)(s*0.6), (int)(s*0.85), (int)(s*0.15)},
                            new int[]{(int)(s*0.4), (int)(s*0.4), (int)(s*0.88), (int)(s*0.88)}, 4);
                    break;
                case SHIELD:
                    Polygon sh = new Polygon(
                            new int[]{(int)(s*0.5), (int)(s*0.85), (int)(s*0.8), (int)(s*0.5), (int)(s*0.2), (int)(s*0.15)},
                            new int[]{(int)(s*0.1), (int)(s*0.25), (int)(s*0.6), (int)(s*0.9), (int)(s*0.6), (int)(s*0.25)}, 6);
                    g2.draw(sh);
                    break;
                case KEY:
                    g2.drawOval((int)(s*0.12), (int)(s*0.3), (int)(s*0.38), (int)(s*0.38));
                    g2.drawLine((int)(s*0.5), (int)(s*0.5), (int)(s*0.88), (int)(s*0.5));
                    g2.drawLine((int)(s*0.75), (int)(s*0.5), (int)(s*0.75), (int)(s*0.7));
                    g2.drawLine((int)(s*0.88), (int)(s*0.5), (int)(s*0.88), (int)(s*0.7));
                    break;
                case GLOBE:
                    g2.drawOval(2, 2, (int)(s-4), (int)(s-4));
                    g2.drawOval((int)(s*0.28), 2, (int)(s*0.44), (int)(s-4));
                    g2.drawLine(2, (int)(s*0.5), (int)(s-2), (int)(s*0.5));
                    break;
                case PLUS:
                    g2.drawLine((int)(s*0.5), 2, (int)(s*0.5), (int)(s-2));
                    g2.drawLine(2, (int)(s*0.5), (int)(s-2), (int)(s*0.5));
                    break;
                case TRASH:
                    g2.drawLine(2, (int)(s*0.25), (int)(s-2), (int)(s*0.25));
                    g2.drawRect((int)(s*0.25), (int)(s*0.25), (int)(s*0.5), (int)(s*0.65));
                    break;
                case COPY:
                    g2.drawRect((int)(s*0.3), (int)(s*0.3), (int)(s*0.55), (int)(s*0.55));
                    g2.drawLine((int)(s*0.15), (int)(s*0.15), (int)(s*0.7), (int)(s*0.15));
                    g2.drawLine((int)(s*0.15), (int)(s*0.15), (int)(s*0.15), (int)(s*0.7));
                    break;
                case REFRESH:
                    g2.drawArc(2, 2, (int)(s-4), (int)(s-4), 30, 290);
                    break;
                case CHECK:
                    g2.drawPolyline(new int[]{(int)(s*0.18), (int)(s*0.42), (int)(s*0.85)},
                            new int[]{(int)(s*0.52), (int)(s*0.78), (int)(s*0.22)}, 3);
                    break;
                case EYE:
                    g2.drawOval(1, (int)(s*0.25), (int)(s-2), (int)(s*0.5));
                    g2.fillOval((int)(s*0.38), (int)(s*0.38), (int)(s*0.24), (int)(s*0.24));
                    break;
                case HEART:
                    g2.fillOval((int)(s*0.15), (int)(s*0.2), (int)(s*0.38), (int)(s*0.38));
                    g2.fillOval((int)(s*0.47), (int)(s*0.2), (int)(s*0.38), (int)(s*0.38));
                    g2.fillPolygon(new int[]{(int)(s*0.17), (int)(s*0.83), (int)(s*0.5)},
                            new int[]{(int)(s*0.45), (int)(s*0.45), (int)(s*0.88)}, 3);
                    break;
                case CART:
                    g2.drawPolyline(new int[]{1, (int)(s*0.25), (int)(s*0.35), (int)(s*0.88), (int)(s*0.78)},
                            new int[]{(int)(s*0.2), (int)(s*0.2), (int)(s*0.65), (int)(s*0.65), (int)(s*0.3)}, 5);
                    g2.fillOval((int)(s*0.35), (int)(s*0.75), 3, 3);
                    g2.fillOval((int)(s*0.72), (int)(s*0.75), 3, 3);
                    break;
                case CLOSE:
                    g2.drawLine(3, 3, (int)(s-3), (int)(s-3));
                    g2.drawLine((int)(s-3), 3, 3, (int)(s-3));
                    break;
                case SEARCH:
                    g2.drawOval(2, 2, (int)(s*0.6), (int)(s*0.6));
                    g2.drawLine((int)(s*0.62), (int)(s*0.62), (int)(s-2), (int)(s-2));
                    break;
            }
            g2.dispose();
        }
    }

    // Flat Dark ComboBoxUI
    static class DarkComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(CARD_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(TEXT_MUTED);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                    g2.dispose();
                }
            };
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setFocusPainted(false);
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(CARD_BG);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // Modern Slim Dark ScrollBarUI
    static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            this.thumbColor = BORDER_COLOR;
            this.trackColor = BG_DARK;
        }
        @Override protected JButton createDecreaseButton(int o) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int o) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isDragging ? ACCENT : CARD_HOVER);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6);
            g2.dispose();
        }
    }

    static class DarkHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
            label.setBackground(CARD_BG);
            label.setForeground(TEXT_MAIN);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    new EmptyBorder(8, 10, 8, 10)));
            return label;
        }
    }

    static class DarkTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setBackground(isSelected ? CARD_BG : (r % 2 == 0 ? PANEL_BG : PANEL_ALT));
            label.setForeground(TEXT_MAIN);
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            return label;
        }
    }

    static class ServiceStatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
            label.setBackground(isSelected ? CARD_BG : (r % 2 == 0 ? PANEL_BG : PANEL_ALT));
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            if (value instanceof ServiceStatus) {
                ServiceStatus st = (ServiceStatus) value;
                label.setText(st.getLabel());
                label.setForeground(st.getColor());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            return label;
        }
    }

    static class ItemStatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
            label.setBackground(isSelected ? CARD_BG : (r % 2 == 0 ? PANEL_BG : PANEL_ALT));
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            if (value instanceof ItemStatus) {
                ItemStatus st = (ItemStatus) value;
                label.setText(st.getLabel());
                label.setForeground(st.getColor());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            return label;
        }
    }

    static class UserSegmentBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
            label.setBackground(isSelected ? CARD_BG : (r % 2 == 0 ? PANEL_BG : PANEL_ALT));
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            if (value instanceof UserSegment) {
                UserSegment seg = (UserSegment) value;
                label.setText(seg.getLabel());
                label.setForeground(seg.getBadgeColor());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            return label;
        }
    }

    static class EventBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
            label.setBackground(isSelected ? CARD_BG : (r % 2 == 0 ? PANEL_BG : PANEL_ALT));
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            if (value instanceof EventType) {
                EventType ev = (EventType) value;
                label.setText(ev.getLabel());
                label.setForeground(ev.getWeight() > 3 ? SUCCESS : (ev.getWeight() < 0 ? DANGER : TEXT_MAIN));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            return label;
        }
    }

    static class EventDistributionCanvas extends JPanel {
        public EventDistributionCanvas() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, w, h, 10, 10);

            g2.setColor(TEXT_MUTED);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.drawString("LIVE INTERACTION EVENT DISTRIBUTION", 14, 22);

            PlatformDataStore ds = PlatformDataStore.INSTANCE;
            List<InteractionEvent> evts = ds.getEvents();
            Map<EventType, Integer> counts = new EnumMap<>(EventType.class);
            for (EventType t : EventType.values()) counts.put(t, 0);
            for (InteractionEvent e : evts) counts.merge(e.getEventType(), 1, Integer::sum);

            EventType[] types = {EventType.VIEW, EventType.CLICK, EventType.LIKE, EventType.BOOKMARK, EventType.PURCHASE, EventType.DISLIKE};
            String[] names = {"View", "Click", "Like", "Bookmark", "Purchase", "Skip"};
            Color[] colors = {
                    new Color(59, 130, 246), new Color(16, 185, 129), new Color(236, 72, 153),
                    new Color(245, 158, 11), new Color(139, 92, 246), new Color(239, 68, 68)
            };

            int maxCount = 1;
            for (EventType t : types) maxCount = Math.max(maxCount, counts.getOrDefault(t, 0));

            int padX = 18;
            int gap = 12;
            int availW = w - (padX * 2);
            int barWidth = Math.max(18, (availW - (types.length - 1) * gap) / types.length);
            int bottomY = h - 30;
            int maxBarH = Math.max(20, h - 75);

            for (int i = 0; i < types.length; i++) {
                int count = counts.getOrDefault(types[i], 0);
                int barH = Math.max(4, (int) (((double) count / maxCount) * maxBarH));
                int x = padX + (i * (barWidth + gap));
                int y = bottomY - barH;

                GradientPaint gp = new GradientPaint(x, y, colors[i], x, bottomY, colors[i].darker());
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barWidth, barH, 6, 6);

                g2.setColor(TEXT_MAIN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String cStr = String.valueOf(count);
                int strW = g2.getFontMetrics().stringWidth(cStr);
                g2.drawString(cStr, x + (barWidth - strW) / 2, Math.max(38, y - 5));

                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                int lW = g2.getFontMetrics().stringWidth(names[i]);
                g2.drawString(names[i], x + (barWidth - lW) / 2, h - 10);
            }
            g2.dispose();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // APPLICATION ENTRY POINT
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            SmartRecommendationPlatform platform = new SmartRecommendationPlatform();
            platform.setVisible(true);
        });
    }
}
