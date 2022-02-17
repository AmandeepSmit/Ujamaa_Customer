package com.ujamaaonline.customer.models.search_gallery;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchGalleryResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("filter_count")
    @Expose
    public Integer filter_count;

    @SerializedName("payload")
    @Expose
    public List<SearchGalleryPayload> payload = null;


    public Integer getFilter_count() {
        return filter_count;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<SearchGalleryPayload> getPayload() {
        return payload;
    }
}