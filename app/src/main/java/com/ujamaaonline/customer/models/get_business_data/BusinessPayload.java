package com.ujamaaonline.customer.models.get_business_data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusinessPayload {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("firstname")
    @Expose
    private String firstname;
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
    private List<WorkingHour> workingHours = null;
    @SerializedName("social_media")
    @Expose
    private SocialMedia socialMedia;
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
    private List<PaymentMethod> paymentMethods = null;
    @SerializedName("disabled_facilities")
    @Expose
    private List<DisabledFacility> disabledFacilities = null;
    @SerializedName("other_features")
    @Expose
    private List<OtherFeature> otherFeatures = null;
    @SerializedName("gallery_images")
    @Expose
    private List<GalleryImage> galleryImages = null;
    @SerializedName("avrg_rateing")
    @Expose
    private Object avrgRateing;
    @SerializedName("reviews_count")
    @Expose
    private Integer reviewsCount;
    @SerializedName("business_data")
    @Expose
    private BusinessData businessData;

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

    public List<WorkingHour> getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(List<WorkingHour> workingHours) {
        this.workingHours = workingHours;
    }

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
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

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<DisabledFacility> getDisabledFacilities() {
        return disabledFacilities;
    }

    public void setDisabledFacilities(List<DisabledFacility> disabledFacilities) {
        this.disabledFacilities = disabledFacilities;
    }

    public List<OtherFeature> getOtherFeatures() {
        return otherFeatures;
    }

    public void setOtherFeatures(List<OtherFeature> otherFeatures) {
        this.otherFeatures = otherFeatures;
    }

    public List<GalleryImage> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<GalleryImage> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public Object getAvrgRateing() {
        return avrgRateing;
    }

    public void setAvrgRateing(Object avrgRateing) {
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

