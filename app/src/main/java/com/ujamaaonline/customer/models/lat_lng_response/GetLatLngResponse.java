package com.ujamaaonline.customer.models.lat_lng_response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetLatLngResponse {

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("result")
    @Expose
    private LatLngResult result;


    public String getError() {
        return error;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LatLngResult getResult() {
        return result;
    }


}