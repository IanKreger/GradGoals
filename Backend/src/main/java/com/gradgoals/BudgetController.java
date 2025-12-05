package com.gradgoals;

// Basic Spring imports for handling REST APIs
import org.springframework.http.HttpHeaders;    
import org.springframework.http.MediaType;       
import org.springframework.http.ResponseEntity;   
import org.springframework.web.bind.annotation.*; 

// Java utilities and file handling
import java.nio.file.Files;       
import java.nio.file.Path;        
import java.util.*;              
import java.util.concurrent.ConcurrentHashMap; 

@RestController                         // Marks this Java class as a REST API
@RequestMapping("/budget")              // Every endpoint begins with /budget
@CrossOrigin(origins = "*")             // Allows calls from any frontend domain
public class BudgetController {

    // Stores each user's budget separately
    // Key = userId, Value = that user's BudgetToolCode
    private final Map<String, BudgetToolCode> userBudgets = new ConcurrentHashMap<>();

    // Retrieves a user's budget; creates one if it doesn't exist yet
    private BudgetToolCode getBudget(String userId) {
        if (userId == null || userId.isEmpty()) {
            return userBudgets.computeIfAbsent("guest", k -> new BudgetToolCode());
        }
        return userBudgets.computeIfAbsent(userId, k -> new BudgetToolCode());
    }

    // Returns all budget items for a given user
    @GetMapping("/items")
    public List<Map<String, Object>> getItems(@RequestParam String userId) {

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

    // Adds a new item to the user's budget
    @PostMapping("/add-item")
    public String addItem(@RequestBody Map<String, Object> body) {

        String userId = (String) body.get("userId");
        BudgetToolCode budget = getBudget(userId);

        String category = (String) body.get("category");
        double amount = Double.parseDouble(body.get("amount").toString());
        String type = (String) body.get("type");

        budget.addItem(category, amount, type);

        return "Item added!";
    }

    // Deletes a specific item by ID for a user
    @DeleteMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id, @RequestParam String userId) {

        BudgetToolCode budget = getBudget(userId);
        budget.removeItem(id);

        return "Deleted!";
    }

    // Returns a summary: total income, total expenses, net monthly difference
    @GetMapping("/summary")
    public Map<String, Double> summary(@RequestParam String userId) {

        BudgetToolCode budget = getBudget(userId);

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", budget.getTotalIncome());
        summary.put("expenses", budget.getTotalExpenses());
        summary.put("net", budget.getNetMonthly());

        return summary;
    }

    // Calculates credit card payoff timeline (not stored — just calculated)
    @PostMapping("/credit-card")
    public Map<String, Object> creditCard(@RequestBody Map<String, Object> body) {

        BudgetToolCode tempBudget = new BudgetToolCode();

        double balance = ((Number) body.get("balance")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        double payment = ((Number) body.get("payment")).doubleValue();

        BudgetToolCode.CreditCardResult result =
                tempBudget.simulateCreditCardPayoff(balance, apr, payment);

        Map<String, Object> response = new HashMap<>();
        response.put("payoffPossible", result.isPayoffPossible());
        response.put("months", result.getMonthsToPayoff());
        response.put("totalInterest", result.getTotalInterest());
        response.put("finalBalance", result.getFinalBalance());

        return response;
    }

    // Calculates student loan monthly payment (also not stored)
    @PostMapping("/student-loan")
    public Map<String, Object> studentLoan(@RequestBody Map<String, Object> body) {

        BudgetToolCode tempBudget = new BudgetToolCode();

        double principal = ((Number) body.get("principal")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        int years = ((Number) body.get("years")).intValue();

        double monthly = tempBudget.studentLoanMonthlyPayment(principal, apr, years);

        Map<String, Object> response = new HashMap<>();
        response.put("monthlyPayment", monthly);

        return response;
    }

    // Exports the user's budget data as a CSV file (credit card / loan removed)
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(@RequestParam String userId) throws Exception {

        BudgetToolCode budget = getBudget(userId);

        // Generate CSV and get its path
        Path csvFile = budget.exportCsv();

        // Read file into byte array
        byte[] data = Files.readAllBytes(csvFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=budget_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
