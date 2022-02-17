package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.share_earn_models.GetCreditResponse;
import com.ujamaaonline.customer.models.share_earn_models.ReferralCodeResponse;
//import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareAndEarnPointActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView credits, inviteBusiness, inviteApp,msgText;
    private String code, referralCode;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_and_earn_point);
        initView(ShareAndEarnPointActivity.this);
    }

    private void initView(Context context) {
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = loginPreferences.getString(Constants.HEADER_TOKEN,"");
        credits = findViewById(R.id.se_credits);
        inviteBusiness = findViewById(R.id.se_inviteBusiness);
        inviteBusiness.setOnClickListener(this);
        inviteApp=findViewById(R.id.se_shareApp);
        inviteApp.setOnClickListener(this);
        msgText=findViewById(R.id.se_msgText);
        getReferralCode();
        getCredits();
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.se_shareApp:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ujamaa Customer");
                String shareMessage= "\nInstall Ujamaa Customer app using this link and use referral code to earn points\n"+ "CODE: "+referralCode+"\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps";
                //shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;

            case R.id.se_inviteBusiness:
                Intent shareIntent1 = new Intent(Intent.ACTION_SEND);
                shareIntent1.setType("text/plain");
                shareIntent1.putExtra(Intent.EXTRA_SUBJECT, "Ujamaa Business");
                String shareMessage1= "\nInstall Ujamaa Business app using this link and use referral code to earn points\n"+ "CODE: "+referralCode+"\n";
                shareMessage1 = shareMessage1 + "https://play.google.com/store/apps";
                //shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent1.putExtra(Intent.EXTRA_TEXT, shareMessage1);
                startActivity(Intent.createChooser(shareIntent1, "choose one"));
                break;
        }
    }


    private void getReferralCode() {
        if (!GlobalUtil.isNetworkAvailable(ShareAndEarnPointActivity.this)) {
            UIUtil.showNetworkDialog(ShareAndEarnPointActivity.this);
            return;
        }
        progress.showProgressBar();
        apiInterface.getReferralCode(Constants.BEARER.concat(this.headerToken)).enqueue(new Callback<ReferralCodeResponse>() {
            @Override
            public void onResponse(Call<ReferralCodeResponse> call, Response<ReferralCodeResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        code = response.body().getPayload();
                        code="<font color='#65A3FF'>"+code+"</font>";
                        msgText.setText(Html.fromHtml("Invite Black-owned businesses to the UJAMAA Online community and earn points! You both earn points when they sign up and use your referral code." + code));
                        referralCode=response.body().getPayload();
                    } else {
                    }
                } else {
                }
                progress.hideProgressBar();
            }
            @Override
            public void onFailure(Call<ReferralCodeResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(ShareAndEarnPointActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getCredits() {
        if (!GlobalUtil.isNetworkAvailable(ShareAndEarnPointActivity.this)) {
            UIUtil.showNetworkDialog(ShareAndEarnPointActivity.this);
            return;
        }
        progress.showProgressBar();
        apiInterface.getCredits(Constants.BEARER.concat(this.headerToken)).enqueue(new Callback<GetCreditResponse>() {
            @Override
            public void onResponse(Call<GetCreditResponse> call, Response<GetCreditResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        credits.setText(String.valueOf(response.body().getPayload()));
                    } else {
                    }
                } else {
                }
                progress.hideProgressBar();
            }
            @Override
            public void onFailure(Call<GetCreditResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(ShareAndEarnPointActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
