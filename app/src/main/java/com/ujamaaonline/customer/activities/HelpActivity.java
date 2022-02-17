package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.ujamaaonline.customer.R;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        findViewById(R.id.tv_faq).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_faq:
                startActivity(new Intent(HelpActivity.this, FaqActivity.class));
                break;
        }
    }
    public void backBtnCick(View view) {
        this.onBackPressed();
    }

    public void reportSomthingClick(View view) {
        composeEmail("Report Something","report@ujamaaonline.co.uk");
    }

    public void workingClick(View view) {
        composeEmail("Something Isn't Working","customercare@ujamaaonline.co.uk");
    }

    public void feedbackClick(View view) {

        composeEmail("General Help or Feedback","feedback@ujamaaonline.co.uk");
    }

    public void composeEmail(String subject,String mailto) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+mailto+ "?subject=" + Uri.encode(subject)+ "&body=" + Uri.encode("Please leave the information below so we can better assist you.\nDevice Name:\nAndroid Version:\nApp Version:\nBusiness ID:\nLocalisation:\nInternet Connection Status"))); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT,"");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}