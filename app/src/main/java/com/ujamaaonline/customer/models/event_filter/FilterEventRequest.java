package com.ujamaaonline.customer.models.event_filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterEventRequest {
    @SerializedName("is_all")
    @Expose
    private Integer is_all;
    @SerializedName("is_liked")
    @Expose
    private Integer is_liked;
    @SerializedName("is_hidden")
    @Expose
    private Integer is_hidden;

    @SerializedName("event_cat")
    @Expose
    private Integer eventCat;


    @SerializedName("sort_by")
    @Expose
    private Integer sort_by;


    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("max")
    @Expose
    private Integer max;
    @SerializedName("event_for")
    @Expose
    private String eventFor;

    @SerializedName("start_date")
    @Expose
    private String start_date;

    @SerializedName("end_date")
    @Expose
    private String end_date;

    @SerializedName("event_type")
    @Expose
    private Integer eventType;
    @SerializedName("is_show_free_event")
    @Expose
    private Integer isShowFreeEvent;
    @SerializedName("is_hide_online_event")
    @Expose
    private Integer isHideOnlineEvent;
    @SerializedName("is_show_online_event")
    @Expose
    private Integer isShowOnlineEvent;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("mile")
    @Expose
    private String mile;


    public FilterEventRequest(Integer is_all, Integer is_liked, Integer is_hidden, String lat, String lng, String mile) {
        this.is_all = is_all;
        this.is_liked = is_liked;
        this.is_hidden = is_hidden;
        this.lat=lat;
        this.lng=lng;
        this.mile=mile;
    }

    public FilterEventRequest(Integer eventCat, Integer age, Integer max, String eventFor, Integer eventType, Integer isShowFreeEvent, Integer isHideOnlineEvent, Integer isShowOnlineEvent, String date,Integer sort_by) {
        this.eventCat = eventCat;
        this.age = age;
        this.sort_by = sort_by;
        this.max = max;
        this.start_date = eventFor;
        this.eventType = eventType;
        this.isShowFreeEvent = isShowFreeEvent;
        this.isHideOnlineEvent = isHideOnlineEvent;
        this.isShowOnlineEvent = isShowOnlineEvent;
        this.end_date = date;
    }
}