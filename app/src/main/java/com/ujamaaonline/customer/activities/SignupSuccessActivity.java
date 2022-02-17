package com.ujamaaonline.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.utils.ObjectUtil;

public class SignupSuccessActivity extends AppCompatActivity implements View.OnClickListener {

    private String firstName, registerMessage, userImage;
    private TextView tv_fName, tv_message, tv_Ok;
    private ImageView iv_profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_success);
        if (ObjectUtil.isNonEmptyStr(this.getIntent().getStringExtra("firstName")) && ObjectUtil.isNonEmptyStr(this.getIntent().getStringExtra("message"))
                && ObjectUtil.isNonEmptyStr(this.getIntent().getStringExtra("userImage"))) {
            this.firstName = this.getIntent().getStringExtra("firstName");
            this.registerMessage = this.getIntent().getStringExtra("message");
            this.userImage = this.getIntent().getStringExtra("userImage");
        }
        this.initView();
    }

    private void initView() {
        this.tv_fName = this.findViewById(R.id.tv_fName);
        this.tv_message = this.findViewById(R.id.tv_message);
        this.tv_Ok = this.findViewById(R.id.tv_Ok);
        this.iv_profile_pic = this.findViewById(R.id.iv_profile_pic);
        this.tv_Ok.setOnClickListener(this);
        if (ObjectUtil.isNonEmptyStr(firstName))
            tv_fName.setText(firstName.concat("!"));
        if (ObjectUtil.isNonEmptyStr(registerMessage))
            tv_message.setText(registerMessage);
        if (ObjectUtil.isNonEmptyStr(userImage))
            loadImages(this.userImage, this.iv_profile_pic);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_Ok) {
            this.startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            SignupSuccessActivity.this.finish();
        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Glide.with(this)
                .load(imagePath)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }
}