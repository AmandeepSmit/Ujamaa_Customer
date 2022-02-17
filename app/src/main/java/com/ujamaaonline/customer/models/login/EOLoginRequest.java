package com.ujamaaonline.customer.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EOLoginRequest {
    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("customer_id")
    @Expose
    private Integer customer_id;

    @SerializedName("is_local")
    @Expose
    private Integer isLocal;

    @SerializedName("event_id")
    @Expose
    private Integer event_id;

    @SerializedName("is_hide")
    @Expose
    private Integer is_hide;

    @SerializedName("is_subscriber")
    @Expose
    private Integer isSubscriber;
    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("is_like")
    @Expose
    private Integer isLike;
    @SerializedName("is_share")
    @Expose
    private Integer isShare;
    @SerializedName("is_event")
    @Expose
    private Integer isEvent;

    @SerializedName("month")
    @Expose
    private String month;


    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("is_liked")
    @Expose
    private Integer is_liked;

    @SerializedName("is_hidden")
    @Expose
    private Integer is_hidden;

    @SerializedName("is_all")
    @Expose
    private Integer is_all;


    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("cat_ids")
    @Expose
    private List<Integer> cat_ids=null;

    @SerializedName("shorting")
    @Expose
    private Integer shorting;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("review_id")
    @Expose
    private String review_id;


    @SerializedName("distance_unit")
    @Expose
    private String distance_unit;

    @SerializedName("report")
    @Expose
    private String report;

    @SerializedName("business_id")
    @Expose
    private String business_id;

    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("firebase_token")
    @Expose
    private String firebase_token;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("device_type")
    @Expose
    private String deviceType;


    @SerializedName("old_password")
    @Expose
    private String oldPassword;
    @SerializedName("new_password")
    @Expose
    private String newPassword;


    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public void setIs_hide(Integer is_hide) {
        this.is_hide = is_hide;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIs_liked(Integer is_liked) {
        this.is_liked = is_liked;
    }

    public void setIs_hidden(Integer is_hidden) {
        this.is_hidden = is_hidden;
    }

    public void setIs_all(Integer is_all) {
        this.is_all = is_all;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public void setIsLocal(Integer isLocal) {
        this.isLocal = isLocal;
    }

    public void setIsSubscriber(Integer isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
    }

    public void setIsShare(Integer isShare) {
        this.isShare = isShare;
    }

    public void setIsEvent(Integer isEvent) {
        this.isEvent = isEvent;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setCat_ids(List<Integer> cat_ids) {
        this.cat_ids = cat_ids;
    }

    public void setShorting(Integer shorting) {
        this.shorting = shorting;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
