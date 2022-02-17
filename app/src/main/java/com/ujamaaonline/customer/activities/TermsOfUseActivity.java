package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.terms_of_use.TermsOfUseResponse;
import com.ujamaaonline.customer.models.user_profile.UserProfileResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class TermsOfUseActivity extends AppCompatActivity {
    private TextView tvTitle;
    private WebView webView;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        initView();
        if (getIntent().hasExtra("terms"))
            tvTitle.setText("Terms of Use");
        else tvTitle.setText("Privacy Policy");
    }
    private void initView() {
        this.progress = new GlobalProgressDialog(TermsOfUseActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        tvTitle = findViewById(R.id.tv_title);
        webView = findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        getTermsOfUse();
    }
    public void backBtnCick(View view) {
        this.onBackPressed();
    }
    private void getTermsOfUse() {
        if (!GlobalUtil.isNetworkAvailable(TermsOfUseActivity.this)) {
            UIUtil.showNetworkDialog(TermsOfUseActivity.this);
            return;
        }
            progress.showProgressBar();
        apiInterface.getTermsOfUse().enqueue(new Callback<TermsOfUseResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<TermsOfUseResponse> call, Response<TermsOfUseResponse> response) {
                    progress.hideProgressBar();
                if (response.body().getStatus()) {
                    if (getIntent().hasExtra("terms")) {
                        if (!TextUtils.isEmpty(response.body().getPayload().getcTermsOfUse()))
                            webView.loadUrl(response.body().getPayload().getcTermsOfUse());
                    } else {
                        if (!TextUtils.isEmpty(response.body().getPayload().getcPrivacePolicy()))
                            webView.loadUrl(response.body().getPayload().getcPrivacePolicy());
                    }
                } else {
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<TermsOfUseResponse> call, Throwable t) {

                if (t.getMessage() != null) {
                        progress.hideProgressBar();
                    Toast.makeText(TermsOfUseActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}