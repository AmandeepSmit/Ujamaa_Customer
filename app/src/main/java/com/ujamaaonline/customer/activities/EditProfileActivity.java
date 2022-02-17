package com.ujamaaonline.customer.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.user_profile.ProfilePayload;
import com.ujamaaonline.customer.models.user_profile.UpdateProfileRequest;
import com.ujamaaonline.customer.models.user_profile.UserProfileResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ProfilePayload profilePayload = null;
    private EditText etFname, etLname, etEmail, etPostCode, etPhone, etDob, etGander;
    private ImageView imgUser;
    private TextView tvUserId, tvFirstLtr;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private MultipartBody.Part imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();
        if (getIntent() != null) {
            if (getIntent().hasExtra("profile_data")) {
                profilePayload = (ProfilePayload) getIntent().getSerializableExtra("profile_data");
                if (profilePayload != null) {
                    if (!TextUtils.isEmpty(profilePayload.image)) {
                        Picasso.get().load(profilePayload.image).error(R.drawable.ic_user_two).placeholder(R.drawable.ic_user_two).into(imgUser);
                    } else {
                        tvFirstLtr.setVisibility(View.VISIBLE);
                        tvFirstLtr.setText(profilePayload.firstname.substring(0, 1).toUpperCase());
                    }
                    etFname.setText(profilePayload.firstname);
                    etLname.setText(profilePayload.lastname);
                    etEmail.setText(profilePayload.email);
                    etPostCode.setText(profilePayload.postcode);
                    etDob.setText(profilePayload.dob);
                    etPhone.setText(profilePayload.mobile);
                    etGander.setText(profilePayload.gender);
                    tvUserId.setText("" + profilePayload.id);

                }
            }
        }
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(EditProfileActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        imgUser = findViewById(R.id.img_user);
        etFname = findViewById(R.id.et_fist_name);
        etGander = findViewById(R.id.et_gender);
        etLname = findViewById(R.id.et_fname);
        etEmail = findViewById(R.id.et_email);
        tvFirstLtr = findViewById(R.id.tv_name);
        etPostCode = findViewById(R.id.et_postal_code);
        etDob = findViewById(R.id.et_dob);
        tvUserId = findViewById(R.id.tv_userId);
        tvUserId.setOnClickListener(this);
        etPhone = findViewById(R.id.et_phone);
        findViewById(R.id.tv_edit).setOnClickListener(this);
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }

    private void openImagePicker() {
        Options options = Options.init()
                .setRequestCode(101)
                .setCount(1)
                .setExcludeVideos(true)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
        Pix.start(EditProfileActivity.this, options);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                openImagePicker();
                break;
            case R.id.tv_userId:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", tvUserId.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "copied your User ID!'", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateUserProfile() {
        if (!GlobalUtil.isNetworkAvailable(EditProfileActivity.this)) {
            UIUtil.showNetworkDialog(EditProfileActivity.this);
            return;
        }
        progress.showProgressBar();
        apiInterface.updateCustomerProfile(
                BEARER.concat(this.headerToken),
                RequestBody.create(etFname.getText().toString(), MediaType.parse("text/plain")), RequestBody.create(etLname.getText().toString(), MediaType.parse("text/plain")), RequestBody.create(etPhone.getText().toString(), MediaType.parse("text/plain")), RequestBody.create(etPostCode.getText().toString(), MediaType.parse("text/plain")), RequestBody.create(etGander.getText().toString(), MediaType.parse("text/plain")), imgFile).enqueue(new Callback<BusinessListResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<BusinessListResponse> call, Response<BusinessListResponse> response) {
                progress.hideProgressBar();
                if (response.body().getStatus()) {
                    Toast.makeText(EditProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("load_profile_api"));
                    finish();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<BusinessListResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(EditProfileActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File imgFile = new File(resultUri.getPath());
                RequestBody requestFile =
                        RequestBody.create(imgFile, MediaType.parse("multipart/form-data"));
                this.imgFile = MultipartBody.Part.createFormData("image", imgFile.getName(), requestFile);
//                profileUpdate = true;l .0
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(EditProfileActivity.this).getContentResolver(), resultUri);
                    Glide.with(EditProfileActivity.this).load(bmp).into(imgUser);
                    tvFirstLtr.setVisibility(View.GONE);
//                    mediaFile =s persistImage(bmp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (data != null && resultCode == RESULT_OK && requestCode == 101) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            assert resultArray != null;
            for (int i = 0; i < resultArray.size(); i++) {
                String path = resultArray.get(i);
                File file = new File(path);
                Uri imageUri = Uri.fromFile(new File(file.getAbsolutePath()));

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(EditProfileActivity.this);
            }
        }
    }

    public void saveBtnClick(View view) {
        if (!TextUtils.isEmpty(etFname.getText().toString().trim()) && !TextUtils.isEmpty(etLname.getText().toString().trim()) && !TextUtils.isEmpty(etPostCode.getText().toString().trim()) && !TextUtils.isEmpty(etGander.getText().toString().trim()))
            updateUserProfile();
        else Toast.makeText(this, "all fields are required", Toast.LENGTH_SHORT).show();
    }
}