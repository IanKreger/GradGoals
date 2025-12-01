package com.gradgoals;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GoalCheckerTest {

    @Test
    void testCreateGoal() {
        GoalChecker checker = new GoalChecker();

        GoalChecker.SavingsGoal goal = checker.createGoal("Car", 5000);

        assertNotNull(goal.getId());
        assertEquals("Car", goal.getName());
        assertEquals(5000, goal.getTargetAmount());
        assertEquals(0, goal.getCurrentAmount());
    }

    @Test
    void testGetAllGoals() {
        GoalChecker checker = new GoalChecker();

        checker.createGoal("Laptop", 1200);
        checker.createGoal("Bike", 800);

        List<GoalChecker.SavingsGoal> goals = checker.getAllGoals();

        assertEquals(2, goals.size());
    }

    @Test
    void testAddToGoal() {
        GoalChecker checker = new GoalChecker();
        GoalChecker.SavingsGoal goal = checker.createGoal("Trip", 1000);

        checker.addToGoal(goal.getId(), 200);

        assertEquals(200, goal.getCurrentAmount());
    }

    @Test
    void testAddToGoalOverTarget() {
        GoalChecker checker = new GoalChecker();
        GoalChecker.SavingsGoal goal = checker.createGoal("Gaming PC", 1500);

        checker.addToGoal(goal.getId(), 2000);  // too much

        assertEquals(1500, goal.getCurrentAmount());  // should not exceed target
    }

    @Test
    void testAddToNonExistingGoal() {
        GoalChecker checker = new GoalChecker();

        GoalChecker.SavingsGoal result = checker.addToGoal("fake-id", 300);

        assertNull(result);
    }

    @Test
    void testRemoveGoal() {
        GoalChecker checker = new GoalChecker();
        GoalChecker.SavingsGoal goal = checker.createGoal("Phone", 900);

        boolean removed = checker.removeGoal(goal.getId());

        assertTrue(removed);
        assertTrue(checker.getAllGoals().isEmpty());
    }

    @Test
    void testRemoveNonExistingGoal() {
        GoalChecker checker = new GoalChecker();

        boolean removed = checker.removeGoal("does-not-exist");

        assertFalse(removed);
    }
}
