package com.gradgoals;

import spark.Spark;

public class Server {

    public void startServer() {
        // Set port (for Render / local)
        Spark.port(getAssignedPort());

        // Basic test route
        Spark.get("/", (request, response) -> "GradGoals Backend Running!");

        // Example: add a route to return a test budget summary
        Spark.get("/budget-summary", (req, res) -> {
            BudgetToolCode budget = new BudgetToolCode();
            // Example: empty budget
            double totalIncome = budget.getTotalIncome();
            double totalExpenses = budget.getTotalExpenses();
            double net = budget.getNetMonthly();

            return String.format("Income: %.2f, Expenses: %.2f, Net: %.2f",
                    totalIncome, totalExpenses, net);
        });

        // Add more routes here for CRUD operations
    }

    private int getAssignedPort() {
        String port = System.getenv("PORT");
        if (port != null) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid PORT environment variable, defaulting to 4567");
            }
        }
        return 4567; // default local port
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
