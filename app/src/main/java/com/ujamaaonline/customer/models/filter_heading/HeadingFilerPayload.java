package com.ujamaaonline.customer.models.filter_heading;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeadingFilerPayload {
    boolean isSelected = false;


    public HeadingFilerPayload(String filterName) {
        this.filterName = filterName;
    }

    @SerializedName("filter_name")
    @Expose
    public String filterName;

    @SerializedName("slug")
    @Expose
    public String slug;

    @SerializedName("feature_id")
    @Expose
    public String featureId;

    @SerializedName("filter_icon")
    @Expose
    public String filterIcon;

    @SerializedName("filter_icon_selected")
    @Expose
    public String filterIconSelected;

    @SerializedName("price_options")
    @Expose
    public List<FiterPriceOption> priceOptions = null;


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFilterName() {
        return filterName;
    }

    public String getSlug() {
        return slug;
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getFilterIcon() {
        return filterIcon;
    }

    public String getFilterIconSelected() {
        return filterIconSelected;
    }

    public List<FiterPriceOption> getPriceOptions() {
        return priceOptions;
    }
}
