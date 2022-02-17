package com.ujamaaonline.customer.models.chat_models;

public class NotificationModel {
    private String title;
    private String body;
    private String sender_id;
    private String sound;
    private String businessName;
    private String businessLogo;


    public NotificationModel(String title, String body, String sender_id, String sound, String businessName) {
        this.title = title;
        this.body = body;
        this.sender_id = sender_id;
        this.sound = sound;
        this.businessName = businessName;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
