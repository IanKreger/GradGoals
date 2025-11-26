package com.gradgoals;

import java.util.Scanner;

// Facade
class LoginFacade {
    private final LoginService loginService;

    // Default constructor (used for assignment tests)
    public LoginFacade() {
        DatabaseConnector dbConnector = new DatabaseConnector();
        Authenticator authenticator = new BasicAuthenticator();
        this.loginService = new DefaultLoginService(dbConnector, authenticator);
    }

    // Constructor used for dependency injection (tests / real apps)
    public LoginFacade(LoginService loginService) {
        this.loginService = loginService;
    }

    public boolean login(String username, String password) {
        return loginService.performLogin(username, password);
    }
}

// Abstraction
interface LoginService {
    boolean performLogin(String username, String password);
}

// Concrete implementation
class DefaultLoginService implements LoginService {
    private final DatabaseConnector dbConnector;
    private final Authenticator authenticator;

    public DefaultLoginService(DatabaseConnector dbConnector, Authenticator authenticator) {
        this.dbConnector = dbConnector;
        this.authenticator = authenticator;
    }

    @Override
    public boolean performLogin(String username, String password) {
        dbConnector.connect();
        boolean success = authenticator.checkCredentials(username, password);
        dbConnector.disconnect();
        return success;
    }
}

// Fake DB for testing / assignment
class DatabaseConnector {
    public void connect() {
        System.out.println("Connecting to database... (placeholder)");
    }

    public void disconnect() {
        System.out.println("Disconnecting from database... (placeholder)");
    }
}

// Authenticator abstraction
interface Authenticator {
    boolean checkCredentials(String username, String password);
}

// Fake authenticator for assignment
class BasicAuthenticator implements Authenticator {
    @Override
    public boolean checkCredentials(String username, String password) {
        return username.equals("admin") && password.equals("1234");
    }
}

// Main program
public class GradGoalsLoginSystem {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        LoginFacade facade = new LoginFacade();

        System.out.println("=== Basic Java Login System (SOLID + Facade Pattern) ===");
        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        if (facade.login(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }

        input.close();
    }
}
