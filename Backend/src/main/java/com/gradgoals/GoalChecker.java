package com.gradgoals;

// This class manages savings goals: creating them, updating progress, returning all goals, and deleting goals.
import java.util.*;
import java.util.UUID;

public class GoalChecker {

    // Represents a single savings goal created by the user
    public class SavingsGoal {
        private String id;             // unique ID for the goal
        private String name;           // description/name of the goal
        private double targetAmount;   // total amount the user wants to save
        private double currentAmount;  // how much the user has saved so far

        // Default constructor automatically generates a unique ID
        public SavingsGoal() {
            this.id = UUID.randomUUID().toString();
        }

        // Constructor used when the user creates a new goal
        public SavingsGoal(String name, double targetAmount) {
            this.id = UUID.randomUUID().toString(); // unique ID per goal
            this.name = name;
            this.targetAmount = targetAmount;
            this.currentAmount = 0;                // starting at zero saved
        }

        // Getters & Setters for accessing and updating fields
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getTargetAmount() { return targetAmount; }
        public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
        public double getCurrentAmount() { return currentAmount; }
        public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

        // Adds money to the goal while preventing the amount from exceeding the target
        public void addAmount(double amount) {
            this.currentAmount += amount;
            if (this.currentAmount > targetAmount) {
                this.currentAmount = targetAmount;
            }
        }
    }

    // Stores all savings goals created in this session
    private final List<SavingsGoal> goals = new ArrayList<>();

    // Returns a copy of all current goals
    public List<SavingsGoal> getAllGoals() {
        return new ArrayList<>(goals);
    }

    // Creates a new goal and adds it to the list
    public SavingsGoal createGoal(String name, double targetAmount) {
        SavingsGoal goal = new SavingsGoal(name, targetAmount);
        goals.add(goal);
        return goal;
    }

    // Adds an amount to a specific goal by ID and returns the updated goal
    public SavingsGoal addToGoal(String id, double amount) {
        for (SavingsGoal goal : goals) {
            if (goal.getId().equals(id)) {
                goal.addAmount(amount);
                return goal;
            }
        }
        return null; // If no matching goal is found
    }

    // Removes a goal from the list by ID
    public boolean removeGoal(String id) {
        return goals.removeIf(g -> g.getId().equals(id));
    }
}
