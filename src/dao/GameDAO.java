package dao;

import database.DatabaseManager;
import models.Game;
import models.Genre;
import models.Platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public boolean addGame(Game game) {
        int platformId = getOrCreatePlatformId(game.getPlatform());
        String sql = "INSERT INTO games (game_title, platform, genre_name, is_completed) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, game.getTitle());
            if (platformId > 0) {
                pstmt.setInt(2, platformId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            String genreNorm = null;
            if (game.getGenre() != null && game.getGenre().getTitle() != null) {
                String g = game.getGenre().getTitle().trim();
                if (!g.isEmpty()) {
                    genreNorm = g.substring(0,1).toUpperCase() + (g.length() > 1 ? g.substring(1).toLowerCase() : "");
                }
            }
            pstmt.setString(3, genreNorm);
            pstmt.setInt(4, game.isCompleted() ? 1 : 0);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding game: " + e.getMessage());
            return false;
        }
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, p.platform_title, p.manufacturer FROM games g " +
                     "LEFT JOIN platform p ON g.platform = p.platform_id " +
                     "ORDER BY g.game_id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                games.add(mapResultSetToGame(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error reading games: " + e.getMessage());
        }
        return games;
    }

    public Game getGameById(int id) {
        String sql = "SELECT g.*, p.platform_title, p.manufacturer FROM games g " +
                     "LEFT JOIN platform p ON g.platform = p.platform_id " +
                     "WHERE g.game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGame(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching game: " + e.getMessage());
        }
        return null;
    }

    public List<Game> searchGames(String searchTerm) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, p.platform_title, p.manufacturer FROM games g " +
                     "LEFT JOIN platform p ON g.platform = p.platform_id " +
                     "WHERE g.game_title ILIKE ? OR p.platform_title ILIKE ? OR p.manufacturer ILIKE ? OR g.genre_name ILIKE ? " +
                     "ORDER BY g.game_id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    games.add(mapResultSetToGame(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching games: " + e.getMessage());
        }
        return games;
    }

    public boolean updateGame(Game game) {
        int platformId = getOrCreatePlatformId(game.getPlatform());
        String sql = "UPDATE games SET game_title = ?, platform = ?, genre_name = ?, is_completed = ? WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, game.getTitle());
            if (platformId > 0) {
                pstmt.setInt(2, platformId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            String genreNorm = null;
            if (game.getGenre() != null && game.getGenre().getTitle() != null) {
                String g = game.getGenre().getTitle().trim();
                if (!g.isEmpty()) {
                    genreNorm = g.substring(0,1).toUpperCase() + (g.length() > 1 ? g.substring(1).toLowerCase() : "");
                }
            }
            pstmt.setString(3, genreNorm);
            pstmt.setInt(4, game.isCompleted() ? 1 : 0);
            pstmt.setInt(5, game.getId());
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating game: " + e.getMessage());
            return false;
        }
    }

    private int getOrCreatePlatformId(Platform platform) {
        if (platform == null || platform.getTitle() == null || platform.getTitle().isBlank()) {
            return -1;
        }

        String selectSql = "SELECT platform_id FROM platform WHERE platform_title ILIKE ?";
        String insertSql = "INSERT INTO platform (platform_title, manufacturer) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if it exists
            try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
                selectPstmt.setString(1, platform.getTitle().trim());
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("platform_id");
                        
                        // Update manufacturer if it's provided and potentially different
                        if (platform.getManufacturer() != null && !platform.getManufacturer().isBlank()) {
                            String updateSql = "UPDATE platform SET manufacturer = ? WHERE platform_id = ?";
                            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                                updatePstmt.setString(1, platform.getManufacturer().trim());
                                updatePstmt.setInt(2, id);
                                updatePstmt.executeUpdate();
                            }
                        }
                        return id;
                    }
                }
            }

            // Create new
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPstmt.setString(1, platform.getTitle().trim());
                insertPstmt.setString(2, platform.getManufacturer() != null ? platform.getManufacturer().trim() : null);
                insertPstmt.executeUpdate();
                try (ResultSet rs = insertPstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error managing platform: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateGameStatus(int id, boolean isCompleted) {
        String sql = "UPDATE games SET is_completed = ? WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, isCompleted ? 1 : 0);
            pstmt.setInt(2, id);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating game: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGame(int id) {
        String sql = "DELETE FROM games WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting game: " + e.getMessage());
            return false;
        }
    }

    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setId(rs.getInt("game_id"));
        game.setTitle(rs.getString("game_title"));
        
        Platform platform = new Platform();
        platform.setId(rs.getInt("platform"));
        platform.setTitle(rs.getString("platform_title"));
        platform.setManufacturer(rs.getString("manufacturer"));
        game.setPlatform(platform);
        
        Genre genre = new Genre();
        genre.setTitle(rs.getString("genre_name"));
        game.setGenre(genre);
        
        game.setCompleted(rs.getInt("is_completed") == 1);
        return game;
    }
}
