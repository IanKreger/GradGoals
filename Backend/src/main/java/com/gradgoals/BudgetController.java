package com.gradgoals;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = "*")  // allow Netlify or local
public class BudgetController {

    private final BudgetToolCode budget = new BudgetToolCode();

    // GET all budget items
    @GetMapping("/items")
    public List<Map<String, Object>> getItems() {

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

    // POST add a new item
    @PostMapping("/add-item")
    public String addItem(@RequestBody Map<String, Object> body) {
        String category = (String) body.get("category");
        double amount = Double.parseDouble(body.get("amount").toString());
        String type = (String) body.get("type");

        budget.addItem(category, amount, type);

        return "Item added!";
    }
}
