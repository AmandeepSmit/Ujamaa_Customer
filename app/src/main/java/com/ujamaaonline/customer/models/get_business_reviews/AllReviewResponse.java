package com.ujamaaonline.customer.models.get_business_reviews;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllReviewResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("filter_count")
    @Expose
    private Integer filter_count;

    @SerializedName("payload")
    @Expose
    private List<ReviewsPayload> payload = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }


    public Integer getFilter_count() {
        return filter_count;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ReviewsPayload> getPayload() {
        return payload;
    }

    public void setPayload(List<ReviewsPayload> payload) {
        this.payload = payload;
    }

}