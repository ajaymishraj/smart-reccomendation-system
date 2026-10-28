package com.recommendation.platform.engine;

import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Polymorphism: implements RecommendationStrategy
public class PopularityBasedRecommendation implements RecommendationStrategy {

    private static final int MAX_RECOMMENDATIONS = 5;

    @Override
    public String getStrategyName() { return "Popularity-Based Recommendation"; }

    /**
     * Returns top-N items sorted by viewCount + enrollment + rating.
     * Items already interacted with by the user are excluded.
     */
    @Override
    public List<Item> recommend(User user, List<Item> allItems, List<Item> userItems) {

        // IDs the user already interacted with
        List<Integer> seen = userItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toList());

        return allItems.stream()
                .filter(Item::isAvailable)
                .filter(i -> !seen.contains(i.getItemId()))
                .sorted(Comparator.comparingDouble(this::popularityScore).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .collect(Collectors.toList());
    }

    // Higher score = more popular
    private double popularityScore(Item item) {
        return (item.getViewCount() * 0.3)
             + (item.getTotalRatings() * 0.4)
             + (item.getAverageRating() * 10 * 0.3);
    }
}
