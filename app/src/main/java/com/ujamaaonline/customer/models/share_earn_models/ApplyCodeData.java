package com.ujamaaonline.customer.models.share_earn_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplyCodeData {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("result")
    @Expose
    private ApplyCodeResults result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ApplyCodeResults getResult() {
        return result;
    }

    public void setResult(ApplyCodeResults result) {
        this.result = result;
    }


}
