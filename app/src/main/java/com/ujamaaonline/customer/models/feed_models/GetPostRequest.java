package com.ujamaaonline.customer.models.feed_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPostRequest {
    @SerializedName("is_all")
    @Expose
    private Integer isAll;
    @SerializedName("is_local")
    @Expose
    private Integer isLocal;
    @SerializedName("is_subscriber")
    @Expose
    private Integer isSubscriber;
    @SerializedName("is_liked")
    @Expose
    private Integer isLiked;
    @SerializedName("is_hidden")
    @Expose
    private Integer isHidden;
    @SerializedName("your_subscriptions")
    @Expose
    private Integer yourSubscriptions;

    @SerializedName("is_event")
    @Expose
    private Integer isEvent;
    @SerializedName("search")
    @Expose
    private String search;


    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("mile")
    @Expose
    private String mile;


    public GetPostRequest(Integer isLocal, Integer isSubscriber, Integer isEvent, String search) {
        this.isAll = isAll;
        this.isLocal = isLocal;
        this.isSubscriber = isSubscriber;
        this.isLiked = isLiked;
        this.isHidden = isHidden;
        this.yourSubscriptions = yourSubscriptions;
        this.isEvent = isEvent;
        this.search = search;
    }

    public GetPostRequest(Integer isAll, Integer isLocal, Integer isSubscriber, Integer isLiked, Integer isHidden, Integer yourSubscriptions, String lat, String lng, String miles) {
        this.isAll = isAll;
        this.isLocal = isLocal;
        this.isSubscriber = isSubscriber;
        this.isLiked = isLiked;
        this.isHidden = isHidden;
        this.yourSubscriptions = yourSubscriptions;
        this.lat = lat;
        this.lng = lng;
        this.mile = miles;
    }


    public GetPostRequest(Integer isAll, Integer isLocal, Integer isSubscriber, Integer isLiked, Integer isHidden, Integer yourSubscriptions) {
        this.isAll = isAll;
        this.isLocal = isLocal;
        this.isSubscriber = isSubscriber;
        this.isLiked = isLiked;
        this.isHidden = isHidden;
        this.yourSubscriptions = yourSubscriptions;
    }


    public GetPostRequest(Integer isAll, Integer isLiked, Integer isHidden) {
        this.isAll = isAll;
        this.isLocal = isLocal;
        this.isSubscriber = isSubscriber;
        this.isLiked = isLiked;
        this.isHidden = isHidden;
        this.yourSubscriptions = yourSubscriptions;
    }


    public Integer getIsAll() {
        return isAll;
    }

    public void setIsAll(Integer isAll) {
        this.isAll = isAll;
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

    public Integer getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
    }

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public Integer getYourSubscriptions() {
        return yourSubscriptions;
    }

    public void setYourSubscriptions(Integer yourSubscriptions) {
        this.yourSubscriptions = yourSubscriptions;
    }
}
