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

        checker.addToGoal(goal.getId(), 2000);

        assertEquals(1500, goal.getCurrentAmount());
    }

    @Test
    void testAddToNonExistingGoal() {
        GoalChecker checker = new GoalChecker();

        assertNull(checker.addToGoal("fake-id", 300));
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

        assertFalse(checker.removeGoal("does-not-exist"));
    }

    @Test
    void testAddAmountDoesNotExceedTarget() {
        GoalChecker checker = new GoalChecker();
        GoalChecker.SavingsGoal goal = checker.createGoal("Laptop", 1000);

        checker.addToGoal(goal.getId(), 1200);

        assertEquals(1000, goal.getCurrentAmount());
    }

    @Test
    void testCreateMultipleGoalsHaveUniqueIds() {
        GoalChecker checker = new GoalChecker();

        var g1 = checker.createGoal("Goal1", 100);
        var g2 = checker.createGoal("Goal2", 200);

        assertNotEquals(g1.getId(), g2.getId());
    }

    @Test
    void testRemoveGoalActuallyRemovesIt() {
        GoalChecker checker = new GoalChecker();
        var goal = checker.createGoal("Test", 300);

        boolean removed = checker.removeGoal(goal.getId());

        assertTrue(removed);
        assertTrue(checker.getAllGoals().isEmpty());
    }

    @Test
    void testAddNegativeAmountDoesNothing() {
        GoalChecker checker = new GoalChecker();
        var goal = checker.createGoal("Car Fund", 5000);

        checker.addToGoal(goal.getId(), -200);

        assertEquals(0.0, goal.getCurrentAmount());
    }

    @Test
    void testGetAllGoalsReturnsCopyNotReference() {
        GoalChecker checker = new GoalChecker();
        checker.createGoal("A", 100);

        List<GoalChecker.SavingsGoal> goals = checker.getAllGoals();
        goals.clear();

        assertEquals(1, checker.getAllGoals().size());
    }
}
