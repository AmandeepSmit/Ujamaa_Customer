package com.ujamaaonline.customer.models.feed_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkClickRequest {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_event")
    @Expose
    private Integer isEvent;
    @SerializedName("is_post")
    @Expose
    private Integer isPost;

    public LinkClickRequest(Integer id, Integer isEvent, Integer isPost) {
        this.id = id;
        this.isEvent = isEvent;
        this.isPost = isPost;
    }
}
