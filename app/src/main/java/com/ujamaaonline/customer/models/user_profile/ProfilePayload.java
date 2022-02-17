package com.ujamaaonline.customer.models.user_profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfilePayload implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("firstname")
    @Expose
    public String firstname;
    @SerializedName("lastname")
    @Expose
    public String lastname;

    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("mobile")
    @Expose
    public String mobile;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("postcode")
    @Expose
    public String postcode;
    @SerializedName("dob")
    @Expose
    public String dob;


    @SerializedName("customer_distance_unit")
    @Expose
    public String customer_distance_unit;

}
