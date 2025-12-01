package com.gradgoals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetToolCode1 {

    // ---------- MODEL CLASSES ----------
    public class BudgetItem {
        private String id;
        private String category;
        private double amount;
        private String type;  // "income" or "expense"

        public BudgetItem(String category, double amount, String type) {
            this.id = UUID.randomUUID().toString();
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
        private double amount; // monthly

        public Expense(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() { return name; }
        public double getAmount() { return amount; }
    }

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

    // ---------- DATA STORAGE ----------
    private final List<BudgetItem> items = new ArrayList<>();

    public void addItem(String category, double amount, String type) {
        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            throw new IllegalArgumentException("Type must be 'income' or 'expense'.");
        }
        items.add(new BudgetItem(category, amount, type));
    }

    public void removeItem(String id) {
        items.removeIf(i -> i.getId().equals(id));
    }

    public List<BudgetItem> getAllItems() {
        return new ArrayList<>(items);
    }

    public double getTotalIncome() {
        return items.stream()
                .filter(i -> i.getType().equals("income"))
                .mapToDouble(BudgetItem::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return items.stream()
                .filter(i -> i.getType().equals("expense"))
                .mapToDouble(BudgetItem::getAmount)
                .sum();
    }

    public double getNetMonthly() {
        return round(getTotalIncome() - getTotalExpenses());
    }

    // ---------- CREDIT CARD PAYOFF ----------
    public CreditCardResult simulateCreditCardPayoff(double balance, double apr, double payment) {

        double monthlyRate = apr / 100.0 / 12.0;

        // If payment won't even cover one month's interest → impossible
        double firstInterest = balance * monthlyRate;
        if (payment <= firstInterest && monthlyRate > 0) {
            double nextBalance = balance + firstInterest - payment;
            return new CreditCardResult(false, 0, 0, round(nextBalance));
        }

        double b = balance;
        double totalInterest = 0;
        int months = 0;
        final int MAX_MONTHS = 3600;

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

    // ---------- STUDENT LOAN CALCULATION ----------
    public double studentLoanMonthlyPayment(double principal, double apr, int years) {
        if (principal <= 0) return 0;

        int n = years * 12;
        double r = apr / 100.0 / 12.0;

        if (r == 0) return principal / n;

        return principal * r / (1 - Math.pow(1 + r, -n));
    }

    // ---------- CSV EXPORT ----------
    private String csvEscape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n");
        String escaped = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }

    public Path exportCsv(
            double ccBalance, double ccApr, double ccPayment, CreditCardResult cc,
            double loanPrincipal, double loanApr, int loanYears, double loanMonthly
    ) throws IOException {

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path file = Path.of("budget_export_" + ts + ".csv");

        try (BufferedWriter w = Files.newBufferedWriter(file)) {

            // ---- BUDGET SUMMARY ----
            w.write("Section,Item,Value,Notes\n");

            w.write("Income,Total Income," + getTotalIncome() + ",\n");
            w.write("Expenses,Total Expenses," + getTotalExpenses() + ",\n");
            w.write("Net,Net Monthly," + getNetMonthly() + ",\n");

            // ---- ITEM DETAILS ----
            w.write("\nDetails,Category,Amount,Type\n");
            for (BudgetItem item : items) {
                w.write("Item," +
                        csvEscape(item.getCategory()) + "," +
                        item.getAmount() + "," +
                        item.getType() +
                        "\n");
            }

            // ---- CREDIT CARD ----
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

            // ---- STUDENT LOAN ----
            w.write("\nStudent Loan,Principal," + loanPrincipal + ",\n");
            w.write("Student Loan,APR (%)," + loanApr + ",\n");
            w.write("Student Loan,Term (Years)," + loanYears + ",\n");
            w.write("Student Loan,Monthly Payment," + round(loanMonthly) + ",\n");
        }

        return file;
    }

    // ---------- UTILITY ----------
    private double round(double v) {
        return new BigDecimal("" + v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
