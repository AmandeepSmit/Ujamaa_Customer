package com.ujamaaonline.customer.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListData;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.business_data.OtherFeature;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;
import com.ujamaaonline.customer.models.cat_filter.RelatedItem;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;


import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;

public class ActivityMap extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraIdleListener, View.OnClickListener {
    private GoogleMap mMap;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng latLng;
    private final int REQUEST_CODE = 123;
    private double lat, lng;
    private boolean isLoggedIn = false;
    private Marker marker;
    private TextView tvFilter;
    private RecyclerView recRelatedCat, recOtherFeature;
    private BottomSheetBehavior sheetBehavior;
    private FilterBusinessRequest filterBusinessRequest;
    private FilterBusinessRequest previousSelected;
    private List<OtherFeature> listOtherFeatures = new ArrayList<>();
    private TextView title, tvReviewed, tvDistance, tvHighRated, tvRelevance, priceTypeOne, priceTypeTwo, priceTypeThree, priceTypeFour, tvLocation, tvReviews,
            tvWebsite, tvVideo, tvOpenNow, tvOpenAt, tvMinAge, tvContactNumber, tvMessage, tvApply, tvReputation, tvDisabledFacility, selectedTab, selectedPriceType,
            selectedOpenType;
    private Integer customerId;
    private String postCode = "", headerToken = "";
    private List<RelatedItem> relateditemList = new ArrayList<>();
    private String businessCatId, type;
    private List<BusinessListData> bList;
    private LinearLayout layoutBottomSheet, layoutFilter, layoutSearchTab, layoutProfileTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        title = findViewById(R.id.mp_cat_name_search);
        title.setText(getIntent().getStringExtra("cat_name"));
        bList = (List<BusinessListData>) getIntent().getSerializableExtra("business_list");
        listOtherFeatures = (List<OtherFeature>) getIntent().getSerializableExtra("listOtherFeatures");

        if (getIntent().hasExtra("filter_data")) {
            previousSelected = (FilterBusinessRequest) getIntent().getSerializableExtra("filter_pre");
            filterBusinessRequest = (FilterBusinessRequest) getIntent().getSerializableExtra("filter_data");
            if (getIntent().hasExtra("post")) {
                postCode = getIntent().getStringExtra("post");
            }
            type = getIntent().getStringExtra("type");
        }

        initView();
        setAllClickLisneners();
        findViewById(R.id.mp_listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.mp_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
    }

    private void addStaticData() {
        relateditemList.add(new RelatedItem("You are subscribed to them"));
        relateditemList.add(new RelatedItem("You've bookmarked them"));
        relateditemList.add(new RelatedItem("You've liked an image from them"));
//        relateditemList.add("They are in lists you've made or follow");
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
        tvOpenAt.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        tvReviews.setOnClickListener(this);
        tvWebsite.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvMinAge.setOnClickListener(this);
        tvContactNumber.setOnClickListener(this);
        tvMessage.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        tvReputation.setOnClickListener(this);
        tvDisabledFacility.setOnClickListener(this);
        layoutFilter.setOnClickListener(this);
        layoutBottomSheet.setOnClickListener(this);
        findViewById(R.id.tv_reset).setOnClickListener(this);
    }

    private List<Integer> otherFeaturesIds = new ArrayList<>();

    private int getIntValue(TextView value) {
        if (value == null)
            return 0;
        else return value.getText().length();
    }

    private void getBusinessList(boolean isFilter) {
        Call<BusinessListResponse> call = null;

        if (otherFeaturesIds.size() > 0)
            otherFeaturesIds.clear();
        if (!GlobalUtil.isNetworkAvailable(getApplicationContext())) {
            UIUtil.showNetworkDialog(getApplicationContext());
            return;
        }

//        filterBusinessRequest.setType(Integer.valueOf(type));
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

        progress.showProgressBar();
        call.enqueue(new Callback<BusinessListResponse>() {
            @Override
            public void onResponse(Call<BusinessListResponse> call, Response<BusinessListResponse> response) {
                bList.clear();
                if (response.isSuccessful()) {

                    tvFilter.setText("Filter");

                    if(response.body().getFilter_count()!=null)
                    {
                        if (response.body().getFilter_count()>0)
                        {
                            tvFilter.setText("Filter("+response.body().getFilter_count()+")");
                        }
                        else
                        {
                            tvFilter.setText("Filter");
                        }
                    }
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0) {
                            bList.addAll(response.body().getPayload());
                            mMap.clear();
                            loadDataOnMap();
                        }
                    } else {
                        Toast.makeText(ActivityMap.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityMap.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }

            @Override
            public void onFailure(Call<BusinessListResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(ActivityMap.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(ActivityMap.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        customerId = loginPreferences.getInt(USER_ID, 0);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        businessCatId = getIntent().getStringExtra("businessId");
        tvFilter=findViewById(R.id.tv_filter);
        layoutBottomSheet = findViewById(R.id.filter_bottom_sheet_cat);
        isLoggedIn = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        tvReviewed = findViewById(R.id.tv_reviewed);
        tvDistance = findViewById(R.id.tv_distance);
        tvDistance = findViewById(R.id.tv_distance);
        tvRelevance = findViewById(R.id.tv_relevance);
        tvHighRated = findViewById(R.id.tv_rated);
        priceTypeOne = findViewById(R.id.tv_price_one);
        priceTypeTwo = findViewById(R.id.tv_price_two);
        priceTypeThree = findViewById(R.id.tv_price_three);
        priceTypeFour = findViewById(R.id.tv_price_four);
        tvLocation = findViewById(R.id.tv_location);
        tvReviews = findViewById(R.id.tv_reviews);
        recRelatedCat = findViewById(R.id.rec_related_cat);
        tvWebsite = findViewById(R.id.tv_website);
        tvOpenNow = findViewById(R.id.tv_open_now);
        tvVideo = findViewById(R.id.tv_video);
        tvOpenAt = findViewById(R.id.tv_open_at);
        tvMinAge = findViewById(R.id.tv_min_age);
        recOtherFeature = findViewById(R.id.rec_other_feature);
        recOtherFeature.setAdapter(new OtherFeaturesAdapter(listOtherFeatures));
        tvContactNumber = findViewById(R.id.tv_cont_number);
        tvMessage = findViewById(R.id.tv_msg_avail);
        tvApply = findViewById(R.id.tv_apply);
        tvReputation = findViewById(R.id.tv_rep_cred);
        tvDisabledFacility = findViewById(R.id.tv_disable_fac);
        layoutFilter = findViewById(R.id.layout_filter);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        addValue();
        addStaticData();
        recRelatedCat.setAdapter(new RelatedCatAdapter(relateditemList));
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_CODE);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    onLocationChanged(location);
                } else {
                    onLocationChanged(ObjectUtil.getLocation(ActivityMap.this));
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        getLocation();

    }
//    private Bitmap createCustomerMarker(Context context ){
//        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customer_marker, null);
//        cusMarker = view.findViewById(R.id.customerMarker);
//        // markerBg=view.findViewById(R.id.marker_bg);
//        Picasso.get().load(grumerImage).into(cusMarker);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;
//    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            mMap.moveCamera(center);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Phone Location")
            );
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .bearing(0)
                    .tilt(50)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        loadDataOnMap();

    }

    private void loadDataOnMap() {
        if (bList != null) {
            if (bList.size() > 0) {
                for (BusinessListData businessListData : bList) {
                    if (!TextUtils.isEmpty(businessListData.getBusiness_lat()) && !TextUtils.isEmpty(businessListData.getBusiness_long())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(businessListData.getBusiness_lat()), Double.parseDouble(businessListData.getBusiness_long()))).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(getApplicationContext(), businessListData.getBusiness_logo()))));
                            }
                        });

                    }
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityMap.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                } else {
                    new androidx.appcompat.app.AlertDialog.Builder(ActivityMap.this)
                            .setMessage("Please Grant Permission first")
                            .setCancelable(false)
                            .setPositiveButton("GO TO SETTING", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(uri);
                                    getApplicationContext().startActivity(intent);
                                }
                            }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        }
    }

    @Override
    public void onCameraIdle() {
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng)
//                .bearing(0)
//                .tilt(0)
//                .build();
        float zoom = mMap.getCameraPosition().zoom;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    private Bitmap createCustomMarker(Context context, String url) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customer_marker, null);
        ImageView markerImage = view.findViewById(R.id.customerMarker);
//        Glide.with(context)
//                .asBitmap().load(url).placeholder(R.drawable.default_image)
//                .listener(new RequestListener<Bitmap>() {
//                              @Override
//                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
//                                  Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
//                                  return false;
//                              }
//
//                              @Override
//                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
//                                  Toast.makeText(context, "loaded", Toast.LENGTH_SHORT).show();
//                                  runOnUiThread(new Runnable() {
//                                      @Override
//                                      public void run() {
//                                          markerImage.setImageBitmap(bitmap);
//                                      }
//                                  });
//
//                                  return false;
//                              }
//                          }
//                ).submit();


        Picasso.get().load(url).error(R.drawable.default_image).placeholder(R.drawable.default_image).into(markerImage);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
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
                startActivity(new Intent(ActivityMap.this, ActivityMap.class).putExtra("business_list", (Serializable) bList));
                break;
            case R.id.tv_reset:
                finish();
                startActivity(getIntent().putExtra("businessId", businessCatId).putExtra("type", type).putExtra("filter_data", previousSelected).putExtra("filter_pre", previousSelected));
                break;
        }
    }

    private void seletedMultipletab(TextView newTab) {
        if (((ColorDrawable) newTab.getBackground()).getColor() == getResources().getColor(R.color.white)) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
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
        mTimePicker = new TimePickerDialog(ActivityMap.this, R.style.DialogTheme, onStartTimeListener, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    TimePickerDialog.OnTimeSetListener onStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvOpenAt.setText("Open at:" + timeFormater.format(hourOfDay) + ":" + timeFormater.format(minute));
            changeOpenType(tvOpenAt);
        }
    };

    private void changeOpenType(TextView newTab) {
        if (selectedOpenType != null) {
            if (selectedOpenType.equals(newTab)) {
                selectedOpenType.setBackgroundColor(getResources().getColor(R.color.white));
                selectedOpenType.setTextColor(getResources().getColor(R.color.black));
                selectedOpenType = null;
                return;
            }
            selectedOpenType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedOpenType.setTextColor(getResources().getColor(R.color.black));
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        }
        selectedOpenType = newTab;
    }

    private Dialog dialog;

    private void ageDialog() {
        dialog = new Dialog(ActivityMap.this);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_age_select);
        RecyclerView ageRecyler = dialog.findViewById(R.id.ra_ageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityMap.this);
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

    private void askLoginDialog() {
        LayoutInflater li = LayoutInflater.from(ActivityMap.this);
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        AlertDialog sDialog = new AlertDialog.Builder(ActivityMap.this).setView(dialogView).setCancelable(false).create();
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
                startActivityForResult(new Intent(ActivityMap.this, ViewPagerActivity.class).putExtra("fromFilter", true), 110);
                sDialog.dismiss();
            }
        });
    }

    private void changePriceType(TextView newTab) {
        if (selectedPriceType != null) {
            if (selectedPriceType.equals(newTab)) {
                selectedPriceType.setBackgroundColor(getResources().getColor(R.color.white));
                selectedPriceType.setTextColor(getResources().getColor(R.color.black));
                selectedPriceType = null;
                return;
            }
            selectedPriceType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedPriceType.setTextColor(getResources().getColor(R.color.black));
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        }
        selectedPriceType = newTab;
    }

    private void changeTab(TextView newTab) {
        newTab.setBackgroundColor(getResources().getColor(R.color.black));
        newTab.setTextColor(getResources().getColor(R.color.white));
        if (selectedTab != null) {
            if (selectedTab.equals(newTab))
                return;
            selectedTab.setBackgroundColor(getResources().getColor(R.color.white));
            selectedTab.setTextColor(getResources().getColor(R.color.black));
        }
        selectedTab = newTab;
    }

//    private void getServiceList(){
//        if (!BaseUtil.isNetworkAvailable(ActivityMap.this)) {
//
//            Toast.makeText(this, "Not connected. Please check your internet!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        ApiClient.getClient().getServiceList("Bearer " + BaseUtil.getUserToken(getApplicationContext())).enqueue(new Callback<ServiceListResponse>() {
//            @Override
//            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus()) {
//                        slist = response.body().getPayload();
////                        for (ServiceListPayload serviceListPayload:slist)
////                        {
////                        //    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(getApplicationContext()))));
////                        }
//                    } else {
//                    }
//                } else {
//                    Toast.makeText(ActivityMap.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
//                Toast.makeText(ActivityMap.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
                    } else {
                        listOtherFeatures.get(position).setSelected(false);
                        holder.imgTick.setVisibility(View.INVISIBLE);
                    }
                }
            });


        }

        private void loadImage(String url, ImageView img) {
            Glide.with(ActivityMap.this)
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
                    if (isLoggedIn) {
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
}