package com.gradgoals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingService {

    // Map<resourceId, List of Ratings>
    private Map<String, List<Rating>> ratings = new HashMap<>();

    // Add or update a rating (no comment anymore)
    public void addRating(Rating rating) {
        ratings.putIfAbsent(rating.getResourceId(), new ArrayList<>());

        List<Rating> list = ratings.get(rating.getResourceId());

        // If this user already rated, update stars only
        for (Rating r : list) {
            if (r.getUserId().equals(rating.getUserId())) {
                r.setStars(rating.getStars());
                return;
            }
        }

        // Otherwise add new rating
        list.add(rating);
    }

    // Get average stars for a resource
    public double getAverage(String resourceId) {
        List<Rating> list = ratings.get(resourceId);
        if (list == null || list.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (Rating r : list) {
            total += r.getStars();
        }

        return total / (double) list.size();
    }

    // Get a user's rating for a resource
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
