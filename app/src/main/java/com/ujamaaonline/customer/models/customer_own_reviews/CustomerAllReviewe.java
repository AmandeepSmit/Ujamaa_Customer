package com.ujamaaonline.customer.models.customer_own_reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerAllReviewe {
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
    @SerializedName("usefull_count")
    @Expose
    private Integer usefullCount;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("summery_line")
    @Expose
    private String summeryLine;
    @SerializedName("business_logo")
    @Expose
    private String businessLogo;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("customer_profile")
    @Expose
    private String customerProfile;
    @SerializedName("review_images")
    @Expose
    private List<CustomerReviewImage> reviewImages = null;
    @SerializedName("review_replys")
    @Expose
    private List<CustomerReviewReply> reviewReplys = null;

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

    public Integer getUsefullCount() {
        return usefullCount;
    }

    public void setUsefullCount(Integer usefullCount) {
        this.usefullCount = usefullCount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getSummeryLine() {
        return summeryLine;
    }

    public void setSummeryLine(String summeryLine) {
        this.summeryLine = summeryLine;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
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

    public List<CustomerReviewImage> getReviewImages() {
        return reviewImages;
    }

    public void setReviewImages(List<CustomerReviewImage> reviewImages) {
        this.reviewImages = reviewImages;
    }

    public List<CustomerReviewReply> getReviewReplys() {
        return reviewReplys;
    }

    public void setReviewReplys(List<CustomerReviewReply> reviewReplys) {
        this.reviewReplys = reviewReplys;
    }


}
