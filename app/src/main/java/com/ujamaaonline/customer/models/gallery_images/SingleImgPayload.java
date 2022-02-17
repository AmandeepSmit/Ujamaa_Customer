package com.ujamaaonline.customer.models.gallery_images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleImgPayload {
    @SerializedName("liked_img")
    @Expose
    public Integer likedImg;
    @SerializedName("business_id")
    @Expose
    public Integer businessId;
    @SerializedName("business_name")
    @Expose
    public String businessName;
    @SerializedName("img")
    @Expose
    public SingleImgDetail img;

    public Integer getBusinessId() {
        return businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public SingleImgDetail getImg() {
        return img;
    }
}
