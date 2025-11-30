package com.gradgoals;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BudgetController {

    @GetMapping("/")
    public String home() {
        return "GradGoals Backend Running!";
    }

    @GetMapping("/budget-summary")
    public String getBudgetSummary() {
        BudgetToolCode budget = new BudgetToolCode();
        double totalIncome = budget.getTotalIncome();
        double totalExpenses = budget.getTotalExpenses();
        double net = budget.getNetMonthly();

        return String.format("Income: %.2f, Expenses: %.2f, Net: %.2f",
                totalIncome, totalExpenses, net);
    }
}