package com.ujamaaonline.customer.models.business_data;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryImage implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("is_liked")
    @Expose
    private Integer is_liked;

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("galler_image")
    @Expose
    private String gallerImage;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("hastag_count")
    @Expose
    private Integer hastagCount;
    @SerializedName("image_hastags")

    @Expose
    private List<Object> imageHastags = null;

    public Integer getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Integer is_liked) {
        this.is_liked = is_liked;
    }

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

    public String getGallerImage() {
        return gallerImage;
    }

    public void setGallerImage(String gallerImage) {
        this.gallerImage = gallerImage;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getHastagCount() {
        return hastagCount;
    }

    public void setHastagCount(Integer hastagCount) {
        this.hastagCount = hastagCount;
    }

    public List<Object> getImageHastags() {
        return imageHastags;
    }

    public void setImageHastags(List<Object> imageHastags) {
        this.imageHastags = imageHastags;
    }

}
