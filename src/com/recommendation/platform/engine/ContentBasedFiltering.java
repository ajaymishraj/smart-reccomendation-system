package com.recommendation.platform.engine;

import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.ItemCategory;
import com.recommendation.platform.model.RegularUser;
import com.recommendation.platform.model.User;

import java.util.*;
import java.util.stream.Collectors;

// Polymorphism: implements RecommendationStrategy
public class ContentBasedFiltering implements RecommendationStrategy {

    private static final int MAX_RECOMMENDATIONS = 5;

    @Override
    public String getStrategyName() { return "Content-Based Filtering"; }

    /**
     * Recommends items whose category matches the user's past interactions or preferences.
     */
    @Override
    public List<Item> recommend(User user, List<Item> allItems, List<Item> userItems) {

        Set<ItemCategory> likedCategories = new HashSet<>();

        // From interaction history
        for (Item item : userItems) {
            likedCategories.add(item.getCategory());
        }

        // From explicit preferences (if RegularUser)
        if (user instanceof RegularUser) {
            RegularUser ru = (RegularUser) user;
            for (String cat : ru.getPreferredCategories()) {
                try {
                    likedCategories.add(ItemCategory.valueOf(cat.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // Fallback: return popular items if no preference data
        if (likedCategories.isEmpty()) {
            return allItems.stream()
                    .filter(Item::isAvailable)
                    .sorted(Comparator.comparingDouble(Item::getAverageRating).reversed())
                    .limit(MAX_RECOMMENDATIONS)
                    .collect(Collectors.toList());
        }

        List<Integer> seen = userItems.stream()
                .map(Item::getItemId).collect(Collectors.toList());

        return allItems.stream()
                .filter(Item::isAvailable)
                .filter(i -> !seen.contains(i.getItemId()))
                .filter(i -> likedCategories.contains(i.getCategory()))
                .sorted(Comparator.comparingDouble(Item::getAverageRating).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .collect(Collectors.toList());
    }
}
