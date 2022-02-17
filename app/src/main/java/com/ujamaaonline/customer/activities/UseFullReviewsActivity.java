package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.fragments.MyReviewsFragment;
import com.ujamaaonline.customer.fragments.ReviewsFragment;

public class UseFullReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_full_reviews);
        if (getIntent().hasExtra("profile")) {
            Bundle bundle = new Bundle();
            bundle.putString("profile", "");
            MyReviewsFragment myReviewsFragment = new MyReviewsFragment();
            myReviewsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.review_container, myReviewsFragment).commit();
        }
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }
}