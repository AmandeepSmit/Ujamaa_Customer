package com.ujamaaonline.customer.models.user_profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateProfileRequest {

    @SerializedName("firstname")
    @Expose
    private RequestBody firstname;
    @SerializedName("lastname")
    @Expose
    private RequestBody lastname;
    @SerializedName("postcode")
    @Expose
    private RequestBody postcode;
    @SerializedName("mobile")
    @Expose
    private RequestBody mobile;


    @SerializedName("gender")
    @Expose
    private RequestBody gender;

    public UpdateProfileRequest(RequestBody firstname, RequestBody lastname, RequestBody postcode, RequestBody mobile, RequestBody gender) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.postcode = postcode;
        this.mobile = mobile;
        this.gender = gender;
    }


}