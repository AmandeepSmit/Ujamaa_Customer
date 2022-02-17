package com.ujamaaonline.customer.models.customer_own_reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerReviewsPayload {
    @SerializedName("total_posted_count")
    @Expose
    private Integer totalPostedCount;
    @SerializedName("review_with_img_count")
    @Expose
    private Integer reviewWithImgCount;
    @SerializedName("usefull_people_count")
    @Expose
    private Integer usefullPeopleCount;
    @SerializedName("all_reviewes")
    @Expose
    private List<CustomerAllReviewe> allReviewes = null;

    public Integer getTotalPostedCount() {
        return totalPostedCount;
    }

    public void setTotalPostedCount(Integer totalPostedCount) {
        this.totalPostedCount = totalPostedCount;
    }

    public Integer getReviewWithImgCount() {
        return reviewWithImgCount;
    }

    public void setReviewWithImgCount(Integer reviewWithImgCount) {
        this.reviewWithImgCount = reviewWithImgCount;
    }

    public Integer getUsefullPeopleCount() {
        return usefullPeopleCount;
    }

    public void setUsefullPeopleCount(Integer usefullPeopleCount) {
        this.usefullPeopleCount = usefullPeopleCount;
    }

    public List<CustomerAllReviewe> getAllReviewes() {
        return allReviewes;
    }

    public void setAllReviewes(List<CustomerAllReviewe> allReviewes) {
        this.allReviewes = allReviewes;
    }


}
