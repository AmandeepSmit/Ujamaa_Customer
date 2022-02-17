package com.ujamaaonline.customer.models.cat_filter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ujamaaonline.customer.models.business_data.OtherFeature;

public class AllOtherFeaturesResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private List<OtherFeature> payload = null;

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

    public List<OtherFeature> getPayload() {
        return payload;
    }

    public void setPayload(List<OtherFeature> payload) {
        this.payload = payload;
    }

}