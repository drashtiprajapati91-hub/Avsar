package com.example.avsar;

public class NotificationModel {
    public String title;
    public String message;
    public String timestamp;

    public NotificationModel() {}  // Required for Firebase

    public NotificationModel(String title, String message, String timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }
}
