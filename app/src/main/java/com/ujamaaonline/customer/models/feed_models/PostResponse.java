package com.ujamaaonline.customer.models.feed_models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("filter_count")
    @Expose
    private Integer filter_count;


    @SerializedName(value = "post_payload", alternate = {"search_payload","location_payload"})
    @Expose
    private List<PostPayload> postPayload = null;

    @SerializedName(value = "event_payloads", alternate = {"event_calender_payload","event_location_payload"})
    @Expose
    private List<EventPayload> eventPayload = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<PostPayload> getPostPayload() {
        return postPayload;
    }
    public List<EventPayload> getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(List<EventPayload> eventPayload) {
        this.eventPayload = eventPayload;
    }

    public Integer getFilter_count() {
        return filter_count;
    }
}
