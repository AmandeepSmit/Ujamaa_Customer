package com.ujamaaonline.customer.models.get_business_data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialMedia {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("instagrm")
    @Expose
    private String instagrm;
    @SerializedName("youtube")
    @Expose
    private String youtube;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("pinterst")
    @Expose
    private String pinterst;
    @SerializedName("linkedin")
    @Expose
    private String linkedin;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

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

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagrm() {
        return instagrm;
    }

    public void setInstagrm(String instagrm) {
        this.instagrm = instagrm;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getPinterst() {
        return pinterst;
    }

    public void setPinterst(String pinterst) {
        this.pinterst = pinterst;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
