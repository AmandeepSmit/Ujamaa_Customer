package com.ujamaaonline.customer.models.business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;

import java.util.List;

public class BusinessPayload {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("bookmarked")
    @Expose
    private Integer bookmarked;

    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("background_image")
    @Expose
    private String background_image;

    @SerializedName("business_logo")
    @Expose
    private String business_logo;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("profile_completed")
    @Expose
    private String profileCompleted;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("subscribed")
    @Expose
    private Integer subscribed;
    @SerializedName("summry_line")
    @Expose
    private SummryLine summryLine;
    @SerializedName("contact_info")
    @Expose
    private ContactInfo contactInfo;
    @SerializedName("working_hours")
    @Expose
    private WorkingHours workingHours = null;
    @SerializedName("social_media")
    @Expose
    private List<SocialMedia> socialMedia;
    @SerializedName("business_desc")
    @Expose
    private BusinessDesc businessDesc;
    @SerializedName("reputation_credential")
    @Expose
    private List<ReputationCredential> reputationCredential = null;
    @SerializedName("product_services")
    @Expose
    private List<ProductService> productServices = null;
    @SerializedName("meet_the_team")
    @Expose
    private List<MeetTheTeam> meetTheTeam = null;
    @SerializedName("payment_methods")
    @Expose
    private List<PaymentSection> paymentMethods = null;
    @SerializedName("disabled_facilities")
    @Expose
    private List<DisabledFacility> disabledFacilities = null;
    @SerializedName("other_features")
    @Expose
    private List<OtherFeatureSection> otherFeatures = null;
    @SerializedName("gallery_images")
    @Expose
    private List<SearchGalleryPayload> galleryImages = null;
    @SerializedName("avrg_rateing")
    @Expose
    private String avrgRateing;
    @SerializedName("reviews_count")
    @Expose
    private Integer reviewsCount;
    @SerializedName("business_data")
    @Expose
    private BusinessData businessData;


    public Integer getBookmarked() {
        return bookmarked;
    }

    public String getBusiness_logo() {
        return business_logo;
    }

    public String getBackground_image() {
        return background_image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(String profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Integer subscribed) {
        this.subscribed = subscribed;
    }

    public SummryLine getSummryLine() {
        return summryLine;
    }

    public void setSummryLine(SummryLine summryLine) {
        this.summryLine = summryLine;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public List<SocialMedia>  getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(List<SocialMedia> socialMedia) {
        this.socialMedia = socialMedia;
    }

    public BusinessDesc getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(BusinessDesc businessDesc) {
        this.businessDesc = businessDesc;
    }

    public List<ReputationCredential> getReputationCredential() {
        return reputationCredential;
    }

    public void setReputationCredential(List<ReputationCredential> reputationCredential) {
        this.reputationCredential = reputationCredential;
    }

    public List<ProductService> getProductServices() {
        return productServices;
    }

    public void setProductServices(List<ProductService> productServices) {
        this.productServices = productServices;
    }

    public List<MeetTheTeam> getMeetTheTeam() {
        return meetTheTeam;
    }

    public void setMeetTheTeam(List<MeetTheTeam> meetTheTeam) {
        this.meetTheTeam = meetTheTeam;
    }

    public List<PaymentSection> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentSection> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<DisabledFacility> getDisabledFacilities() {
        return disabledFacilities;
    }

    public void setDisabledFacilities(List<DisabledFacility> disabledFacilities) {
        this.disabledFacilities = disabledFacilities;
    }

    public List<OtherFeatureSection> getOtherFeatures() {
        return otherFeatures;
    }

    public void setOtherFeatures(List<OtherFeatureSection> otherFeatures) {
        this.otherFeatures = otherFeatures;
    }

    public List<SearchGalleryPayload> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<SearchGalleryPayload> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public String getAvrgRateing() {
        return avrgRateing;
    }

    public void setAvrgRateing(String avrgRateing) {
        this.avrgRateing = avrgRateing;
    }

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(Integer reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public BusinessData getBusinessData() {
        return businessData;
    }

    public void setBusinessData(BusinessData businessData) {
        this.businessData = businessData;
    }

}

