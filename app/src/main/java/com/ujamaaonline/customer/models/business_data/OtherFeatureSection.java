package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtherFeatureSection {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("user_id")
    @Expose
    private Integer user_id;


    @SerializedName("feature_id")
    @Expose
    private Integer feature_id;


    @SerializedName("created_at")
    @Expose
    private String created_at;


    @SerializedName("updated_at")
    @Expose
    private String updated_at;


    @SerializedName("other_featur")
    @Expose
    private OtherFeature other_featur;

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getFeature_id() {
        return feature_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public OtherFeature getOther_featur() {
        return other_featur;
    }
}
