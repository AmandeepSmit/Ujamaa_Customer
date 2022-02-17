package com.ujamaaonline.customer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VisitProfileRequest {
    @SerializedName("business_id")
    @Expose
    private Integer businessId;

    public VisitProfileRequest(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getBusinessId() {
        return businessId;
    }
    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }
}
