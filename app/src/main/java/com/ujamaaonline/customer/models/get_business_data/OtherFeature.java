package com.ujamaaonline.customer.models.get_business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtherFeature {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("feature_icon")
    @Expose
    private String featureIcon;
    @SerializedName("feature_name")
    @Expose
    private String featureName;
    @SerializedName("position")
    @Expose
    private Integer position;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeatureIcon() {
        return featureIcon;
    }

    public void setFeatureIcon(String featureIcon) {
        this.featureIcon = featureIcon;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}