package com.ujamaaonline.customer.models.businessList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusinessListResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("filter_count")
    @Expose
    private Integer filter_count;

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private List<BusinessListData> payload = null;

    public Integer getFilter_count() {
        return filter_count;
    }

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

    public List<BusinessListData> getPayload() {
        return payload;
    }

    public void setPayload(List<BusinessListData> payload) {
        this.payload = payload;
    }

}
