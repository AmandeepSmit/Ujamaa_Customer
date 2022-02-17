package com.ujamaaonline.customer.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.ChatListAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.chat_models.ChatUserList;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRecepient extends AppCompatActivity {

    private TextView busName,contactInfo,recName,refNumber,date,jobTitle,workSummery,manValue,vatValue,quoteEstimate,sendTo,save,tvNumber,tvEmail;
    private ImageView delete;
    private String businessId,customerId,mAddress,mPhone,vatInfo,key,customerName,phoneNumber,screenShotPath;
    private Boolean vatRegistered;
    private SessionSecuredPreferences loginPreferences;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private String headerToken;
    private RecyclerView cRecyclerView;
    private ImageView filterBtn;
    private DatabaseReference reference;
    private List<ChatUserList> uList = new ArrayList<>();
    private ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepient);
        initViews(ActivityRecepient.this);
    }

    private void initViews(Context context) {
        this.progress = new GlobalProgressDialog(ActivityRecepient.this);
        this.apiInterface = APIClient.getClient();
        tvNumber=findViewById(R.id.rn_email);
        tvEmail=findViewById(R.id.rn_link);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.customerId = String.valueOf(this.loginPreferences.getInt(Constants.USER_ID, 0));
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        busName = findViewById(R.id.rn_businessName);
        recName = findViewById(R.id.rn_recipientName);
        refNumber = findViewById(R.id.rn_refName);
        date = findViewById(R.id.rn_date);
        jobTitle = findViewById(R.id.rn_jobTitle);
        workSummery = findViewById(R.id.rn_workSummery);
        manValue = findViewById(R.id.rn_manValue);
        vatValue = findViewById(R.id.rn_vat);
        contactInfo=findViewById(R.id.rn_contactInfo);
        quoteEstimate = findViewById(R.id.rn_quoteEstimate);
        tvEmail.setText(TextUtils.isEmpty(getIntent().getStringExtra("businessEmail"))?"":getIntent().getStringExtra("businessEmail"));
        contactInfo.setText(getIntent().getStringExtra("businessAddress"));
        busName.setText(getIntent().getStringExtra("businessName"));
        quoteEstimate.setText(getIntent().getStringExtra("quoteEstimate"));
        recName.setText(getIntent().getStringExtra("recipientName"));
        refNumber.setText( getIntent().getStringExtra("refNumber"));
        jobTitle.setText(getIntent().getStringExtra("jobTitle"));
        quoteEstimate.setText(getIntent().getStringExtra("quoteEstimate"));
        workSummery.setText(getIntent().getStringExtra("workSummery"));
        date.setText(getIntent().getStringExtra("date"));
        tvNumber.setText(TextUtils.isEmpty(getIntent().getStringExtra("phone"))?"":getIntent().getStringExtra("phone"));
        vatValue.setText("VAT " + getIntent().getStringExtra("vatInfo"));
        vatRegistered = getIntent().getBooleanExtra("vatRegistered", false);
        key = getIntent().getStringExtra("key");

        customerId = getIntent().getStringExtra("customerId");
        customerName = getIntent().getStringExtra("customerName");

        findViewById(R.id.rn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        verifystoragepermissions(this);
        findViewById(R.id.rn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(Uri.fromFile(screenshot(getWindow().getDecorView().getRootView(), "UjamaaOnline")))
                        .setAspectRatio(1, 1)
                        .start(ActivityRecepient.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File imgFile = new File(resultUri.getPath());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                createDirectoryAndSaveFile(bitmap,"UjamaaOnline");
                Toast.makeText(ActivityRecepient.this, "Screenshot saved", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        Date date = new Date();
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        File direct = new File(Environment.getExternalStorageDirectory() + "/UjamaaOnline");
        if (!direct.exists()) {
            direct.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory().toString()+"/UjamaaOnline", fileName+format+".jpg");
        File fdelete = new File(screenShotPath);
        if (fdelete.exists()) {
            fdelete.delete();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected  File screenshot(View view, String filename) {
        Date date = new Date();
        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        try {
            // Initialising the directory of storage
            String dirpath = Environment.getExternalStorageDirectory().toString() + "";
            File file = new File(dirpath);
            if (!file.exists()) {
                boolean mkdir = file.mkdir();
            }

            // File name
            String path = dirpath + "/" + filename + "-" + format + ".jpeg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageurl = new File(path);
            screenShotPath=path;
            FileOutputStream outputStream = new FileOutputStream(imageurl);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageurl;


        } catch (FileNotFoundException io) {
            io.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    // verifying if storage permission is given or not
    public static void verifystoragepermissions(Activity activity) {
        int permissions = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
