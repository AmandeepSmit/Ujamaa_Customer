package com.ujamaaonline.customer.models.signup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EORegisterPayload implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("image")
    @Expose
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String firstname) {
        this.userName = firstname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
