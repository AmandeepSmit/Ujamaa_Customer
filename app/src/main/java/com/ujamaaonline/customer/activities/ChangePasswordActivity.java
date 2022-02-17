package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etCurrentPwd, etNewPassword;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private String email="";
    private TextView etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }
    private void initView() {
        this.progress = new GlobalProgressDialog(ChangePasswordActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        etEmail=findViewById(R.id.cp_email);
        etCurrentPwd = findViewById(R.id.cp_currentPass);
        email=getIntent().getStringExtra("email");
        etEmail.setText("New password for "+email);
        etNewPassword = findViewById(R.id.cp_newPass);
    }

    public void saveBtnClick(View view) {
        if (isValidate()) {
            changePassword();
        }
    }

    private boolean isValidate() {
        if (TextUtils.isEmpty(etCurrentPwd.getText().toString().trim())) {
            Toast.makeText(this, "please enter current password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(etNewPassword.getText().toString().trim())) {
            Toast.makeText(this, "please enter new password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (etNewPassword.getText().toString().length()<6) {
            Toast.makeText(this, "password must be 6 digit or characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changePassword() {
        if (!GlobalUtil.isNetworkAvailable(ChangePasswordActivity.this)) {
            UIUtil.showNetworkDialog(ChangePasswordActivity.this);
            return;
        }
        progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setOldPassword(etCurrentPwd.getText().toString());
        loginRequest.setNewPassword(etNewPassword.getText().toString());
        apiInterface.changePassword(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<BusinessListResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<BusinessListResponse> call, Response<BusinessListResponse> response) {
                progress.hideProgressBar();
                if (response.body().getStatus()) {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<BusinessListResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ChangePasswordActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }
}