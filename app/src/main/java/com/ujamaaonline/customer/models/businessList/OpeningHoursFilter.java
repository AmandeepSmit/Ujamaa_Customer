package com.ujamaaonline.customer.models.businessList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpeningHoursFilter implements Serializable {
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("open_close_at")
    @Expose
    public String openCloseAt;

    public String getStatus() {
        return status;
    }

    public String getOpenCloseAt() {
        return openCloseAt;
    }
}