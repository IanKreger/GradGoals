package com.gradgoals;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    // MAP: Key = UserID, Value = That User's GoalChecker (List of goals)
    private static final Map<String, GoalChecker> userGoals = new ConcurrentHashMap<>();

    // Helper: Get the correct "notebook" for the specific user
    private GoalChecker getGoalChecker(String userId) {
        if (userId == null || userId.isEmpty()) userId = "guest";
        return userGoals.computeIfAbsent(userId, k -> new GoalChecker());
    }

    // 1. GET ALL GOALS (Requires userId)
    @GetMapping("/all")
    public List<GoalChecker.SavingsGoal> getAllGoals(@RequestParam String userId) {
        return getGoalChecker(userId).getAllGoals();
    }

    // 2. CREATE GOAL (Requires userId in body)
    @PostMapping("/create")
    public GoalChecker.SavingsGoal createGoal(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String name = (String) body.get("name");
        double targetAmount = ((Number) body.get("targetAmount")).doubleValue();
        
        return getGoalChecker(userId).createGoal(name, targetAmount);
    }

    // 3. ADD FUNDS (Requires userId in body)
    @PostMapping("/add")
    public GoalChecker.SavingsGoal addToGoal(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String id = (String) body.get("id");
        double amount = ((Number) body.get("amount")).doubleValue();
        
        return getGoalChecker(userId).addToGoal(id, amount);
    }

    // 4. DELETE GOAL (Requires userId in Query Param)
    @DeleteMapping("/delete/{id}")
    public String deleteGoal(@PathVariable String id, @RequestParam String userId) {
        boolean removed = getGoalChecker(userId).removeGoal(id);
        return removed ? "Goal removed" : "Goal not found";
    }
}