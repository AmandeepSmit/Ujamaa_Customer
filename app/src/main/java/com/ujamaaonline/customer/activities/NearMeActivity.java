package com.ujamaaonline.customer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListData;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.business_data.OtherFeature;
import com.ujamaaonline.customer.models.cat_filter.AllOtherFeaturesResponse;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;
import com.ujamaaonline.customer.models.cat_filter.RelatedItem;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.filter_heading.FiterPriceOption;
import com.ujamaaonline.customer.models.filter_heading.HeadingFilerPayload;
import com.ujamaaonline.customer.models.filter_heading.HeadingFilterResponse;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;
import static com.ujamaaonline.customer.utils.Constants.USER_POST_CODE;

public class NearMeActivity extends AppCompatActivity implements View.OnClickListener {
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken, catName, fromType;
    private Integer customerId;
    private LinearLayout toolbarLaout;
    private Toolbar toolbar;
    private RecyclerView recyclerView, recRelatedCat, recOtherFeature, recHeading;
    private List<BusinessListData> bList = new ArrayList<>();
    private BusinessCategoryAdapter businessCategoryAdapter;
    private String businessCatId, type;
    private Integer priceRange = 1, sortType = 1;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet, layoutFilter, layoutSearchTab, layoutProfileTab;
    private List<OtherFeature> listOtherFeatures = new ArrayList<>();
    private List<RelatedItem> relateditemList = new ArrayList<>();
    private FilterBusinessRequest filterBusinessRequest;
    private FilterBusinessRequest previousSelected;
    private boolean isLoggedIn = false;
    private ImageView imgBack;
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatListTemp = new ArrayList<>();
    private List<Integer> selectedCatList = new ArrayList<>();
    private LinearLayout layoutSort;
    private String sort = "0";
    private String postCode="";
    private DecimalFormat formater = new DecimalFormat("0.0");
    private List<HeadingFilerPayload> headingFilterList = new ArrayList<>();
    private TextView tvRelevance, tvDistance, tvHighRated, tvReviewed, selectedTab, priceTypeOne, priceTypeTwo, priceTypeThree, priceTypeFour, selectedPriceType, tvOpenAt, tvMinAge,
            tvOpenNow,tvNearMe, selectedOpenType, tvLocation, tvReviews, tvWebsite, tvVideo, tvContactNumber, tvMessage, tvReputation, tvDisabledFacility, tvApply, tvEmptyRewviews, tvTitle, tvFilter, tvSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);
        if (getIntent().hasExtra("filter_data")) {
            previousSelected = (FilterBusinessRequest) getIntent().getSerializableExtra("filter_pre");
            filterBusinessRequest = (FilterBusinessRequest) getIntent().getSerializableExtra("filter_data");
            if (getIntent().hasExtra("post"))
            {
               postCode=getIntent().getStringExtra("post");
            }
        }
        initViews();
        setAllClickLisneners();
    }

    private void setAllClickLisneners() {
        tvReviewed.setOnClickListener(this);
        tvDistance.setOnClickListener(this);
        tvHighRated.setOnClickListener(this);
        tvRelevance.setOnClickListener(this);
        priceTypeOne.setOnClickListener(this);
        priceTypeTwo.setOnClickListener(this);
        priceTypeThree.setOnClickListener(this);
        priceTypeFour.setOnClickListener(this);
        tvOpenNow.setOnClickListener(this);
        findViewById(R.id.layout_map).setOnClickListener(this);
        tvOpenAt.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        tvReviews.setOnClickListener(this);
        tvWebsite.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvMinAge.setOnClickListener(this);
        tvContactNumber.setOnClickListener(this);
        tvMessage.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        tvReputation.setOnClickListener(this);
        tvDisabledFacility.setOnClickListener(this);
        layoutBottomSheet.setOnClickListener(this);
        tvFilter.setOnClickListener(this);
        findViewById(R.id.tv_reset).setOnClickListener(this);
    }

    private void initViews() {
        this.progress = new GlobalProgressDialog(NearMeActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        isLoggedIn = this.loginPreferences.getBoolean(IS_LOGGED_IN, false);
        if (TextUtils.isEmpty(postCode))
        {
            postCode=loginPreferences.getString(USER_POST_CODE,"");
        }
        businessCatId = getIntent().getStringExtra("businessId");
        catName = getIntent().getStringExtra("cat_name");
        customerId = loginPreferences.getInt(USER_ID, 0);
        recOtherFeature = findViewById(R.id.rec_other_feature);
        recOtherFeature.setAdapter(new OtherFeaturesAdapter(listOtherFeatures));
        layoutBottomSheet = findViewById(R.id.filter_bottom_sheet_cat);
        recRelatedCat = findViewById(R.id.rec_related_cat);
        toolbar = findViewById(R.id.toolbar);
        layoutSort = findViewById(R.id.layout_sort);
        layoutFilter = findViewById(R.id.layout_filter);
        tvRelevance = findViewById(R.id.tv_relevance);
        tvDistance = findViewById(R.id.tv_distance);
        tvNearMe=findViewById(R.id.tv_near_me);
        tvFilter = findViewById(R.id.tv_filter);
        tvSortBy = findViewById(R.id.tv_sort_by);
        toolbarLaout = findViewById(R.id.toolbar_layout);
        tvHighRated = findViewById(R.id.tv_rated);
        tvMinAge = findViewById(R.id.tv_min_age);
        layoutSearchTab = findViewById(R.id.layout_search_tab);
        layoutProfileTab = findViewById(R.id.layout_profile_tab);
        tvReviewed = findViewById(R.id.tv_reviewed);
        priceTypeOne = findViewById(R.id.tv_price_one);
        imgBack = findViewById(R.id.img_back);
        tvTitle = findViewById(R.id.tv_cat_name_search);
        tvTitle.setText(catName);
        tvApply = findViewById(R.id.tv_apply);
        recHeading = findViewById(R.id.rec_heading);
        recHeading.setHasFixedSize(true);
        recHeading.setAdapter(new HeadingAdpater(headingFilterList));
        tvOpenAt = findViewById(R.id.tv_open_at);
        tvEmptyRewviews = findViewById(R.id.tv_empty_data);
        tvOpenNow = findViewById(R.id.tv_open_now);
        priceTypeTwo = findViewById(R.id.tv_price_two);
        priceTypeThree = findViewById(R.id.tv_price_three);
        priceTypeFour = findViewById(R.id.tv_price_four);
        tvLocation = findViewById(R.id.tv_location);
        tvNearMe.setText(postCode);
        tvReviews = findViewById(R.id.tv_reviews);
        tvWebsite = findViewById(R.id.tv_website);
        tvVideo = findViewById(R.id.tv_video);
        tvContactNumber = findViewById(R.id.tv_cont_number);
        tvMessage = findViewById(R.id.tv_msg_avail);
        tvReputation = findViewById(R.id.tv_rep_cred);
        tvDisabledFacility = findViewById(R.id.tv_disable_fac);
        layoutFilter.setOnClickListener(this);
        addStaticData();
        recRelatedCat.setAdapter(new RelatedCatAdapter(relateditemList));
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        recyclerView = findViewById(R.id.nm_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        type = getIntent().getStringExtra("type");
        changeTab(tvRelevance);
        if (getIntent().hasExtra("profile")) {
            fromType = "profile";
            toolbar.setVisibility(View.VISIBLE);
            layoutProfileTab.setVisibility(View.VISIBLE);
            toolbarLaout.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            tvLocation.setVisibility(View.GONE);
            layoutSearchTab.setVisibility(View.GONE);
        } else {
            fromType = "";
            toolbar.setVisibility(View.GONE);
            layoutProfileTab.setVisibility(View.GONE);
            layoutSearchTab.setVisibility(View.VISIBLE);
            toolbarLaout.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvLocation.setVisibility(View.VISIBLE);
        }
        layoutSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(NearMeActivity.this);
                View sheetView = getLayoutInflater().inflate(R.layout.dialog_sort_reviews, null);
                mBottomSheetDialog.setContentView(sheetView);
                sheetView.findViewById(R.id.layout_most_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "0";
                        tvSortBy.setText("Most Recent");
                        getBusinessList(false);
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_hightest_rating).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "1";
                        tvSortBy.setText("Highest Rating");
                        getBusinessList(false);
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_most_useful).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "2";
                        tvSortBy.setText("Most Useful");
                        getBusinessList(false);
                        mBottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.tv_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });

                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                mBottomSheetDialog.show();
            }
        });
        if (!fromType.equalsIgnoreCase("profile")) {
            getOtherFeatures();
            getHeadings();
            addValue();
        }
        getBusinessList(false);
        getFilterCat();
    }

    private void addStaticData() {
        relateditemList.add(new RelatedItem("You are subscribed to them"));
        relateditemList.add(new RelatedItem("You've bookmarked them"));
        relateditemList.add(new RelatedItem("You've liked an image from them"));
//        relateditemList.add("They are in lists you've made or follow");
    }

    private void changePriceType(TextView newTab) {
        if (selectedPriceType != null) {
            if (selectedPriceType.equals(newTab)) {
                selectedPriceType.setBackgroundColor(getResources().getColor(R.color.white));
                selectedPriceType.setTextColor(getResources().getColor(R.color.black));
                checkType(newTab.getText().toString(), false);
                selectedPriceType = null;
                return;
            }
            checkType(newTab.getText().toString(), false);
            selectedPriceType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedPriceType.setTextColor(getResources().getColor(R.color.black));
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        }
        checkType(newTab.getText().toString(), true);
        selectedPriceType = newTab;
    }

    private void changePriceRange(int range) {
        switch (range) {
            case 1:
                changePriceType(priceTypeOne);
                break;
            case 2:
                changePriceType(priceTypeTwo);
                break;
            case 3:
                changePriceType(priceTypeThree);
                break;
            case 4:
                changePriceType(priceTypeFour);
                break;
        }
    }

    private void changeOpenType(TextView newTab) {
        if (selectedOpenType != null) {
            if (selectedOpenType.equals(newTab)) {
                selectedOpenType.setBackgroundColor(getResources().getColor(R.color.white));
                selectedOpenType.setTextColor(getResources().getColor(R.color.black));
                checkType(newTab.getText().toString(), false);
                selectedOpenType = null;
                return;
            }
            selectedOpenType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedOpenType.setTextColor(getResources().getColor(R.color.black));
            checkType(selectedOpenType.getText().toString(), false);
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        }
        checkType(newTab.getText().toString(), true);
        selectedOpenType = newTab;
    }

    private Dialog dialog;

    private void ageDialog() {
        dialog = new Dialog(NearMeActivity.this);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_age_select);
        RecyclerView ageRecyler = dialog.findViewById(R.id.ra_ageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NearMeActivity.this);
        ageRecyler.setLayoutManager(linearLayoutManager);
        ageRecyler.setHasFixedSize(true);
        AgeAdapter ageAdapter = new AgeAdapter(ageList);
        ageRecyler.setAdapter(ageAdapter);
        dialog.show();
    }

    List<String> ageList = new ArrayList<>();

    private void addValue() {
        ageList.add("None");
        for (int i = 10; i < 100; i++) {
            ageList.add(String.valueOf(i));
        }
    }

    private void changeTab(TextView newTab) {
        newTab.setBackgroundColor(getResources().getColor(R.color.black));
        newTab.setTextColor(getResources().getColor(R.color.white));
        checkType(newTab.getText().toString(), true);
        if (selectedTab != null) {
            if (selectedTab.equals(newTab))
                return;
            selectedTab.setBackgroundColor(getResources().getColor(R.color.white));
            selectedTab.setTextColor(getResources().getColor(R.color.black));
            checkType(selectedTab.getText().toString(), false);
        }
        selectedTab = newTab;
    }

    private void seletedMultipletab(TextView newTab) {
        if (((ColorDrawable) newTab.getBackground()).getColor() == getResources().getColor(R.color.white)) {
            checkType(newTab.getText().toString(), true);
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            checkType(newTab.getText().toString(), false);
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
        }
    }
    private void selectedAge(TextView newTab, boolean isDeSelected) {
        if (isDeSelected) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
        }
    }
    private List<Integer> otherFeaturesIds = new ArrayList<>();

    private void getBusinessList(boolean isFilter) {
        Call<BusinessListResponse> call = null;

        if (!fromType.equalsIgnoreCase("profile")) {
            if (otherFeaturesIds.size() > 0)
                otherFeaturesIds.clear();
            if (!GlobalUtil.isNetworkAvailable(getApplicationContext())) {
                UIUtil.showNetworkDialog(getApplicationContext());
                return;
            }
            filterBusinessRequest.setType(Integer.valueOf(type));
            if (!TextUtils.isEmpty(businessCatId))
                filterBusinessRequest.setMainCatId(Integer.valueOf(businessCatId));
            else filterBusinessRequest.setMainCatId(0);
            if (isFilter) {
                filterBusinessRequest.setPriceRange(getIntValue(selectedPriceType));
                if (((ColorDrawable) tvRelevance.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setOrderBY(1);
                if (((ColorDrawable) tvDistance.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setOrderBY(2);
                if (((ColorDrawable) tvHighRated.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setOrderBY(3);
                if (((ColorDrawable) tvReviewed.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setOrderBY(4);
                if (((ColorDrawable) tvOpenNow.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setOpenNow(1);
                else filterBusinessRequest.setOpenNow(0);
                if (((ColorDrawable) tvOpenAt.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setOpen_at(tvOpenAt.getText().toString().split(":")[1] + ":" + tvOpenAt.getText().toString().split(":")[2]);
                else filterBusinessRequest.setOpen_at(null);
                if (((ColorDrawable) tvMinAge.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setAgeRestriction(Integer.valueOf(tvMinAge.getText().toString().split(":")[1]));
                else filterBusinessRequest.setAgeRestriction(null);
                if (relateditemList.get(0).isSelected())
                    filterBusinessRequest.setSubscribed(1);
                else filterBusinessRequest.setSubscribed(0);
                if (relateditemList.get(1).isSelected())
                    filterBusinessRequest.setInMyBookmark(1);
                else filterBusinessRequest.setInMyBookmark(0);
                if (relateditemList.get(2).isSelected())
                    filterBusinessRequest.setInMyImgLike(1);
                else filterBusinessRequest.setInMyImgLike(0);
                if (relateditemList.get(0).isSelected() || relateditemList.get(1).isSelected() || relateditemList.get(2).isSelected())
                    filterBusinessRequest.setCustomerId(customerId);
                else
                    filterBusinessRequest.setCustomerId(null);
//                if (((ColorDrawable) tvLocation.getBackground()).getColor() == getResources().getColor(R.color.black))
//                    filterBusinessRequest.setHaveLocation(1);
//                else filterBusinessRequest.setHaveLocation(0);
                if (((ColorDrawable) tvReviews.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setHaveReviews(1);
                else filterBusinessRequest.setHaveReviews(0);
                if (((ColorDrawable) tvWebsite.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setHaveWebsite(1);
                else filterBusinessRequest.setHaveWebsite(0);
                if (((ColorDrawable) tvContactNumber.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setHaveContactNo(1);
                else filterBusinessRequest.setHaveContactNo(0);
                if (((ColorDrawable) tvMessage.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setMessageAvailable(1);
                else filterBusinessRequest.setMessageAvailable(0);
                if (((ColorDrawable) tvDisabledFacility.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setHaveDisabledFacilitie(1);
                else filterBusinessRequest.setHaveDisabledFacilitie(0);
                if (((ColorDrawable) tvReputation.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setHaveReputationalCred(1);
                else filterBusinessRequest.setHaveReputationalCred(0);
                //todo ageRestrictions   pending
//                filterBusinessRequest.setMessageAvailable(1);
                for (OtherFeature otherFeature : listOtherFeatures)
                    if (otherFeature.isSelected())
                        otherFeaturesIds.add(otherFeature.getId());
                filterBusinessRequest.setOtherFeatures(otherFeaturesIds);
            }
            call = apiInterface.getBusinessList(filterBusinessRequest);
        } else if (fromType.equalsIgnoreCase("profile")) {
            EOLoginRequest loginRequest = new EOLoginRequest();
            loginRequest.setShorting(Integer.valueOf(sort));
            loginRequest.setCat_ids(selectedCatList);
            call = apiInterface.getBookmarkedBusinesses(BEARER.concat(this.headerToken), loginRequest);
        }
        progress.showProgressBar();
        call.enqueue(new Callback<BusinessListResponse>() {
            @Override
            public void onResponse(Call<BusinessListResponse> call, Response<BusinessListResponse> response) {
                tvEmptyRewviews.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                bList.clear();
                if (response.isSuccessful()) {
                    if(response.body().getFilter_count()!=null)
                    {
                        ((TextView) findViewById(R.id.tv_filterr) ).setText(response.body().getFilter_count()==0?"Filter":"Filter ("+response.body().getFilter_count()+")");
                        ((TextView) findViewById(R.id.tv_filter) ).setText(response.body().getFilter_count()==0?"Filter":"Filter ("+response.body().getFilter_count()+")");
                    }
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0) {
                            bList.addAll(response.body().getPayload());
                            businessCategoryAdapter = new BusinessCategoryAdapter(bList);
                            recyclerView.setAdapter(businessCategoryAdapter);
                            tvEmptyRewviews.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(NearMeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NearMeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }

            @Override
            public void onFailure(Call<BusinessListResponse> call, Throwable t) {
                tvEmptyRewviews.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                progress.hideProgressBar();
                Toast.makeText(NearMeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getIntValue(TextView value) {
        if (value == null)
            return 0;
        else return value.getText().length();
    }

    private void getOtherFeatures() {
        progress.showProgressBar();
        apiInterface.getOtherfeaturesFilter(BEARER.concat(this.headerToken)).enqueue(new Callback<AllOtherFeaturesResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AllOtherFeaturesResponse> call, Response<AllOtherFeaturesResponse> response) {
                progress.hideProgressBar();
                listOtherFeatures.clear();
                recOtherFeature.setVisibility(View.GONE);
//                    tvEmptyData.setVisibility(View.VISIBLE);
                if (!ObjectUtil.isEmpty(response.body())) {
                    AllOtherFeaturesResponse allOtherFeaturesResponse = response.body();
                    if (!ObjectUtil.isEmpty(allOtherFeaturesResponse)) {
                        if (allOtherFeaturesResponse.getStatus() == RESPONSE_SUCCESS) {
                            if (!ObjectUtil.isEmpty(response.body())) {
                                if (allOtherFeaturesResponse.getPayload().size() > 0) {
                                    recOtherFeature.setVisibility(View.VISIBLE);
//                                        tvEmptyData.setVisibility(View.GONE);
                                    listOtherFeatures.addAll(allOtherFeaturesResponse.getPayload());
                                    recOtherFeature.getAdapter().notifyDataSetChanged();
                                }
                            }
                        } else {
                            Toast.makeText(NearMeActivity.this, allOtherFeaturesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AllOtherFeaturesResponse> call, Throwable t) {
                listOtherFeatures.clear();
                recOtherFeature.setVisibility(View.GONE);
//                    tvEmptyData.setVisibility(View.VISIBLE);
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(NearMeActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkType(String name, Boolean isSeleted) {
        for (int i = 1; i < headingFilterList.size(); i++) {
            if (headingFilterList.get(i).getFilterName().equalsIgnoreCase(name)) {
                if (headingFilterList.get(i).getPriceOptions() == null) {
                    headingFilterList.get(i).setSelected(isSeleted);
                    recHeading.getAdapter().notifyItemChanged(i);
                }
            } else if (headingFilterList.get(i).getPriceOptions() != null) {
                for (int j = 0; j < headingFilterList.get(i).getPriceOptions().size(); j++) {
                    if (headingFilterList.get(i).getPriceOptions().get(j).shortName.equalsIgnoreCase(name)) {
                        headingFilterList.get(i).getPriceOptions().get(j).setSelected(isSeleted);
                        recHeading.getAdapter().notifyItemChanged(i);
                    }
                }
            }
        }
    }
    private void getHeadings() {
        GetBusinessRequest request = new GetBusinessRequest();
        if (!TextUtils.isEmpty(businessCatId))
            request.setCat_id(businessCatId);
        else request.setCat_id("0");
        apiInterface.getHeadings(request).enqueue(new Callback<HeadingFilterResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<HeadingFilterResponse> call, Response<HeadingFilterResponse> response) {
                headingFilterList.clear();
                findViewById(R.id.layout_heading).setVisibility(View.GONE);
                recHeading.setVisibility(View.GONE);
                if (!ObjectUtil.isEmpty(response.body())) {
                    HeadingFilterResponse allOtherFeaturesResponse = response.body();
                    if (!ObjectUtil.isEmpty(allOtherFeaturesResponse)) {
                        if (allOtherFeaturesResponse.getStatus() == RESPONSE_SUCCESS) {
                            if (!ObjectUtil.isEmpty(response.body())) {
                                if (allOtherFeaturesResponse.getPayload().size() > 0) {
                                    recHeading.setVisibility(View.VISIBLE);
                                    findViewById(R.id.layout_heading).setVisibility(View.VISIBLE);

                                    headingFilterList.addAll(allOtherFeaturesResponse.getPayload());
                                    headingFilterList.add(new HeadingFilerPayload("All Filter"));
                                    recHeading.getAdapter().notifyDataSetChanged();
                                }
                            }
                        } else {
                            Toast.makeText(NearMeActivity.this, allOtherFeaturesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<HeadingFilterResponse> call, Throwable t) {
                listOtherFeatures.clear();
                recHeading.setVisibility(View.GONE);
//                    tvEmptyData.setVisibility(View.VISIBLE);
                if (t.getMessage() != null) {
                    Toast.makeText(NearMeActivity.this, "Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFilterCat() {
        if (!GlobalUtil.isNetworkAvailable(NearMeActivity.this)) {
            UIUtil.showNetworkDialog(NearMeActivity.this);
            return;
        }
        apiInterface.getFilterCat().enqueue(new Callback<FilterCatResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<FilterCatResponse> call, Response<FilterCatResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0) {
                            filterCatList.addAll(response.body().getPayload());
                            filterCatListTemp.addAll(response.body().getPayload());
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<FilterCatResponse> call, Throwable t) {
//
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_filter:
                if (!getIntent().hasExtra("profile")) {
                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    } else
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.tv_filter:
                startActivityForResult(new Intent(NearMeActivity.this, ProfileFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList).putExtra("businessId",businessCatId), 202);
                break;
            case R.id.tv_relevance:
                changeTab(tvRelevance);
                break;
            case R.id.tv_distance:
                changeTab(tvDistance);
                break;
            case R.id.tv_rated:
                changeTab(tvHighRated);
                break;
            case R.id.tv_reviewed:
                changeTab(tvReviewed);
                break;
            case R.id.tv_price_one:
                changePriceType(priceTypeOne);
                break;
            case R.id.tv_price_two:
                changePriceType(priceTypeTwo);
                break;
            case R.id.tv_min_age:
                ageDialog();
                break;
            case R.id.tv_price_three:
                changePriceType(priceTypeThree);
                break;
            case R.id.tv_price_four:
                changePriceType(priceTypeFour);
                break;
            case R.id.tv_open_at:
                openTimePicker();
                break;
            case R.id.tv_open_now:
                changeOpenType(tvOpenNow);
                break;
            case R.id.tv_location:
                seletedMultipletab(tvLocation);
                break;
            case R.id.tv_reviews:
                seletedMultipletab(tvReviews);
                break;
            case R.id.tv_website:
                seletedMultipletab(tvWebsite);
                break;
            case R.id.tv_video:
                seletedMultipletab(tvVideo);
                break;
            case R.id.tv_cont_number:
                seletedMultipletab(tvContactNumber);
                break;
            case R.id.tv_msg_avail:
                seletedMultipletab(tvMessage);
                break;
            case R.id.tv_rep_cred:
                seletedMultipletab(tvReputation);
                break;
            case R.id.tv_disable_fac:
                seletedMultipletab(tvDisabledFacility);
                break;
            case R.id.tv_apply:
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                getBusinessList(true);
                break;
            case R.id.img_back:
                onBackPressed();
                break;

            case R.id.layout_map:
                startActivity(new Intent(NearMeActivity.this, ActivityMap.class).putExtra("business_list", (Serializable) bList).putExtra("listOtherFeatures", (Serializable) listOtherFeatures).putExtra("filter_pre",previousSelected).putExtra("filter_data",filterBusinessRequest).putExtra("post",postCode).putExtra("type",type).putExtra("cat_name",catName));
                break;
            case R.id.tv_reset:
                finish();
                startActivity(getIntent().putExtra("businessId", businessCatId).putExtra("type", type).putExtra("filter_data", previousSelected).putExtra("filter_pre", previousSelected));
                break;
        }
    }

    DecimalFormat timeFormater = new DecimalFormat("00");
    Calendar mcurrentTime;
    TimePickerDialog mTimePicker;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    private void openTimePicker() {
        mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(NearMeActivity.this, R.style.DialogTheme, onStartTimeListener, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    TimePickerDialog.OnTimeSetListener onStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvOpenAt.setText("Open at:" + timeFormater.format(hourOfDay) + ":" + timeFormater.format(minute));
            changeOpenType(tvOpenAt);
        }
    };

    public void backBtnCick(View view) {
        finish();
    }
    //todo============================== Business Adapter ==============================

    public class BusinessCategoryAdapter extends RecyclerView.Adapter<BusinessCategoryAdapter.ViewHolder> {
        private List<BusinessListData> mList;

        public BusinessCategoryAdapter(List<BusinessListData> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_business, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bName.setText(mList.get(position).getBusinessName());
            Picasso.get().load(mList.get(position).getBusiness_logo()).into(holder.bImage);
            holder.tvSummaryLine.setText(TextUtils.isEmpty(mList.get(position).getSummery_line()) ? "" : mList.get(position).getSummery_line());
            holder.tvContact.setText(TextUtils.isEmpty(mList.get(position).getBusiness_contact()) ? "" : mList.get(position).getBusiness_contact());

            if (mList.get(position).getDistance()!=null){
                holder.distance.setText(mList.get(position).getDistance());
            }else{
                holder.distance.setVisibility(View.GONE);
            }

            if (mList.get(position).getMessageStatus() == 1)
                holder.cardViewMessage.setVisibility(View.VISIBLE);
            else holder.cardViewMessage.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mList.get(position).getAvrg_rateing())) {
                holder.ratingBar.setRating(Float.parseFloat(mList.get(position).getAvrg_rateing()));
                holder.tvRatingCount.setText(formater.format(Float.parseFloat(mList.get(position).getAvrg_rateing())));
                holder.tvReviewCount.setText(mList.get(position).getReviews_count() + " Reviews");
            }
            if (mList.get(position).getCurrent_working_status() != null) {
                if (!TextUtils.isEmpty(mList.get(position).getCurrent_working_status().getStatus())) {
                    if (mList.get(position).getCurrent_working_status().getStatus().equalsIgnoreCase("Closed")) {
                        holder.tvOpenStatus.setText(mList.get(position).getCurrent_working_status().getStatus());
                        holder.tvOpenStatus.setTextColor(getResources().getColor(R.color.darkRed));
                    } else if (mList.get(position).getCurrent_working_status().getStatus().equalsIgnoreCase("Open")) {
                        holder.tvOpenStatus.setText(mList.get(position).getCurrent_working_status().getStatus());
                        holder.tvOpenStatus.setTextColor(getResources().getColor(R.color.green));
                    }
                }
                holder.tvTime.setText(TextUtils.isEmpty(mList.get(position).getCurrent_working_status().getOpenCloseAt()) ? "" : mList.get(position).getCurrent_working_status().getOpenCloseAt());
            }else{
                holder.timeLayout.setVisibility(View.GONE);
            }



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), BusinessProfile.class);
                    intent.putExtra("businessCatId", String.valueOf(mList.get(position).getId()));
                    intent.putExtra("businessLogo", mList.get(position).getBusiness_long());
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView bImage;
            private TextView bName, tvSummaryLine, tvRatingCount, tvContact, tvOpenStatus, tvTime, tvReviewCount,distance;
            private RatingBar ratingBar;
            private CardView cardViewMessage;
            private LinearLayout timeLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bName = itemView.findViewById(R.id.rcb_bName);
                bImage = itemView.findViewById(R.id.rcb_bImage);
                tvSummaryLine = itemView.findViewById(R.id.tv_summary_line);
                ratingBar = itemView.findViewById(R.id.star_ratingbar);
                tvRatingCount = itemView.findViewById(R.id.tv_rating_count);
                tvContact = itemView.findViewById(R.id.tv_contact);
                tvTime = itemView.findViewById(R.id.tvtime);
                tvOpenStatus = itemView.findViewById(R.id.tvstatus);
                tvReviewCount = itemView.findViewById(R.id.tv_review_cout);
                cardViewMessage = itemView.findViewById(R.id.card_view_msg);
                distance=itemView.findViewById(R.id.rcb_miles);
                timeLayout=itemView.findViewById(R.id.time_layout);
            }
        }
    }

    //todo============================== Related Category Adapter==============================

    public class RelatedCatAdapter extends RecyclerView.Adapter<RelatedCatAdapter.RelatedCatViewHolder> {

        private List<RelatedItem> mList;

        public RelatedCatAdapter(List<RelatedItem> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public RelatedCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RelatedCatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_filter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RelatedCatViewHolder holder, int position) {
            holder.bName.setText(mList.get(position).getName());
            if (mList.get(position).isSelected())
                holder.imgTick.setVisibility(View.VISIBLE);
            else
                holder.imgTick.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLoggedIn) {
                        if (holder.imgTick.getVisibility() == View.INVISIBLE) {
                            holder.imgTick.setVisibility(View.VISIBLE);
                            relateditemList.get(position).setSelected(true);
                            sendType(position, true);
                        } else {
                            relateditemList.get(position).setSelected(false);
                            holder.imgTick.setVisibility(View.INVISIBLE);
                            sendType(position, false);
                        }
                    } else {
                        askLoginDialog();
                    }
                }
            });
        }

        private void sendType(int position, boolean isSelected) {
            switch (position) {
                case 0:
                    checkType("in my subscribed", isSelected);
                    break;
                case 1:
                    checkType("in my bookmarks", isSelected);
                    break;
                case 2:
                    checkType("in my image likes", isSelected);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class RelatedCatViewHolder extends RecyclerView.ViewHolder {
            private TextView bName;
            private ImageView imgTick;

            public RelatedCatViewHolder(@NonNull View itemView) {
                super(itemView);
                bName = itemView.findViewById(R.id.tv_name);
                imgTick = itemView.findViewById(R.id.img_tick);
            }
        }
    }

    private void askLoginDialog() {
        LayoutInflater li = LayoutInflater.from(NearMeActivity.this);
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        AlertDialog sDialog = new AlertDialog.Builder(NearMeActivity.this).setView(dialogView).setCancelable(false).create();
        TextView title = dialogView.findViewById(R.id.dr_title);
        TextView desc = dialogView.findViewById(R.id.dr_desc);
        TextView back = dialogView.findViewById(R.id.tv_cancel);
        TextView block = dialogView.findViewById(R.id.tv_reset);
        back.setText("Cancel");
        block.setText("Login Now");
        title.setText("Login");
        desc.setText("You have to be logged in to use this");
        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        sDialog.show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDialog.dismiss();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NearMeActivity.this, ViewPagerActivity.class).putExtra("fromFilter", true), 110);
                sDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isLoggedIn = loginPreferences.getBoolean(IS_LOGGED_IN, false);
            customerId = loginPreferences.getInt(USER_ID, 0);
            if (data != null && data.hasExtra("filter_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList = (List<FilterCatPayload>) data.getSerializableExtra("filter_cat");
                if (filterCatList.size() > 0) {
                    for (FilterCatPayload filterCatPayload : filterCatList) {
                        if (filterCatPayload.isChecked()) {
                            selectedCatList.add(filterCatPayload.getId());
                        }
                    }
                    getBusinessList(false);
                }
            } else if (data.hasExtra("reset_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList.addAll(filterCatListTemp);
                getBusinessList(false);
            }
        }
    }

    //todo============================== Other features Adapter==============================

    public class OtherFeaturesAdapter extends RecyclerView.Adapter<OtherFeaturesAdapter.OtherFeaturesViewHolder> {

        private List<OtherFeature> mList;

        public OtherFeaturesAdapter(List<OtherFeature> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public OtherFeaturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OtherFeaturesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_other_feature, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OtherFeaturesViewHolder holder, int position) {
            OtherFeature otherFeature = mList.get(position);
            holder.bName.setText(otherFeature.getFeatureName());
            holder.imgTick.setVisibility(View.INVISIBLE);
            if (otherFeature.getFeatureIcon() != null) {
                holder.imgIcon.setVisibility(View.VISIBLE);
                loadImage(otherFeature.getFeatureIcon(), holder.imgIcon);
            }
            if (mList.get(position).isSelected())
                holder.imgTick.setVisibility(View.VISIBLE);
            else
                holder.imgTick.setVisibility(View.INVISIBLE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.imgTick.getVisibility() == View.INVISIBLE) {
                        holder.imgTick.setVisibility(View.VISIBLE);
                        listOtherFeatures.get(position).setSelected(true);
                        checkType(otherFeature.getFeatureName(), true);
                    } else {
                        listOtherFeatures.get(position).setSelected(false);
                        checkType(otherFeature.getFeatureName(), false);
                        holder.imgTick.setVisibility(View.INVISIBLE);
                    }
                }
            });


        }

        private void loadImage(String url, ImageView img) {
            Glide.with(NearMeActivity.this)
                    .asBitmap()
                    .load(url)
                    .placeholder(R.drawable.ic_palceholder)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            img.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class OtherFeaturesViewHolder extends RecyclerView.ViewHolder {
            private TextView bName;
            private ImageView imgIcon, imgTick;

            public OtherFeaturesViewHolder(@NonNull View itemView) {
                super(itemView);
                bName = itemView.findViewById(R.id.rof_name);
                imgIcon = itemView.findViewById(R.id.rof_img);
                imgTick = itemView.findViewById(R.id.img_tick);
            }
        }
    }


    private void addRequest(String name, boolean isSelected) {
        switch (name.toLowerCase()) {
            case "open now":
                if (isSelected)
                    changeOpenType(tvOpenNow);
                else
                    changeOpenType(tvOpenNow);
                break;
            case "distance":
                if (isSelected)
                    seletedMultipletab(tvDistance);
                else
                    seletedMultipletab(tvDistance);
                break;
            case "highest rated":
                if (isSelected)
                    seletedMultipletab(tvHighRated);
                else
                    seletedMultipletab(tvHighRated);
                break;
            case "most reviewed":
                if (isSelected)
                    seletedMultipletab(tvReviewed);
                else
                    seletedMultipletab(tvReviewed);
                break;
            case "location":
                if (isSelected)
                    seletedMultipletab(tvLocation);
                else
                    seletedMultipletab(tvLocation);
                break;
            case "reviews":

                if (isSelected)
                    seletedMultipletab(tvReviews);
                else
                    seletedMultipletab(tvReviews);
                break;
            case "website":
                if (isSelected)
                    seletedMultipletab(tvWebsite);
                else
                    seletedMultipletab(tvWebsite);
                break;
            case "images":
                if (isSelected)
                    seletedMultipletab(tvVideo);
                else
                    seletedMultipletab(tvVideo);
                break;
            case "contact number":
                if (isSelected)
                    seletedMultipletab(tvContactNumber);
                else
                    seletedMultipletab(tvContactNumber);
                break;
            case "messaging available":
                if (isSelected)
                    seletedMultipletab(tvMessage);
                else
                    seletedMultipletab(tvMessage);
                break;
            case "reputation & credentials":
                if (isSelected)
                    seletedMultipletab(tvReputation);
                else
                    seletedMultipletab(tvReputation);
                break;
            case "disabled facilities":
                if (isSelected)
                    seletedMultipletab(tvDisabledFacility);
                else
                    seletedMultipletab(tvDisabledFacility);
                break;
            case "in my bookmarks":
                relateditemList.get(1).setSelected(isSelected);
                recRelatedCat.getAdapter().notifyDataSetChanged();
                break;
            case "in my image likes":
                relateditemList.get(2).setSelected(isSelected);
                recRelatedCat.getAdapter().notifyDataSetChanged();
                break;
            case "in my subscribed":
                relateditemList.get(0).setSelected(isSelected);
                recRelatedCat.getAdapter().notifyDataSetChanged();
                break;
            case "relevance":
                break;
        }
    }


    //todo============================== Filter headig Adapter==============================

    public class HeadingAdpater extends RecyclerView.Adapter<HeadingAdpater.HeadignViewHolder> {
        private List<HeadingFilerPayload> mList;

        public HeadingAdpater(List<HeadingFilerPayload> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public HeadignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HeadignViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_tab, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HeadignViewHolder holder, int position) {
            HeadingFilerPayload headingFilerPayload = mList.get(position);
            holder.bName.setText(headingFilerPayload.getFilterName());

            if (position < mList.size() - 1) {
                if (headingFilerPayload.isSelected()) {
                    holder.mainRow.setCardBackgroundColor(getResources().getColor(R.color.black));
                    holder.imgIcon.setColorFilter(ContextCompat.getColor(NearMeActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    holder.bName.setTextColor(getResources().getColor(R.color.white));
                } else {
                    holder.mainRow.setCardBackgroundColor(getResources().getColor(R.color.yellow));
                    holder.imgIcon.setColorFilter(ContextCompat.getColor(NearMeActivity.this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    holder.bName.setTextColor(getResources().getColor(R.color.black));
                }

                if (mList.get(position).getPriceOptions() != null) {
                    holder.recSubHeading.setAdapter(new SubHeadingAdapter(mList.get(position).getPriceOptions()));
                }
                if (!TextUtils.isEmpty(headingFilerPayload.getFilterIcon())) {
                    loadImage(headingFilerPayload.getFilterIcon(), holder.imgIcon);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((mList.get(position).getFilterName().equalsIgnoreCase("In My Image Likes") || mList.get(position).getFilterName().equalsIgnoreCase("In My Bookmarks")) && !isLoggedIn) {
                            askLoginDialog();
                            return;
                        }
                        if (mList.get(position).getPriceOptions() == null) {
                            if (holder.mainRow.getCardBackgroundColor().getDefaultColor() == getResources().getColor(R.color.yellow)) {
                                holder.mainRow.setCardBackgroundColor(getResources().getColor(R.color.black));
                                holder.imgIcon.setColorFilter(ContextCompat.getColor(NearMeActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                                holder.bName.setTextColor(getResources().getColor(R.color.white));
                                if (!TextUtils.isEmpty(headingFilerPayload.getSlug()) && headingFilerPayload.getFeatureId() == null) {
                                    addRequest(headingFilerPayload.getFilterName(), true);
                                } else if (headingFilerPayload.getFeatureId() != null) {
                                    for (int i = 0; i < listOtherFeatures.size(); i++) {
                                        if (String.valueOf(listOtherFeatures.get(i).getId()).equals(headingFilerPayload.getFeatureId())) {
                                            listOtherFeatures.get(i).setSelected(true);
                                            recOtherFeature.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                }
                            } else {
                                if (!TextUtils.isEmpty(headingFilerPayload.getSlug()) && headingFilerPayload.getFeatureId() == null) {
                                    addRequest(headingFilerPayload.getFilterName(), false);
                                } else if (headingFilerPayload.getFeatureId() != null) {
                                    for (int i = 0; i < listOtherFeatures.size(); i++) {
                                        if (String.valueOf(listOtherFeatures.get(i).getId()).equals(headingFilerPayload.getFeatureId())) {
                                            listOtherFeatures.get(i).setSelected(false);
                                            recOtherFeature.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                }
                                holder.mainRow.setCardBackgroundColor(getResources().getColor(R.color.yellow));
                                holder.imgIcon.setColorFilter(ContextCompat.getColor(NearMeActivity.this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                                holder.bName.setTextColor(getResources().getColor(R.color.black));
                            }
                        } else {
                            if (mList.get(position).getPriceOptions().size() > 0) {
                                if (holder.layoutSubHead.getVisibility() == View.VISIBLE)
                                    holder.layoutSubHead.setVisibility(View.GONE);
                                else
                                    holder.layoutSubHead.setVisibility(View.VISIBLE);
                            }
                        }
                      if (!holder.bName.getText().toString().equalsIgnoreCase("price"))
                        getBusinessList(true);
                    }
                });
            } else {
                holder.imgIcon.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        } else
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
            }

        }


        private void loadImage(String url, ImageView img) {
            Glide.with(NearMeActivity.this)
                    .asBitmap()
                    .load(url)
                    .placeholder(R.drawable.ic_palceholder)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            img.setImageBitmap(resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class HeadignViewHolder extends RecyclerView.ViewHolder {
            private TextView bName;
            private ImageView imgIcon, imgTick;
            private CardView mainRow;
            private LinearLayout layoutSubHead;
            private RecyclerView recSubHeading;


            public HeadignViewHolder(@NonNull View itemView) {
                super(itemView);
                bName = itemView.findViewById(R.id.tv_title);
                imgIcon = itemView.findViewById(R.id.img_icon);
                mainRow = itemView.findViewById(R.id.main_row);
                layoutSubHead = itemView.findViewById(R.id.layout_sub_head);
                recSubHeading = itemView.findViewById(R.id.rec_sub_heading);
            }
        }
    }


    //todo============================== Sub Heading Adapter==============================

    public class SubHeadingAdapter extends RecyclerView.Adapter<SubHeadingAdapter.SubHeadingViewHolder> {
        private List<FiterPriceOption> mList;
        private LinearLayout previousLayout;
        private TextView previousText;

        public SubHeadingAdapter(List<FiterPriceOption> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public SubHeadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubHeadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header_sub_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubHeadingViewHolder holder, int position) {
            FiterPriceOption otherFeature = mList.get(position);
            holder.tvPrice.setText(otherFeature.getShortName());

            if (otherFeature.isSelected()) {
                holder.layoutPrice.setBackgroundColor(getResources().getColor(R.color.black));
                holder.tvPrice.setTextColor(getResources().getColor(R.color.white));
                priceRange = holder.tvPrice.getText().toString().length();
                if (previousLayout != null) {
                    previousLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                    previousText.setTextColor(getResources().getColor(R.color.black));
                }
                previousLayout = holder.layoutPrice;
                previousText = holder.tvPrice;
            } else {
                changePriceRange(priceRange);
                holder.layoutPrice.setBackgroundColor(getResources().getColor(R.color.yellow));
                holder.tvPrice.setTextColor(getResources().getColor(R.color.black));
                previousLayout = null;
                previousText = null;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ColorDrawable) holder.layoutPrice.getBackground()).getColor() == getResources().getColor(R.color.yellow)) {
                        holder.layoutPrice.setBackgroundColor(getResources().getColor(R.color.black));
                        holder.tvPrice.setTextColor(getResources().getColor(R.color.white));
                        mList.get(position).setSelected(true);
                        priceRange = holder.tvPrice.getText().toString().length();
                        changePriceRange(priceRange);
                        if (previousLayout != null) {
                            previousLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                            previousText.setTextColor(getResources().getColor(R.color.black));
                        }
                        previousLayout = holder.layoutPrice;
                        previousText = holder.tvPrice;

                    } else {
                        changePriceRange(priceRange);
                        mList.get(position).setSelected(false);
                        holder.layoutPrice.setBackgroundColor(getResources().getColor(R.color.yellow));
                        holder.tvPrice.setTextColor(getResources().getColor(R.color.black));
                        previousLayout = null;
                        previousText = null;
                    }
                    getBusinessList(true);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class SubHeadingViewHolder extends RecyclerView.ViewHolder {
            private TextView tvPrice;
            private LinearLayout layoutPrice;

            public SubHeadingViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPrice = itemView.findViewById(R.id.tv_title);
                layoutPrice = itemView.findViewById(R.id.layout_title);
            }
        }
    }

    //todo==================================Age Adapter=========================
    public class AgeAdapter extends RecyclerView.Adapter<AgeAdapter.AgeHolder> {
        private List<String> ageModel;

        public AgeAdapter(List<String> ageModel) {
            this.ageModel = ageModel;
        }

        @NonNull
        @Override
        public AgeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AgeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_age, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AgeHolder holder, int position) {
            holder.age.setText(ageModel.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ageModel.get(position).equalsIgnoreCase("None")) {
                        selectedAge(tvMinAge, false);
                        tvMinAge.setText("Age Restrictions...");
                    } else {
                        selectedAge(tvMinAge, true);
                        tvMinAge.setText("Age Restrictions:" + ageModel.get(position));
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return ageModel.size();
        }

        public class AgeHolder extends RecyclerView.ViewHolder {
            TextView age;

            public AgeHolder(@NonNull View itemView) {
                super(itemView);
                age = itemView.findViewById(R.id.ageText);
            }
        }
    }

}
