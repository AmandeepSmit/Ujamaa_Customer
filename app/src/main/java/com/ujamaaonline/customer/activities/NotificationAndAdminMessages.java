package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.ViewPagerAdapter;
import com.ujamaaonline.customer.fragments.AdminMessagesFragment;
import com.ujamaaonline.customer.fragments.EventPostFragment;
import com.ujamaaonline.customer.fragments.FragmentSubscriberPost;
import com.ujamaaonline.customer.fragments.LocalFeedFragment;
import com.ujamaaonline.customer.fragments.NotificationFragment;

public class NotificationAndAdminMessages extends AppCompatActivity {

    private TabLayout feedTabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_and_admin_messasge);
        this.feedTabLayout = findViewById(R.id.na_TabLayout);
        this.viewPager = findViewById(R.id.na_ViewPager);
        viewPagerSetup();

        findViewById(R.id.nam_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationAndAdminMessages.super.onBackPressed();
            }
        });
    }

    private void viewPagerSetup() {
        this.viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 2);
        this.viewPager.setOffscreenPageLimit(2);
        this.viewPagerAdapter.addFragment(new NotificationFragment(),"Notifications");
        this.viewPagerAdapter.addFragment(new AdminMessagesFragment(), "Admin Messages");
        this.viewPager.setAdapter(this.viewPagerAdapter);
        this.feedTabLayout.setupWithViewPager(viewPager);
    }
}