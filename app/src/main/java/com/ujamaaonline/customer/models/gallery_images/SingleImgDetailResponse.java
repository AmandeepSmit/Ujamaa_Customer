package com.ujamaaonline.customer.models.gallery_images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleImgDetailResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("payload")
    @Expose
    public SingleImgPayload payload;


    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public SingleImgPayload getPayload() {
        return payload;
    }
}