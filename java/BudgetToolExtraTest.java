import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BudgetToolExtraTest {

    // 1. Credit card payoff - zero APR
    @Test
    void ccPayoff_zeroApr_paysOffExactly() {
        // Arrange
        double balance = 1000;
        double apr = 0.0;
        double payment = 100;

        // Act
        BudgetTool.CreditCardResult r = BudgetTool.simulateCreditCardPayoff(balance, apr, payment);

        // Assert
        assertTrue(r.payoffPossible);
        assertEquals(10, r.monthsToPayoff);
        assertEquals(0.0, r.totalInterest, 1e-9);
    }

    // 2. Credit card payoff - high APR and low payment (should fail)
    @Test
    void ccPayoff_highAprLowPayment_notPossible() {
        // Arrange
        double balance = 1000;
        double apr = 30;
        double payment = 15;

        // Act
        BudgetTool.CreditCardResult r = BudgetTool.simulateCreditCardPayoff(balance, apr, payment);

        // Assert
        assertFalse(r.payoffPossible);
        assertTrue(r.finalBalance > balance);
    }

    // 3. Loan monthly payment - 1 year term
    @Test
    void loanMonthly_oneYearLoan_reasonablePayment() {
        // Arrange
        double principal = 1200;
        double apr = 12;
        int years = 1;

        // Act
        double payment = BudgetTool.studentLoanMonthlyPayment(principal, apr, years);

        // Assert
        assertTrue(payment > 0);
        assertTrue(payment < principal);
    }

    // 4. Loan monthly payment - very long term
    @Test
    void loanMonthly_longTerm_smallerPayment() {
        // Arrange
        double principal = 10000;
        double apr = 5;
        int years = 30;

        // Act
        double payment = BudgetTool.studentLoanMonthlyPayment(principal, apr, years);

        // Assert
        assertTrue(payment < 600); // should be small monthly payment
    }

    // 5. csvEscape - newlines handled
    @Test
    void csvEscape_newlines_areQuoted() {
        // Arrange
        String input = "Line1\nLine2";

        // Act
        String escaped = BudgetTool.csvEscape(input);

        // Assert
        assertTrue(escaped.startsWith("\"") && escaped.endsWith("\""));
    }

    // 6. csvEscape - commas and quotes mixed
    @Test
    void csvEscape_commasAndQuotes_mixedHandled() {
        // Arrange
        String input = "Item, \"Special\"";

        // Act
        String escaped = BudgetTool.csvEscape(input);

        // Assert
        assertTrue(escaped.contains("\"\""));
        assertTrue(escaped.startsWith("\""));
    }

    // 7. round2 - negative number
    @Test
    void round2_negativeNumber_roundsCorrectly() {
    // Arrange
    double value = -12.345;

    // Act
    double result = BudgetTool.round2(value);

    // Assert
    assertEquals(-12.35, result, 1e-9); // -12.345 → -12.35 (HALF_UP)
}

    // 8. exportCsv - creates file successfully
    @Test
    void exportCsv_createsFile(@TempDir Path tmp) throws IOException {
        // Arrange
        double income = 5000;
        List<BudgetTool.Expense> expenses = List.of(
                new BudgetTool.Expense("Rent", 2000),
                new BudgetTool.Expense("Food", 400)
        );
        double net = 2600;
        double ccBalance = 1000, ccApr = 20, ccPayment = 100;
        BudgetTool.CreditCardResult cc = BudgetTool.simulateCreditCardPayoff(ccBalance, ccApr, ccPayment);
        double loanPrincipal = 10000, loanApr = 5; int years = 10;
        double loanMonthly = BudgetTool.studentLoanMonthlyPayment(loanPrincipal, loanApr, years);

        // Act
        Path file = BudgetTool.exportCsv(income, expenses, net,
                ccBalance, ccApr, ccPayment, cc,
                loanPrincipal, loanApr, years, loanMonthly);

        // Assert
        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.contains("Income"));
        assertTrue(content.contains("Student Loan"));
    }

    // 9. round2 - boundary case .005
    @Test
    void round2_boundaryCase_roundsUp() {
    // Arrange
    double value = 1.005;

    // Act
    double result = BudgetTool.round2(value);

    // Assert
    assertEquals(1.01, result, 1e-9); // 1.005 → 1.01 (HALF_UP)
}

    // 10. simulateCreditCardPayoff - small balance small payment
    @Test
    void ccPayoff_smallBalance_roundsToFewMonths() {
        // Arrange
        double balance = 50;
        double apr = 10;
        double payment = 25;

        // Act
        BudgetTool.CreditCardResult r = BudgetTool.simulateCreditCardPayoff(balance, apr, payment);

        // Assert
        assertTrue(r.payoffPossible);
        assertTrue(r.monthsToPayoff <= 3);
    }
}
