package com.ujamaaonline.customer.models.customer_own_reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerOwnReviews {

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
    private CustomerReviewsPayload payload;

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

    public CustomerReviewsPayload getPayload() {
        return payload;
    }

    public Integer getFilter_count() {
        return filter_count;
    }

    public void setPayload(CustomerReviewsPayload payload) {
        this.payload = payload;
    }

}