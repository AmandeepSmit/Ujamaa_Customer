package com.ujamaaonline.customer.models.recent_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentSearchData {
    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("main_cat_id")
    @Expose
    private Integer mainCatId;
    @SerializedName("store_search")
    @Expose
    private Integer storeSearch;
    @SerializedName("type")
    @Expose
    private Integer type;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getMainCatId() {
        return mainCatId;
    }

    public void setMainCatId(Integer mainCatId) {
        this.mainCatId = mainCatId;
    }

    public Integer getStoreSearch() {
        return storeSearch;
    }

    public void setStoreSearch(Integer storeSearch) {
        this.storeSearch = storeSearch;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
