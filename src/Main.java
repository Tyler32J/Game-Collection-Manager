import dao.GameDAO;
import database.DatabaseManager;
import models.Game;
import models.Genre;
import models.Platform;
import java.util.List;

public class Main {
    private static final GameDAO gameDAO = new GameDAO();

    public static void main(String[] args) {
        DatabaseManager.initialize();
        IO.println("Welcome to the Game Collection Manager!");
        
        boolean running = true;
        while (running) {
            printMenu();
            int choice = IO.readInt();
            
            switch (choice) {
                case 1:
                    addGame();
                    break;
                case 2:
                    viewAllGames();
                    break;
                case 3:
                    searchGames();
                    break;
                case 4:
                    updateGame();
                    break;
                case 5:
                    deleteGame();
                    break;
                case 0:
                    running = false;
                    IO.println("Goodbye!");
                    break;
                default:
                    IO.println("Invalid choice. Try again.");
            }
        }
    }

    private static void printMenu() {
        IO.println("\n=======================================");
        IO.println("      GAME COLLECTION MANAGER          ");
        IO.println("=======================================");
        IO.println("  [1] Add New Game");
        IO.println("  [2] View Collection");
        IO.println("  [3] Search for a Game");
        IO.println("  [4] Update a Game");
        IO.println("  [5] Delete a Game");
        IO.println("---------------------------------------");
        IO.println("  [0] Exit Application");
        IO.println("=======================================");
        IO.print("Selection > ");
    }

    private static void addGame() {
        IO.print("Enter title: ");
        String title = IO.readString();
        
        IO.print("Enter platform name (PC, Xbox, PlayStation, Nintendo Switch, etc): ");
        String pName = IO.readString();
        
        IO.print("Enter manufacturer (Sony, Microsoft, Nintendo, Windows, etc): ");
        String pMaker = IO.readString();
        Platform platform = new Platform(0, pName, pMaker);

        IO.print("Enter genre (Action, Strategy, Adventure etc): ");
        String gName = IO.readString();
        Genre genre = new Genre(0, gName);

        IO.print("Is it completed? (y/n): ");
        boolean isCompleted = IO.readString().equalsIgnoreCase("y");

        Game game = new Game(title, platform, genre, isCompleted);
        if (gameDAO.addGame(game)) {
            IO.println("Game added successfully!");
        } else {
            IO.println("(!) Error: Could not save the game. Please check your database connection.");
        }
    }

    private static void viewAllGames() {
        List<Game> games = gameDAO.getAllGames();
        if (games.isEmpty()) {
            IO.println("\n(!) Your collection is currently empty.");
        } else {
            IO.println("\n--- CURRENT COLLECTION ---");
            IO.println(String.format("%-4s | %-20s | %-12s | %-12s | %-10s", "ID", "Title", "Platform", "Genre", "Status"));
            IO.println("-------------------------------------------------------------------------");

            for (Game game : games) {
                String status = game.isCompleted() ? "Completed" : "In Progress";
                String platformName = game.getPlatform() != null ? game.getPlatform().getTitle() : "N/A";
                String genreName = game.getGenre() != null ? game.getGenre().getTitle() : "N/A";
                
                IO.println(String.format("%-4d | %-20s | %-12s | %-12s | %-10s",
                        game.getId(),
                        truncate(game.getTitle(), 20),
                        truncate(platformName, 12),
                        truncate(genreName, 12),
                        status));
            }
            IO.println("-------------------------------------------------------------------------");
        }
    }

    private static void searchGames() {
        IO.print("You can search by Title, Platform, Manufacturer, or Genre:");
        String term = IO.readString();
        List<Game> games = gameDAO.searchGames(term);
        if (games.isEmpty()) {
            IO.println("\n(!) No games found matching '" + term + "'.");
        } else {
            IO.println("\n--- SEARCH RESULTS ---");
            IO.println(String.format("%-4s | %-20s | %-12s | %-12s | %-10s", "ID", "Title", "Platform", "Genre", "Status"));
            IO.println("-------------------------------------------------------------------------");

            for (Game game : games) {
                String status = game.isCompleted() ? "Completed" : "In Progress";
                String platformName = game.getPlatform() != null ? game.getPlatform().getTitle() : "N/A";
                String genreName = game.getGenre() != null ? game.getGenre().getTitle() : "N/A";

                IO.println(String.format("%-4d | %-20s | %-12s | %-12s | %-10s",
                        game.getId(),
                        truncate(game.getTitle(), 20),
                        truncate(platformName, 12),
                        truncate(genreName, 12),
                        status));
            }
            IO.println("-------------------------------------------------------------------------");
        }
    }

    private static void updateGame() {
        IO.print("Enter game ID to update: ");
        int id = IO.readInt();
        Game game = gameDAO.getGameById(id);
        
        if (game == null) {
            IO.println("(!) Error: Game with ID " + id + " not found.");
            return;
        }

        IO.println("\n--- Updating Game: " + game.getTitle() + " ---");
        IO.println("Leave blank to keep current value.");

        IO.print("New title [" + game.getTitle() + "]: ");
        String title = IO.readString();
        if (!title.isBlank()) {
            game.setTitle(title);
        }

        String currentPlatform = game.getPlatform() != null ? game.getPlatform().getTitle() : "N/A";
        IO.print("New platform [" + currentPlatform + "]: ");
        String pName = IO.readString();
        if (!pName.isBlank()) {
            String currentMaker = game.getPlatform() != null ? game.getPlatform().getManufacturer() : "";
            IO.print("New manufacturer [" + currentMaker + "]: ");
            String pMaker = IO.readString();
            if (pMaker.isBlank()) pMaker = currentMaker;
            game.setPlatform(new Platform(0, pName, pMaker));
        } else if (game.getPlatform() != null) {
            // Platform name didn't change, but maybe manufacturer should?
            String currentMaker = game.getPlatform().getManufacturer();
            IO.print("New manufacturer [" + currentMaker + "]: ");
            String pMaker = IO.readString();
            if (!pMaker.isBlank()) {
                game.getPlatform().setManufacturer(pMaker);
            }
        }

        String currentGenre = game.getGenre() != null ? game.getGenre().getTitle() : "N/A";
        IO.print("New genre [" + currentGenre + "]: ");
        String gName = IO.readString();
        if (!gName.isBlank()) {
            game.getGenre().setTitle(gName);
        }

        IO.print("Is it completed? (y/n) [" + (game.isCompleted() ? "y" : "n") + "]: ");
        String completedInput = IO.readString();
        if (!completedInput.isBlank()) {
            game.setCompleted(completedInput.equalsIgnoreCase("y"));
        }

        if (gameDAO.updateGame(game)) {
            IO.println("Game updated successfully!");
        } else {
            IO.println("(!) Error: Could not update the game in the database.");
        }
    }

    private static void deleteGame() {
        IO.print("Enter game ID to delete: ");
        int id = IO.readInt();
        if (gameDAO.deleteGame(id)) {
            IO.println("Game deleted successfully!");
        } else {
            IO.println("(!) Error: Game with ID " + id + " not found or database error.");
        }
    }

    // Helper method to keep the table neat
    private static String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}
