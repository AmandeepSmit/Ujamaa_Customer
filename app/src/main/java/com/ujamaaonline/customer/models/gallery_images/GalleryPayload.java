package com.ujamaaonline.customer.models.gallery_images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;

import java.util.List;

public class GalleryPayload {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("gallery_images")
    @Expose
    private List<SearchGalleryPayload> galleryImages = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<SearchGalleryPayload> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<SearchGalleryPayload> galleryImages) {
        this.galleryImages = galleryImages;
    }
}
