import database.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Attempting to connect to PostgreSQL...");
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn != null) {
                System.out.println("[SUCCESS] Successfully connected to the database!");
            } else {
                System.out.println("[FAILURE] Connection failed (null).");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Could not connect to the database.");
            System.out.println("Reason: " + e.getMessage());
            if (e.getMessage().contains("password authentication failed")) {
                System.out.println("TIP: It looks like the password in DatabaseManager.java is incorrect.");
            } else if (e.getMessage().contains("database \"game_collection\" does not exist")) {
                System.out.println("TIP: You need to create the database named 'game_collection' in pgAdmin first.");
            } else if (e.getMessage().contains("Connection to localhost:5432 refused")) {
                System.out.println("TIP: PostgreSQL server is not running or the port is blocked.");
            }
        }
    }
}
