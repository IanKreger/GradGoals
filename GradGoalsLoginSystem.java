

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Facade
class LoginFacade {
    private final LoginService loginService;

    public LoginFacade(LoginService loginService) {
        this.loginService = loginService;
    }

    public boolean login(String username, String password) {
        return loginService.performLogin(username, password);
    }
}

//Abstraction for login logic
interface LoginService {
    boolean performLogin(String username, String password);
}

//Concrete implementation
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

//Database Connection
class DatabaseConnector {
    private final String URL = "jdbc:mysql://localhost:3306/GradGoalsApp";
    private final String USER = "root";
    private final String PASSWORD = "your_my_sql_password";
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

//Authentication 
interface Authenticator {
    boolean checkCredentials(String username, String password);
}

class BasicAuthenticator implements Authenticator {
    @Override
    public boolean checkCredentials(String username, String password) {
        String validUser = "admin";
        String validPass = "1234";
        return username.equals(validUser) && password.equals(validPass);
    }
}

//  Main Application
public class GradGoalsLoginSystem {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        DatabaseConnector connector = new DatabaseConnector();
        Authenticator authenticator = new BasicAuthenticator();
        LoginService loginService = new DefaultLoginService(connector, authenticator);
        LoginFacade facade = new LoginFacade(loginService);

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
