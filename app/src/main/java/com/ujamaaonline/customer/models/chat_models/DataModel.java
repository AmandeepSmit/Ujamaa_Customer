package com.ujamaaonline.customer.models.chat_models;

public class DataModel {

    private String title;
    private String sender_id;
    private String message;
    private String businessName;
    private String businessLogo;

    public DataModel(String title, String sender_id, String message, String businessName) {
        this.title = title;
        this.sender_id = sender_id;
        this.message = message;
        this.businessName = businessName;
    }
}
