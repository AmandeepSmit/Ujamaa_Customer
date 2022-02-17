package com.ujamaaonline.customer.models.share_earn_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReferralCodeResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("payload")
    @Expose
    private String payload;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
