package com.ujamaaonline.customer.models.event_filter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventFilterCatResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data_payload")
    @Expose
    private List<EventDataPayload> dataPayload = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<EventDataPayload> getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(List<EventDataPayload> dataPayload) {
        this.dataPayload = dataPayload;
    }

}