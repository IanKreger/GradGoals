package com.gradgoals;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class RatingController {

    // The Controller holds this instance, keeping the data alive.
    private final RatingService ratingService = new RatingService();

    // 1. SAVE RATING
    @PostMapping("/ratings")
    public String addRating(@RequestBody Rating rating) {
        ratingService.addRating(rating);
        return "Rating saved.";
    }

    // 2. GET AVERAGE (For the number displayed next to stars)
    @GetMapping("/ratings/average")
    public double getAverage(@RequestParam String resourceId) {
        return ratingService.getAverage(resourceId);
    }

    // 3. GET USER RATING (To highlight the stars the user previously clicked)
    @GetMapping("/ratings/user")
    public Rating getUserRating(
            @RequestParam String resourceId,
            @RequestParam String userId
    ) {
        return ratingService.getUserRating(resourceId, userId);
    }
}