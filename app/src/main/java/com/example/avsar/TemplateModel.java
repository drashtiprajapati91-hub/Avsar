package com.example.avsar;

public class TemplateModel {
    private String title;
    private String imageUrl;
    private String themeColor;

    public TemplateModel() {} // Needed for Firebase

    public TemplateModel(String title, String imageUrl, String description, String themeColor) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.themeColor = themeColor;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public String getThemeColor() {
        return themeColor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }
}
