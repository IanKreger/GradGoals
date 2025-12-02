package com.gradgoals;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalChecker goalChecker = new GoalChecker();

    @GetMapping("/all")
    public List<GoalChecker.SavingsGoal> getAllGoals() {
        return goalChecker.getAllGoals();
    }

    @PostMapping("/create")
    public GoalChecker.SavingsGoal createGoal(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        double targetAmount = ((Number) body.get("targetAmount")).doubleValue();
        return goalChecker.createGoal(name, targetAmount);
    }

    @PostMapping("/add")
    public GoalChecker.SavingsGoal addToGoal(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        double amount = ((Number) body.get("amount")).doubleValue();
        return goalChecker.addToGoal(id, amount);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteGoal(@PathVariable String id) {
        boolean removed = goalChecker.removeGoal(id);
        return removed ? "Goal removed" : "Goal not found";
    }
}
