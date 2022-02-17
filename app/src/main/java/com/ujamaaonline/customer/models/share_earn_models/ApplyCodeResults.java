package com.ujamaaonline.customer.models.share_earn_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApplyCodeResults {
    @SerializedName("credit")
    @Expose
    private String credit;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("amount_off")
    @Expose
    private Integer amountOff;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("duration_in_months")
    @Expose
    private Object durationInMonths;
    @SerializedName("livemode")
    @Expose
    private Boolean livemode;
    @SerializedName("max_redemptions")
    @Expose
    private Object maxRedemptions;
    @SerializedName("metadata")
    @Expose
    private List<Object> metadata = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("percent_off")
    @Expose
    private Object percentOff;
    @SerializedName("redeem_by")
    @Expose
    private Object redeemBy;
    @SerializedName("times_redeemed")
    @Expose
    private Integer timesRedeemed;
    @SerializedName("valid")
    @Expose
    private Boolean valid;


    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getAmountOff() {
        return amountOff;
    }

    public void setAmountOff(Integer amountOff) {
        this.amountOff = amountOff;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Object getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Object durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public void setLivemode(Boolean livemode) {
        this.livemode = livemode;
    }

    public Object getMaxRedemptions() {
        return maxRedemptions;
    }

    public void setMaxRedemptions(Object maxRedemptions) {
        this.maxRedemptions = maxRedemptions;
    }

    public List<Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Object> metadata) {
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(Object percentOff) {
        this.percentOff = percentOff;
    }

    public Object getRedeemBy() {
        return redeemBy;
    }

    public void setRedeemBy(Object redeemBy) {
        this.redeemBy = redeemBy;
    }

    public Integer getTimesRedeemed() {
        return timesRedeemed;
    }

    public void setTimesRedeemed(Integer timesRedeemed) {
        this.timesRedeemed = timesRedeemed;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

}
