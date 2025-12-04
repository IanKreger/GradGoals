package com.gradgoals;

import java.util.*;
import java.util.UUID;

public class GoalChecker {

    public class SavingsGoal {
        private String id;
        private String name;
        private double targetAmount;
        private double currentAmount;

        public SavingsGoal() {
            this.id = UUID.randomUUID().toString();
        }

        public SavingsGoal(String name, double targetAmount) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.targetAmount = targetAmount;
            this.currentAmount = 0;
        }

        // Getters & Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getTargetAmount() { return targetAmount; }
        public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
        public double getCurrentAmount() { return currentAmount; }
        public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

        public void addAmount(double amount) {
            this.currentAmount += amount;
            if (this.currentAmount > targetAmount) this.currentAmount = targetAmount;
        }
    }

    private final List<SavingsGoal> goals = new ArrayList<>();

    public List<SavingsGoal> getAllGoals() {
        return new ArrayList<>(goals);
    }

    public SavingsGoal createGoal(String name, double targetAmount) {
        SavingsGoal goal = new SavingsGoal(name, targetAmount);
        goals.add(goal);
        return goal;
    }

    public SavingsGoal addToGoal(String id, double amount) {
        for (SavingsGoal goal : goals) {
            if (goal.getId().equals(id)) {
                goal.addAmount(amount);
                return goal;
            }
        }
        return null;
    }

    public boolean removeGoal(String id) {
        return goals.removeIf(g -> g.getId().equals(id));
    }
}
