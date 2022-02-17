package com.ujamaaonline.customer.models.search_category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatHeadingPayload {
    @SerializedName("have_heading")
    @Expose
    private Integer haveHeading;
    @SerializedName("category_data")
    @Expose
    private CategoryData categoryData;

    public Integer getHaveHeading() {
        return haveHeading;
    }

    public void setHaveHeading(Integer haveHeading) {
        this.haveHeading = haveHeading;
    }

    public CategoryData getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(CategoryData categoryData) {
        this.categoryData = categoryData;
    }

}
