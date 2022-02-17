package com.ujamaaonline.customer.models.recent_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;

public class RecentSearchPayload {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("search_data")
    @Expose
    private FilterBusinessRequest searchData;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FilterBusinessRequest getSearchData() {
        return searchData;
    }

    public void setSearchData(FilterBusinessRequest searchData) {
        this.searchData = searchData;
    }

}
