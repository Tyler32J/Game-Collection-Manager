package models;

public class Game extends BaseModel {
    private Platform platform;
    private Genre genre;
    private boolean isCompleted;

    // Constructors
    public Game() {}

    public Game(String title, Platform platform, Genre genre, boolean isCompleted) {
        this.title = title;
        this.platform = platform;
        this.genre = genre;
        this.isCompleted = isCompleted;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", platform=" + platform +
                ", genre=" + genre +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
