package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialMedia {
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("url")
    @Expose
    private String url;

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }
}
