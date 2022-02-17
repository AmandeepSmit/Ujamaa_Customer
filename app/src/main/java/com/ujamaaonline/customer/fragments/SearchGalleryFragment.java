package com.ujamaaonline.customer.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityGalleryDetail;
import com.ujamaaonline.customer.activities.MainActivity;
import com.ujamaaonline.customer.activities.NearMeActivity;
import com.ujamaaonline.customer.activities.ProfileFilterActivity;
import com.ujamaaonline.customer.activities.ViewPagerActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.business_data.GetBusinessData;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.business_data.OtherFeature;
import com.ujamaaonline.customer.models.cat_filter.AllOtherFeaturesResponse;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;
import com.ujamaaonline.customer.models.cat_filter.RelatedItem;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.gallery_images.GalleryImagesResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class SearchGalleryFragment extends Fragment implements View.OnClickListener {

    private View view, bottomSheetView;
    private List<SearchGalleryPayload> galleryList = new ArrayList<>();
    private RecyclerView recGallery, recOtherFeature, recRelatedCat;
    private TextView tvEmptyData, tvRelevance, tvDistance, tvHighRated, tvReviewed, selectedTab, priceTypeOne, priceTypeTwo, priceTypeThree, priceTypeFour, selectedPriceType, tvOpenAt, tvMinAge,
            tvOpenNow, selectedOpenType, tvLocation, tvReviews, tvWebsite, tvVideo, tvContactNumber, tvMessage, tvReputation, tvDisabledFacility, tvApply, tvEmptyRewviews, tvTitle;
    private GridLayoutManager gridLayoutManager;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private String headerToken;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isLogin = false;
    private EditText setSearch;
    private Integer userId;
    private String fromType = "";
    private List<OtherFeature> listOtherFeatures = new ArrayList<>();
    private LinearLayout layoutEmpty, layoutGallery, layoutEmptyData;
    private Integer gridValue = 3;
    private FilterBusinessRequest filterBusinessRequest;
    private SessionSecuredPreferences loginPreferences;
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatListTemp = new ArrayList<>();
    private List<Integer> selectedCatList = new ArrayList<>();
    private LinearLayout layoutSort;
    private List<RelatedItem> relateditemList = new ArrayList<>();

    public SearchGalleryFragment(String fromType) {
        this.fromType = fromType;
    }

    public SearchGalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_gallery, container, false);
        Fade fade = new Fade();
        View decor = getActivity().getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getActivity().getWindow().setEnterTransition(fade);
        getActivity().getWindow().setExitTransition(fade);
        addStaticData();

        initView();
        setOnClickListereners();
        return view;
    }

    private void resetFilter() {
        otherFeaturesIds.clear();
        if (((ColorDrawable) tvVideo.getBackground()).getColor() == getResources().getColor(R.color.black)) {
            seletedMultipletab(tvVideo);
        }
        if (((ColorDrawable) tvDistance.getBackground()).getColor() == getResources().getColor(R.color.black)) {
            seletedMultipletab(tvDistance);
        }
        if (((ColorDrawable) tvHighRated.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvHighRated);
        if (((ColorDrawable) tvReviewed.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvReviewed);
        if (((ColorDrawable) priceTypeOne.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(priceTypeOne);
        if (((ColorDrawable) priceTypeTwo.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(priceTypeTwo);
        if (((ColorDrawable) priceTypeThree.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(priceTypeThree);
        if (((ColorDrawable) priceTypeFour.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(priceTypeFour);
        if (((ColorDrawable) tvOpenNow.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvOpenNow);
        if (((ColorDrawable) tvOpenAt.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvOpenAt);
        if (((ColorDrawable) tvLocation.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvLocation);
        if (((ColorDrawable) tvReviews.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvReviews);
        if (((ColorDrawable) tvWebsite.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvWebsite);
        if (((ColorDrawable) tvContactNumber.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvContactNumber);
        if (((ColorDrawable) tvMessage.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvMessage);
        if (((ColorDrawable) tvDisabledFacility.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvDisabledFacility);
        if (((ColorDrawable) tvReputation.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvReputation);
        if (relateditemList.size() > 0) {
            for (int i = 1; i < relateditemList.size(); i++) {
                if (relateditemList.get(i).isSelected())
                    relateditemList.get(i).setSelected(false);
            }
        }
        if (((ColorDrawable) tvMinAge.getBackground()).getColor() == getResources().getColor(R.color.black))
            seletedMultipletab(tvMinAge);

        for (int i = 1; i < listOtherFeatures.size(); i++) {
            if (listOtherFeatures.get(i).isSelected())
                listOtherFeatures.get(i).setSelected(false);
        }
        recOtherFeature.getAdapter().notifyDataSetChanged();
        recRelatedCat.getAdapter().notifyDataSetChanged();
    }

    private void getFilterCat() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
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

    private void setOnClickListereners() {
        tvReviewed.setOnClickListener(bottomSheetClick);
        tvDistance.setOnClickListener(bottomSheetClick);
        tvHighRated.setOnClickListener(bottomSheetClick);
        tvRelevance.setOnClickListener(bottomSheetClick);
        priceTypeOne.setOnClickListener(bottomSheetClick);
        priceTypeTwo.setOnClickListener(bottomSheetClick);
        priceTypeThree.setOnClickListener(bottomSheetClick);
        priceTypeFour.setOnClickListener(bottomSheetClick);
        tvOpenNow.setOnClickListener(bottomSheetClick);
        tvOpenAt.setOnClickListener(bottomSheetClick);
        tvLocation.setOnClickListener(bottomSheetClick);
        tvReviews.setOnClickListener(bottomSheetClick);
        tvWebsite.setOnClickListener(bottomSheetClick);
        tvVideo.setOnClickListener(bottomSheetClick);
        tvMinAge.setOnClickListener(bottomSheetClick);
        tvContactNumber.setOnClickListener(bottomSheetClick);
        tvMessage.setOnClickListener(bottomSheetClick);
        tvApply.setOnClickListener(bottomSheetClick);
        tvReputation.setOnClickListener(bottomSheetClick);
        tvDisabledFacility.setOnClickListener(bottomSheetClick);
        setSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(setSearch.getText().toString().trim())) {
                        if (filterBusinessRequest == null)
                            filterBusinessRequest = new FilterBusinessRequest();

                        filterBusinessRequest.hastag = setSearch.getText().toString().replace(" ", ",");
                        if (isLogin)
                            filterBusinessRequest.setCustomerId(userId);
                        getGalleryImages(false);
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "please enter search keyword", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
        });
        view.findViewById(R.id.img_one).setOnClickListener(this);
        view.findViewById(R.id.img_two).setOnClickListener(this);
        view.findViewById(R.id.img_three).setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK )
        {
            if (data!=null && data.hasExtra("filter_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList = (List<FilterCatPayload>) data.getSerializableExtra("filter_cat");
                if (filterCatList.size() > 0) {
                    for (FilterCatPayload filterCatPayload : filterCatList) {
                        if (filterCatPayload.isChecked()) {
                            selectedCatList.add(filterCatPayload.getId());
                        }
                    }
                    getGalleryImages(false);
                }
            }
            else if (data.hasExtra("reset_cat"))
            {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList.addAll(filterCatListTemp);
                getGalleryImages(false);
            }
        }

    }

    View.OnClickListener bottomSheetClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_relevance:
                    changeTab(tvRelevance);
                    break;
                case R.id.tv_distance:
                    ((MainActivity) getActivity()).checkLocationPermission("gallery");
//                    changeTab(tvDistance);
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
                    getGalleryImages(true);
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.tv_reset:
                    resetFilter();
                    filterBusinessRequest = null;
                    bottomSheetDialog.dismiss();
//                    finish();
//                    startActivity(getIntent().putExtra("businessId", businessCatId).putExtra("type", type).putExtra("filter_data", previousSelected).putExtra("filter_pre", previousSelected));
                    break;
            }
        }
    };

    private void changeOpenType(TextView newTab) {
        if (selectedOpenType != null) {
            if (selectedOpenType.equals(newTab)) {
                selectedOpenType.setBackgroundColor(getResources().getColor(R.color.white));
                selectedOpenType.setTextColor(getResources().getColor(R.color.black));
//                checkType(newTab.getText().toString(), false);
                selectedOpenType = null;
                return;
            }
            selectedOpenType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedOpenType.setTextColor(getResources().getColor(R.color.black));
//            checkType(selectedOpenType.getText().toString(), false);
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        }
//        checkType(newTab.getText().toString(), true);
        selectedOpenType = newTab;
    }

    private void changeTab(TextView newTab) {
        newTab.setBackgroundColor(getResources().getColor(R.color.black));
        newTab.setTextColor(getResources().getColor(R.color.white));
//        checkType(newTab.getText().toString(), true);
        if (selectedTab != null) {
            if (selectedTab.equals(newTab))
                return;
            selectedTab.setBackgroundColor(getResources().getColor(R.color.white));
            selectedTab.setTextColor(getResources().getColor(R.color.black));
//            checkType(selectedTab.getText().toString(), false);
        }
        selectedTab = newTab;
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeTab(tvDistance);
        }
    };


    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void seletedMultipletab(TextView newTab) {
        if (((ColorDrawable) newTab.getBackground()).getColor() == getResources().getColor(R.color.white)) {
//            checkType(newTab.getText().toString(), true);
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
//            checkType(newTab.getText().toString(), false);
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private Dialog dialog;

    private void ageDialog() {
        dialog = new Dialog(getActivity());
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_age_select);
        RecyclerView ageRecyler = dialog.findViewById(R.id.ra_ageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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

    DecimalFormat timeFormater = new DecimalFormat("00");
    Calendar mcurrentTime;
    TimePickerDialog mTimePicker;

    private void openTimePicker() {
        mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(getActivity(), R.style.DialogTheme, onStartTimeListener, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    TimePickerDialog.OnTimeSetListener onStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvOpenAt.setText("Open at:" + timeFormater.format(hourOfDay) + ":" + timeFormater.format(minute));
            changeOpenType(tvOpenAt);
        }
    };

    private void changePriceType(TextView newTab) {
        if (selectedPriceType != null) {
            if (selectedPriceType.equals(newTab)) {
                selectedPriceType.setBackgroundColor(getResources().getColor(R.color.white));
                selectedPriceType.setTextColor(getResources().getColor(R.color.black));
//                checkType(newTab.getText().toString(), false);
                selectedPriceType = null;
                return;
            }
//            checkType(newTab.getText().toString(), false);
            selectedPriceType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedPriceType.setTextColor(getResources().getColor(R.color.black));
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        }
//        checkType(newTab.getText().toString(), true);
        selectedPriceType = newTab;
    }


    private void initView() {
        this.progress = new GlobalProgressDialog(view.getContext());
        this.apiInterface = APIClient.getClient();
        layoutGallery = view.findViewById(R.id.layout_gallery);
        getActivity().registerReceiver(receiver, new IntentFilter("location"));
        layoutEmpty = view.findViewById(R.id.layout_empty);
        filterBusinessRequest = new FilterBusinessRequest();
        bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_cat_filter, null);
        recOtherFeature = bottomSheetView.findViewById(R.id.rec_other_feature);
        recOtherFeature.setAdapter(new OtherFeaturesAdapter(listOtherFeatures));
        tvEmptyData = view.findViewById(R.id.tv_empty_gallery);
        tvRelevance = bottomSheetView.findViewById(R.id.tv_relevance);
        changeTab(tvRelevance);
        tvDistance = bottomSheetView.findViewById(R.id.tv_distance);
        tvHighRated = bottomSheetView.findViewById(R.id.tv_rated);
        tvMinAge = bottomSheetView.findViewById(R.id.tv_min_age);
        tvReviewed = bottomSheetView.findViewById(R.id.tv_reviewed);
        priceTypeOne = bottomSheetView.findViewById(R.id.tv_price_one);
        recRelatedCat = bottomSheetView.findViewById(R.id.rec_related_cat);
        recRelatedCat.setAdapter(new RelatedCatAdapter(relateditemList));
        tvTitle = bottomSheetView.findViewById(R.id.tv_cat_name_search);
        bottomSheetView.findViewById(R.id.tv_reset).setOnClickListener(bottomSheetClick);
//        tvTitle.setText(catName);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        tvApply = bottomSheetView.findViewById(R.id.tv_apply);
        tvOpenAt = bottomSheetView.findViewById(R.id.tv_open_at);
        tvEmptyRewviews = bottomSheetView.findViewById(R.id.tv_empty_data);
        tvOpenNow = bottomSheetView.findViewById(R.id.tv_open_now);
        priceTypeTwo = bottomSheetView.findViewById(R.id.tv_price_two);
        priceTypeThree = bottomSheetView.findViewById(R.id.tv_price_three);
        priceTypeFour = bottomSheetView.findViewById(R.id.tv_price_four);
        tvLocation = bottomSheetView.findViewById(R.id.tv_location);
        tvReviews = bottomSheetView.findViewById(R.id.tv_reviews);
        tvWebsite = bottomSheetView.findViewById(R.id.tv_website);
        tvVideo = bottomSheetView.findViewById(R.id.tv_video);
        tvContactNumber = bottomSheetView.findViewById(R.id.tv_cont_number);
        tvMessage = bottomSheetView.findViewById(R.id.tv_msg_avail);
        tvReputation = bottomSheetView.findViewById(R.id.tv_rep_cred);
        tvDisabledFacility = bottomSheetView.findViewById(R.id.tv_disable_fac);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        gridLayoutManager = new GridLayoutManager(getActivity(), 3,GridLayoutManager.VERTICAL,false);
        setSearch = view.findViewById(R.id.et_search);
        userId = this.loginPreferences.getInt(USER_ID, 0);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        recGallery = view.findViewById(R.id.rec_gallery);
        recGallery.setLayoutManager(gridLayoutManager);
        recGallery.setAdapter(new GalleryAdapter());
        view.findViewById(R.id.layout_filter).setOnClickListener(this);
        if (fromType.equalsIgnoreCase("profile")) {
            setSearch.setVisibility(View.GONE);
            tvEmptyData.setText("No Liked Images");
            getGalleryImages(false);
            getFilterCat();
        } else {
            setSearch.setVisibility(View.VISIBLE);
            getOtherFeatures();
        }
        addValue();
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
                            Toast.makeText(getActivity(), allOtherFeaturesResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_one:
                gridLayoutManager.setSpanCount(1);
                break;
            case R.id.img_two:
                gridLayoutManager.setSpanCount(2);
                break;
            case R.id.img_three:
                gridLayoutManager.setSpanCount(3);
                break;
            case R.id.layout_filter:
                if (!fromType.equalsIgnoreCase("profile"))
                showBottomSheetDialog();
                else
                    startActivityForResult(new Intent(getActivity(), ProfileFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList), 202);
                break;
        }
        recGallery.setLayoutManager(gridLayoutManager);
        recGallery.getAdapter().notifyDataSetChanged();
    }

    private void addStaticData() {
        relateditemList.add(new RelatedItem("You are subscribed to them"));
        relateditemList.add(new RelatedItem("You've bookmarked them"));
        relateditemList.add(new RelatedItem("You've liked an image from them"));
//        relateditemList.add("They are in lists you've made or follow");
    }

    public void showBottomSheetDialog() {
        if(bottomSheetDialog.isShowing())
        {
            bottomSheetDialog.dismiss();
        }
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private int getIntValue(TextView value) {
        if (value == null)
            return 0;
        else return value.getText().length();
    }
    private List<Integer> otherFeaturesIds = new ArrayList<>();

    private void getGalleryImages(boolean isFilter) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }

        Call<SearchGalleryResponse> call = null;
        if (!fromType.equalsIgnoreCase("profile")) {
            if (otherFeaturesIds.size() > 0)
                otherFeaturesIds.clear();
            if (!GlobalUtil.isNetworkAvailable(getActivity())) {
                UIUtil.showNetworkDialog(getActivity());
                return;
            }
            if (isFilter) {
                filterBusinessRequest.setCustomerLat(null);
                filterBusinessRequest.setCustomerLong(null);
                filterBusinessRequest.setPriceRange(getIntValue(selectedPriceType));
                if (((ColorDrawable) tvRelevance.getBackground()).getColor() == getResources().getColor(R.color.black)) {
                    filterBusinessRequest.setOrderBY(1);
                }
                if (((ColorDrawable) tvDistance.getBackground()).getColor() == getResources().getColor(R.color.black)) {
                    filterBusinessRequest.setOrderBY(2);
                    if (((MainActivity) getActivity()).getLocation() != null) {
                        Location location = ((MainActivity) getActivity()).getLocation();
                        filterBusinessRequest.setCustomerLat(String.valueOf(location.getLatitude()));
                        filterBusinessRequest.setCustomerLong(String.valueOf(location.getLongitude()));
                    }
                }
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
                    filterBusinessRequest.setCustomerId(userId);
                else
                    filterBusinessRequest.setCustomerId(null);
                if (((ColorDrawable) tvLocation.getBackground()).getColor() == getResources().getColor(R.color.black))
                    filterBusinessRequest.setHaveLocation(1);
                else filterBusinessRequest.setHaveLocation(0);
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
            call = apiInterface.getImageSerch(filterBusinessRequest);
        } else {
            EOLoginRequest loginRequest = new EOLoginRequest();
            loginRequest.setCat_ids(selectedCatList);

            call = apiInterface.getLikedImages(BEARER.concat(this.headerToken),loginRequest);
        }
        progress.showProgressBar();
        call.enqueue(new Callback<SearchGalleryResponse>() {
            @Override
            public void onResponse(Call<SearchGalleryResponse> call, Response<SearchGalleryResponse> response) {
                layoutGallery.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyData.setText("No Gallery Images !");

                if (response.isSuccessful()) {
                        if(response.body().filter_count!=null)
                            ((TextView) view.findViewById(R.id.tv_filter)).setText("Filter"+(response.body().filter_count==0?"":" ("+response.body().filter_count+")"));
                    if (response.body().getStatus()) {

                        if (response.body().getPayload().size() > 0) {
                            galleryList.clear();
                            galleryList.addAll(response.body().getPayload());
                            layoutGallery.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                        } else {
                            if (!fromType.equalsIgnoreCase("profile"))
                            layoutGallery.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                            filterBusinessRequest = null;
                            tvEmptyData.setText("No Gallery Images !");
                        }
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        filterBusinessRequest = null;
                    }
                } else {
                    filterBusinessRequest = null;
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                recGallery.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchGalleryResponse> call, Throwable t) {
                if (!fromType.equalsIgnoreCase("profile"))
                layoutGallery.setVisibility(View.GONE);
                filterBusinessRequest = null;
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyData.setText("No Gallery Images !");
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void likeImage(Integer imgId, ImageView img, boolean isLIked,int position) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            changeView(isLIked, img);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest();
        request.setImg_id(imgId);

        apiInterface.likeImage(BEARER.concat(this.headerToken), request).enqueue(new Callback<GalleryImagesResponse>() {
            @Override
            public void onResponse(Call<GalleryImagesResponse> call, Response<GalleryImagesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        galleryList.get(position).setLikedImg(1);
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        changeView(isLIked, img);
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                    changeView(isLIked, img);
                }
            }

            @Override
            public void onFailure(Call<GalleryImagesResponse> call, Throwable t) {
                changeView(isLIked, img);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeView(boolean isLIked, ImageView img) {
        if (isLIked)
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
        else
            img.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim));
    }

    //todo ====================== items Adapter ============================
    class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
        private boolean isLiked = false;
        ConstraintLayout.LayoutParams params ;

        public GalleryAdapter() {
            params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 40);
        }

        @NonNull
        @Override
        public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_media, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
            SearchGalleryPayload searchGalleryPayload = galleryList.get(position);
            if (!TextUtils.isEmpty(searchGalleryPayload.getImg()))
                Picasso.get().load(searchGalleryPayload.getImg()).resize(500, 500).into(holder.imgGallery);
            if (searchGalleryPayload.likedImg == 1) {
                holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim));
            } else {
                holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
            }
            holder.imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!fromType.equalsIgnoreCase("profile")) {
                        if (isLogin) {
                            if (holder.imgLike.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_heart).getConstantState())) {
                                holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim));
                                isLiked = true;
                            } else {
                                holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
                                isLiked = false;
                            }
                            likeImage(searchGalleryPayload.getImgId(), holder.imgLike, isLiked,position);
                            holder.likeAnim.setVisibility(View.VISIBLE);
                            animateHeart(holder.likeAnim);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.likeAnim.setVisibility(View.GONE);
                                }
                            }, 400);
                        } else {
                            Toast.makeText(getActivity(), "please login first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActivityGalleryDetail.class);
                    intent.putExtra("img_payload", searchGalleryPayload);
                    if (fromType.equalsIgnoreCase("profile"))
                        intent.putExtra("profile", "");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), holder.imgGallery, ViewCompat.getTransitionName(holder.imgGallery));
                    startActivity(intent, options.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return galleryList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public void animateHeart(final ImageView view) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            prepareAnimation(scaleAnimation);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            prepareAnimation(alphaAnimation);
            AnimationSet animation = new AnimationSet(true);
            animation.addAnimation(alphaAnimation);
            animation.addAnimation(scaleAnimation);
            animation.setDuration(400);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }

        private Animation prepareAnimation(Animation animation) {
            animation.setRepeatCount(1);
            animation.setRepeatMode(Animation.REVERSE);
            return animation;
        }

        class GalleryViewHolder extends RecyclerView.ViewHolder {

            ImageView imgGallery, imgLike, likeAnim;
            ConstraintLayout itemLaout;

            public GalleryViewHolder(@NonNull View itemView) {
                super(itemView);
                imgGallery = itemView.findViewById(R.id.img_gallery);
                imgLike = itemView.findViewById(R.id.img_like);
                likeAnim = itemView.findViewById(R.id.img_heart);
                itemLaout=itemView.findViewById(R.id.item_laout);

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
        public RelatedCatAdapter.RelatedCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RelatedCatAdapter.RelatedCatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_filter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RelatedCatAdapter.RelatedCatViewHolder holder, int position) {
            holder.bName.setText(mList.get(position).getName());
            if (mList.get(position).isSelected())
                holder.imgTick.setVisibility(View.VISIBLE);
            else
                holder.imgTick.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLogin) {
                        if (holder.imgTick.getVisibility() == View.INVISIBLE) {
                            holder.imgTick.setVisibility(View.VISIBLE);
                            relateditemList.get(position).setSelected(true);
                        } else {
                            relateditemList.get(position).setSelected(false);
                            holder.imgTick.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        askLoginDialog();
                    }
                }
            });
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
        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        AlertDialog sDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setCancelable(false).create();
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
                startActivityForResult(new Intent(getActivity(), ViewPagerActivity.class).putExtra("fromFilter", true), 110);
                sDialog.dismiss();
            }
        });
    }


    //todo==================================Age Adapter=========================
    public class AgeAdapter extends RecyclerView.Adapter<AgeAdapter.AgeHolder> {
        private List<String> ageModel;

        public AgeAdapter(List<String> ageModel) {
            this.ageModel = ageModel;
        }

        @NonNull
        @Override
        public AgeAdapter.AgeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AgeAdapter.AgeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_age, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AgeAdapter.AgeHolder holder, int position) {
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

    private void selectedAge(TextView newTab, boolean isDeSelected) {
        if (isDeSelected) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
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
        public OtherFeaturesAdapter.OtherFeaturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OtherFeaturesAdapter.OtherFeaturesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_other_feature, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OtherFeaturesAdapter.OtherFeaturesViewHolder holder, int position) {
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
//                        checkType(otherFeature.getFeatureName(), true);
                    } else {
                        listOtherFeatures.get(position).setSelected(false);
//                        checkType(otherFeature.getFeatureName(), false);
                        holder.imgTick.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        private void loadImage(String url, ImageView img) {
            Glide.with(getActivity())
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


//    private void loadImages(String imagePath, ImageView imageView) {
//        Glide.with(this)
//                .load(imagePath)
//                .error(R.mipmap.ic_launcher)
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .into(imageView);
//    }

}