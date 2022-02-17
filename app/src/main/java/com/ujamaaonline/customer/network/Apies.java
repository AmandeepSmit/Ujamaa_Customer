package com.ujamaaonline.customer.network;

public interface Apies {

    String CHECK_USER_EXIST_URL = "auth/checkprofilelink?profilelink=";
    String ACTIVATE_ACCOUNT = "users/activate?otp=";
    String FORGET_PASSWORD = "auth/forgot-password?email=";
    String FORGET_PASSWORD_OPT = "auth/verify-otp?otp=";
    String FEATURED_TAB = "post/explore?";

}
