package com.gradgoals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RatingService {

    // INSTANCE VARIABLE (Not Static): 
    // This Map lives only as long as this specific RatingService object lives.
    private final Map<String, List<Rating>> ratings = new ConcurrentHashMap<>();

    public void addRating(Rating rating) {
        // Ensure the list exists for this resource
        ratings.putIfAbsent(rating.getResourceId(), new ArrayList<>());
        
        List<Rating> list = ratings.get(rating.getResourceId());

        // Check if this user already rated it; if so, update their stars
        for (Rating r : list) {
            if (r.getUserId().equals(rating.getUserId())) {
                r.setStars(rating.getStars());
                return;
            }
        }

        // If not found, add the new rating
        list.add(rating);
    }

    public double getAverage(String resourceId) {
        List<Rating> list = ratings.get(resourceId);
        
        // Avoid division by zero
        if (list == null || list.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (Rating r : list) {
            total += r.getStars();
        }

        return total / (double) list.size();
    }

    public Rating getUserRating(String resourceId, String userId) {
        List<Rating> list = ratings.get(resourceId);
        if (list == null) return null;

        for (Rating r : list) {
            if (r.getUserId().equals(userId)) {
                return r;
            }
        }
        return null;
    }
}