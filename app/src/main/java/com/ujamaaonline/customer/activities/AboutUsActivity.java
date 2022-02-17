package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.faq_model.FaqResponse;
import com.ujamaaonline.customer.models.faq_model.GetAboutUsResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvAboutUs;
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private String websiteLink, instagramLink, facebookLink, twitterLink;
    private LinearLayout layoutWebsite, layoutFacebook, layoutInstagram, layoutTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }

    private void initView() {
        tvAboutUs = findViewById(R.id.tv_about_us);
        this.progress = new GlobalProgressDialog(AboutUsActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        layoutWebsite = findViewById(R.id.layout_website);
        layoutWebsite.setOnClickListener(this);
        layoutFacebook = findViewById(R.id.layout_facebook);
        layoutFacebook.setOnClickListener(this);
        layoutInstagram = findViewById(R.id.layout_insta);
        layoutInstagram.setOnClickListener(this);
        layoutTwitter = findViewById(R.id.layout_twitter);
        layoutTwitter.setOnClickListener(this);
        getAboutUs();
    }

    private void getAboutUs() {
        if (!GlobalUtil.isNetworkAvailable(AboutUsActivity.this)) {
            UIUtil.showNetworkDialog(AboutUsActivity.this);
            return;
        }
        progress.showProgressBar();
        apiInterface.getAboutUs().enqueue(new Callback<GetAboutUsResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GetAboutUsResponse> call, Response<GetAboutUsResponse> response) {
                progress.hideProgressBar();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        tvAboutUs.setText(Html.fromHtml(response.body().getPayload().getDescription()));
                    }
                    if (!TextUtils.isEmpty(response.body().getPayload().getWebsite())) {
                        layoutWebsite.setVisibility(View.VISIBLE);
                        websiteLink = response.body().getPayload().getWebsite();
                    }
                    if (!TextUtils.isEmpty(response.body().getPayload().getInstagrm())) {
                        layoutInstagram.setVisibility(View.VISIBLE);
                        instagramLink = response.body().getPayload().getInstagrm();
                    }
                    if (!TextUtils.isEmpty(response.body().getPayload().getFacebook())) {
                        layoutFacebook.setVisibility(View.VISIBLE);
                        facebookLink = response.body().getPayload().getFacebook();
                    }
                    if (!TextUtils.isEmpty(response.body().getPayload().getTwitter())) {
                        layoutTwitter.setVisibility(View.VISIBLE);
                        twitterLink = response.body().getPayload().getTwitter();
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GetAboutUsResponse> call, Throwable t) {
                progress.hideProgressBar();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_website:
                if (!TextUtils.isEmpty(websiteLink))
                    openLink(websiteLink);
                break;
            case R.id.layout_insta:
                if (!TextUtils.isEmpty(instagramLink))
                    openLink(instagramLink);
                break;
            case R.id.layout_facebook:
                if (!TextUtils.isEmpty(facebookLink))
                    openLink(facebookLink);
                break;
            case R.id.layout_twitter:
                if (!TextUtils.isEmpty(twitterLink))
                    openLink(twitterLink);
                break;
        }
    }

    private void openLink(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }
}