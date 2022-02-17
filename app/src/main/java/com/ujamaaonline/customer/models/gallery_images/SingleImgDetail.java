package com.ujamaaonline.customer.models.gallery_images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleImgDetail {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("galler_image")
    @Expose
    public String gallerImage;
    @SerializedName("position")
    @Expose
    public Integer position;
    @SerializedName("hastag_count")
    @Expose
    public Integer hastagCount;
    @SerializedName("image_hastags")
    @Expose
    public List<SingleImageHastag> imageHastags = null;

    public Integer getId() {
        return id;
    }
    public Integer getUserId() {
        return userId;
    }
    public String getGallerImage() {
        return gallerImage;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getHastagCount() {
        return hastagCount;
    }

    public List<SingleImageHastag> getImageHastags() {
        return imageHastags;
    }
}
