package com.ujamaaonline.customer.models.share_earn_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCreditResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("payload")
    @Expose
    private Integer payload;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

}
