package com.ujamaaonline.customer.models.get_business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("company_number")
    @Expose
    private String companyNumber;
    @SerializedName("non_profit")
    @Expose
    private String nonProfit;
    @SerializedName("book_now")
    @Expose
    private Object bookNow;
    @SerializedName("order_now")
    @Expose
    private Object orderNow;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getNonProfit() {
        return nonProfit;
    }

    public void setNonProfit(String nonProfit) {
        this.nonProfit = nonProfit;
    }

    public Object getBookNow() {
        return bookNow;
    }

    public void setBookNow(Object bookNow) {
        this.bookNow = bookNow;
    }

    public Object getOrderNow() {
        return orderNow;
    }

    public void setOrderNow(Object orderNow) {
        this.orderNow = orderNow;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}