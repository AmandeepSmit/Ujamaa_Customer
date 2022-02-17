package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DisableData {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("facilitie_name")
    @Expose
    private String facilitie_name;

    @SerializedName("position")
    @Expose
    private Integer position;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    public Integer getId() {
        return id;
    }

    public String getFacilitie_name() {
        return facilitie_name;
    }

    public Integer getPosition() {
        return position;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
