import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class ChallengeController {

    @PostMapping("/challenge")
    public ChallengeResponse checkChallenge(@RequestBody Challenge challenge) {

        String userAnswer = challenge.getAnswer().trim();
        String correctAnswer = "100"; // 20% of $500

        if (userAnswer.equals(correctAnswer)) {
            return new ChallengeResponse(true, "Correct! Saving 20% is a smart budget habit.");
        }

        return new ChallengeResponse(false, "Incorrect. Try again — 20% of $500 is $100.");
    }
}
