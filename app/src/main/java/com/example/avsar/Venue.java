package com.example.avsar;

public class Venue {
    private String imageUrl;
    private String name;
    private String costPerPlate;
    private String capacity;
    private boolean saved;
    private String location;
    private String id; // 🔥 Add a new field for the Firebase key

    public Venue() {
        // Required for Firebase
    }

    public Venue(String imageUrl, String name, String costPerPlate, String capacity, boolean saved,String location, String id) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.costPerPlate = costPerPlate;
        this.capacity = capacity;
        this.saved = saved;
        this.location = location;
        this.id = id; // 🔥 Initialize the Firebase key
    }

    public String getImageUrl() { return imageUrl; }
    public String getName() { return name; }
    public String getCostPerPlate() { return costPerPlate; }
    public String getCapacity() { return capacity; }
    public boolean isSaved() { return saved; }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public void setSaved(boolean saved) { this.saved = saved; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
