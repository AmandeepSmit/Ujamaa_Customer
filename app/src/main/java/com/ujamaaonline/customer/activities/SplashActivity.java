package com.ujamaaonline.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;

import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class SplashActivity extends AppCompatActivity {

    private SessionSecuredPreferences loginPreferences;
    private boolean isLoggedIn;
    private ImageView ivTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.initView();

    }

    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.isLoggedIn = this.loginPreferences.getBoolean(IS_LOGGED_IN, false);
        ivTitle=findViewById(R.id.iv_title);
        slideUp(ivTitle);

        if (this.isLoggedIn) {
            this.openMainActivity();
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginIntent = new Intent(SplashActivity.this, ViewPagerActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    SplashActivity.this.finish();
                }
            }, 2000);
        }
    }
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                1000,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(800);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


    private void openMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent dashboardIntent = new Intent(SplashActivity.this, MainActivity.class);
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dashboardIntent);
                SplashActivity.this.finish();
            }
        }, 3000);
    }

}
