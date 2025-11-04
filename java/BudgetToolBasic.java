// basic budgeting tool
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BudgetToolBasic {

    // represents income or expense entry
    static class BudgetItem {
        String id;
        String category;
        double amount;
        String type; 

        BudgetItem(String category, double amount, String type) {
            this.id = UUID.randomUUID().toString(); // unique ID for each item
            this.category = category;
            this.amount = amount;
            this.type = type;
        }
    }

    private final List<BudgetItem> items = new ArrayList<>();

    // Add a new income or expense entry
    public void addItem(String category, double amount, String type) {
        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            throw new IllegalArgumentException("Type must be 'income' or 'expense'.");
        }
        items.add(new BudgetItem(category, amount, type));
    }

    // delete an item by its unique ID
    public void removeItemById(String id) {
        items.removeIf(item -> item.id.equals(id));
    }

    // calculate total income
    public double getTotalIncome() {
        double total = 0;
        for (BudgetItem item : items) {
            if (item.type.equalsIgnoreCase("income")) {
                total += item.amount;
            }
        }
        return total;
    }

    // calculate total expenses
    public double getTotalExpenses() {
        double total = 0;
        for (BudgetItem item : items) {
            if (item.type.equalsIgnoreCase("expense")) {
                total += item.amount;
            }
        }
        return total;
    }

    // calculate remaining balance 
    public double getRemainingBalance() {
        return getTotalIncome() - getTotalExpenses();
    }

    // get all items for when we sync up with front end 
    public List<BudgetItem> getAllItems() {
        return new ArrayList<>(items);
    }
}
