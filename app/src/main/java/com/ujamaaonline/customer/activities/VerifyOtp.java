package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtp extends AppCompatActivity {

    private EditText enterOtp;
    private TextView Next;
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private String headerToken;
    private SessionSecuredPreferences loginPreferences;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        apiInterface= APIClient.getClient();
        progress=new GlobalProgressDialog(VerifyOtp.this);
        enterOtp=findViewById(R.id.vo_enterOtp);
        Next=findViewById(R.id.vo_next);
        userId=getIntent().getStringExtra("userId");

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (enterOtp.getText().toString().isEmpty()){
                    Toast.makeText(VerifyOtp.this, "Please enter otp", Toast.LENGTH_SHORT).show();
                }else{
                    verifyOtp();
                }
            }
        });
    }

    private void verifyOtp(){
        if (!GlobalUtil.isNetworkAvailable(this)) {
            UIUtil.showNetworkDialog(this);
            return;
        }
        progress.showProgressBar();
        com.ujamaaonline.business.models.VerifyOtpRequest request=new com.ujamaaonline.business.models.VerifyOtpRequest(userId,enterOtp.getText().toString());
        apiInterface.matchOtp(request).enqueue(new Callback<com.ujamaaonline.business.models.ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<com.ujamaaonline.business.models.ForgotPasswordResponse> call, Response<com.ujamaaonline.business.models.ForgotPasswordResponse> response) {
                if (response.isSuccessful()){
                    if(response.body().getStatus()){
                        Toast.makeText(VerifyOtp.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VerifyOtp.this,ResetPassword.class).
                                putExtra("userId",userId));
                    }else{
                        Toast.makeText(VerifyOtp.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(VerifyOtp.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }
            @Override
            public void onFailure(Call<com.ujamaaonline.business.models.ForgotPasswordResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(VerifyOtp.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}