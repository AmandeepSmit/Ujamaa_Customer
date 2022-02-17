package com.ujamaaonline.customer.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.ViewPagerAdapter;

import java.util.Objects;

public class FeedFragment extends Fragment {

    private TabLayout feedTabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.feedTabLayout = view.findViewById(R.id.feedTabLayout);
        this.viewPager = view.findViewById(R.id.feedViewPager);
        viewPagerSetup();
    }

    private void viewPagerSetup() {
        this.viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 3);
        this.viewPager.setOffscreenPageLimit(3);
        this.viewPagerAdapter.addFragment(new FragmentSubscriberPost(), getString(R.string.subscriberPostTtitle));
        this.viewPagerAdapter.addFragment(new LocalFeedFragment(), getString(R.string.localFeedPost));
        this.viewPagerAdapter.addFragment(new EventPostFragment(), getString(R.string.eventPost));
        this.viewPager.setAdapter(this.viewPagerAdapter);
        this.feedTabLayout.setupWithViewPager(viewPager);
        if (getArguments() != null) {
            if (getArguments().containsKey("first"))
                viewPager.setCurrentItem(2);
            else if (getArguments().containsKey("second"))
                viewPager.setCurrentItem(1);
        }
    }
}
