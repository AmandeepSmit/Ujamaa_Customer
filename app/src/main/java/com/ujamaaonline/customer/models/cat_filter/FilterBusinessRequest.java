package com.ujamaaonline.customer.models.cat_filter;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterBusinessRequest implements Serializable {

    @SerializedName("type")
    @Expose
    public Integer type;
    @SerializedName("store_search")
    @Expose
    public Integer store_search;
    @SerializedName("search")
    @Expose
    public String search;

    @SerializedName("open_at")
    @Expose
    public String open_at;

    @SerializedName("hastag")
    @Expose
    public String hastag;

    @SerializedName("main_cat_id")
    @Expose
    public Integer mainCatId;
    @SerializedName("sub_cat_ids")
    @Expose
    public List<Integer> subCatIds = null;
    @SerializedName("price")
    @Expose
    public Integer priceRange;
    @SerializedName("open_now")
    @Expose
    public Integer openNow;
    @SerializedName("subscribed")
    @Expose
    public Integer subscribed;
    @SerializedName("in_my_bookmarks")
    @Expose
    public Integer inMyBookmark;
    @SerializedName("in_my_image_likes")
    @Expose
    public Integer inMyImgLike;
    @SerializedName("customer_id")
    @Expose
    public Integer customerId;
    @SerializedName("have_location")
    @Expose
    public Integer haveLocation;
    @SerializedName("have_reviews")
    @Expose
    public Integer haveReviews;
    @SerializedName("have_website")
    @Expose
    public Integer haveWebsite;
    @SerializedName("have_contact_no")
    @Expose
    public Integer haveContactNo;
    @SerializedName("message_available")
    @Expose
    public Integer messageAvailable;
    @SerializedName("have_disabled_facilitie")
    @Expose
    public Integer haveDisabledFacilitie;
    @SerializedName("have_reputational_cred")
    @Expose
    public Integer haveReputationalCred;
    @SerializedName("age_restriction")
    @Expose
    public Integer ageRestriction;
    @SerializedName("other_features")
    @Expose
    public List<Integer> otherFeatures = null;
    @SerializedName("customer_lat")
    @Expose
    public String customerLat;
    @SerializedName("customer_long")
    @Expose
    public String customerLong;
    @SerializedName("orderBY")
    @Expose
    public Integer orderBY;

    public void setStore_search(Integer store_search) {
        this.store_search = store_search;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setMainCatId(Integer mainCatId) {
        this.mainCatId = mainCatId;
    }

    public void setSubCatIds(List<Integer> subCatIds) {
        this.subCatIds = subCatIds;
    }

    public void setPriceRange(Integer priceRange) {
        this.priceRange = priceRange;
    }

    public void setOpenNow(Integer openNow) {
        this.openNow = openNow;
    }

    public void setSubscribed(Integer subscribed) {
        this.subscribed = subscribed;
    }

    public void setInMyBookmark(Integer inMyBookmark) {
        this.inMyBookmark = inMyBookmark;
    }

    public void setInMyImgLike(Integer inMyImgLike) {
        this.inMyImgLike = inMyImgLike;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public void setHaveLocation(Integer haveLocation) {
        this.haveLocation = haveLocation;
    }

    public void setHaveReviews(Integer haveReviews) {
        this.haveReviews = haveReviews;
    }

    public void setHaveWebsite(Integer haveWebsite) {
        this.haveWebsite = haveWebsite;
    }

    public void setHaveContactNo(Integer haveContactNo) {
        this.haveContactNo = haveContactNo;
    }

    public void setMessageAvailable(Integer messageAvailable) {
        this.messageAvailable = messageAvailable;
    }

    public void setHaveDisabledFacilitie(Integer haveDisabledFacilitie) {
        this.haveDisabledFacilitie = haveDisabledFacilitie;
    }

    public void setHaveReputationalCred(Integer haveReputationalCred) {
        this.haveReputationalCred = haveReputationalCred;
    }
    public void setOpen_at(String open_at) {
        this.open_at = open_at;
    }
    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public void setOtherFeatures(List<Integer> otherFeatures) {
        this.otherFeatures = otherFeatures;
    }

    public void setCustomerLat(String customerLat) {
        this.customerLat = customerLat;
    }

    public void setCustomerLong(String customerLong) {
        this.customerLong = customerLong;
    }

    public void setOrderBY(Integer orderBY) {
        this.orderBY = orderBY;
    }
}