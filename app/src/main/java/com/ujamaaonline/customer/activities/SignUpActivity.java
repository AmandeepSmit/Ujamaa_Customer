package com.ujamaaonline.customer.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.models.signup.EORegisterResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.StringUtil;
import com.ujamaaonline.customer.utils.UIUtil;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.REQUEST_CODE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;
import static com.ujamaaonline.customer.utils.Constants.USER_IMAGE;
import static com.ujamaaonline.customer.utils.Constants.USER_NAME;
import static com.ujamaaonline.customer.utils.Constants.USER_POST_CODE;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private TextView tv_cancel, tv_done;
    private EditText et_first_name, et_last_name, et_email_address, et_password, et_postal_code, et_age, et_gender, et_phone_number,et_referral_code;
    private ImageView iv_profile_pic;
    private ImageView iv_question_mark;
    private CardView cv_click_pic;
    private String gender;
    private String otherGender;
    private MultipartBody.Part userImagePart;
    private String dateOfBirth;
    private String selectedGender = "";
    private ConstraintLayout tooltipLayout;
    private boolean toolTipShowing = false;
    private LinearLayout toolTipRootView;
    private String fcmToken,deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        deviceId= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.tv_cancel = this.findViewById(R.id.tv_cancel);
        this.tv_done = this.findViewById(R.id.tv_done);
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email_address = this.findViewById(R.id.et_email_address);
        this.et_password = this.findViewById(R.id.et_password);
        this.et_postal_code = this.findViewById(R.id.et_postal_code);
        this.et_age = this.findViewById(R.id.et_age);
        this.et_gender = this.findViewById(R.id.et_gender);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.cv_click_pic = this.findViewById(R.id.cv_click_pic);
        this.iv_profile_pic = this.findViewById(R.id.iv_profile_pic);
        this.iv_question_mark = this.findViewById(R.id.iv_question_mark);
        this.et_referral_code=this.findViewById(R.id.et_referralCode);
        tooltipLayout = findViewById(R.id.tooltip_layout);
        toolTipRootView =findViewById(R.id.tool_tip_root);
        generateFirebaseToken();
    }

    private void setOnClickListener() {
        this.tv_cancel.setOnClickListener(this);
        this.tv_done.setOnClickListener(this);
        this.et_gender.setOnClickListener(this);
        this.et_age.setOnClickListener(this);
        this.cv_click_pic.setOnClickListener(this);
        this.iv_profile_pic.setOnClickListener(this);
        this.iv_question_mark.setOnClickListener(this);
        tooltipLayout.setOnClickListener(this);
        toolTipRootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                SignUpActivity.this.finish();
                break;
            case R.id.et_gender:
                this.showGenderDialog();
                break;
            case R.id.et_age:
                this.selectDOBFromCalender();
                break;
            case R.id.tv_done:
                if (isValidSignUpForm()) {
                    if (!GlobalUtil.isNetworkAvailable(SignUpActivity.this)) {
                        UIUtil.showNetworkDialog(SignUpActivity.this);
                        return;
                    }
                    if(!TextUtils.isEmpty(et_postal_code.getText().toString().trim()))
                        getLatLong(et_postal_code.getText().toString());
                    else
                        Toast.makeText(this, "please enter postal code", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cv_click_pic:
            case R.id.iv_profile_pic:
                this.openGalleryAndCamera();
                break;
            case R.id.iv_question_mark:
            case R.id.tooltip_layout:
            case R.id.tool_tip_root:
                if (toolTipShowing) {
                    toolTipShowing = false;
                    tooltipLayout.setVisibility(View.GONE);
                    toolTipRootView.setVisibility(View.GONE);
                } else {
                    toolTipShowing = true;
                    toolTipRootView.setVisibility(View.VISIBLE);
                    tooltipLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void getLatLong(String postCode) {
        if (!GlobalUtil.isNetworkAvailable(this)) {
            UIUtil.showNetworkDialog(this);
            return;
        }
            progress.showProgressBar();
            apiInterface.getLatLng("https://postcodes.io/postcodes/" + postCode).enqueue(new Callback<GetLatLngResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetLatLngResponse> call, Response<GetLatLngResponse> response) {
                    if (progress.isShowing())
                        progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        GetLatLngResponse editCatResponse = response.body();
                        if (!ObjectUtil.isEmpty(editCatResponse)) {
                            if (editCatResponse.getStatus() == 200) {
                                if ((editCatResponse.getResult().getLatitude() != null) && (editCatResponse.getResult().getLongitude() != null)) {
                                    if (TextUtils.isEmpty(fcmToken))
                                    {

                                        return;
                                    }
                                    registerCustomer(String.valueOf(editCatResponse.getResult().getLatitude()),String.valueOf(editCatResponse.getResult().getLongitude()));
                                }
                            } else {
                                if (!TextUtils.isEmpty(editCatResponse.getError()))
                                    Toast.makeText(SignUpActivity.this, editCatResponse.getError(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (!ObjectUtil.isEmpty(response.errorBody())) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String errorMessage = jsonObject.getString("error");
                            if (!TextUtils.isEmpty(errorMessage)) {
                                et_postal_code.setError(errorMessage);
                                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetLatLngResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        if (progress.isShowing())
                            progress.hideProgressBar();
                        Toast.makeText(SignUpActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void registerCustomer(String lat, String lng) {

            RequestBody fName = RequestBody.create(ObjectUtil.getTextFromView(this.et_first_name), MediaType.parse("text/plain"));
            RequestBody lName = RequestBody.create(ObjectUtil.getTextFromView(this.et_last_name), MediaType.parse("text/plain"));
            RequestBody email = RequestBody.create(ObjectUtil.getTextFromView(this.et_email_address), MediaType.parse("text/plain"));
            RequestBody password = RequestBody.create(ObjectUtil.getTextFromView(this.et_password), MediaType.parse("text/plain"));
            RequestBody postcode = RequestBody.create(ObjectUtil.getTextFromView(this.et_postal_code), MediaType.parse("text/plain"));
            RequestBody age = RequestBody.create(this.dateOfBirth, MediaType.parse("text/plain"));
            RequestBody gender = RequestBody.create(ObjectUtil.getTextFromView(this.et_gender), MediaType.parse("text/plain"));
            RequestBody phone = RequestBody.create(ObjectUtil.getTextFromView(this.et_phone_number), MediaType.parse("text/plain"));
            RequestBody device_id = RequestBody.create(this.deviceId, MediaType.parse("text/plain"));
            RequestBody firbase_Token = RequestBody.create(TextUtils.isEmpty(fcmToken)?"":fcmToken, MediaType.parse("text/plain"));
            RequestBody device_type = RequestBody.create("A", MediaType.parse("text/plain"));
            RequestBody referral_code = RequestBody.create(et_referral_code.getText().toString(), MediaType.parse("text/plain"));
        RequestBody custlat = RequestBody.create(lat, MediaType.parse("text/plain"));
        RequestBody custlng = RequestBody.create(lng, MediaType.parse("text/plain"));

            if(!progress.isShowing());
            progress.showProgressBar();
            apiInterface.registerCustomer(fName, lName, email, password, postcode, age, userImagePart, phone, gender,device_id,firbase_Token,device_type,custlat,custlng,referral_code).enqueue(new Callback<EORegisterResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<EORegisterResponse> call, Response<EORegisterResponse> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EORegisterResponse registerResponse = response.body();
                        if (!ObjectUtil.isEmpty(registerResponse)) {
                            if (registerResponse.isStatus() == RESPONSE_SUCCESS) {
                                saveToken(response.body().getPayload().getId());
                                Toast.makeText(SignUpActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                loginPreferences.edit().putString(HEADER_TOKEN, registerResponse.getPayload().getToken()).apply();
                                loginPreferences.edit().putBoolean(IS_LOGGED_IN, true).apply();
                                loginPreferences.edit().putInt(USER_ID, registerResponse.getPayload().getId()).apply();
                                loginPreferences.edit().putString(USER_NAME ,registerResponse.getPayload().getUsername()).apply();
                                loginPreferences.edit().putString(USER_IMAGE,registerResponse.getPayload().getImage()).apply();
                                loginPreferences.edit().putString(USER_IMAGE,registerResponse.getPayload().getImage()).apply();
                                loginPreferences.edit().putString(USER_POST_CODE, et_postal_code.getText().toString()).apply();
                                //TODO from here show register dialog and click ok move to main activity
                                if (ObjectUtil.isNonEmptyStr(registerResponse.getPayload().getUsername()) && ObjectUtil.isNonEmptyStr(registerResponse.getMessage())) {
                                    openSuccessDialog(registerResponse);
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<EORegisterResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(SignUpActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

//    private void showRegisterDialog(String firstName, String registerMessage) {
//        LayoutInflater li = LayoutInflater.from(this);
//        View dialogView = li.inflate(R.layout.dialog_signup, null);
//        AlertDialog sDialog = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create();
//        TextView tv_fName = dialogView.findViewById(R.id.tv_fName);
//        TextView tv_message = dialogView.findViewById(R.id.tv_message);
//        TextView tv_Ok = dialogView.findViewById(R.id.tv_Ok);
//        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
//        sDialog.show();
//
//        tv_fName.setText(firstName.concat("!"));
//        tv_message.setText(registerMessage);
//
//        tv_Ok.setOnClickListener(v -> {
//            sDialog.dismiss();
//            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
//            SignUpActivity.this.finish();
//        });
//    }

    private void openGalleryAndCamera() {
        Options options = Options.init()
                .setRequestCode(REQUEST_CODE)
                .setCount(1)
                .setFrontfacing(false)
                .setExcludeVideos(true)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
        Pix.start(SignUpActivity.this, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!ObjectUtil.isEmpty(data) && resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            assert resultArray != null;
            String path = resultArray.get(0);
            File file = new File(path);
            Uri imageUri = Uri.fromFile(new File(file.getAbsolutePath()));
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(), imageUri);
                //TODO for image rotation issue in android 10
                ExifInterface ei = new ExifInterface(path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bmp, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bmp, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bmp, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bmp;
                }
                File userImage = convertFileFromBitmap(rotatedBitmap);
                RequestBody reqFile = RequestBody.create(userImage, MediaType.parse("image/*"));
                userImagePart = MultipartBody.Part.createFormData("image", userImage.getName(), reqFile);
                this.iv_profile_pic.setImageBitmap(rotatedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private File convertFileFromBitmap(Bitmap bitmap) {
        String milliSeconds = String.valueOf(System.currentTimeMillis());
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, StringUtil.getStringForID(R.string.app_name) + milliSeconds + ".jpg");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    public void selectDOBFromCalender() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dayofmonth) {
                String nd = "" + dayofmonth;
                String nm;
                if (dayofmonth < 10) {
                    nd = "0" + dayofmonth;
                }
                if ((monthofyear + 1) < 10) {
                    nm = "0" + (monthofyear + 1);
                } else {
                    nm = "" + (monthofyear + 1);
                }
                dateOfBirth = String.valueOf(year).concat("-").concat(nm).concat("-").concat(nd);
                String totalAge = getAgeFromDOB(year, monthofyear, dayofmonth);
                et_age.setText(totalAge);
            }
        }, 1990, 00, 01);
//        c.set(1990,00,01);
//        dpd.getDatePicker().setMaxDate(c.getTime().getTime());
        dpd.show();
    }

    private String getAgeFromDOB(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        int ageInt = age;
        return String.valueOf(ageInt);
    }

    private boolean isOtherGender = false;
    public void showGenderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_gender);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        EditText et_other_gender = dialog.findViewById(R.id.et_other_gender);
        TextView tv_Done = dialog.findViewById(R.id.tv_Done);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            switch (checkedRadioButton.getId()) {
                case R.id.radioMale:
                    gender = "Male";
                    et_other_gender.setEnabled(false);
                    et_other_gender.setText("");
                    isOtherGender = false;
                    return;
                case R.id.radioFemale:
                    gender = "Female";
                    et_other_gender.setEnabled(false);
                    et_other_gender.setText("");
                    isOtherGender = false;
                    return;
                case R.id.radioOther:
                    if (checkedRadioButton.isChecked()) {
                        et_other_gender.setEnabled(true);
                        gender = "Other";
                        isOtherGender = true;
                    }
            }
        });

        tv_Done.setOnClickListener(v -> {
            if (ObjectUtil.isEmptyStr(gender)) {
                Toast.makeText(SignUpActivity.this, "Please select gender", Toast.LENGTH_SHORT).show();
            }
//            else if (ObjectUtil.isEmptyStr(ObjectUtil.getTextFromView(et_other_gender)) && isOtherGender) {
//                Toast.makeText(SignUpActivity.this, "Please enter your gender", Toast.LENGTH_SHORT).show();
//            }
            else {
                dialog.dismiss();
                otherGender = ObjectUtil.getTextFromView(et_other_gender);

                et_gender.setText(gender.equals("Other") ? TextUtils.isEmpty(otherGender)?gender:otherGender : gender);

                gender = null;
            }
        });

        dialog.show();
    }

    private void openSuccessDialog(EORegisterResponse registerResponse) {
        Dialog dialog = new Dialog(this, R.style.ScaleFromCenter);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_signup_success);
        ImageView ivUser = dialog.findViewById(R.id.iv_profile_pic);
        TextView tvUserName = dialog.findViewById(R.id.tv_fName);
        TextView tvNameChar=dialog.findViewById(R.id.tv_name_char);

        TextView message = dialog.findViewById(R.id.tv_message);
        if (ObjectUtil.isNonEmptyStr(registerResponse.getPayload().getUsername()))
            tvUserName.setText(registerResponse.getPayload().getUsername().concat("!"));
        if (ObjectUtil.isNonEmptyStr(registerResponse.getMessage()))
            message.setText(registerResponse.getMessage());
        if (ObjectUtil.isNonEmptyStr(registerResponse.getPayload().getImage()))
            loadImages(registerResponse.getPayload().getImage(), ivUser);
        else {
            tvNameChar.setVisibility(View.VISIBLE);
            tvNameChar.setText(String.valueOf(registerResponse.getPayload().getUsername().toString().charAt(0)));
        }

        dialog.findViewById(R.id.tv_Ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void loadImages(String imagePath, ImageView imageView) {
        Glide.with(this)
                .load(imagePath)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }

    private boolean isValidSignUpForm() {
        String errorMsg = null;
        String firstName = ObjectUtil.getTextFromView(et_first_name);
        String lastName = ObjectUtil.getTextFromView(et_last_name);
        String emailId = ObjectUtil.getTextFromView(et_email_address);
        String password = ObjectUtil.getTextFromView(et_password);
        String postCode = ObjectUtil.getTextFromView(et_postal_code);
        String age = ObjectUtil.getTextFromView(et_age);
        String gender = ObjectUtil.getTextFromView(et_gender);
        String phoneNumber = ObjectUtil.getTextFromView(et_phone_number);

        if (ObjectUtil.isEmptyStr(firstName)) {
            errorMsg = this.getString(R.string.please_enter_first_name);
        } else if (ObjectUtil.isEmptyStr(lastName)) {
            errorMsg = this.getString(R.string.please_enter_last_name);
        } else if (ObjectUtil.isEmptyStr(emailId)) {
            errorMsg = this.getString(R.string.please_enter_email_id);
        } else if (ObjectUtil.isEmptyStr(password)) {
            errorMsg = this.getString(R.string.please_enter_password);
        } else if (ObjectUtil.isEmptyStr(postCode)) {
            errorMsg = this.getString(R.string.please_enter_postcode);
        } else if (ObjectUtil.isEmptyStr(age)) {
            errorMsg = this.getString(R.string.select_date_of_birth);
        } else if (ObjectUtil.isEmptyStr(gender)) {
            errorMsg = this.getString(R.string.please_select_gender);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        }
//        else if (ObjectUtil.isEmpty(userImagePart)) {
//            errorMsg = this.getString(R.string.please_choose_image);
//        }
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
            public void onSuccess(Void aVoid){
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
                            Toast.makeText(SignUpActivity.this,"exception  "+task.getException(),Toast.LENGTH_LONG).show();
                            generateFirebaseToken();
                            return;
                        }
                        // Get new FCM registration token
                        fcmToken = task.getResult();

                    }
                });
    }

    public void termsofuse(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ujamaaonline.co.uk/legal"));
        startActivity(browserIntent);
    }
}
