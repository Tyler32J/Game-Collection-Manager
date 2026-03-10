import database.DatabaseManager;
import java.sql.*;

public class CheckSchema {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "games", null);
            System.out.println("Columns in 'games' table:");
            while (columns.next()) {
                System.out.println("- " + columns.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
