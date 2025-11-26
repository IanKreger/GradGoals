package com.gradgoals;

import static spark.Spark.*;

public class Server {

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        get("/", (req, res) -> "GradGoals Backend Running!");

        // You will add real routes here later
    }

    private static int getHerokuAssignedPort() {
        ProcessHandle current = ProcessHandle.current();
        String port = System.getenv("PORT");
        if (port != null) {
            return Integer.parseInt(port);
        }
        return 4567; // local default
    }
}
