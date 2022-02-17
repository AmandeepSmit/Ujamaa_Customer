package com.ujamaaonline.customer.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.BuildConfig;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.ViewPagerAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.fragments.GalleryFragment;
import com.ujamaaonline.customer.fragments.InformationFragment;
import com.ujamaaonline.customer.fragments.MyReviewsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ujamaaonline.customer.models.VisitProfileRequest;
import com.ujamaaonline.customer.models.VisitProfileResponse;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.get_business_reviews.AllReviewResponse;
import com.ujamaaonline.customer.models.signup.EORegisterResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;

public class BusinessProfile extends AppCompatActivity implements View.OnClickListener {
    private TabLayout profileTabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView busName, sumLine, tvSubscribe, tvSummaryLine, tvBookmark, tvFloatSubscribe;
    private FloatingActionButton fab;
    private ImageView imgBackground, profileImg, img_bookmark;
    private boolean isFABOpen = false, isSubscribed = false, isBookmarked = false;
    private String businessId = "";
    private String headerToken;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private boolean isLogin = false;
    private SessionSecuredPreferences loginPreferences;
    private String imgUrl="";
    private LinearLayout blurLayout, fab6, fab5, fab1, fab2, fab3, fab4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);

        businessId = getIntent().getStringExtra("businessCatId");
        if (TextUtils.isEmpty(businessId))
        {
           businessId= getIntent().getData().getPath().split("deep-linking/")[1];
        }
        initView();
        setOnClickLiseners();
    }

    private void setOnClickLiseners() {
        findViewById(R.id.bp_bookmarks).setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        fab6.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab5.setOnClickListener(this);
    }


    private void visitProfile() {
        if (!GlobalUtil.isNetworkAvailable(BusinessProfile.this)) {
            UIUtil.showNetworkDialog(BusinessProfile.this);
            return;
        }
        VisitProfileRequest request = new VisitProfileRequest(Integer.parseInt(businessId));
        apiInterface.visitProfile(BEARER.concat(this.headerToken), request).enqueue(new Callback<VisitProfileResponse>() {
            @Override
            public void onResponse(Call<VisitProfileResponse> call, Response<VisitProfileResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {

                    } else {
                        Toast.makeText(BusinessProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BusinessProfile.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VisitProfileResponse> call, Throwable t) {
                Toast.makeText(BusinessProfile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shareProfile() {
        if (!GlobalUtil.isNetworkAvailable(BusinessProfile.this)) {
            UIUtil.showNetworkDialog(BusinessProfile.this);
            return;
        }
        VisitProfileRequest request = new VisitProfileRequest(Integer.parseInt(businessId));
        apiInterface.shareProfile(BEARER.concat(this.headerToken), request).enqueue(new Callback<VisitProfileResponse>() {
            @Override
            public void onResponse(Call<VisitProfileResponse> call, Response<VisitProfileResponse> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<VisitProfileResponse> call, Throwable t) {
                Toast.makeText(BusinessProfile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void userData(String name, Integer isSubscribed, Integer bookMarked) {
        busName.setText(name);
        switch (isSubscribed) {
            case 1:
                tvSubscribe.setText("UNSUBSCRIBE");
                this.isSubscribed = true;
                tvFloatSubscribe.setText("Unsubscribe");
                break;
            case 0:
                tvSubscribe.setText("SUBSCRIBE");
                tvFloatSubscribe.setText("Subscribe");
                this.isSubscribed = false;
                break;
        }
        switch (bookMarked) {
            case 1:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    img_bookmark.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bookmarked));
                } else {
                    img_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_bookmarked));
                }
                tvBookmark.setText("Unbookmark");
                isBookmarked = true;
                break;
            case 0:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    img_bookmark.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bookmark));
                } else {
                    img_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_bookmark));
                }
                tvBookmark.setText("Bookmark");
                isBookmarked = false;
                break;
        }
    }

    public void displayProfileImages(String imgurl, Integer type) {
        switch (type) {
            case 1:
                Picasso.get().load(imgurl).placeholder(R.drawable.ic_top_image).error(R.drawable.ic_top_image).resize(500, 500).into(imgBackground);
                break;
            case 2:
                imgUrl=imgurl;
                Picasso.get().load(imgurl).placeholder(R.drawable.ic_user_two).error(R.drawable.ic_user_two).resize(500, 500).into(profileImg);
                break;
            case 3:
                tvSummaryLine.setText(Html.fromHtml(imgurl));
                break;
        }
    }


    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.busName = this.findViewById(R.id.bp_name);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        blurLayout = findViewById(R.id.blur_layout);
        tvSubscribe = findViewById(R.id.tv_subscribe);
        img_bookmark = findViewById(R.id.bp_bookmarks);
        tvSummaryLine = findViewById(R.id.bp_summeryLine);
        tvBookmark = findViewById(R.id.tv_bookmark);
        tvFloatSubscribe = findViewById(R.id.tv_float_subscribe);
        profileImg = findViewById(R.id.profle_img);
        imgBackground = findViewById(R.id.img1);
        isLogin = this.loginPreferences.getBoolean(IS_LOGGED_IN, false);
        tvSubscribe.setOnClickListener(this);
        this.sumLine = this.findViewById(R.id.bp_summeryLine);
        this.profileTabLayout = this.findViewById(R.id.profileTabLayout);
        fab = findViewById(R.id.baseFloatingActionButton);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);
        fab5 = findViewById(R.id.fab5);
        fab6 = findViewById(R.id.fab6);
        this.viewPager = this.findViewById(R.id.viewPager);
//        this.tv_business_name = this.findViewById(R.id.tv_business_name);
        this.viewPagerSetup();
        setOnBackListener();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        visitProfile();
    }

    public void changeViewpager(int pos) {
        viewPager.setCurrentItem(pos);
    }

    private void buttonRotation(Float value) {
        ViewCompat.animate(fab).
                rotation(value).
                withLayer().
                setDuration(300).
                start();
    }

    private void showFABMenu() {
        isFABOpen = true;
        buttonRotation(135f);
        blurLayout.setVisibility(View.VISIBLE);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab1.getChildAt(0).setVisibility(View.VISIBLE);
        fab1.getChildAt(1).setVisibility(View.VISIBLE);
//        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
//        fab2.getChildAt(0).setVisibility(View.VISIBLE);
//        fab2.getChildAt(1).setVisibility(View.VISIBLE);
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.getChildAt(0).setVisibility(View.VISIBLE);
        fab3.getChildAt(1).setVisibility(View.VISIBLE);
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        fab4.getChildAt(0).setVisibility(View.VISIBLE);
        fab4.getChildAt(1).setVisibility(View.VISIBLE);
        fab5.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
        fab5.getChildAt(0).setVisibility(View.VISIBLE);
        fab5.getChildAt(1).setVisibility(View.VISIBLE);
        fab6.animate().translationY(-getResources().getDimension(R.dimen.standard_255));
        fab6.getChildAt(0).setVisibility(View.VISIBLE);
        fab6.getChildAt(1).setVisibility(View.VISIBLE);
    }

    private void closeFABMenu() {
        isFABOpen = false;
        buttonRotation(0f);
        blurLayout.setVisibility(View.GONE);
        fab1.animate().translationY(0);
        fab1.getChildAt(0).setVisibility(View.GONE);
        fab1.getChildAt(1).setVisibility(View.GONE);
//        fab2.animate().translationY(0);
//        fab2.getChildAt(0).setVisibility(View.GONE);
//        fab2.getChildAt(1).setVisibility(View.GONE);
        fab3.animate().translationY(0);
        fab3.getChildAt(0).setVisibility(View.GONE);
        fab3.getChildAt(1).setVisibility(View.GONE);
        fab4.animate().translationY(0);
        fab4.getChildAt(0).setVisibility(View.GONE);
        fab4.getChildAt(1).setVisibility(View.GONE);
        fab5.animate().translationY(0);
        fab5.getChildAt(0).setVisibility(View.GONE);
        fab5.getChildAt(1).setVisibility(View.GONE);
        fab6.animate().translationY(0);
        fab6.getChildAt(0).setVisibility(View.GONE);
        fab6.getChildAt(1).setVisibility(View.GONE);
    }

    private void subscribeBusiness(String businessId) {
        if (!GlobalUtil.isNetworkAvailable(BusinessProfile.this)) {
            UIUtil.showNetworkDialog(BusinessProfile.this);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest(businessId);
        progress.showProgressBar();
        apiInterface.subscribeBusiness(BEARER.concat(this.headerToken), request).enqueue(new Callback<AllReviewResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                progress.hideProgressBar();
//                Toast.makeText(BusinessProfile.this, "onresponse", Toast.LENGTH_SHORT).show();
                if (!ObjectUtil.isEmpty(response.body())) {
                    AllReviewResponse registerResponse = response.body();
                    if (!ObjectUtil.isEmpty(registerResponse)) {
                        if (registerResponse.getStatus() == RESPONSE_SUCCESS) {
//                            Toast.makeText(BusinessProfile.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            InformationFragment informationFragment = (InformationFragment) viewPagerAdapter.instantiateItem(viewPager, 0);
                            informationFragment.reLoadData();
                        } else {
//                            Toast.makeText(BusinessProfile.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AllReviewResponse> call, Throwable t) {
//                Toast.makeText(BusinessProfile.this, "on Error", Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
//                    Toast.makeText(BusinessProfile.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bookmarkBusiness(String businessId) {
        if (!GlobalUtil.isNetworkAvailable(BusinessProfile.this)) {
            UIUtil.showNetworkDialog(BusinessProfile.this);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest(businessId);
        progress.showProgressBar();
        apiInterface.bookmarkBusiness(BEARER.concat(this.headerToken), request).enqueue(new Callback<AllReviewResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                progress.hideProgressBar();
//                Toast.makeText(BusinessProfile.this, "onresponse", Toast.LENGTH_SHORT).show();
                if (!ObjectUtil.isEmpty(response.body())) {
                    AllReviewResponse registerResponse = response.body();
                    if (!ObjectUtil.isEmpty(registerResponse)) {
                        if (registerResponse.getStatus() == RESPONSE_SUCCESS) {
//                            Toast.makeText(BusinessProfile.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            InformationFragment informationFragment = (InformationFragment) viewPagerAdapter.instantiateItem(viewPager, 0);
                            informationFragment.reLoadData();
                        } else {
//                            Toast.makeText(BusinessProfile.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AllReviewResponse> call, Throwable t) {
//                Toast.makeText(BusinessProfile.this, "on Error", Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
//                    Toast.makeText(BusinessProfile.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "ujamaa" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    private void viewPagerSetup(){
        this.viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.viewPager.setOffscreenPageLimit(3);
        this.viewPagerAdapter.addFragment(new InformationFragment(businessId), "Information");
        this.viewPagerAdapter.addFragment(new GalleryFragment(businessId), "Gallery");
        this.viewPagerAdapter.addFragment(new MyReviewsFragment(businessId), "Reviews");
        this.viewPager.setAdapter(this.viewPagerAdapter);
        this.profileTabLayout.setupWithViewPager(this.viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager.getCurrentItem());
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void setOnBackListener() {
        tvSubscribe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_subscribe:
            case R.id.fab4:
                if (isLogin)
                    subscribeBusiness(businessId);
                else {
                    askLoginDialog();
                }
                break;
            case R.id.bp_bookmarks:
            case R.id.fab3:
                if (isLogin)
                    bookmarkBusiness(businessId);
                else {
                    askLoginDialog();
                }
                break;
            case R.id.fab6:
                if (isLogin) {
                    shareProfile();
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share via");
                    String app_url = "https://www.ujamaaonline.co.uk/download?business_id=" + businessId;
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                } else {
                    askLoginDialog();
                }
                break;
            case R.id.fab1:
                if (isLogin)
                {
                    Intent intent = new Intent(this, ActivityChat.class);
                    intent.putExtra("userName", busName.getText().toString());
                    intent.putExtra("businessId", String.valueOf(businessId));
                    intent.putExtra("businessLogo", imgUrl);
                    startActivity(intent);
                }
                else {
                    askLoginDialog();
                }
                break;
            case R.id.fab5:
                closeFABMenu();
                changeViewpager(2);
                break;
//                bookmarkBusiness(businessId);
//                break;
        }
    }

    private void askLoginDialog() {
        LayoutInflater li = LayoutInflater.from(BusinessProfile.this);
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        AlertDialog sDialog = new AlertDialog.Builder(BusinessProfile.this).setView(dialogView).setCancelable(false).create();
        TextView title = dialogView.findViewById(R.id.dr_title);
        TextView desc = dialogView.findViewById(R.id.dr_desc);
        TextView back = dialogView.findViewById(R.id.tv_cancel);
        TextView block = dialogView.findViewById(R.id.tv_reset);
        back.setText("Cancel");
        block.setText("Login Now");
        title.setText("Login");
        desc.setText("You have to be logged in for subscribe");
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
                startActivityForResult(new Intent(BusinessProfile.this, LoginActivity.class).putExtra("fromFilter", true), 110);
                sDialog.dismiss();
            }
        });
    }

    public void backBtnCick(View view) {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
            return;
        }

        finish();
    }

    public void bpShareClick(View view) {
        shareProfile();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share via");
        String app_url = "https://www.ujamaaonline.co.uk/download?business_id=" + businessId;
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void bpMoreBtnclick(View view) {
        if (!isFABOpen) {
            showFABMenu();
        } else {
            closeFABMenu();
        }
    }
}
