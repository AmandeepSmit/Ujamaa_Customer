package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkingHours {
    @SerializedName("today")
    @Expose
    private Today today;
    @SerializedName("all_working_hours")
    @Expose
    List<AllWorkingHour> all_working_hours=null;

    public Today getToday() {
        return today;
    }
    public List<AllWorkingHour> getAll_working_hours() {
        return all_working_hours;
    }
}
