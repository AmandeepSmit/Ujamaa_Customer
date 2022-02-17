package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ujamaaonline.customer.R;

public class ActivityFilter extends AppCompatActivity {
    private Switch switchFilter;
    private boolean isEdited = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initView();
    }

    private void initView() {
        switchFilter = findViewById(R.id.switch_filter);
        if (getIntent().hasExtra("filter")) {
            if (getIntent().getStringExtra("filter").equalsIgnoreCase("1")) {
                switchFilter.setChecked(true);
            }
        }
        switchFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isEdited = true;
            }
        });
    }

    public void applyBtnClick(View view) {
            Intent intent = new Intent();
            if (switchFilter.isChecked())
                intent.putExtra("isChecked", switchFilter.isChecked());
            setResult(RESULT_OK, intent);
            finish();
    }

    public void resetBtnClick(View view) {
        Intent intent = new Intent();
        if (switchFilter.isChecked())
            intent.putExtra("isChecked", false);
        setResult(RESULT_OK, intent);
        finish();
    }
}