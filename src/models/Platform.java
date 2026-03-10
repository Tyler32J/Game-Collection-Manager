package models;

public class Platform extends BaseModel {
    private String manufacturer;

    public Platform() {}

    public Platform(int id, String title, String manufacturer) {
        super(id, title);
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + getId() +
                ", name='" + getTitle() + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                '}';
    }
}
