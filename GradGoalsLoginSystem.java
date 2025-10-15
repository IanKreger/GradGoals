// Changes made by Zoe, ran on VS Code
package src;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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


class DatabaseConnector {
    private final String URL = "jdbc:mysql://localhost:3306/GradGoalsApp";
    private final String USER = "root"; // replace with your MySQL username
    private final String PASSWORD = "your_my_sql_password"; // replace with your MySQL password
    private Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
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
