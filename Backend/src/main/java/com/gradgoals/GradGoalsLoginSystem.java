package com.gradgoals;

import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

// Facade
class LoginFacade {
    private final LoginService loginService;

    public LoginFacade() {
        DatabaseConnector dbConnector = new DatabaseConnector();
        Authenticator authenticator = new BasicAuthenticator();
        this.loginService = new DefaultLoginService(dbConnector, authenticator);
    }

    public LoginFacade(LoginService loginService) {
        this.loginService = loginService;
    }

    public boolean login(String username, String password) {
        return loginService.performLogin(username, password);
    }
}

interface LoginService {
    boolean performLogin(String username, String password);
}

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

class DatabaseConnector {
    public void connect() {
        System.out.println("Connecting to database... (placeholder)");
    }

    public void disconnect() {
        System.out.println("Disconnecting from database... (placeholder)");
    }
}

interface Authenticator {
    boolean checkCredentials(String username, String password);
}

class BasicAuthenticator implements Authenticator {
    @Override
    public boolean checkCredentials(String username, String password) {
        // bcrypt example for your CLI demo
        String storedHash = BCrypt.hashpw("1234", BCrypt.gensalt());
        return BCrypt.checkpw(password, storedHash);
    }
}

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
