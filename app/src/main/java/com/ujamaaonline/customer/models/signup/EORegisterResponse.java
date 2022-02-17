package com.ujamaaonline.customer.models.signup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EORegisterResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private EORegisterPayload payload;

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

    public EORegisterPayload getPayload() {
        return payload;
    }

    public void setPayload(EORegisterPayload payload) {
        this.payload = payload;
    }

}
