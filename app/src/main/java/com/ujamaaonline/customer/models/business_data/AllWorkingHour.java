package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllWorkingHour {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("day_type")
    @Expose
    private String dayType;
    @SerializedName("day_name")
    @Expose
    private String dayName;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("second_start_time")
    @Expose
    private String secondStartTime;
    @SerializedName("second_end_time")
    @Expose
    private String secondEndTime;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSecondStartTime() {
        return secondStartTime;
    }

    public void setSecondStartTime(String secondStartTime) {
        this.secondStartTime = secondStartTime;
    }

    public String getSecondEndTime() {
        return secondEndTime;
    }

    public void setSecondEndTime(String secondEndTime) {
        this.secondEndTime = secondEndTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}