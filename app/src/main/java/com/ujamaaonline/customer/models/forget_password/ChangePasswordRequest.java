package com.ujamaaonline.business.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("old_passwprd")
    @Expose
    private String oldPasswprd;
    @SerializedName("new_password")
    @Expose
    private String newPassword;
    @SerializedName("logout_from_all")
    @Expose
    private Integer logoutFromAll;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("password_confirmation")
    @Expose
    private String passwordConfirmation;

    public ChangePasswordRequest(String oldPasswprd, String newPassword, Integer logoutFromAll) {
        this.oldPasswprd = oldPasswprd;
        this.newPassword = newPassword;
        this.logoutFromAll = logoutFromAll;
    }

    public ChangePasswordRequest(String userId,String password, String passwordConfirmation) {
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.userId=userId;
    }

    public String getOldPasswprd() {
        return oldPasswprd;
    }

    public void setOldPasswprd(String oldPasswprd) {
        this.oldPasswprd = oldPasswprd;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Integer getLogoutFromAll() {
        return logoutFromAll;
    }

    public void setLogoutFromAll(Integer logoutFromAll) {
        this.logoutFromAll = logoutFromAll;
    }
}
