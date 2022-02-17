package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.LogoutRequest;
import com.ujamaaonline.customer.models.LogoutResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.user_profile.ProfilePayload;
import com.ujamaaonline.customer.models.user_profile.UserProfileResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static android.provider.ContactsContract.CommonDataKinds.Organization.TITLE;
import static com.google.firebase.messaging.Constants.MessageNotificationKeys.ICON;
import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ProfilePayload profilePayload = null;
    private ImageView imgUser, imgFirst, imgSecond;
    private TextView tvUserName, tvDistance,tvFirstCharName;
    private SessionSecuredPreferences loginPreferences;
    private LinearLayout layoutDistanceUnit;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private String headerToken;
    private String distanceUnit = "";
    private List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        if (getIntent() != null) {
            if (getIntent().hasExtra("profile_data")) {
                profilePayload = (ProfilePayload) getIntent().getSerializableExtra("profile_data");
               if (profilePayload!=null)
               {
                    if (!TextUtils.isEmpty(profilePayload.image)) {
                    Picasso.get().load(profilePayload.image).error(R.drawable.ic_user_two).placeholder(R.drawable.ic_user_two).into(imgUser);

                }
                    else
                    {
                        tvFirstCharName.setVisibility(View.VISIBLE);
                        tvFirstCharName.setText(profilePayload.firstname.substring(0,1).toUpperCase());
                    }
                tvUserName.setText(profilePayload.firstname + " " + profilePayload.lastname);
                distanceUnit=profilePayload.customer_distance_unit;
                tvDistance.setText(distanceUnit);
               }
            }
        }
    }

    private void initView() {
        imgUser = findViewById(R.id.img_user);
        tvUserName = findViewById(R.id.tv_user_name);
        tvDistance = findViewById(R.id.tv_distance_unit);
        mBottomSheetDialog = new BottomSheetDialog(SettingActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_miles, null);
        imgFirst = sheetView.findViewById(R.id.img_first);
        findViewById(R.id.layout_terms_use).setOnClickListener(this);
        findViewById(R.id.layout_privacy_policy).setOnClickListener(this);
        imgSecond = sheetView.findViewById(R.id.img_second);
        tvFirstCharName=findViewById(R.id.tv_name);
        this.progress = new GlobalProgressDialog(SettingActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        sheetView.findViewById(R.id.layout_most_recent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSecond.setVisibility(View.INVISIBLE);
                imgFirst.setVisibility(View.VISIBLE);
                distanceUnit = "Miles";
            }
        });
        sheetView.findViewById(R.id.layout_hightest_rating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSecond.setVisibility(View.VISIBLE);
                imgFirst.setVisibility(View.INVISIBLE);
                distanceUnit = "Kilometers";
            }
        });
        sheetView.findViewById(R.id.tv_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDistance(true, distanceUnit);
                mBottomSheetDialog.dismiss();
            }
        });
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        registerReceiver(receiver, new IntentFilter("update_name"));
        findViewById(R.id.layout_edit_profile).setOnClickListener(this);
        findViewById(R.id.layout_notification).setOnClickListener(this);
        findViewById(R.id.layout_password).setOnClickListener(this);
        findViewById(R.id.layout_about_us).setOnClickListener(this);
        findViewById(R.id.layout_help).setOnClickListener(this);
        findViewById(R.id.layout_share).setOnClickListener(this);
        findViewById(R.id.tv_logout).setOnClickListener(this);
        layoutDistanceUnit = findViewById(R.id.layout_distance_unit);
        findViewById(R.id.layout_distance_unit).setOnClickListener(this);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.hasExtra("profile_data")) {
                    profilePayload = (ProfilePayload) intent.getSerializableExtra("profile_data");
                    if (!TextUtils.isEmpty(profilePayload.image)) {
                        Picasso.get().load(profilePayload.image).error(R.drawable.ic_user_two).placeholder(R.drawable.ic_user_two).into(imgUser);
                        tvFirstCharName.setVisibility(View.GONE);
                    }
                    else
                    {

                        tvFirstCharName.setVisibility(View.VISIBLE);
                        tvFirstCharName.setText(profilePayload.firstname.substring(0,1).toUpperCase());
                    }
                    tvUserName.setText(profilePayload.firstname + " " + profilePayload.lastname);
                    distanceUnit=profilePayload.customer_distance_unit;
                    tvDistance.setText(distanceUnit);
                }
            }
        }
    };

    private void showDiscardDialog() {
        LayoutInflater li = LayoutInflater.from(SettingActivity.this);
        View dialogView = li.inflate(R.layout.dilaog_discard, null);
        AlertDialog sDialog = new AlertDialog.Builder(SettingActivity.this).setView(dialogView).setCancelable(false).create();
        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        TextView tvMsg = dialogView.findViewById(R.id.tv_msg);
        TextView tv_back = dialogView.findViewById(R.id.tv_back);
        tvTitle.setText("Logout");
        tv_back.setText("Cancel");
        tvMsg.setText("Are you sure! you want to logout?");
        TextView tv_discard_changes = dialogView.findViewById(R.id.tv_discard_changes);
        tv_discard_changes.setText("Logout");
        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        sDialog.show();
        tv_back.setOnClickListener(v -> sDialog.dismiss());
        tv_discard_changes.setOnClickListener(v -> {
            sDialog.dismiss();
            logoutUser();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    public void backBtnCick(View view) {
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_edit_profile:
                if (profilePayload != null)
                    startActivity(new Intent(SettingActivity.this, EditProfileActivity.class).putExtra("profile_data", profilePayload));
                break;
            case R.id.layout_notification:
                startActivity(new Intent(SettingActivity.this, NotificationActivity.class));
                break;
            case R.id.layout_password:
                startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class).putExtra("email", profilePayload.email));
                break;
            case R.id.layout_about_us:
                startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
                break;
            case R.id.layout_help:
                startActivity(new Intent(SettingActivity.this, HelpActivity.class));
                break;
            case R.id.layout_share:
                startActivity(new Intent(SettingActivity.this, ShareAndEarnPointActivity.class));
                break;
            case R.id.tv_logout:
                showDiscardDialog();
                break;
            case R.id.layout_distance_unit:
                mBottomSheetDialog.show();
                break;
            case R.id.layout_terms_use:
                startActivity(new Intent(SettingActivity.this,TermsOfUseActivity.class).putExtra("terms",""));
                break;
            case R.id.layout_privacy_policy:
                startActivity(new Intent(SettingActivity.this,TermsOfUseActivity.class).putExtra("privacy",""));
                break;
        }
    }
    BottomSheetDialog mBottomSheetDialog;
    private void updateDistance(boolean isProgress, String distanceUnit) {
        if (!GlobalUtil.isNetworkAvailable(SettingActivity.this)) {
            UIUtil.showNetworkDialog(SettingActivity.this);
            return;
        }
        if (isProgress)
            progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setDistance_unit(distanceUnit);
        apiInterface.postDistanceUnit(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<UserProfileResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (isProgress)
                    progress.hideProgressBar();
                if (response.body().status) {
                    Toast.makeText(SettingActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    tvDistance.setText(distanceUnit);
                    sendBroadcast(new Intent("load_profile_api"));
                } else {
                    Toast.makeText(SettingActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {

                if (t.getMessage() != null) {
                    if (isProgress)
                        progress.hideProgressBar();
                    Toast.makeText(SettingActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logoutUser(){

      String  deviceId= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        if (!GlobalUtil.isNetworkAvailable(SettingActivity.this)) {
            UIUtil.showNetworkDialog(SettingActivity.this);
            return;
        }
        progress.showProgressBar();
        LogoutRequest request = new LogoutRequest(deviceId);
        apiInterface.logoutUser(Constants.BEARER.concat(this.headerToken), request).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()){
                        loginPreferences.edit().clear().apply();
                        startActivity(new Intent(SettingActivity.this, ViewPagerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                    }else{
                        Toast.makeText(SettingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(SettingActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }
            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    
}