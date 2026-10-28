package com.recommendation.platform.service;

import com.recommendation.platform.engine.*;
import com.recommendation.platform.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationService {

    private final RecommendationEngine engine;
    private final CatalogService       catalogService;
    private final InteractionService   interactionService;

    public RecommendationService(CatalogService catalogService,
                                  InteractionService interactionService) {
        this.catalogService      = catalogService;
        this.interactionService  = interactionService;
        this.engine              = new RecommendationEngine(); // default: popularity
    }

    public List<Item> getRecommendations(User user) {
        List<Item> allItems   = catalogService.getAvailableItems();
        List<Item> userItems  = interactionService.getEnrolledItems(user.getUserId());
        return engine.getRecommendations(user, allItems, userItems);
    }

    public void switchToPopularity() {
        engine.setStrategy(new PopularityBasedRecommendation());
    }

    public void switchToContentBased() {
        engine.setStrategy(new ContentBasedFiltering());
    }

    public void switchToCollaborative() {
        Map<Integer, java.util.Set<Integer>> matrix = interactionService.buildUserItemMatrix();
        engine.setStrategy(new CollaborativeFiltering(matrix));
    }

    public String getCurrentStrategy() { return engine.getCurrentStrategyName(); }
}
