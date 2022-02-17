package com.ujamaaonline.customer.models.search_category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNotificationRequest {

    @SerializedName("get_message_noti")
    @Expose
    private Integer getMessageNoti;
    @SerializedName("subscribe_dis_deals")
    @Expose
    private Integer subscribeDisDeals;
    @SerializedName("subscribe_imp_updates")
    @Expose
    private Integer subscribeImpUpdates;
    @SerializedName("subscribe_genrl_updates")
    @Expose
    private Integer subscribeGenrlUpdates;
    @SerializedName("subscribe_events")
    @Expose
    private Integer subscribeEvents;
    @SerializedName("localfeed_dis_deals")
    @Expose
    private Integer localfeedDisDeals;
    @SerializedName("localfeed_imp_updates")
    @Expose
    private Integer localfeedImpUpdates;
    @SerializedName("localfeed_genrl_updates")
    @Expose
    private Integer localfeedGenrlUpdates;
    @SerializedName("localfeed_events")
    @Expose
    private Integer localfeedEvents;
    @SerializedName("someone_found_useful")
    @Expose
    private Integer someoneFoundUseful;

    public Integer getGetMessageNoti() {
        return getMessageNoti;
    }

    public void setGetMessageNoti(Integer getMessageNoti) {
        this.getMessageNoti = getMessageNoti;
    }

    public Integer getSubscribeDisDeals() {
        return subscribeDisDeals;
    }

    public void setSubscribeDisDeals(Integer subscribeDisDeals) {
        this.subscribeDisDeals = subscribeDisDeals;
    }

    public Integer getSubscribeImpUpdates() {
        return subscribeImpUpdates;
    }

    public void setSubscribeImpUpdates(Integer subscribeImpUpdates) {
        this.subscribeImpUpdates = subscribeImpUpdates;
    }

    public Integer getSubscribeGenrlUpdates() {
        return subscribeGenrlUpdates;
    }

    public void setSubscribeGenrlUpdates(Integer subscribeGenrlUpdates) {
        this.subscribeGenrlUpdates = subscribeGenrlUpdates;
    }

    public Integer getSubscribeEvents() {
        return subscribeEvents;
    }

    public void setSubscribeEvents(Integer subscribeEvents) {
        this.subscribeEvents = subscribeEvents;
    }

    public Integer getLocalfeedDisDeals() {
        return localfeedDisDeals;
    }

    public void setLocalfeedDisDeals(Integer localfeedDisDeals) {
        this.localfeedDisDeals = localfeedDisDeals;
    }

    public Integer getLocalfeedImpUpdates() {
        return localfeedImpUpdates;
    }

    public void setLocalfeedImpUpdates(Integer localfeedImpUpdates) {
        this.localfeedImpUpdates = localfeedImpUpdates;
    }

    public Integer getLocalfeedGenrlUpdates() {
        return localfeedGenrlUpdates;
    }

    public void setLocalfeedGenrlUpdates(Integer localfeedGenrlUpdates) {
        this.localfeedGenrlUpdates = localfeedGenrlUpdates;
    }

    public Integer getLocalfeedEvents() {
        return localfeedEvents;
    }

    public void setLocalfeedEvents(Integer localfeedEvents) {
        this.localfeedEvents = localfeedEvents;
    }

    public Integer getSomeoneFoundUseful() {
        return someoneFoundUseful;
    }

    public void setSomeoneFoundUseful(Integer someoneFoundUseful) {
        this.someoneFoundUseful = someoneFoundUseful;
    }

}