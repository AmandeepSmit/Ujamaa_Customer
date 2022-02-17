package com.ujamaaonline.customer.models.search_gallery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchGalleryPayload implements Serializable {

    @SerializedName(value = "liked_img", alternate = {"is_liked"})
    @Expose
    public Integer likedImg;
    @SerializedName("business_id")
    @Expose
    public Integer businessId;
    @SerializedName(value = "img_id", alternate = {"id"})
    @Expose
    public Integer imgId;

    @SerializedName(value = "img", alternate = {"galler_image"})
    @Expose
    public String img;

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    public void setLikedImg(Integer likedImg) {
        this.likedImg = likedImg;
    }

    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("hastag_count")
    @Expose
    private Integer hastagCount;
    @SerializedName("image_hastags")
    @Expose
    private List<Object> imageHastags = null;

    public Integer getLikedImg() {
        return likedImg;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public Integer getImgId() {
        return imgId;
    }


    public Integer getUserId() {
        return userId;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getHastagCount() {
        return hastagCount;
    }

    public List<Object> getImageHastags() {
        return imageHastags;
    }

    public String getImg() {
        return img;
    }
}
