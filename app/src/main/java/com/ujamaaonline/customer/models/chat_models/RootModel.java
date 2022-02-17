package com.ujamaaonline.customer.models.chat_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootModel {

    @SerializedName("to")
    @Expose
    private String token;
    @SerializedName("priority")
    @Expose
    private String priority;

    @SerializedName("notification")
    @Expose
    private NotificationModel notification;

    @SerializedName("data")
    @Expose
    private DataModel data;

    public RootModel(String token, String priority, NotificationModel notification) {
        this.token = token;
        this.notification = notification;
        this.priority = priority;
    }

    public RootModel(String token, String priority, DataModel data) {
        this.token = token;
        this.data = data;
        this.priority = priority;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    public DataModel getData() {
        return data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }
}