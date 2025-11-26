import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*")
@RestController
public class RatingController {

    private RatingService ratingService = new RatingService();

    // Add/update rating
    @PostMapping("/ratings")
    public String addRating(@RequestBody Rating rating) {
        ratingService.addRating(rating);
        return "Rating saved.";
    }

    // Get the average rating for a resource
    @GetMapping("/ratings/average")
    public double getAverage(@RequestParam String resourceId) {
        return ratingService.getAverage(resourceId);
    }

    // Get a user's rating for a resource
    @GetMapping("/ratings/user")
    public Rating getUserRating(
            @RequestParam String resourceId,
            @RequestParam String userId
    ) {
        return ratingService.getUserRating(resourceId, userId);
    }
}
