package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Today {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("open_close_at")
    @Expose
    private String open_at;

    public String getStatus() {
        return status;
    }
    public String getOpen_at() {
        return open_at;
    }
}
