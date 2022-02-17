package com.ujamaaonline.customer.models.feed_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostPayload {

    @SerializedName("is_hidden_post")
    @Expose
    private Integer is_hidden_post;

    @SerializedName("expire_in_text")
    @Expose
    private String expire_in_text;

    @SerializedName("business_logo")
    @Expose
    private String business_logo;

    @SerializedName("is_count_revealed")
    @Expose
    private Integer is_count_revealed;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("post_title")
    @Expose
    private String postTitle;
    @SerializedName("post")
    @Expose
    private String post;
    @SerializedName("hashtags")
    @Expose
    private String hashtags;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("link_name")
    @Expose
    private String linkName;

    @SerializedName("is_liked")
    @Expose
    private Integer is_liked;

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("is_general")
    @Expose
    private Integer isGeneral;
    @SerializedName("is_important")
    @Expose
    private Integer isImportant;
    @SerializedName("upload")
    @Expose
    private String upload;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("post_expire")
    @Expose
    private String postExpire;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("business_data_id")
    @Expose
    private Integer businessDataId;
    @SerializedName("isDiscountPromotion")
    @Expose
    private Integer isDiscountPromotion;
    @SerializedName("is_local")
    @Expose
    private Integer isLocal;
    @SerializedName("is_subscriber")
    @Expose
    private Integer isSubscriber;
    @SerializedName("expire_in")
    @Expose
    private Integer expireIn;
    @SerializedName("bussiness_name")
    @Expose
    private String bussinessName;
    @SerializedName("created_on")
    @Expose
    private String createdOn;


    public String getExpire_in_text() {
        return expire_in_text;
    }

    public Integer getIs_hidden_post() {
        return is_hidden_post;
    }

    public void setIs_hidden_post(Integer is_hidden_post) {
        this.is_hidden_post = is_hidden_post;
    }

    public Integer getIs_count_revealed() {
        return is_count_revealed;
    }

    public String getBusiness_logo() {
        return business_logo;
    }

    public void setBusiness_logo(String business_logo) {
        this.business_logo = business_logo;
    }

    public void setIs_count_revealed(Integer is_count_revealed) {
        this.is_count_revealed = is_count_revealed;
    }

    public Integer getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Integer is_liked) {
        this.is_liked = is_liked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getIsGeneral() {
        return isGeneral;
    }

    public void setIsGeneral(Integer isGeneral) {
        this.isGeneral = isGeneral;
    }

    public Integer getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Integer isImportant) {
        this.isImportant = isImportant;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPostExpire() {
        return postExpire;
    }

    public void setPostExpire(String postExpire) {
        this.postExpire = postExpire;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBusinessDataId() {
        return businessDataId;
    }

    public void setBusinessDataId(Integer businessDataId) {
        this.businessDataId = businessDataId;
    }

    public Integer getIsDiscountPromotion() {
        return isDiscountPromotion;
    }

    public void setIsDiscountPromotion(Integer isDiscountPromotion) {
        this.isDiscountPromotion = isDiscountPromotion;
    }

    public Integer getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(Integer isLocal) {
        this.isLocal = isLocal;
    }

    public Integer getIsSubscriber() {
        return isSubscriber;
    }

    public void setIsSubscriber(Integer isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public Integer getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(Integer expireIn) {
        this.expireIn = expireIn;
    }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
