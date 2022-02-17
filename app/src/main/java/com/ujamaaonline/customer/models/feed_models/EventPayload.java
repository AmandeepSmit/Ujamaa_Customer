package com.ujamaaonline.customer.models.feed_models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventPayload implements Serializable {

    @SerializedName("is_liked")
    @Expose
    private Integer is_liked;
    @SerializedName("is_hidden_post")
    @Expose
    private Integer is_hidden_post;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("event_title")
    @Expose
    private String eventTitle;
    @SerializedName("event_link")
    @Expose
    private String eventLink;
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
    private String exactLocation;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("ticket_start_price")
    @Expose
    private Object ticketStartPrice;
    @SerializedName("ticket_end_price")
    @Expose
    private Object ticketEndPrice;
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
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("is_like_count")
    @Expose
    private Integer isLikeCount;
    @SerializedName("is_share_count")
    @Expose
    private Integer isShareCount;


    public Integer getIs_hidden_post() {
        return is_hidden_post;
    }

    public void setIs_hidden_post(Integer is_hidden_post) {
        this.is_hidden_post = is_hidden_post;
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

    public String getExactLocation() {
        return exactLocation;
    }

    public void setExactLocation(String exactLocation) {
        this.exactLocation = exactLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getTicketStartPrice() {
        return ticketStartPrice;
    }

    public void setTicketStartPrice(Object ticketStartPrice) {
        this.ticketStartPrice = ticketStartPrice;
    }

    public Object getTicketEndPrice() {
        return ticketEndPrice;
    }

    public void setTicketEndPrice(Object ticketEndPrice) {
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getIsLikeCount() {
        return isLikeCount;
    }

    public void setIsLikeCount(Integer isLikeCount) {
        this.isLikeCount = isLikeCount;
    }

    public Integer getIsShareCount() {
        return isShareCount;
    }

    public void setIsShareCount(Integer isShareCount) {
        this.isShareCount = isShareCount;
    }

}