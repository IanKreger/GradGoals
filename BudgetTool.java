import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BudgetTool - single-file Java app (no external dependencies).
 * Exports an Excel-friendly CSV with your budget, credit card payoff, and student loan info.
 */
public class BudgetTool {

    // ----- Small model types -----
    static class Expense {
        String name;
        double amount; // monthly
        Expense(String name, double amount) { this.name = name; this.amount = amount; }
    }

    static class CreditCardResult {
        boolean payoffPossible;
        int monthsToPayoff;
        double totalInterest;
        double finalBalance; // if not possible, where it trends after one month (for info)
    }

    // ----- Input helpers -----
    private static final Scanner sc = new Scanner(System.in);

    static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(s);
                if (v >= 0) return v;
                System.out.println("Please enter a non-negative number.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static int readNonNegativeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v >= 0) return v;
                System.out.println("Please enter 0 or a positive integer.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Please enter something.");
        }
    }

    // ----- Credit card payoff simulation -----
    // balance: current balance ($)
    // apr: annual percentage rate (e.g., 24 for 24%)
    // payment: planned fixed monthly payment
    static CreditCardResult simulateCreditCardPayoff(double balance, double apr, double payment) {
        CreditCardResult r = new CreditCardResult();
        if (balance <= 0) {
            r.payoffPossible = true;
            r.monthsToPayoff = 0;
            r.totalInterest = 0;
            r.finalBalance = 0;
            return r;
        }

        double monthlyRate = apr / 100.0 / 12.0;
        // If payment is not greater than first month's interest, balance will never go down.
        double firstInterest = balance * monthlyRate;
        if (payment <= firstInterest && monthlyRate > 0) {
            r.payoffPossible = false;
            r.finalBalance = balance + firstInterest - payment; // shows that it grows or stays flat
            return r;
        }

        double b = balance;
        double totalInterest = 0.0;
        int months = 0;
        // Safety cap to avoid infinite loops in weird inputs
        int MAX_MONTHS = 3600; // 300 years is plenty!
        while (b > 0 && months < MAX_MONTHS) {
            double interest = b * monthlyRate;
            totalInterest += interest;
            double principal = payment - interest;
            if (principal <= 0) { // fallback guard
                r.payoffPossible = false;
                r.finalBalance = b + interest - payment;
                return r;
            }
            b -= principal;
            months++;
            // If we cross below zero, adjust final month to exact payoff (no negative balance).
            if (b < 0) {
                // Recompute last month precisely so we don't over-count interest
                // (Optional refinement; current approach is close enough for planning.)
                b = 0;
            }
        }
        r.payoffPossible = (b <= 0);
        r.monthsToPayoff = months;
        r.totalInterest = totalInterest;
        r.finalBalance = b;
        return r;
    }

    // ----- Student loan monthly payment (amortization) -----
    // principal ($), apr (%), termYears
    static double studentLoanMonthlyPayment(double principal, double apr, int termYears) {
        if (principal <= 0) return 0.0;
        int n = termYears * 12;
        double r = apr / 100.0 / 12.0; // monthly rate
        if (r == 0) {
            return principal / n;
        }
        return principal * (r) / (1 - Math.pow(1 + r, -n));
    }

    // ----- CSV export -----
    static String csvEscape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String t = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + t + "\"" : t;
    }

    static Path exportCsv(
            double income,
            List<Expense> expenses,
            double net,
            double ccBalance, double ccApr, double ccPayment, CreditCardResult cc,
            double loanPrincipal, double loanApr, int loanYears, double loanMonthly
    ) throws IOException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path out = Path.of("budget_report_" + ts + ".csv");
        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            // Header
            w.write("Section,Item,Value,Notes\n");

            // Income
            w.write("Income,Monthly Income," + income + ",\n");

            // Expenses
            w.write("Expenses,Count," + expenses.size() + ",\n");
            for (Expense e : expenses) {
                w.write("Expenses," + csvEscape(e.name) + "," + e.amount + ",Monthly\n");
            }

            // Totals
            double totalExpenses = expenses.stream().mapToDouble(ex -> ex.amount).sum();
            w.write("Totals,Total Monthly Expenses," + totalExpenses + ",\n");
            w.write("Totals,Net Monthly (Income - Expenses)," + net + ",\n");

            // Credit Card
            w.write("Credit Card,Starting Balance," + ccBalance + ",\n");
            w.write("Credit Card,APR (%)," + ccApr + ",\n");
            w.write("Credit Card,Planned Monthly Payment," + ccPayment + ",\n");
            if (cc.payoffPossible) {
                w.write("Credit Card,Months to Payoff," + cc.monthsToPayoff + ",\n");
                w.write("Credit Card,Total Interest," + round2(cc.totalInterest) + ",\n");
            } else {
                w.write("Credit Card,Payoff Possible,No,Increase payment or lower APR\n");
                w.write("Credit Card,One-Month Projection Balance," + round2(cc.finalBalance) + ",Payment too low\n");
            }

            // Student Loan
            w.write("Student Loan,Principal," + loanPrincipal + ",\n");
            w.write("Student Loan,APR (%)," + loanApr + ",\n");
            w.write("Student Loan,Term (Years)," + loanYears + ",\n");
            w.write("Student Loan,Estimated Monthly Payment," + round2(loanMonthly) + ",Standard amortization\n");
        }
        return out;
    }

   static double round2(double v) {
    return new BigDecimal(Double.toString(v))
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
}

    }


    // ----- Main flow -----
    public static void main(String[] args) {
        System.out.println("=== GradGoals Budget Tool (Single-File Java) ===");
        System.out.println("All amounts are monthly unless noted. Press Enter after each input.");
        System.out.println();

        double income = readPositiveDouble("Monthly income ($): ");

        int nExpenses = readNonNegativeInt("How many monthly expense items do you want to enter? ");
        List<Expense> expenses = new ArrayList<>();
        for (int i = 1; i <= nExpenses; i++) {
            System.out.println("Expense " + i + " of " + nExpenses + ":");
            String name = readNonEmpty("  Name (e.g., Rent, Groceries): ");
            double amount = readPositiveDouble("  Amount per month ($): ");
            expenses.add(new Expense(name, amount));
        }

        double totalExpenses = expenses.stream().mapToDouble(e -> e.amount).sum();
        double net = income - totalExpenses;

        System.out.println();
        System.out.println("--- Credit Card Payoff Estimator ---");
        double ccBalance = readPositiveDouble("Current credit card balance ($): ");
        double ccApr = readPositiveDouble("Credit card APR (% per year): ");
        double ccPayment = readPositiveDouble("Planned fixed monthly payment ($): ");
        CreditCardResult cc = simulateCreditCardPayoff(ccBalance, ccApr, ccPayment);

        System.out.println();
        System.out.println("--- Student Loan Monthly Payment ---");
        double loanPrincipal = readPositiveDouble("Student loan principal ($): ");
        double loanApr = readPositiveDouble("Student loan APR (% per year): ");
        int loanYears = readNonNegativeInt("Repayment term (years): ");
        double loanMonthly = studentLoanMonthlyPayment(loanPrincipal, loanApr, loanYears);

        // Print a simple summary
        System.out.println();
        System.out.println("========== SUMMARY ==========");
        System.out.printf("Income: $%.2f /mo%n", income);
        System.out.printf("Total Expenses: $%.2f /mo%n", totalExpenses);
        System.out.printf("Net (Income - Expenses): $%.2f /mo%n", net);

        System.out.println();
        System.out.println("Credit Card:");
        System.out.printf("  Balance: $%.2f | APR: %.2f%% | Payment: $%.2f%n", ccBalance, ccApr, ccPayment);
        if (cc.payoffPossible) {
            System.out.printf("  Months to payoff: %d | Total interest: $%.2f%n", cc.monthsToPayoff, cc.totalInterest);
        } else {
            System.out.println("  Payment too low to reduce balance. Increase payment or reduce APR.");
        }

        System.out.println();
        System.out.println("Student Loan:");
        System.out.printf("  Principal: $%.2f | APR: %.2f%% | Term: %d years%n", loanPrincipal, loanApr, loanYears);
        System.out.printf("  Estimated monthly payment: $%.2f%n", loanMonthly);

        // Export CSV
        try {
            Path out = exportCsv(income, expenses, net, ccBalance, ccApr, ccPayment, cc,
                                 loanPrincipal, loanApr, loanYears, loanMonthly);
            System.out.println();
            System.out.println("CSV exported: " + out.toAbsolutePath());
            System.out.println("Open the CSV in Excel to view and share.");
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }

        System.out.println("====================================");
        System.out.println("Done. Thanks for using the GradGoals Budget Tool!");
    }
}
