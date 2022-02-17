package com.ujamaaonline.customer.models.chat_models;

public class QuoteModel {
    private String VATInformation;
    private String businessAddress;
    private String businessTelephone;
    private String jobReferenceNumber;
    private String jobTitle;
    private String qouteDate;
    private String recipientName;
    private String summaryOfWork;
    private String typeOfCost;
    private Boolean vatRegistered;
    private String key;
    private String price;
    private  String businessEmail;

    public QuoteModel(){

    }

    public QuoteModel(String VATInformation, String businessAddress, String businessTelephone, String jobReferenceNumber, String jobTitle, String qouteDate, String recipientName, String summaryOfWork, String typeOfCost, Boolean vatRegistered, String key,String businessEmail,String price) {
        this.VATInformation = VATInformation;
        this.businessAddress = businessAddress;
        this.businessTelephone = businessTelephone;
        this.jobReferenceNumber = jobReferenceNumber;
        this.jobTitle = jobTitle;
        this.qouteDate = qouteDate;
        this.recipientName = recipientName;
        this.summaryOfWork = summaryOfWork;
        this.typeOfCost = typeOfCost;
        this.vatRegistered = vatRegistered;
        this.key=key;
        this.price=price;
        this.businessEmail=businessEmail;
    }

    public String getPrice() {
        return price;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public String getVATInformation() {
        return VATInformation;
    }

    public void setVATInformation(String VATInformation) {
        this.VATInformation = VATInformation;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessTelephone() {
        return businessTelephone;
    }

    public void setBusinessTelephone(String businessTelephone) {
        this.businessTelephone = businessTelephone;
    }

    public String getJobReferenceNumber() {
        return jobReferenceNumber;
    }

    public void setJobReferenceNumber(String jobReferenceNumber) {
        this.jobReferenceNumber = jobReferenceNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getQouteDate() {
        return qouteDate;
    }

    public void setQouteDate(String qouteDate) {
        this.qouteDate = qouteDate;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSummaryOfWork() {
        return summaryOfWork;
    }

    public void setSummaryOfWork(String summaryOfWork) {
        this.summaryOfWork = summaryOfWork;
    }

    public String getTypeOfCost() {
        return typeOfCost;
    }

    public void setTypeOfCost(String typeOfCost) {
        this.typeOfCost = typeOfCost;
    }

    public Boolean getVatRegistered() {
        return vatRegistered;
    }

    public void setVatRegistered(Boolean vatRegistered) {
        this.vatRegistered = vatRegistered;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
