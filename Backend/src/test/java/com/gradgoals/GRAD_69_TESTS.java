package com.gradgoals;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class BudgetToolCodeTest69 {

    private BudgetToolCode69 tool;

    @BeforeEach
    void setup() {
        tool = new BudgetToolCode69();
    }

    // 1 Add item with invalid type should throw
    @Test
    void testAddItemInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            tool.addItem("WeirdCategory", 100, "invalidType");
        });
    }

    // 2 Total income rounds correctly
    @Test
    void testTotalIncomeRounding() {
        tool.addItem("Salary", 1000.555, "income");
        tool.addItem("Bonus", 200.444, "income");
        assertEquals(1200.999, tool.getTotalIncome(), 0.001);
    }

    // 3 Net monthly calculation with negative result
    @Test
    void testNetMonthlyNegative() {
        tool.addItem("Salary", 1000, "income");
        tool.addItem("Rent", 1200, "expense");
        assertEquals(-200.0, tool.getNetMonthly());
    }

    // 4 Credit card payoff impossible if payment < first month interest
    @Test
    void testCreditCardImpossible() {
        BudgetToolCode.CreditCardResult res = tool.simulateCreditCardPayoff(1000, 12, 5);
        assertFalse(res.isPayoffPossible());
        assertTrue(res.getFinalBalance() > 1000);
    }

    // 5 Credit card payoff possible and months > 0
    @Test
    void testCreditCardPayoffSuccess() {
        BudgetToolCode.CreditCardResult res = tool.simulateCreditCardPayoff(1000, 12, 200);
        assertTrue(res.isPayoffPossible());
        assertTrue(res.getMonthsToPayoff() > 0);
        assertEquals(0.0, res.getFinalBalance());
    }

    // 6 Student loan zero principal returns 0
    @Test
    void testStudentLoanZeroPrincipal() {
        double payment = tool.studentLoanMonthlyPayment(0, 5, 10);
        assertEquals(0, payment);
    }

    // 7 Student loan zero APR returns principal/n
    @Test
    void testStudentLoanZeroAPR() {
        double payment = tool.studentLoanMonthlyPayment(12000, 0, 10); // 120 months
        assertEquals(100, payment);
    }

    @Test
    void testExportCsvContents() throws IOException {
    tool.addItem("Salary", 1200, "income");
    tool.addItem("Rent", 800, "expense");
    tool.addItem("Groceries", 300, "expense");

    BudgetToolCode.CreditCardResult ccRes = tool.simulateCreditCardPayoff(500, 12, 200);
    double loanMonthly = tool.studentLoanMonthlyPayment(10000, 5, 5);

    Path file = tool.exportCsv(500, 12, 200, ccRes, 10000, 5, 5, loanMonthly);

    String content = Files.readString(file);

    // Check that totals appear correctly
    assertTrue(content.contains("Total Income,1200.0"));
    assertTrue(content.contains("Total Expenses,1100.0"));
    assertTrue(content.contains("Net Monthly,100.0"));

    // Check that individual items appear
    assertTrue(content.contains("Salary,1200.0,income"));
    assertTrue(content.contains("Rent,800.0,expense"));
    assertTrue(content.contains("Groceries,300.0,expense"));

    // Check that credit card info is included
    if (ccRes.isPayoffPossible()) {
        assertTrue(content.contains("Months to Payoff," + ccRes.getMonthsToPayoff()));
        assertTrue(content.contains("Total Interest," + ccRes.getTotalInterest()));
    } else {
        assertTrue(content.contains("Payoff Possible,No"));
    }

    // Check that student loan info is included
    assertTrue(content.contains("Principal,10000.0"));
    assertTrue(content.contains("Monthly Payment," + Math.round(loanMonthly * 100.0) / 100.0));

    // Clean up
    Files.deleteIfExists(file);
    }
    // 9 Removing an item decreases list size
    @Test
    void testRemoveItemReducesSize() {
        tool.addItem("Salary", 1000, "income");
        tool.addItem("Rent", 500, "expense");
        String idToRemove = tool.getAllItems().get(0).getId();
        tool.removeItem(idToRemove);
        assertEquals(1, tool.getAllItems().size());
    }

    // 10 CSV escaping handles quotes and commas
    @Test
    void testCsvEscaping() throws IOException {
        tool.addItem("Food, Snacks", 200, "expense");
        BudgetToolCode.CreditCardResult ccRes = tool.simulateCreditCardPayoff(0, 0, 0);
        double loanMonthly = tool.studentLoanMonthlyPayment(0, 0, 0);
        Path file = tool.exportCsv(0, 0, 0, ccRes, 0, 0, 0, loanMonthly);

        String content = Files.readString(file);
        assertTrue(content.contains("\"Food, Snacks\""));

        Files.deleteIfExists(file);
    }
}
