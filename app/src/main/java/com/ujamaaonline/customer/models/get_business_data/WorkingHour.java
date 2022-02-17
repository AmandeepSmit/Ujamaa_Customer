package com.ujamaaonline.customer.models.get_business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkingHour {

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
    private Object startTime;
    @SerializedName("end_time")
    @Expose
    private Object endTime;
    @SerializedName("second_start_time")
    @Expose
    private Object secondStartTime;
    @SerializedName("second_end_time")
    @Expose
    private Object secondEndTime;
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

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public Object getSecondStartTime() {
        return secondStartTime;
    }

    public void setSecondStartTime(Object secondStartTime) {
        this.secondStartTime = secondStartTime;
    }

    public Object getSecondEndTime() {
        return secondEndTime;
    }

    public void setSecondEndTime(Object secondEndTime) {
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