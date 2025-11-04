import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Test_BudgetTool_Zoe {

    @Test
    void testAddIncome() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Job", 1000, "income");
        assertEquals(1000, tool.getTotalIncome());
    }

    @Test
    void testAddExpense() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Rent", 500, "expense");
        assertEquals(500, tool.getTotalExpenses());
    }

    @Test
    void testBalanceAfterAdding() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Job", 1000, "income");
        tool.addItem("Food", 200, "expense");
        assertEquals(800, tool.getRemainingBalance());
    }

    @Test
    void testRemoveEntryById() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Job", 1000, "income");
        tool.addItem("Rent", 500, "expense");

        // grab the ID of the expense item
        String idToRemove = tool.getAllItems().get(1).id;

        // remove by ID and check
        tool.removeItemById(idToRemove);
        assertEquals(0, tool.getTotalExpenses());
    }

    @Test
    void testMultipleIncomes() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Job", 1000, "income");
        tool.addItem("Freelance", 200, "income");
        assertEquals(1200, tool.getTotalIncome());
    }

    @Test
    void testMultipleExpenses() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Rent", 600, "expense");
        tool.addItem("Groceries", 200, "expense");
        assertEquals(800, tool.getTotalExpenses());
    }

    @Test
    void testEmptyTotals() {
        BudgetTool tool = new BudgetTool();
        assertEquals(0, tool.getTotalIncome());
        assertEquals(0, tool.getTotalExpenses());
        assertEquals(0, tool.getRemainingBalance());
    }

    @Test
    void testInvalidType() {
        BudgetTool tool = new BudgetTool();
        assertThrows(IllegalArgumentException.class, () -> {
            tool.addItem("Gift", 100, "bonus");
        });
    }

    @Test
    void testRemoveNonexistentId() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Job", 1000, "income");
        tool.removeItemById("fake-id-123");
        assertEquals(1000, tool.getTotalIncome());
    }

    @Test
    void testBalanceAllExpenses() {
        BudgetTool tool = new BudgetTool();
        tool.addItem("Rent", 500, "expense");
        tool.addItem("Groceries", 200, "expense");
        assertEquals(-700, tool.getRemainingBalance());
    }
}