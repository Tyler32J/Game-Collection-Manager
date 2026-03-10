package models;

public class Genre extends BaseModel {

    public Genre() {}

    public Genre(int id, String title) {
        super(id, title);
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + getId() +
                ", name='" + getTitle() + '\'' +
                '}';
    }
}
