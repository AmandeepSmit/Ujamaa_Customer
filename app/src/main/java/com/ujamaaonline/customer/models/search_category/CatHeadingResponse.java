package com.ujamaaonline.customer.models.search_category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatHeadingResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private CatHeadingPayload payload;

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

    public CatHeadingPayload getPayload() {
        return payload;
    }

    public void setPayload(CatHeadingPayload payload) {
        this.payload = payload;
    }

}
