package com.ujamaaonline.customer.models.customer_own_reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerReviewImage {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("review_id")
    @Expose
    private Integer reviewId;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
