package com.ujamaaonline.customer.models.recent_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentSearchResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private RecentSearchPayload payload;

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

    public RecentSearchPayload getPayload() {
        return payload;
    }

    public void setPayload(RecentSearchPayload payload) {
        this.payload = payload;
    }

}