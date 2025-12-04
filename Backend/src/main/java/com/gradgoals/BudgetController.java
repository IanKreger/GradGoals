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
            // If no ID provided, treat them as a "guest"
            return userBudgets.computeIfAbsent("guest", k -> new BudgetToolCode());
        }
        // Get existing budget or make a new one for this user
        return userBudgets.computeIfAbsent(userId, k -> new BudgetToolCode());
    }

    // Returns all budget items for a given user
    @GetMapping("/items")
    public List<Map<String, Object>> getItems(@RequestParam String userId) {

        // Load user's budget data
        BudgetToolCode budget = getBudget(userId);

        // Convert Java objects into simple JSON-friendly Maps
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

        // Identify user who is adding the item
        String userId = (String) body.get("userId");
        BudgetToolCode budget = getBudget(userId);

        // Extract item details from request
        String category = (String) body.get("category");
        double amount = Double.parseDouble(body.get("amount").toString());
        String type = (String) body.get("type");

        // Save item
        budget.addItem(category, amount, type);

        return "Item added!";
    }

    // Deletes a specific item by ID for a user
    @DeleteMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id, @RequestParam String userId) {

        // Load correct user's budget
        BudgetToolCode budget = getBudget(userId);

        // Remove item
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

        // Temporary instance since we aren't saving these results
        BudgetToolCode tempBudget = new BudgetToolCode();

        // Extract numbers from request
        double balance = ((Number) body.get("balance")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        double payment = ((Number) body.get("payment")).doubleValue();

        // Run calculation
        BudgetToolCode.CreditCardResult result =
                tempBudget.simulateCreditCardPayoff(balance, apr, payment);

        // Build response JSON
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

        // Extract request numbers
        double principal = ((Number) body.get("principal")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        int years = ((Number) body.get("years")).intValue();

        // Compute monthly payment
        double monthly = tempBudget.studentLoanMonthlyPayment(principal, apr, years);

        Map<String, Object> response = new HashMap<>();
        response.put("monthlyPayment", monthly);

        return response;
    }

    // Exports the user's budget data as a CSV file
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(@RequestParam String userId) throws Exception {

        // Load user’s budget
        BudgetToolCode budget = getBudget(userId);

        // Credit card and loan values are dummy here because the tool doesn't store history
        var cc = budget.simulateCreditCardPayoff(0, 0, 1);
        double loanMonthly = budget.studentLoanMonthlyPayment(0, 0, 1);

        // Generate CSV and get its path
        Path csvFile = budget.exportCsv(
                0, 0, 1, cc,
                0, 0, 1, loanMonthly
        );

        // Read file into byte array
        byte[] data = Files.readAllBytes(csvFile);

        // Return the file as a downloadable response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=budget_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
