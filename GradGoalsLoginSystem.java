package LoginSytem;

import java.util.Scanner;

// Facade class
class LoginFacade {
    private DatabaseConnector dbConnector;
    private Authenticator authenticator;

    public LoginFacade() {
        dbConnector = new DatabaseConnector();
        authenticator = new Authenticator();
    }

    public boolean login(String username, String password) {
        dbConnector.connect();
        boolean success = authenticator.checkCredentials(username, password);
        dbConnector.disconnect();
        return success;
    }
}

// Simulated database connection class
class DatabaseConnector {
    public void connect() {
        System.out.println("Connecting to database... (placeholder)");
    }

    public void disconnect() {
        System.out.println("Disconnecting from database... (placeholder)");
    }
}

// Handles credential checking (currently hardcoded)
class Authenticator {
    public boolean checkCredentials(String username, String password) {
        String validUser = "admin";
        String validPass = "1234";
        return username.equals(validUser) && password.equals(validPass);
    }
}

// Main class that uses the Facade
public class GradGoalsLoginSystem {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        LoginFacade loginFacade = new LoginFacade();

        System.out.println("=== Basic Java Login System (with Facade Pattern) ===");

        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        if (loginFacade.login(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }

        input.close();
    }
}
