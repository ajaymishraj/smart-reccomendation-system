package com.recommendation.platform.engine;

import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * RecommendationEngine — Strategy Pattern Context.
 * Delegates recommendation logic to whichever strategy is currently set.
 * Polymorphism in action: same getRecommendations() call behaves differently
 * depending on which strategy is injected.
 */
public class RecommendationEngine {

    private RecommendationStrategy strategy;

    public RecommendationEngine() {
        // Default strategy
        this.strategy = new PopularityBasedRecommendation();
    }

    // Dependency injection — swap strategies at runtime
    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
        System.out.println("✔ Strategy changed to: " + strategy.getStrategyName());
    }

    public String getCurrentStrategyName() { return strategy.getStrategyName(); }

    public List<Item> getRecommendations(User user, List<Item> allItems, List<Item> userItems) {
        if (allItems == null || allItems.isEmpty()) return new ArrayList<>();
        return strategy.recommend(user, allItems, userItems);
    }
}
