package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

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

public class ForgotPassword extends AppCompatActivity {

    private EditText enterEmail;
    private TextView Next;
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private String headerToken;
    private SessionSecuredPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        apiInterface= APIClient.getClient();
        progress=new GlobalProgressDialog(ForgotPassword.this);
        enterEmail=findViewById(R.id.fp_currentPass);
        Next=findViewById(R.id.fp_next);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (enterEmail.getText().toString().isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }else if (!GlobalUtil.isValidEmail(enterEmail.getText().toString())) {
                    Toast.makeText(ForgotPassword.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                }else{
                    forgotPassword();
                }
            }
        });
    }

    private void forgotPassword(){
        if (!GlobalUtil.isNetworkAvailable(this)) {
            UIUtil.showNetworkDialog(this);
            return;
        }
        progress.showProgressBar();
        com.ujamaaonline.business.models.ForgotPasswordRequest request=new com.ujamaaonline.business.models.ForgotPasswordRequest(enterEmail.getText().toString());
        apiInterface.forgotPassword(request).enqueue(new Callback<com.ujamaaonline.business.models.ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<com.ujamaaonline.business.models.ForgotPasswordResponse> call, Response<com.ujamaaonline.business.models.ForgotPasswordResponse> response) {
                if (response.isSuccessful()){
                    if(response.body().getStatus()){
                        Toast.makeText(ForgotPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPassword.this, VerifyOtp.class)
                        .putExtra("userId",String.valueOf(response.body().getPayload())));
                        finish();
                    }else{
                        Toast.makeText(ForgotPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ForgotPassword.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }
            @Override
            public void onFailure(Call<com.ujamaaonline.business.models.ForgotPasswordResponse> call, Throwable t) {
               progress.hideProgressBar();
                Toast.makeText(ForgotPassword.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}