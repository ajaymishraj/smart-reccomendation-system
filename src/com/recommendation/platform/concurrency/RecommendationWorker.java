package com.recommendation.platform.concurrency;

import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.User;
import com.recommendation.platform.service.RecommendationService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Unit 4 Requirement: Multithreading & Concurrency
 * Implements Runnable to pre-calculate recommendations asynchronously in a worker thread.
 */
public class RecommendationWorker implements Runnable {

    private final User user;
    private final RecommendationService recommendationService;
    private final List<Item> resultCache = new CopyOnWriteArrayList<>();
    private volatile boolean completed = false;
    private Runnable onCompleteCallback;

    public RecommendationWorker(User user, RecommendationService recommendationService) {
        this.user = user;
        this.recommendationService = recommendationService;
    }

    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    @Override
    public void run() {
        System.out.println("[Thread: " + Thread.currentThread().getName() +
                           "] Computing recommendations for " + user.getName() + "...");
        try {
            // Simulate intensive collaborative/content filtering computation
            Thread.sleep(150);
            List<Item> recs = recommendationService.getRecommendations(user);
            synchronized (resultCache) {
                resultCache.clear();
                resultCache.addAll(recs);
            }
            completed = true;
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
            System.out.println("[Thread: " + Thread.currentThread().getName() +
                               "] Finished calculation for " + user.getName() + ". Found " + recs.size() + " items.");
        } catch (InterruptedException e) {
            System.err.println("[Thread: " + Thread.currentThread().getName() + "] Worker interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public boolean isCompleted() { return completed; }

    public List<Item> getResultCache() {
        synchronized (resultCache) {
            return new CopyOnWriteArrayList<>(resultCache);
        }
    }
}
