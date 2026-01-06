package org.example;

public class Character {
    private int id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String title;
    private String family;
    private String image;
    private String imageUrl;

    public Character() {}

    public Character(int id, String firstName, String lastName, String fullName, String title, String family, String image, String imageUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.title = title;
        this.family = family;
        this.image = image;
        this.imageUrl = imageUrl;
    }

    // Metodi GETTER e SETTER
    // ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Nome
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    // Cognome
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    // Nome completo
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    // Soprannome
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Casa
    public String getFamily() { return family; }
    public void setFamily(String family) { this.family = family; }

    // Immagine
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    // Url dell'immagine
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

