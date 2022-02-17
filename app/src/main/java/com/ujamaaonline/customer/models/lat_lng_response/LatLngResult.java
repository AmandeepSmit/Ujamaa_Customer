package com.ujamaaonline.customer.models.lat_lng_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatLngResult {
    @SerializedName("postcode")
    @Expose
    private String postcode;
    @SerializedName("quality")
    @Expose
    private Integer quality;
    @SerializedName("eastings")
    @Expose
    private Integer eastings;
    @SerializedName("northings")
    @Expose
    private Integer northings;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("nhs_ha")
    @Expose
    private String nhsHa;
    @SerializedName("longitude")
    @Expose
    private Float longitude;
    @SerializedName("latitude")
    @Expose
    private Float latitude;
    @SerializedName("european_electoral_region")
    @Expose
    private String europeanElectoralRegion;
    @SerializedName("primary_care_trust")
    @Expose
    private String primaryCareTrust;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("lsoa")
    @Expose
    private String lsoa;
    @SerializedName("msoa")
    @Expose
    private String msoa;
    @SerializedName("incode")
    @Expose
    private String incode;
    @SerializedName("outcode")
    @Expose
    private String outcode;
    @SerializedName("parliamentary_constituency")
    @Expose
    private String parliamentaryConstituency;
    @SerializedName("admin_district")
    @Expose
    private String adminDistrict;
    @SerializedName("parish")
    @Expose
    private String parish;
    @SerializedName("admin_county")
    @Expose
    private Object adminCounty;
    @SerializedName("admin_ward")
    @Expose
    private String adminWard;
    @SerializedName("ced")
    @Expose
    private Object ced;
    @SerializedName("ccg")
    @Expose
    private String ccg;
    @SerializedName("nuts")
    @Expose
    private String nuts;

    public String getPostcode() {
        return postcode;
    }

    public Integer getQuality() {
        return quality;
    }

    public Integer getEastings() {
        return eastings;
    }

    public Integer getNorthings() {
        return northings;
    }

    public String getCountry() {
        return country;
    }

    public String getNhsHa() {
        return nhsHa;
    }

    public Float getLongitude() {
        return longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public String getEuropeanElectoralRegion() {
        return europeanElectoralRegion;
    }

    public String getPrimaryCareTrust() {
        return primaryCareTrust;
    }

    public String getRegion() {
        return region;
    }

    public String getLsoa() {
        return lsoa;
    }

    public String getMsoa() {
        return msoa;
    }

    public String getIncode() {
        return incode;
    }

    public String getOutcode() {
        return outcode;
    }
    public String getParliamentaryConstituency() {
        return parliamentaryConstituency;
    }

    public String getAdminDistrict() {
        return adminDistrict;
    }

    public String getParish() {
        return parish;
    }

    public Object getAdminCounty() {
        return adminCounty;
    }

    public String getAdminWard() {
        return adminWard;
    }

    public Object getCed() {
        return ced;
    }

    public String getCcg() {
        return ccg;
    }

    public String getNuts() {
        return nuts;
    }

}
