package com.ujamaaonline.customer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.SlideData;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ViewPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewpager;
    private TabLayout su_SliderDots;
    private LinearLayout layout_account;
    private TextView tvSignIn, tvSignUp, tvSkip;
    private static int currentPage = 0;
    private ImageView tvBack;
    private static int NUM_PAGES = 0;
    private ViewPagerAdapter viewPagerAdapter;
    private boolean fromFilter = false;
    private ArrayList<SlideData> slideDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(View.GONE);
        this.initView();
        this.setOnClickListener();
    }
    private void initView() {

        this.viewpager = this.findViewById(R.id.viewpager);
        this.su_SliderDots = this.findViewById(R.id.sliding_tabs);
        this.layout_account = this.findViewById(R.id.layout_account);
        this.tvSignIn = this.findViewById(R.id.tvSignIn);
        tvBack = findViewById(R.id.iv_back);
        this.tvSignUp = this.findViewById(R.id.tvSignUp);
        this.tvSkip = this.findViewById(R.id.tvSkip);
        slideDataList.clear();
        slideDataList.add(new SlideData("Find Anything", "Find various products, services and content from thousands of Black-owned businesses across the UK."));
        slideDataList.add(new SlideData("Search through Images", "Easily search for products and services using images!"));
        slideDataList.add(new SlideData("Stay Connected", "You’ll have a Feed tab so you can stay up to date with the businesses, find events, receive discounts and more."));
        slideDataList.add(new SlideData("Interact and Explore", "You can let the businesses and other customers know about your experiences by leaving reviews."));
        slideDataList.add(new SlideData("Welcome to UJAMAA Online!", "Create an account now to discover and connect with thousands of Black-owned businesses across the UK."));
        this.setViewPagerWithTimer(slideDataList);
        if (getIntent().hasExtra("fromFilter")) {
            fromFilter = true;
            tvBack.setVisibility(View.VISIBLE);
            tvSkip.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

    private void setOnClickListener() {
        this.tvSignIn.setOnClickListener(this);
        this.tvSignUp.setOnClickListener(this);
        this.tvSkip.setOnClickListener(this);
        this.tvBack.setOnClickListener(this);

    }

    private void setViewPagerWithTimer(ArrayList<SlideData> hotTrendingList) {
        this.viewPagerAdapter = new ViewPagerAdapter(this);
        this.viewpager.setAdapter(this.viewPagerAdapter);
        su_SliderDots.setupWithViewPager(viewpager, true);
        NUM_PAGES = slideDataList.size();
//        // Auto start of viewpager
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable Update = new Runnable() {
            public void run() {
//                if (currentPage == NUM_PAGES) {
//                    currentPage = 0;
//                }
                viewpager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                if (currentPage == 4)
                    layout_account.setVisibility(View.VISIBLE);
                else
                    layout_account.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignUp:
                this.startActivity(new Intent(ViewPagerActivity.this, SignUpActivity.class));
                break;
            case R.id.tvSignIn:
                if (!fromFilter)
                    startActivity(new Intent(ViewPagerActivity.this, LoginActivity.class));
                else
                    startActivityForResult(new Intent(ViewPagerActivity.this, LoginActivity.class).putExtra("fromFilter",fromFilter), 121);
                break;
            case R.id.tvSkip:
                Intent dashboardIntent = new Intent(ViewPagerActivity.this, MainActivity.class);
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dashboardIntent);
                ViewPagerActivity.this.finish();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //TODO ========================== view pager  Adapter===============================
    private class ViewPagerAdapter extends PagerAdapter {

        private final Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ObjectUtil.isEmpty(slideDataList.size()) ? 0 : slideDataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, @NotNull Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_dots_view_pager, container, false);

            TextView tv_title = view.findViewById(R.id.tv_title);
            TextView tv_description = view.findViewById(R.id.tv_description);

            SlideData slideData = slideDataList.get(position);
            tv_title.setText(slideData.getTitle());
            tv_description.setText(slideData.getDescription());

            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }
    }
}