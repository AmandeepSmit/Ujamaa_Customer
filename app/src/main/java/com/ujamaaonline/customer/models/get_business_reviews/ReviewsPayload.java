package com.ujamaaonline.customer.models.get_business_reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsPayload {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("rateing")
    @Expose
    private String rateing;

    @SerializedName("usefull_count")
    @Expose
    private Integer usefull_count;

    @SerializedName("business_name")
    @Expose
    private String business_name;
    @SerializedName("summery_line")
    @Expose
    private String summery_line;
    @SerializedName("business_logo")
    @Expose
    private String business_logo;


    @SerializedName("marked_as_useful")
    @Expose
    private Integer marked_as_useful;

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
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("customer_profile")
    @Expose
    private String customerProfile;
    @SerializedName("review_images")
    @Expose
    private List<ReviewImage> reviewImages = null;
    @SerializedName("review_replys")
    @Expose
    private List<Object> reviewReplys = null;

    public String getBusiness_name() {
        return business_name;
    }

    public String getSummery_line() {
        return summery_line;
    }

    public String getBusiness_logo() {
        return business_logo;
    }

    public Integer getMarked_as_useful() {
        return marked_as_useful;
    }

    public Integer getUsefull_count() {
        return usefull_count;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getRateing() {
        return rateing;
    }

    public void setRateing(String rateing) {
        this.rateing = rateing;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(String customerProfile) {
        this.customerProfile = customerProfile;
    }

    public List<ReviewImage> getReviewImages() {
        return reviewImages;
    }

    public void setReviewImages(List<ReviewImage> reviewImages) {
        this.reviewImages = reviewImages;
    }

    public List<Object> getReviewReplys() {
        return reviewReplys;
    }

    public void setReviewReplys(List<Object> reviewReplys) {
        this.reviewReplys = reviewReplys;
    }

}
