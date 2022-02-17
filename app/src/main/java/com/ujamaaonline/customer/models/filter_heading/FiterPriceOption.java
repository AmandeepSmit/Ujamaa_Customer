package com.ujamaaonline.customer.models.filter_heading;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FiterPriceOption {
    boolean isSelected = true;

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("short_name")
    @Expose
    public String shortName;
    @SerializedName("position")
    @Expose
    public Integer position;


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Integer getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public Integer getPosition() {
        return position;
    }
}
