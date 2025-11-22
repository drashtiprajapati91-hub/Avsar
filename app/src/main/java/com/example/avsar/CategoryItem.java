package com.example.avsar;

public class CategoryItem {
    private String title;
    private String cost;
    private String location;
    private String imageUrl;
    private double ratings;

    // ✅ Required no-argument constructor for Firebase
    public CategoryItem() {
    }

    public CategoryItem(String title, String cost, String location, String imageUrl, double ratings) {
        this.title = title;
        this.cost = cost;
        this.location = location;
        this.imageUrl = imageUrl;
        this.ratings = ratings;
    }

    // ✅ Getters and setters required for Firebase

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }
}
