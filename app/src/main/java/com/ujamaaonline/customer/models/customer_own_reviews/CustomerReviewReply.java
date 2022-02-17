package com.ujamaaonline.customer.models.customer_own_reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerReviewReply {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("review_id")
    @Expose
    private Integer reviewId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("have_img")
    @Expose
    private String haveImg;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("reply_images")
    @Expose
    private List<Object> replyImages = null;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHaveImg() {
        return haveImg;
    }

    public void setHaveImg(String haveImg) {
        this.haveImg = haveImg;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Object> getReplyImages() {
        return replyImages;
    }

    public void setReplyImages(List<Object> replyImages) {
        this.replyImages = replyImages;
    }
}
