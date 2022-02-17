package com.ujamaaonline.customer.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOLoginPayload {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user_data")
    @Expose
    private LoginUserData user_data;

    public LoginUserData getUser_data() {
        return user_data;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
