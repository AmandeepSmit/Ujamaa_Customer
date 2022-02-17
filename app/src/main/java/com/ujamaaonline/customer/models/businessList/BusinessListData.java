package com.ujamaaonline.customer.models.businessList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ujamaaonline.customer.models.get_business_reviews.ReviewImage;

import java.io.Serializable;
import java.util.List;

public class BusinessListData implements Serializable {

    @SerializedName("business_id")
    @Expose
    private Integer id;

    @SerializedName("business_name")
    @Expose
    private String businessName;

    @SerializedName("business_contact")
    @Expose
    private String business_contact;


    @SerializedName("business_logo")
    @Expose
    private String business_logo;

    @SerializedName("message_status")
    @Expose
    private Integer messageStatus;

    @SerializedName("summery_line")
    @Expose
    private String summery_line;

    @SerializedName("avrg_rateing")
    @Expose
    private String avrg_rateing;

    @SerializedName("reviews_count")
    @Expose
    private Integer reviews_count;

    @SerializedName("current_working_status")
    @Expose
    private OpeningHoursFilter current_working_status;

    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("business_address")
    @Expose
    private String business_address;


    @SerializedName("business_website")
    @Expose
    private String business_website;


    @SerializedName("business_lat")
    @Expose
    private String business_lat;

    @SerializedName("business_long")
    @Expose
    private String business_long;

    public String getBusiness_contact() {
        return business_contact;
    }

    public Integer getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public String getSummery_line() {
        return summery_line;
    }

    public String getAvrg_rateing() {
        return avrg_rateing;
    }

    public Integer getReviews_count() {
        return reviews_count;
    }

    public OpeningHoursFilter getCurrent_working_status() {
        return current_working_status;
    }

    public String getDistance() {
        return distance;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public String getBusiness_website() {
        return business_website;
    }

    public String getBusiness_lat() {
        return business_lat;
    }

    public String getBusiness_long() {
        return business_long;
    }

    public String getBusiness_logo() {
        return business_logo;
    }
}
