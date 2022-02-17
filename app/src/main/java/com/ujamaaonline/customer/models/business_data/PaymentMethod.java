package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentMethod {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("payment_icon")
    @Expose
    private String paymentIcon;
    @SerializedName("payment_name")
    @Expose
    private String paymentName;
    @SerializedName("position")
    @Expose
    private Integer position;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaymentIcon() {
        return paymentIcon;
    }

    public void setPaymentIcon(String paymentIcon) {
        this.paymentIcon = paymentIcon;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}