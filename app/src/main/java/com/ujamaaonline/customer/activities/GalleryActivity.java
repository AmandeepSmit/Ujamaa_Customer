package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.fragments.SearchGalleryFragment;

public class GalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container,new SearchGalleryFragment("profile")).commit();
    }
    public void backBtnCick(View view) {
        onBackPressed();
    }
}