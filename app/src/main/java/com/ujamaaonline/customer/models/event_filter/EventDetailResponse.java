package com.ujamaaonline.customer.models.event_filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventDetailResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("event_detail_payload")
    @Expose
    private EventDetailPayload eventDetailPayload;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public EventDetailPayload getEventDetailPayload() {
        return eventDetailPayload;
    }

    public void setEventDetailPayload(EventDetailPayload eventDetailPayload) {
        this.eventDetailPayload = eventDetailPayload;
    }

}