package com.gradgoals;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BudgetToolCodeTest {

    @Test
    void testAddIncomeItem() {
        BudgetToolCode tool = new BudgetToolCode();
        tool.addItem("Job", 3000, "income");

        assertEquals(3000, tool.getTotalIncome());
        assertEquals(0, tool.getTotalExpenses());
    }

    @Test
    void testAddExpenseItem() {
        BudgetToolCode tool = new BudgetToolCode();
        tool.addItem("Rent", 1200, "expense");

        assertEquals(1200, tool.getTotalExpenses());
        assertEquals(0, tool.getTotalIncome());
    }

    @Test
    void testInvalidTypeThrowsException() {
        BudgetToolCode tool = new BudgetToolCode();
        assertThrows(IllegalArgumentException.class, () ->
                tool.addItem("Food", 100, "wrongType"));
    }

    @Test
    void testRemoveItem() {
        BudgetToolCode tool = new BudgetToolCode();
        tool.addItem("Food", 400, "expense");

        List<BudgetToolCode.BudgetItem> items = tool.getAllItems();
        String id = items.get(0).getId();

        tool.removeItem(id);

        assertTrue(tool.getAllItems().isEmpty());
    }

    @Test
    void testNetMonthlyCalculation() {
        BudgetToolCode tool = new BudgetToolCode();
        tool.addItem("Job", 3000, "income");
        tool.addItem("Rent", 1000, "expense");
        tool.addItem("Food", 500, "expense");

        assertEquals(1500.00, tool.getNetMonthly());
    }

    @Test
    void testCreditCardPayoffPossible() {
        BudgetToolCode tool = new BudgetToolCode();
        var result = tool.simulateCreditCardPayoff(1000, 12, 200);

        assertTrue(result.isPayoffPossible());
        assertTrue(result.getMonthsToPayoff() > 0);
        assertEquals(0.00, result.getFinalBalance());
    }

    @Test
    void testCreditCardPayoffImpossible() {
        BudgetToolCode tool = new BudgetToolCode();
        var result = tool.simulateCreditCardPayoff(1000, 20, 5);

        assertFalse(result.isPayoffPossible());
        assertTrue(result.getFinalBalance() > 1000);
    }

    @Test
    void testStudentLoanZeroInterest() {
        BudgetToolCode tool = new BudgetToolCode();
        double monthly = tool.studentLoanMonthlyPayment(12000, 0, 10);

        assertEquals(100, monthly, 0.0001);
    }

    @Test
    void testStudentLoanPositiveInterest() {
        BudgetToolCode tool = new BudgetToolCode();
        double monthly = tool.studentLoanMonthlyPayment(10000, 5, 10);

        assertTrue(monthly > 80);
        assertTrue(monthly < 120);
    }

    @Test
    void testCsvExportFileCreated() throws IOException {
        BudgetToolCode tool = new BudgetToolCode();

        tool.addItem("Job", 3000, "income");
        tool.addItem("Rent", 1200, "expense");

        var cc = tool.simulateCreditCardPayoff(500, 10, 100);
        double loan = tool.studentLoanMonthlyPayment(5000, 5, 10);

        Path file = tool.exportCsv(500, 10, 100, cc, 5000, 5, 10, loan);

        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 0);

        Files.deleteIfExists(file);
    }
}
