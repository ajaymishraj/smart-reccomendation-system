package com.recommendation.platform.engine;

import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.User;

import java.util.List;

// Abstraction via Interface — Strategy Pattern
public interface RecommendationStrategy {
    String getStrategyName();
    List<Item> recommend(User user, List<Item> allItems, List<Item> userItems);
}
