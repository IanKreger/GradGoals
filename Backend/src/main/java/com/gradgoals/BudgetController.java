package com.gradgoals;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // Import for thread-safe map

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = "*")
public class BudgetController {

    // CHANGED: Instead of one budget, we have a Map of them.
    // Key = UserID, Value = That user's BudgetToolCode object
    private static final Map<String, BudgetToolCode> userBudgets = new ConcurrentHashMap<>();

    // HELPER: This retrieves the specific budget for a specific user
    private BudgetToolCode getBudget(String userId) {
        if (userId == null || userId.isEmpty()) {
            // Fallback for testing if no ID is sent
            return userBudgets.computeIfAbsent("guest", k -> new BudgetToolCode());
        }
        // If user exists, return their budget. If not, create a new one.
        return userBudgets.computeIfAbsent(userId, k -> new BudgetToolCode());
    }

    // -------------------------------
    // GET ALL ITEMS
    // -------------------------------
    @GetMapping("/items")
    public List<Map<String, Object>> getItems(@RequestParam String userId) {
        // 1. Get the correct budget
        BudgetToolCode budget = getBudget(userId);
        
        List<Map<String, Object>> result = new ArrayList<>();

        for (BudgetToolCode.BudgetItem item : budget.getAllItems()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("category", item.getCategory());
            map.put("amount", item.getAmount());
            map.put("type", item.getType());
            result.add(map);
        }
        return result;
    }

    // -------------------------------
    // ADD ITEM
    // -------------------------------
    @PostMapping("/add-item")
    public String addItem(@RequestBody Map<String, Object> body) {
        // 1. Get User ID from the JSON body
        String userId = (String) body.get("userId");
        BudgetToolCode budget = getBudget(userId);

        String category = (String) body.get("category");
        double amount = Double.parseDouble(body.get("amount").toString());
        String type = (String) body.get("type");

        budget.addItem(category, amount, type);
        return "Item added!";
    }

    // -------------------------------
    // DELETE ITEM
    // -------------------------------
    @DeleteMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id, @RequestParam String userId) {
        // 1. Get the correct budget
        BudgetToolCode budget = getBudget(userId);
        
        budget.removeItem(id);
        return "Deleted!";
    }

    // -------------------------------
    // SUMMARY
    // -------------------------------
    @GetMapping("/summary")
    public Map<String, Double> summary(@RequestParam String userId) {
        BudgetToolCode budget = getBudget(userId);
        
        Map<String, Double> m = new HashMap<>();
        m.put("income", budget.getTotalIncome());
        m.put("expenses", budget.getTotalExpenses());
        m.put("net", budget.getNetMonthly());
        return m;
    }

    // -------------------------------
    // CREDIT CARD SIMULATOR
    // (Note: This is pure math, but we still use the user's instance for consistency)
    // -------------------------------
    @PostMapping("/credit-card")
    public Map<String, Object> creditCard(@RequestBody Map<String, Object> body) {
        // We can just use a temporary instance or the user's. 
        // Let's use a temp one since this doesn't save data.
        BudgetToolCode tempBudget = new BudgetToolCode();

        double balance = ((Number) body.get("balance")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        double payment = ((Number) body.get("payment")).doubleValue();

        BudgetToolCode.CreditCardResult r =
                tempBudget.simulateCreditCardPayoff(balance, apr, payment);

        Map<String, Object> res = new HashMap<>();
        res.put("payoffPossible", r.isPayoffPossible());
        res.put("months", r.getMonthsToPayoff());
        res.put("totalInterest", r.getTotalInterest());
        res.put("finalBalance", r.getFinalBalance());

        return res;
    }

    // -------------------------------
    // STUDENT LOAN CALCULATION
    // -------------------------------
    @PostMapping("/student-loan")
    public Map<String, Object> studentLoan(@RequestBody Map<String, Object> body) {
        BudgetToolCode tempBudget = new BudgetToolCode();

        double principal = ((Number) body.get("principal")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        int years = ((Number) body.get("years")).intValue();

        double monthly = tempBudget.studentLoanMonthlyPayment(principal, apr, years);

        Map<String, Object> res = new HashMap<>();
        res.put("monthlyPayment", monthly);

        return res;
    }

    // -------------------------------
    // CSV EXPORT
    // -------------------------------
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(@RequestParam String userId) throws Exception {
        // MUST get the specific user's budget to print THEIR items
        BudgetToolCode budget = getBudget(userId);

        // We pass dummy values for the loan/CC parts as they aren't stored in history
        var cc = budget.simulateCreditCardPayoff(0, 0, 1);
        double loanMonthly = budget.studentLoanMonthlyPayment(0, 0, 1);

        Path file = budget.exportCsv(
                0, 0, 1, cc,
                0, 0, 1, loanMonthly
        );

        byte[] data = Files.readAllBytes(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=budget_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}