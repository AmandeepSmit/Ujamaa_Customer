package com.ujamaaonline.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.login.EOLoginResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;
import static com.ujamaaonline.customer.utils.Constants.USER_IMAGE;
import static com.ujamaaonline.customer.utils.Constants.USER_NAME;
import static com.ujamaaonline.customer.utils.Constants.USER_POST_CODE;
import static com.ujamaaonline.customer.utils.Constants.USER_ROLE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private ImageView iv_back;
    private EditText et_email, et_password;
    private TextView tvSignIn, tvForgetPassword;
    private boolean isFilter = false;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getIntent().hasExtra("fromFilter")) {
            isFilter = true;
        }
        this.initView();
        this.setOnClickListener();
        generateFirebaseToken();
    }
    private void initView() {
        deviceId= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.iv_back = this.findViewById(R.id.iv_back);
        this.et_email = this.findViewById(R.id.et_email);
        this.et_password = this.findViewById(R.id.et_password);
        this.tvSignIn = this.findViewById(R.id.tvSignIn);
        this.tvForgetPassword = this.findViewById(R.id.tvForgetPassword);
    }

    private void setOnClickListener() {
        this.iv_back.setOnClickListener(this);
        this.tvSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                LoginActivity.this.finish();
                break;
            case R.id.tvSignIn:
                if (isValidLoginForm()) {
                    if (!GlobalUtil.isNetworkAvailable(LoginActivity.this)) {
                        UIUtil.showNetworkDialog(LoginActivity.this);
                        return;
                    }
                    this.loginCustomer();
                }
                break;
        }
    }

    private void loginCustomer() {
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setEmail(ObjectUtil.getTextFromView(et_email));
        loginRequest.setPassword(ObjectUtil.getTextFromView(et_password));
        loginRequest.setFirebase_token(fcmToken);
        loginRequest.setDeviceId(deviceId);
        loginRequest.setDeviceType("A");
        if (!ObjectUtil.isEmpty(loginRequest)) {
            progress.showProgressBar();
            apiInterface.loginUser(loginRequest).enqueue(new Callback<EOLoginResponse>() {

                @EverythingIsNonNull
                @Override
                public void onResponse(Call<EOLoginResponse> call, Response<EOLoginResponse> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOLoginResponse loginResponse = response.body();
                        if (!ObjectUtil.isEmpty(loginResponse)) {
                            if (loginResponse.isStatus() == RESPONSE_SUCCESS) {
                                saveToken(response.body().getPayload().getId());
                                Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                loginPreferences.edit().putString(HEADER_TOKEN, loginResponse.getPayload().getToken()).apply();
                                loginPreferences.edit().putBoolean(IS_LOGGED_IN, true).apply();
                                loginPreferences.edit().putString(USER_ROLE, loginResponse.getPayload().getRole()).apply();
                                loginPreferences.edit().putInt(USER_ID, loginResponse.getPayload().getId()).apply();
                                loginPreferences.edit().putString(USER_NAME, loginResponse.getPayload().getUserName()).apply();
                                loginPreferences.edit().putString(USER_IMAGE, loginResponse.getPayload().getImage()).apply();
                                loginPreferences.edit().putString(USER_IMAGE, loginResponse.getPayload().getImage()).apply();
                                loginPreferences.edit().putString(USER_POST_CODE, loginResponse.getPayload().getUser_data().getPostcode()).apply();
                                if (isFilter) {
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    LoginActivity.this.finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<EOLoginResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(LoginActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private String fcmToken;

    private void saveToken(Integer payload) {
        DatabaseReference c_reference;
        c_reference = FirebaseDatabase.getInstance().getReference("firebase_token").child(String.valueOf(payload));
        HashMap<String, String> data2 = new HashMap<>();
        data2.put("token", fcmToken);
        data2.put("type", "A");
//        startActivity(new Intent(Login.this, MainActivity.class));
//        finish();
        c_reference.setValue(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Token not saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firebase Token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        fcmToken = task.getResult();

                    }
                });
    }

    private boolean isValidLoginForm() {
        String errorMsg = null;
        String emailId = ObjectUtil.getTextFromView(et_email);
        String password = ObjectUtil.getTextFromView(et_password);

        if (ObjectUtil.isEmptyStr(emailId)) {
            errorMsg = this.getString(R.string.please_enter_email_id);
        } else if (ObjectUtil.isEmptyStr(password)) {
            errorMsg = this.getString(R.string.please_enter_password);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        }
        /* else if (password.length() < 8) {
            errorMsg = this.getString(R.string.password_length);
        } else if (!GlobalUtil.validatePassword(PASSWORD_PATTERN, password)) {
            errorMsg = this.getString(R.string.password_should_be_special);
        }*/

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            //new GlobalAlertDialog(LoginActivity.this, false, true, false).show(errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void forgetPasswordClick(View view) {
        startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
    }
}