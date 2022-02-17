package com.ujamaaonline.customer.models.event_filter;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventDetailPayload implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("event_title")
    @Expose
    private String eventTitle;
    @SerializedName("event_link")
    @Expose
    private String eventLink;

    @SerializedName("is_bussiness_subscribed")
    @Expose
    private Integer is_bussiness_subscribed;

    @SerializedName("business_logo")
    @Expose
    private String business_logo;
    @SerializedName("event_description")
    @Expose
    private String eventDescription;
    @SerializedName("hashtags")
    @Expose
    private String hashtags;
    @SerializedName("event_starts")
    @Expose
    private String eventStarts;
    @SerializedName("event_ends")
    @Expose
    private String eventEnds;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("is_physical")
    @Expose
    private Integer isPhysical;
    @SerializedName("is_online")
    @Expose
    private Integer isOnline;
    @SerializedName("exact_location")
    @Expose
    private Object exactLocation;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("ticket_start_price")
    @Expose
    private String ticketStartPrice;
    @SerializedName("ticket_end_price")
    @Expose
    private String ticketEndPrice;
    @SerializedName("is_free_event")
    @Expose
    private Integer isFreeEvent;
    @SerializedName("is_subscriber")
    @Expose
    private Integer isSubscriber;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("bussiness_name")
    @Expose
    private String bussinessName;
    @SerializedName("summry_line")
    @Expose
    private String summryLine;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("is_liked")
    @Expose
    private Integer isLiked;

    public Integer getIs_bussiness_subscribed() {
        return is_bussiness_subscribed;
    }
    public void setIs_bussiness_subscribed(Integer is_bussiness_subscribed) {
        this.is_bussiness_subscribed = is_bussiness_subscribed;
    }
    public String getBusiness_logo() {
        return business_logo;
    }
    public void setBusiness_logo(String business_logo) {
        this.business_logo = business_logo;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getEventTitle() {
        return eventTitle;
    }
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    public String getEventLink() {
        return eventLink;
    }
    public void setEventLink(String eventLink) {
        this.eventLink = eventLink;
    }
    public String getEventDescription() {
        return eventDescription;
    }
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getEventStarts() {
        return eventStarts;
    }

    public void setEventStarts(String eventStarts) {
        this.eventStarts = eventStarts;
    }

    public String getEventEnds() {
        return eventEnds;
    }

    public void setEventEnds(String eventEnds) {
        this.eventEnds = eventEnds;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getIsPhysical() {
        return isPhysical;
    }

    public void setIsPhysical(Integer isPhysical) {
        this.isPhysical = isPhysical;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }

    public Object getExactLocation() {
        return exactLocation;
    }

    public void setExactLocation(Object exactLocation) {
        this.exactLocation = exactLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTicketStartPrice() {
        return ticketStartPrice;
    }

    public void setTicketStartPrice(String ticketStartPrice) {
        this.ticketStartPrice = ticketStartPrice;
    }

    public String getTicketEndPrice() {
        return ticketEndPrice;
    }

    public void setTicketEndPrice(String ticketEndPrice) {
        this.ticketEndPrice = ticketEndPrice;
    }

    public Integer getIsFreeEvent() {
        return isFreeEvent;
    }

    public void setIsFreeEvent(Integer isFreeEvent) {
        this.isFreeEvent = isFreeEvent;
    }

    public Integer getIsSubscriber() {
        return isSubscriber;
    }

    public void setIsSubscriber(Integer isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public String getSummryLine() {
        return summryLine;
    }

    public void setSummryLine(String summryLine) {
        this.summryLine = summryLine;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
    }

}