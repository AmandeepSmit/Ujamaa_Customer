package com.ujamaaonline.customer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.NotificationActivity;
import com.ujamaaonline.customer.activities.NotificationAndAdminMessages;
import com.ujamaaonline.customer.activities.SettingActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.user_profile.ProfilePayload;
import com.ujamaaonline.customer.models.user_profile.UserProfileResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private ImageView imgUser;
    private TextView tvUserName, tvTitle;
    private RelativeLayout layoutImg;
    private ProfilePayload profilePayload = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initView();
        return view;
    }
    private void initView() {
        this.progress = new GlobalProgressDialog(view.getContext());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        getActivity().registerReceiver(receiver, new IntentFilter("load_profile_api"));
        viewPager = view.findViewById(R.id.up_view_pager);
        tabLayout = view.findViewById(R.id.up_tablayout);
        tvTitle = view.findViewById(R.id.tv_title);
        layoutImg = view.findViewById(R.id.layout_img);
        tvUserName = view.findViewById(R.id.tv_user_name);
        imgUser = view.findViewById(R.id.img_user);
        view.findViewById(R.id.img_notification).setOnClickListener(this);
        view.findViewById(R.id.img_setting).setOnClickListener(this);
        viewPagerSetUp();
        getUserProfile(true);
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getUserProfile(false);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private void getUserProfile(boolean isProgress) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        EOLoginRequest loginRequest = new EOLoginRequest();
        if (!ObjectUtil.isEmpty(loginRequest)) {
            if (isProgress)
                progress.showProgressBar();
            apiInterface.getUserProfile(BEARER.concat(this.headerToken)).enqueue(new Callback<UserProfileResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (isProgress)
                        progress.hideProgressBar();
                    if (response.body().status) {
                        if (response.body().payload != null) {
                            tvTitle.setText(TextUtils.isEmpty(response.body().payload.firstname) ? "Profile" : response.body().payload.firstname);
                            tvUserName.setText(response.body().payload.firstname.substring(0, 1));
                            tvUserName.setVisibility(View.VISIBLE);
                            profilePayload = response.body().payload;
                            if (!TextUtils.isEmpty(response.body().payload.image)) {
                                Picasso.get().load(response.body().payload.image).error(R.drawable.dummy).resize(600, 600).into(imgUser);
                                tvUserName.setVisibility(View.GONE);
                            }
                            if (profilePayload!=null)
                            getActivity().sendBroadcast(new Intent("update_name").putExtra("profile_data", profilePayload));
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {

                    if (t.getMessage() != null) {
                        if (isProgress)
                            progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void viewPagerSetUp() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.add(new YourActivityFragment(), "Your Activity");
        viewPagerAdapter.add(new YourReviewsFragment(), "Your Reviews");
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 0) {
//                    layoutImg.setVisibility(View.VISIBLE);
//                } else {
//                    layoutImg.setVisibility(View.GONE);
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_setting:
                if (profilePayload != null)
                    startActivity(new Intent(getActivity(), SettingActivity.class).putExtra("profile_data", profilePayload));
                break;
            case R.id.img_notification:
                startActivity(new Intent(getActivity(), NotificationAndAdminMessages.class));
                break;

        }
    }

//todo ========================== View Pager Adapter =================================

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void add(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}