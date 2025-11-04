import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


class DatabaseConnector {
    private final String URL = "jdbc:mysql://localhost:3306/GradGoalsApp";
    private final String USER = "root";
    private final String PASSWORD = "your_my_sql_password";
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

class LoginService {
    private final DatabaseConnector dbConnector;

    public LoginService(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public boolean login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = dbConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }
}

public class GradGoalsLoginSystem {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        DatabaseConnector connector = new DatabaseConnector();
        LoginService loginService = new LoginService(connector);

        System.out.println("=== GradGoals Login System ===");
        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        if (loginService.login(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }

        input.close();
    }
}