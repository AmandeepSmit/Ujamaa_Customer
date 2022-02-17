package com.ujamaaonline.customer.models.filter_heading;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeadingFilterResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("payload")
    @Expose
    public List<HeadingFilerPayload> payload = null;

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<HeadingFilerPayload> getPayload() {
        return payload;
    }
}