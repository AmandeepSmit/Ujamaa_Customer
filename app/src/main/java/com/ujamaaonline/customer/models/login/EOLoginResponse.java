package com.ujamaaonline.customer.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOLoginResponse {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private EOLoginPayload payload;



    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EOLoginPayload getPayload() {
        return payload;
    }

    public void setPayload(EOLoginPayload payload) {
        this.payload = payload;
    }
}
