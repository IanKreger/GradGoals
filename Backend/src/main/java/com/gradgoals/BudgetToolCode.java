// This class handles all budgeting logic for a user, including storing income/expenses,
// running loan and credit-card calculators, and exporting everything to CSV.

package com.gradgoals;

// These imports allow the class to work with files, dates, lists, math operations, etc.
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetToolCode {

    // These small classes represent different types of financial data
    // that the budgeting tool needs to store.

    public class BudgetItem {
        private String id;
        private String category;
        private double amount;
        private String type;  // income or expense

        // Creates a new budget entry for income or expense
        public BudgetItem(String category, double amount, String type) {
            this.id = UUID.randomUUID().toString(); // unique identifier
            this.category = category;
            this.amount = amount;
            this.type = type.toLowerCase();
        }

        public String getId() { return id; }
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
        public String getType() { return type; }
    }

    public class Expense {
        private String name;
        private double amount; // monthly amount

        // Stores a simple recurring expense
        public Expense(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() { return name; }
        public double getAmount() { return amount; }
    }

    // Stores the result of the credit-card payoff simulation
    public class CreditCardResult {
        private boolean payoffPossible;
        private int monthsToPayoff;
        private double totalInterest;
        private double finalBalance;

        public CreditCardResult(boolean payoffPossible, int months, double totalInterest, double finalBalance) {
            this.payoffPossible = payoffPossible;
            this.monthsToPayoff = months;
            this.totalInterest = totalInterest;
                       this.finalBalance = finalBalance;
        }

        public boolean isPayoffPossible() { return payoffPossible; }
        public int getMonthsToPayoff() { return monthsToPayoff; }
        public double getTotalInterest() { return totalInterest; }
        public double getFinalBalance() { return finalBalance; }
    }

    // Stores all income and expense items for the current user session
    private final List<BudgetItem> items = new ArrayList<>();

    // Adds a new income or expense to the budget
    public void addItem(String category, double amount, String type) {
        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            throw new IllegalArgumentException("Type must be 'income' or 'expense'.");
        }
        items.add(new BudgetItem(category, amount, type));
    }

    // Removes an item using its ID
    public void removeItem(String id) {
        items.removeIf(i -> i.getId().equals(id));
    }

    // Returns all budget items
    public List<BudgetItem> getAllItems() {
        return new ArrayList<>(items);
    }

    // Adds up all income items
    public double getTotalIncome() {
        return items.stream()
                .filter(i -> i.getType().equals("income"))
                .mapToDouble(BudgetItem::getAmount)
                .sum();
    }

    // Adds up all expense items
    public double getTotalExpenses() {
        return items.stream()
                .filter(i -> i.getType().equals("expense"))
                .mapToDouble(BudgetItem::getAmount)
                .sum();
    }

    // Money left after expenses
    public double getNetMonthly() {
        return round(getTotalIncome() - getTotalExpenses());
    }

    // Runs a simulation on how long it takes to pay off a credit card
    public CreditCardResult simulateCreditCardPayoff(double balance, double apr, double payment) {

        double monthlyRate = apr / 100.0 / 12.0;

        // If the monthly payment doesn't even cover the interest,
        // the user can never pay it off
        double firstInterest = balance * monthlyRate;
        if (payment <= firstInterest && monthlyRate > 0) {
            double nextBalance = balance + firstInterest - payment;
            return new CreditCardResult(false, 0, 0, round(nextBalance));
        }

        double b = balance;
        double totalInterest = 0;
        int months = 0;
        final int MAX_MONTHS = 3600; // safety limit

        while (b > 0 && months < MAX_MONTHS) {
            double interest = b * monthlyRate;
            totalInterest += interest;

            double principal = payment - interest;
            if (principal <= 0) {
                return new CreditCardResult(false, 0, 0, round(b + interest - payment));
            }

            b -= principal;
            if (b < 0) b = 0;

            months++;
        }

        return new CreditCardResult(true, months, round(totalInterest), round(b));
    }

    // Calculates a student loan monthly payment using the standard formula
    public double studentLoanMonthlyPayment(double principal, double apr, int years) {
        if (principal <= 0) return 0;

        int n = years * 12;      // total payments
        double r = apr / 100.0 / 12.0; // monthly interest rate

        // If APR is zero, payments are just principal divided evenly
        if (r == 0) return principal / n;

        return principal * r / (1 - Math.pow(1 + r, -n));
    }

    // Prepares text so it is valid inside a CSV file
    private String csvEscape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n");
        String escaped = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }

    // Creates a CSV file containing all budget information,
    // including the credit card and student loan results
    public Path exportCsv(
            double ccBalance, double ccApr, double ccPayment, CreditCardResult cc,
            double loanPrincipal, double loanApr, int loanYears, double loanMonthly
    ) throws IOException {

        // Generates a timestamped filename
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path file = Path.of("budget_export_" + ts + ".csv");

        // Writes all budget, loan, and credit card info into the CSV
        try (BufferedWriter w = Files.newBufferedWriter(file)) {

            w.write("Section,Item,Value,Notes\n");

            // Basic budget totals
            w.write("Income,Total Income," + getTotalIncome() + ",\n");
            w.write("Expenses,Total Expenses," + getTotalExpenses() + ",\n");
            w.write("Net,Net Monthly," + getNetMonthly() + ",\n");

            // List of all budget items
            w.write("\nDetails,Category,Amount,Type\n");
            for (BudgetItem item : items) {
                w.write("Item," +
                        csvEscape(item.getCategory()) + "," +
                        item.getAmount() + "," +
                        item.getType() +
                        "\n");
            }

            // Credit card results
            w.write("\nCredit Card,Balance," + ccBalance + ",\n");
            w.write("Credit Card,APR (%)," + ccApr + ",\n");
            w.write("Credit Card,Payment," + ccPayment + ",\n");

            if (cc.isPayoffPossible()) {
                w.write("Credit Card,Months to Payoff," + cc.getMonthsToPayoff() + ",\n");
                w.write("Credit Card,Total Interest," + cc.getTotalInterest() + ",\n");
            } else {
                w.write("Credit Card,Payoff Possible,No,Payment too low\n");
                w.write("Credit Card,One-Month Projected Balance," + cc.getFinalBalance() + ",\n");
            }

            // Student loan summary
            w.write("\nStudent Loan,Principal," + loanPrincipal + ",\n");
            w.write("Student Loan,APR (%)," + loanApr + ",\n");
            w.write("Student Loan,Term (Years)," + loanYears + ",\n");
            w.write("Student Loan,Monthly Payment," + round(loanMonthly) + ",\n");
        }

        return file;
    }

    // Rounds numbers to two decimal places
    private double round(double v) {
        return new BigDecimal("" + v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
