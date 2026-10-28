package com.recommendation.platform.engine;

import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.User;

import java.util.*;
import java.util.stream.Collectors;

// Polymorphism: implements RecommendationStrategy
public class CollaborativeFiltering implements RecommendationStrategy {

    private static final int MAX_RECOMMENDATIONS = 5;

    // Simulated: user → itemIds they interacted with
    private final Map<Integer, Set<Integer>> userItemMatrix;

    public CollaborativeFiltering(Map<Integer, Set<Integer>> userItemMatrix) {
        this.userItemMatrix = userItemMatrix;
    }

    @Override
    public String getStrategyName() { return "Collaborative Filtering"; }

    /**
     * Finds similar users (those who liked the same items) and recommends
     * items they interacted with that the current user hasn't seen yet.
     */
    @Override
    public List<Item> recommend(User user, List<Item> allItems, List<Item> userItems) {

        Set<Integer> myItems = userItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toSet());

        // Score every other user by overlap with current user
        Map<Integer, Integer> similarityScore = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : userItemMatrix.entrySet()) {
            int otherUserId = entry.getKey();
            if (otherUserId == user.getUserId()) continue;
            Set<Integer> intersection = new HashSet<>(entry.getValue());
            intersection.retainAll(myItems);
            if (!intersection.isEmpty()) {
                similarityScore.put(otherUserId, intersection.size());
            }
        }

        // Collect candidate items from similar users
        Set<Integer> candidateItemIds = new LinkedHashSet<>();
        similarityScore.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> {
                    Set<Integer> theirItems = userItemMatrix.get(e.getKey());
                    theirItems.stream()
                              .filter(id -> !myItems.contains(id))
                              .forEach(candidateItemIds::add);
                });

        // Map ids → Items and return top-N by rating
        Map<Integer, Item> itemMap = new HashMap<>();
        allItems.forEach(i -> itemMap.put(i.getItemId(), i));

        return candidateItemIds.stream()
                .map(itemMap::get)
                .filter(Objects::nonNull)
                .filter(Item::isAvailable)
                .sorted(Comparator.comparingDouble(Item::getAverageRating).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .collect(Collectors.toList());
    }
}
