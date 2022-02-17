package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ujamaaonline.customer.models.get_business_data.PaymentMethod;

public class PaymentSection {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("user_id")
    @Expose
    private Integer user_id;

    @SerializedName("payment_method")
    @Expose
    private String payment_method;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;


    @SerializedName("payment")
    @Expose
    private PaymentMethod payment;

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public PaymentMethod getPayment() {
        return payment;
    }
}
