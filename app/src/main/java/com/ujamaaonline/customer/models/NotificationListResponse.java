package com.ujamaaonline.customer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationListResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("notification_payload")
    @Expose
    private List<NotificationPayload> notificationPayload = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<NotificationPayload> getNotificationPayload() {
        return notificationPayload;
    }

    public void setNotificationPayload(List<NotificationPayload> notificationPayload) {
        this.notificationPayload = notificationPayload;
    }
}
