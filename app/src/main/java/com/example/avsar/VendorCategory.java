package com.example.avsar;

public class VendorCategory {
    private int imageResId;
    private String name;

    public VendorCategory(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }
}
