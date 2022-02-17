package com.ujamaaonline.customer.models.feed_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedLocationFilterRequest {
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("mile")
    @Expose
    private Integer mile;
    @SerializedName("is_event")
    @Expose
    private Integer isEvent;
    @SerializedName("is_local")
    @Expose
    private Integer isLocal;

    public FeedLocationFilterRequest(String lat, String lng, Integer mile, Integer isEvent, Integer isLocal) {
        this.lat = lat;
        this.lng = lng;
        this.mile = mile;
        this.isEvent = isEvent;
        this.isLocal = isLocal;
    }
}
