package com.ujamaaonline.customer.models.chat_models;

public class ChatModel {

    private String message;
    private String senderId;
    private String senderName;
    private String senderType;
    private String timeStamp;
    private String is_seen;
    private String b_remove;
    private String c_remove;
    private String messageType;
    private String key;

    public ChatModel(String message, String senderId, String senderName, String senderType, String timeStamp, String is_seen, String b_remove, String c_remove, String messageType,String key) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderType = senderType;
        this.timeStamp = timeStamp;
        this.is_seen = is_seen;
        this.b_remove = b_remove;
        this.c_remove = c_remove;
        this.messageType = messageType;
        this.key=key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIs_seen() {
        return is_seen;
    }

    public void setIs_seen(String is_seen) {
        this.is_seen = is_seen;
    }

    public String getB_remove() {
        return b_remove;
    }

    public void setB_remove(String b_remove) {
        this.b_remove = b_remove;
    }

    public String getC_remove() {
        return c_remove;
    }

    public void setC_remove(String c_remove) {
        this.c_remove = c_remove;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getKey() {
        return key;
    }
}
