package com.example.avsar;

public class Package {
    private String imageUrl;
    private String title;
    private String description;
    private String cost;
    private String duration;

    public Package() {
        // Default constructor required for Firebase
    }

    public Package(String imageUrl, String title, String description, String cost, String duration) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.duration = duration;
    }

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCost() { return cost; }
    public String getDuration() { return duration; }
}
