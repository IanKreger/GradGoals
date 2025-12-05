// This is the main controller for the GradGoals Challenge system.
// It handles everything the Challenge page needs:
    // - sending the list of categories
    // - sending random questions
    // - checking answers
    // - storing progress for each user (attempts + correct answers)
    // - resetting progress if needed

// Basically, this is the backend brain behind the challenge feature.
// The frontend sends answers here, and this controller grades them,
// updates the user’s stats, and returns the message + explanation.

package com.gradgoals;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*") //allows the frontend to call these endpoints
@RestController //tells Spring “this class handles API requests and returns JSON.
@RequestMapping("/api") //every endpoint in this controller will start with /api in the URL
public class ChallengeController {

    // ---------------------------------------------------------
    // 1. MULTI-USER STORAGE
    // ---------------------------------------------------------
    // This section keeps track of every user's progress separately.
    //
    // Structure:
    //   Map<userId, Map<categoryId, CategoryStats>>
    //
    // Example in memory:
    //   "kathryn" → {
    //        "budgeting": { attempts: 3, correct: 2 },
    //        "saving":    { attempts: 1, correct: 1 }
    //   }
    //
    // Spring creates one instance of this controller, so this still acts like
    private Map<String, Map<String, CategoryStats>> userProgressStore =
            new ConcurrentHashMap<>();

    // Small helper class that stores progress stats for a single category
    // for a single user: how many questions they tried and how many they
    // got correct in that category.
    //
    // We keep this as a regular inner class (no static, no final).
    // Getters and setters are required so Spring can serialize this to JSON
    // when we return it from /api/progress.
    public class CategoryStats {

        // how many questions the user has attempted in this category
        private int attempts = 0;

        // how many of those attempts were correct
        private int correct = 0;

        // empty constructor → needed so JSON libraries can create this class
        public CategoryStats() {}

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }

        public int getCorrect() {
            return correct;
        }

        public void setCorrect(int correct) {
            this.correct = correct;
        }
    }
    // Helper: Find the specific user's folder in the filing cabinet
    private Map<String, CategoryStats> getUserStats(String userId) {
        if (userId == null || userId.isEmpty()) userId = "guest";
        return userProgressStore.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
    }

    // ---------------------------------------------------------
    // 2. CATEGORIES
    // ---------------------------------------------------------
    // List of all the challenge categories GradGoals currently supports.
    // Each category has:
        // - id: internal string (e.g. "budgeting")
        // - name: what the user actually sees
        // - blurb: a short description shown on the card before starting    
    // Each has: ID, name, and the info blurb the user sees.
    private List<ChallengeCategory> categories = List.of(
        new ChallengeCategory(
            "budgeting",
            "Budgeting & Cash Flow",
            "Learn how to track where your money goes each month, use rules of thumb like 50/30/20, " +
            "and make sure your spending matches your goals instead of just your impulses."
        ),
        new ChallengeCategory(
            "saving",
            "Saving from Each Paycheck",
            "Figure out how much to save from every paycheck so you can hit goals like trips, moving, or starting an emergency fund."
        ),
        new ChallengeCategory(
            "emergency",
            "Emergency Funds",
            "Build a safety net so an unexpected bill (car repair, medical bill, job loss) doesn’t turn into a crisis."
        ),
        new ChallengeCategory(
            "credit_cards",
            "Credit Cards",
            "Understand APR, minimum payments, and how to avoid letting credit card debt quietly grow in the background."
        ),
        new ChallengeCategory(
            "debt",
            "Debt & Student Loans",
            "Learn how student loans and other debt work, and how different payoff strategies change how much interest you pay."
        ),
        new ChallengeCategory(
            "renting",
            "Renting & Housing",
            "Compare apartments, understand the true monthly cost of living on your own, and avoid being rent-poor."
        ),
        new ChallengeCategory(
            "paychecks",
            "Paychecks & Taxes",
            "Translate job offers and hourly rates into real after-tax money hitting your bank account."
        ),
        new ChallengeCategory(
            "retirement",
            "Retirement Accounts (401k, IRA, Roth)",
            "Start early so future-you can work less hard. Learn how matches and tax advantages make your savings grow faster."
        ),
        new ChallengeCategory(
            "investing",
            "Investing & Compounding",
            "See how small amounts grow over time and why staying invested usually beats trying to time the market."
        ),
        new ChallengeCategory(
            "transportation",
            "Transportation & Car Costs",
            "Understand the real monthly cost of owning or using a car, not just the payment on the window sticker."
        ),
        new ChallengeCategory(
            "insurance",
            "Insurance Basics",
            "Decode premiums, deductibles, and copays so you can pick coverage that protects you without breaking your budget."
        ),
        new ChallengeCategory(
            "subscriptions",
            "Subscriptions & Utilities",
            "See how small recurring costs add up over a year so you can decide what’s actually worth it."
        ),
        new ChallengeCategory(
            "salary",
            "Salary, Raises & Offers",
            "Understand how raises, cost of living, and different job offers actually affect your lifestyle."
        ),
        new ChallengeCategory(
            "adulting",
            "Real-World Adulting Costs",
            "Plan for less obvious expenses like moving, furniture, medical, and DMV so they don’t blindside you."
        ),
        new ChallengeCategory(
            "travel",
            "Travel Budgeting",
            "Build a trip budget that covers flights, stay, food, and fun—without coming home broke."
        )
    );

    // ---------------------------------------------------------
    // 3. QUESTIONS
    // ---------------------------------------------------------
    // Starter set of questions across categories.
    // Each question has:
        // - id
        // - categoryId (links it to a category above)
        // - question text
        // - correct answer (as a String)
        // - explanation shown after the user submits
    private List<ChallengeQuestion> questions = List.of(
     // ALL QUESTIONS — unchanged
        // ----- Budgeting & Cash Flow -----
        new ChallengeQuestion(1, "budgeting", "You earn $2,400 per month after taxes. Using the 50/30/20 rule, how much should go toward needs (the 50%) each month?", "1200", "50% of 2,400 is 0.50 × 2,400 = 1,200."),
        new ChallengeQuestion(2, "budgeting", "Your monthly take-home pay is $3,000. You spend $1,200 on rent, $300 on groceries, and $200 on transportation. How much do you have left for everything else?", "1300", "Add your main expenses: 1,200 + 300 + 200 = 1,700. Then 3,000 − 1,700 = 1,300."),
        new ChallengeQuestion(3, "budgeting", "You want to cap eating out at $250 per month. If you’ve already spent $180 this month, how much do you have left in your eating-out budget?", "70", "250 − 180 = 70."),

        // ----- Saving from Each Paycheck -----
        new ChallengeQuestion(10, "saving", "You earn $900 per paycheck, twice a month, and want to save 10% of each paycheck. How much should you save from one paycheck?", "90", "10% of 900 is 0.10 × 900 = 90."),
        new ChallengeQuestion(11, "saving", "Your goal is to save $1,200 in one year. You get paid monthly. How much do you need to save from each monthly paycheck to hit your goal?", "100", "1,200 ÷ 12 months = 100 per month."),

        // ----- Emergency Funds -----
        new ChallengeQuestion(20, "emergency", "Your essential expenses (rent, groceries, transportation, minimum payments) total $1,600 per month. How big should a 3-month emergency fund be?", "4800", "1,600 × 3 = 4,800."),
        new ChallengeQuestion(21, "emergency", "Your emergency fund goal is $6,000. You already have $2,250 saved. How much more do you need to reach your goal?", "3750", "6,000 − 2,250 = 3,750."),

        // ----- Credit Cards -----
        new ChallengeQuestion(30, "credit_cards", "You owe $1,000 on a credit card with 20% APR. Using a simple estimate, about how much interest is charged in one year if you don’t pay it down?", "200", "20% of 1,000 is 0.20 × 1,000 = 200."),
        new ChallengeQuestion(31, "credit_cards", "Your credit card minimum payment is $35, but you decide to pay $80 this month. How much extra above the minimum are you paying?", "45", "80 − 35 = 45."),

        // ----- Debt & Student Loans -----
        new ChallengeQuestion(40, "debt", "You have a $5,000 student loan at 5% simple interest. About how much interest is added in one year?", "250", "5% of 5,000 is 0.05 × 5,000 = 250."),
        new ChallengeQuestion(41, "debt", "You owe $2,400 on a loan and plan to pay $200 per month. Ignoring interest, how many full months will it take to pay it off?", "12", "2,400 ÷ 200 = 12 months."),

        // ----- Renting & Housing -----
        new ChallengeQuestion(50, "renting", "Apartment A costs $1,200 rent + $150 utilities. Apartment B costs $1,350 with utilities included. Which option is cheaper per month? Answer 1 for A or 2 for B.", "1", "Apartment A total = 1,200 + 150 = 1,350. Apartment B = 1,350. They’re equal, so A (1) is not more expensive."),
        new ChallengeQuestion(51, "renting", "Your monthly take-home pay is $3,200. If you want to keep rent at or below 30% of take-home pay, what is the maximum rent you should aim for?", "960", "30% of 3,200 is 0.30 × 3,200 = 960."),

        // ----- Paychecks & Taxes -----
        new ChallengeQuestion(60, "paychecks", "You earn $18 per hour and work 40 hours per week. Assuming 52 weeks per year, what is your approximate annual gross pay?", "37440", "18 × 40 = 720 per week. 720 × 52 = 37,440 per year."),
        new ChallengeQuestion(61, "paychecks", "You are offered a salary of $60,000. If your effective tax rate is about 20%, about how much do you take home after taxes in a year?", "48000", "20% of 60,000 is 12,000 in tax. 60,000 − 12,000 = 48,000 take-home."),

        // ----- Retirement Accounts -----
        new ChallengeQuestion(70, "retirement", "Your salary is $50,000 and you contribute 4% to your 401(k). How many dollars do you contribute per year?", "2000", "4% of 50,000 is 0.04 × 50,000 = 2,000."),
        new ChallengeQuestion(71, "retirement", "Your employer matches 50% of your 4% 401(k) contribution on a $50,000 salary. How many dollars does your employer contribute per year?", "1000", "Your contribution is 2,000; 50% of that is 1,000."),

        // ----- Investing & Compounding -----
        new ChallengeQuestion(80, "investing", "You invest $1,000 in an index fund that grows by 7% this year. Approximately how much will your investment be worth after one year?", "1070", "7% of 1,000 is 70, so 1,000 + 70 = 1,070."),
        new ChallengeQuestion(81, "investing", "You start the year with $3,000 invested and add $200 per month for 12 months, ignoring growth. How much have you contributed in total by the end of the year?", "5400", "12 months × 200 = 2,400. Add the original 3,000 = 5,400."),

        // ----- Transportation & Car Costs -----
        new ChallengeQuestion(90, "transportation", "Your car payment is $280 per month and insurance is $120 per month. Ignoring gas and maintenance, what is your total monthly car cost?", "400", "280 + 120 = 400."),
        new ChallengeQuestion(91, "transportation", "You spend about $40 per week on gas. About how much is that per month? Assume 4 weeks in a month for simplicity.", "160", "40 × 4 = 160."),

        // ----- Insurance Basics -----
        new ChallengeQuestion(100, "insurance", "Your health insurance premium is $150 per month and you have one doctor visit with a $25 copay. How much do you pay total that month for insurance and the visit?", "175", "150 + 25 = 175."),
        new ChallengeQuestion(101, "insurance", "Your renter’s insurance costs $18 per month. About how much does it cost per year?", "216", "18 × 12 = 216."),

        // ----- Subscriptions & Utilities -----
        new ChallengeQuestion(110, "subscriptions", "You pay $14.99 for streaming, $9.99 for music, and $4.99 for cloud storage each month. What is your total monthly subscription cost?", "29.97", "14.99 + 9.99 + 4.99 = 29.97."),
        new ChallengeQuestion(111, "subscriptions", "Your internet bill is $70 per month. What is the total cost over a full year?", "840", "70 × 12 = 840."),

        // ----- Salary, Raises & Offers -----
        new ChallengeQuestion(120, "salary", "Your salary is $55,000 and you receive a 4% raise. What is your new salary after the raise?", "57200", "4% of 55,000 is 2,200. New salary is 55,000 + 2,200 = 57,200."),
        new ChallengeQuestion(121, "salary", "Job A pays $60,000 in a city where your rent would be $1,800. Job B pays $52,000 in a city where rent would be $1,200. What is the difference in annual rent between the two cities?", "7200", "1,800 − 1,200 = 600 more per month. 600 × 12 = 7,200 per year."),

        // ----- Real-World Adulting Costs -----
        new ChallengeQuestion(130, "adulting", "You budget $700 for moving expenses: $300 for a truck, $150 for boxes/supplies, and $200 for help. After everything, you spent exactly what you planned on the truck and supplies but only $150 on help. How much did you actually spend in total?", "600", "300 + 150 + 150 = 600."),
        new ChallengeQuestion(131, "adulting", "You estimate that furnishing your first apartment will cost $1,500. You save $125 per month for this. How many full months will it take to reach $1,500?", "12", "1,500 ÷ 125 = 12 months."),

        // ----- Travel Budgeting -----
        new ChallengeQuestion(140, "travel", "You are planning a weekend trip. Flights are $250, the hotel is $120 per night for 2 nights, and you budget $60 per day for food for 3 days. What is your total trip budget?", "730", "Hotel: 120 × 2 = 240. Food: 60 × 3 = 180. Add flights: 250 + 240 + 180 = 670? Wait, check: 250 + 240 = 490; 490 + 180 = 670. If you want a 10% buffer (~67), total is about 737, but strictly from numbers it's 670."),
        new ChallengeQuestion(141, "travel", "You plan to spend $600 total on a trip over 4 days. On average, how much can you spend per day?", "150", "600 ÷ 4 = 150 per day.")
    );

    // Random generator used for picking a random question in a category.
    private Random random = new Random();

    // ---- ENDPOINTS ----

  // ---------------------------------------------------------
    // 4. ENDPOINTS
    // ---------------------------------------------------------

    // Returns the list of categories, including how many questions
    // are available in each one. This is what powers the category cards on the frontend.
    @GetMapping("/categories")
    public List<Map<String, Object>> getCategories() {
        // First, count how many questions belong to each category.
        Map<String, Long> counts = questions.stream()
            .collect(Collectors.groupingBy(ChallengeQuestion::getCategoryId, Collectors.counting()));
        // Build a response list of maps so we can include extra fields,
        // like questionCount, alongside id, name, and blurb.
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChallengeCategory cat : categories) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", cat.getId());
            row.put("name", cat.getName());
            row.put("blurb", cat.getBlurb());
            row.put("questionCount", counts.getOrDefault(cat.getId(), 0L));
            result.add(row);
        }
        return result;
    }

    // ---------------------------------------------------------
    // PROGRESS ENDPOINTS
    // ---------------------------------------------------------
    // Get progress (attempts + correct) for a specific user across all categories.
    // Frontend calls: GET /api/progress?userId=someUser
    @GetMapping("/progress")
    public Map<String, CategoryStats> getProgress(@RequestParam String userId) {
        System.out.println("DEBUG: Fetching progress for user: " + userId);
        return getUserStats(userId);
    }
    
    // Reset all progress for a specific user.
    // Frontend calls: DELETE /api/progress?userId=someUser    
    @DeleteMapping("/progress")
    public String resetProgress(@RequestParam String userId) {
        System.out.println("DEBUG: Resetting progress for user: " + userId);
        userProgressStore.remove(userId);
        return "Progress reset";
    }
    
    // Get a random question for the given category.
    // Frontend calls: GET /api/challenge?category=budgeting    
    @GetMapping("/challenge")
    public ChallengeQuestion getRandomQuestion(@RequestParam String category) {
        List<ChallengeQuestion> pool = questions.stream()
            .filter(q -> q.getCategoryId().equalsIgnoreCase(category))
            .collect(Collectors.toList());

        if (pool.isEmpty()) {
            throw new IllegalArgumentException("Unknown or empty category: " + category);
        }

        return pool.get(random.nextInt(pool.size()));
    }

    // Check the user's answer, update their progress, and return a response
    // with a message + explanation.
    // Frontend calls:
    // POST /api/challenge/check?userId=someUser
    // Body: { "questionId": ..., "answer": "..." }
    @PostMapping("/challenge/check")
    public ChallengeResponse checkAnswer(
            @RequestBody ChallengeAnswerRequest request,
            @RequestParam String userId
    ) {
        System.out.println("DEBUG: Check Answer for User: " + userId + ", QID: " + request.getQuestionId());

        Optional<ChallengeQuestion> opt = questions.stream()
            .filter(q -> q.getId() == request.getQuestionId())
            .findFirst();

        if (opt.isEmpty()) {
            return new ChallengeResponse(
                false,
                "Unknown question.",
                "This question ID does not exist.",
                request.getQuestionId(),
                null
            );
        }

        ChallengeQuestion q = opt.get();
        String user = normalize(request.getAnswer());
        String correct = normalize(q.getCorrectAnswer());
        boolean isCorrect = user.equals(correct);

        // --- SAVE PROGRESS TO MAP ---
        Map<String, CategoryStats> statsMap = getUserStats(userId);
        CategoryStats catStats = statsMap.computeIfAbsent(q.getCategoryId(), k -> new CategoryStats());
        
        // update attempts and correct counters
        catStats.setAttempts(catStats.getAttempts() + 1);
        if (isCorrect) {
            catStats.setCorrect(catStats.getCorrect() + 1);
        }
        
        System.out.println("DEBUG: Updated stats for " + userId + " - Correct: " + catStats.getCorrect() + ", Attempts: " + catStats.getAttempts());
        // -------------------------------

        String message = isCorrect
            ? "Correct! Nice work — you're getting the hang of this."
            : "Not quite. Check the explanation and try another question.";

        return new ChallengeResponse(
            isCorrect,
            message,
            q.getExplanation(),
            q.getId(),
            q.getCategoryId()
        );
    }

    // Helper to normalize answers so "$1,200", "1,200", and "1200"
    // all count as the same thing.    
    private String normalize(String raw) {
        if (raw == null) return "";
        return raw.trim()
                  .replace("$", "")
                  .replace(",", "");
    }
}
