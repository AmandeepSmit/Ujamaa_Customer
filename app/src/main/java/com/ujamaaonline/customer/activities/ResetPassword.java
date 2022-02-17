package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private EditText newPass,confirmPass;
    private TextView save;
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private String headerToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        userId=getIntent().getStringExtra("userId");
        apiInterface= APIClient.getClient();
        progress=new GlobalProgressDialog(ResetPassword.this);
        newPass=findViewById(R.id.rp_newPass);
        confirmPass=findViewById(R.id.rp_confirmPass);
        save=findViewById(R.id.rp_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPass.getText().toString().isEmpty()){
                    Toast.makeText(ResetPassword.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                }else if (confirmPass.getText().toString().isEmpty())
                {
                    Toast.makeText(ResetPassword.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
                }else{
                    changePassword();
                }
            }
        });
    }

    private void changePassword(){
        if(!GlobalUtil.isNetworkAvailable(this)){
            UIUtil.showNetworkDialog(this);
            return;
        }
        progress.showProgressBar();
        com.ujamaaonline.business.models.ChangePasswordRequest request=new com.ujamaaonline.business.models.ChangePasswordRequest(userId,newPass.getText().toString(),confirmPass.getText().toString());
        apiInterface.resetPassword(request).enqueue(new Callback<com.ujamaaonline.business.models.ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<com.ujamaaonline.business.models.ChangePasswordResponse> call, Response<com.ujamaaonline.business.models.ChangePasswordResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()){
                        Toast.makeText(ResetPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ResetPassword.this,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                    }else{
                        Toast.makeText(ResetPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ResetPassword.this,"Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }
            @Override
            public void onFailure(Call<com.ujamaaonline.business.models.ChangePasswordResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(ResetPassword.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}