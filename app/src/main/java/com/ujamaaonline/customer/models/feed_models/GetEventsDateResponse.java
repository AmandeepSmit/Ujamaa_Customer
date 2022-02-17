package com.ujamaaonline.customer.models.feed_models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetEventsDateResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("event_date_payload")
    @Expose
    private List<String> eventDatePayload = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getEventDatePayload() {
        return eventDatePayload;
    }

    public void setEventDatePayload(List<String> eventDatePayload) {
        this.eventDatePayload = eventDatePayload;
    }

}