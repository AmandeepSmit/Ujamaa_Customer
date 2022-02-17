package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetBusinessRequest {

    @SerializedName("is_local")
    @Expose
    private Integer isLocal;


    @SerializedName("is_hidden")
    @Expose
    private Integer is_hidden;




    @SerializedName("is_subscriber")
    @Expose
    private Integer isSubscriber;
    @SerializedName("is_event")
    @Expose
    private Integer isEvent;
    @SerializedName("is_discount")
    @Expose
    private Integer isDiscount;
    @SerializedName("is_important")
    @Expose
    private Integer isImportant;
    @SerializedName("is_general")
    @Expose
    private Integer isGeneral;
    @SerializedName("category_id")
    @Expose
    private String categoryId;

    @SerializedName("is_liked")
    @Expose
    private Integer is_liked;

    @SerializedName("business_id")
    @Expose
    private String businessId;


    @SerializedName("img_id")
    @Expose
    private Integer img_id;

    @SerializedName("hastag")
    @Expose
    private String hastag;


    @SerializedName("customer_id")
    @Expose
    private String customer_id;


    @SerializedName("cat_id")
    @Expose
    private String cat_id;


    public void setIs_hidden(Integer is_hidden) {
        this.is_hidden = is_hidden;
    }

    public void setIs_liked(Integer is_liked) {
        this.is_liked = is_liked;
    }

    public void setIsLocal(Integer isLocal) {
        this.isLocal = isLocal;
    }

    public void setIsSubscriber(Integer isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public void setIsEvent(Integer isEvent) {
        this.isEvent = isEvent;
    }

    public void setIsDiscount(Integer isDiscount) {
        this.isDiscount = isDiscount;
    }

    public void setIsImportant(Integer isImportant) {
        this.isImportant = isImportant;
    }

    public void setIsGeneral(Integer isGeneral) {
        this.isGeneral = isGeneral;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setImg_id(Integer img_id) {
        this.img_id = img_id;
    }

    public void setHastag(String hastag) {
        this.hastag = hastag;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public GetBusinessRequest(String businessId) {
        this.businessId = businessId;
    }

    public GetBusinessRequest() {
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

}
