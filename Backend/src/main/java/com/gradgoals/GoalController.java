package com.gradgoals;

// This controller handles all REST API requests for creating, viewing, updating, and deleting savings goals for each user.

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController                           // Marks this class as a REST API controller
@RequestMapping("/goals")                 // All routes here start with /goals
@CrossOrigin(origins = "*")               // Allows requests from any frontend origin
public class GoalController {

    // Stores each user's goals separately.
    // KEY = userId, VALUE = that user's personal GoalChecker instance (their list of goals).
    private final Map<String, GoalChecker> userGoals = new ConcurrentHashMap<>();

    // Retrieves the GoalChecker for a user, creating one if it doesn't already exist.
    // If userId is missing, a "guest" userId is used.
    private GoalChecker getGoalChecker(String userId) {
        if (userId == null || userId.isEmpty()) userId = "guest";
        return userGoals.computeIfAbsent(userId, k -> new GoalChecker());
    }

    // 1. Returns ALL goals for a specific user.
    // The userId is required as a query parameter.
    @GetMapping("/all")
    public List<GoalChecker.SavingsGoal> getAllGoals(@RequestParam String userId) {
        return getGoalChecker(userId).getAllGoals();
    }

    // 2. Creates a new savings goal for a specific user.
    // userId, name, and targetAmount must be included in the request body.
    @PostMapping("/create")
    public GoalChecker.SavingsGoal createGoal(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String name = (String) body.get("name");
        double targetAmount = ((Number) body.get("targetAmount")).doubleValue();
        
        return getGoalChecker(userId).createGoal(name, targetAmount);
    }

    // 3. Adds money to a user's goal.
    // Body must include userId, goal id, and the amount to add.
    @PostMapping("/add")
    public GoalChecker.SavingsGoal addToGoal(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String id = (String) body.get("id");
        double amount = ((Number) body.get("amount")).doubleValue();
        
        return getGoalChecker(userId).addToGoal(id, amount);
    }

    // 4. Deletes a goal based on its ID.
    // Requires both goal id in the URL and userId in the query.
    @DeleteMapping("/delete/{id}")
    public String deleteGoal(@PathVariable String id, @RequestParam String userId) {
        boolean removed = getGoalChecker(userId).removeGoal(id);
        return removed ? "Goal removed" : "Goal not found";
    }
}
