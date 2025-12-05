package com.gradgoals;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final Map<String, BudgetToolCode> userBudgets = new ConcurrentHashMap<>();

    private BudgetToolCode getBudget(String userId) {
        if (userId == null || userId.isEmpty()) {
            return userBudgets.computeIfAbsent("guest", k -> new BudgetToolCode());
        }
        return userBudgets.computeIfAbsent(userId, k -> new BudgetToolCode());
    }

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

    @DeleteMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id, @RequestParam String userId) {
        BudgetToolCode budget = getBudget(userId);
        budget.removeItem(id);
        return "Deleted!";
    }

    @GetMapping("/summary")
    public Map<String, Double> summary(@RequestParam String userId) {
        BudgetToolCode budget = getBudget(userId);

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", budget.getTotalIncome());
        summary.put("expenses", budget.getTotalExpenses());
        summary.put("net", budget.getNetMonthly());

        return summary;
    }

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

    // ⭐ FIXED: EXPORT NOW USES REAL VALUES, NOT ZEROES
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportCsv(@RequestBody Map<String, Object> body) throws Exception {

        String userId = (String) body.get("userId");
        BudgetToolCode budget = getBudget(userId);

        double ccBalance = ((Number) body.get("ccBalance")).doubleValue();
        double ccApr = ((Number) body.get("ccApr")).doubleValue();
        double ccPayment = ((Number) body.get("ccPayment")).doubleValue();

        BudgetToolCode.CreditCardResult cc =
                budget.simulateCreditCardPayoff(ccBalance, ccApr, ccPayment);

        double loanPrincipal = ((Number) body.get("loanPrincipal")).doubleValue();
        double loanApr = ((Number) body.get("loanApr")).doubleValue();
        int loanYears = ((Number) body.get("loanYears")).intValue();

        double loanMonthly =
                budget.studentLoanMonthlyPayment(loanPrincipal, loanApr, loanYears);

        Path csvFile = budget.exportCsv(
                ccBalance, ccApr, ccPayment, cc,
                loanPrincipal, loanApr, loanYears, loanMonthly
        );

        byte[] data = Files.readAllBytes(csvFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=budget_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}

