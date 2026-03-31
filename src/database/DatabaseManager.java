package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // UPDATE THESE DETAILS AFTER INSTALLING POSTGRESQL
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DATABASE = "Game_Collection_Manager";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Admin32#"; // Replace with your actual password

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        String platform_table = "CREATE TABLE IF NOT EXISTS platform (" +
                "platform_id SERIAL PRIMARY KEY," +
                "platform_title TEXT UNIQUE NOT NULL," +
                "manufacturer TEXT" +
                ");";

        String games_table = "CREATE TABLE IF NOT EXISTS games (" +
                "game_id SERIAL PRIMARY KEY," +
                "game_title TEXT NOT NULL," +
                "platform INTEGER REFERENCES platform(platform_id)," +
                "genre_name TEXT," +
                "is_completed INTEGER DEFAULT 0" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Rename columns in existing tables
            String[] renames = {
                "ALTER TABLE games RENAME COLUMN id TO game_id",
                "ALTER TABLE games RENAME COLUMN title TO game_title",
                "ALTER TABLE games RENAME COLUMN platform_id TO platform"
            };

            for (String rename : renames) {
                try {
                    stmt.execute(rename);
                } catch (SQLException e) {
                }
            }

            // 2. Ensure platform table exists
            stmt.execute(platform_table);
            
            // Ensure unique constraint on platform_title if it was somehow missing
            try {
                stmt.execute("ALTER TABLE platform ADD CONSTRAINT platform_title_unique UNIQUE (platform_title)");
            } catch (SQLException e) {}

            // 3. Ensure games table exists
            stmt.execute(games_table);

            // 4. Ensure columns exist (especially for migration)
            String[] alterQueries = {
                "ALTER TABLE games ADD COLUMN IF NOT EXISTS platform INTEGER REFERENCES platform(platform_id)",
                "ALTER TABLE games ADD COLUMN IF NOT EXISTS platform_name TEXT",
                "ALTER TABLE games ADD COLUMN IF NOT EXISTS manufacturer TEXT",
                "ALTER TABLE games ADD COLUMN IF NOT EXISTS genre_name TEXT",
                "ALTER TABLE games ADD COLUMN IF NOT EXISTS is_completed INTEGER DEFAULT 0"
            };

            for (String query : alterQueries) {
                try {
                    stmt.execute(query);
                } catch (SQLException e) {}
            }

            // 5. Data Migration: Extract existing platforms from games table into platform table
            try {
                stmt.execute("INSERT INTO platform (platform_title, manufacturer) " +
                           "SELECT DISTINCT platform_name, manufacturer FROM games " +
                           "WHERE platform_name IS NOT NULL " +
                           "ON CONFLICT (platform_title) DO NOTHING");
                
                // Link platform_id back to games
                stmt.execute("UPDATE games g SET platform = p.platform_id " +
                           "FROM platform p " +
                           "WHERE g.platform IS NULL " +
                           "AND g.platform_name IS NOT NULL " +
                           "AND g.platform_name ILIKE p.platform_title");
            } catch (SQLException e) {
                System.err.println("Migration warning: " + e.getMessage());
            }

            // 6. Seed default platforms if the table is empty
            String[] seedPlatforms = {
                "INSERT INTO platform (platform_title, manufacturer) VALUES ('PlayStation', 'Sony') ON CONFLICT DO NOTHING",
                "INSERT INTO platform (platform_title, manufacturer) VALUES ('Xbox', 'Microsoft') ON CONFLICT DO NOTHING",
                "INSERT INTO platform (platform_title, manufacturer) VALUES ('PC', 'Windows') ON CONFLICT DO NOTHING",
                "INSERT INTO platform (platform_title, manufacturer) VALUES ('Nintendo Switch', 'Nintendo') ON CONFLICT DO NOTHING"
            };
            for (String sql : seedPlatforms) {
                stmt.execute(sql);
            }

            // Normalize existing genre values
            try {
                stmt.execute("UPDATE games SET genre_name = UPPER(LEFT(genre_name,1)) || LOWER(SUBSTRING(genre_name FROM 2)) WHERE genre_name IS NOT NULL");
            } catch (SQLException e) {
            }

            // 7. Cleanup legacy columns
            String[] cleanupQueries = {
                "ALTER TABLE games DROP COLUMN IF EXISTS platform_id",
                "ALTER TABLE games DROP COLUMN IF EXISTS platform_name",
                "ALTER TABLE games DROP COLUMN IF EXISTS manufacturer",
                "ALTER TABLE games DROP COLUMN IF EXISTS genre_description",
                "ALTER TABLE games DROP COLUMN IF EXISTS genre"
            };
            for (String query : cleanupQueries) {
                try {
                    stmt.execute(query);
                } catch (SQLException e) {
                }
            }

        } catch (SQLException e) {
            System.err.println("CRITICAL: Error initializing database: " + e.getMessage());
            System.err.println("Please check if the database 'game_collection' exists and your credentials in DatabaseManager.java are correct.");
        }
    }
}
