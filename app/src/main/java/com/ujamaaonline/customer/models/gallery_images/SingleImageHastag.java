package com.ujamaaonline.customer.models.gallery_images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleImageHastag {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("img_id")
    @Expose
    public Integer imgId;
    @SerializedName("hastag")
    @Expose
    public String hastag;

    public Integer getId() {
        return id;
    }

    public Integer getImgId() {
        return imgId;
    }

    public String getHastag() {
        return hastag;
    }

    @Override
    public String toString() {
        return hastag.contains("#")?hastag:"#"+hastag;
    }
}
