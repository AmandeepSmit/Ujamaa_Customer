package com.ujamaaonline.customer.models.terms_of_use;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsOfUseResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private TermsOfUsePayload payload;

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

    public TermsOfUsePayload getPayload() {
        return payload;
    }

    public void setPayload(TermsOfUsePayload payload) {
        this.payload = payload;
    }

}