package com.ujamaaonline.customer.models.terms_of_use;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsOfUsePayload {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("c_terms_of_use")
    @Expose
    private String cTermsOfUse;
    @SerializedName("c_privace_policy")
    @Expose
    private String cPrivacePolicy;
    @SerializedName("b_terms_of_use")
    @Expose
    private String bTermsOfUse;
    @SerializedName("b_privace_policy")
    @Expose
    private String bPrivacePolicy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getcTermsOfUse() {
        return cTermsOfUse;
    }

    public void setcTermsOfUse(String cTermsOfUse) {
        this.cTermsOfUse = cTermsOfUse;
    }

    public String getcPrivacePolicy() {
        return cPrivacePolicy;
    }

    public void setcPrivacePolicy(String cPrivacePolicy) {
        this.cPrivacePolicy = cPrivacePolicy;
    }

    public String getbTermsOfUse() {
        return bTermsOfUse;
    }

    public void setbTermsOfUse(String bTermsOfUse) {
        this.bTermsOfUse = bTermsOfUse;
    }

    public String getbPrivacePolicy() {
        return bPrivacePolicy;
    }

    public void setbPrivacePolicy(String bPrivacePolicy) {
        this.bPrivacePolicy = bPrivacePolicy;
    }
}
