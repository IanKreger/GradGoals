package com.gradgoals;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetToolCode1 budget = new BudgetToolCode();

    // -------------------------------
    // GET ALL ITEMS
    // -------------------------------
    @GetMapping("/items")
    public List<Map<String, Object>> getItems() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (BudgetToolCode1.BudgetItem item : budget.getAllItems()) {
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
    public String deleteItem(@PathVariable String id) {
        budget.removeItem(id);
        return "Deleted!";
    }

    // -------------------------------
    // SUMMARY
    // -------------------------------
    @GetMapping("/summary")
    public Map<String, Double> summary() {
        Map<String, Double> m = new HashMap<>();
        m.put("income", budget.getTotalIncome());
        m.put("expenses", budget.getTotalExpenses());
        m.put("net", budget.getNetMonthly());
        return m;
    }

    // -------------------------------
    // CREDIT CARD SIMULATOR
    // -------------------------------
    @PostMapping("/credit-card")
    public Map<String, Object> creditCard(@RequestBody Map<String, Object> body) {

        double balance = ((Number) body.get("balance")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        double payment = ((Number) body.get("payment")).doubleValue();

        BudgetToolCode.CreditCardResult r =
                budget.simulateCreditCardPayoff(balance, apr, payment);

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

        double principal = ((Number) body.get("principal")).doubleValue();
        double apr = ((Number) body.get("apr")).doubleValue();
        int years = ((Number) body.get("years")).intValue();

        double monthly = budget.studentLoanMonthlyPayment(principal, apr, years);

        Map<String, Object> res = new HashMap<>();
        res.put("monthlyPayment", monthly);

        return res;
    }

    // -------------------------------
    // CSV EXPORT
    // -------------------------------
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() throws Exception {

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
