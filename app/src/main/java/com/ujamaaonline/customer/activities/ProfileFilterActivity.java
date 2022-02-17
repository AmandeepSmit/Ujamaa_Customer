package com.ujamaaonline.customer.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.fragments.YourReviewsFragment;
import com.ujamaaonline.customer.models.customer_own_reviews.CustomerAllReviewe;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileFilterActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recFilter;
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private RecyclerView recCat;
    private TextView tvDiscountDeals, tvGeneralInfo, tvImportantUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_profile);
        if (getIntent().hasExtra("filter_cat")) {
            filterCatList = (List<FilterCatPayload>) getIntent().getSerializableExtra("filter_cat");
        }
        if (getIntent().hasExtra("from_feed")) {
            findViewById(R.id.laout_post_type).setVisibility(View.VISIBLE);
        }
        initView();
    }

    private void initView() {
        recFilter = findViewById(R.id.rec_profile_filter);
        recFilter.setHasFixedSize(true);
        recFilter.setAdapter(new FilterAdapter());
        tvDiscountDeals = findViewById(R.id.tv_discount_deals);
        tvDiscountDeals.setOnClickListener(this);
        tvGeneralInfo = findViewById(R.id.tv_general_info);
        tvGeneralInfo.setOnClickListener(this);
        tvImportantUpdates = findViewById(R.id.tv_important_updates);
        tvImportantUpdates.setOnClickListener(this);
    }

    public void resetBtnClick(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("reset_cat", "");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void seletedMultipletab(TextView newTab, int position) {
        if (((ColorDrawable) newTab.getBackground()).getColor() == getResources().getColor(R.color.white)) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            if (position != -1)
                filterCatList.get(position).setChecked(true);
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.dark_gray));
            if (position != -1)
                filterCatList.get(position).setChecked(false);
        }
    }

    public void applyBtnClick(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("filter_cat", (Serializable) filterCatList);
        if (getIntent().hasExtra("from_feed"))
        {
            resultIntent.putExtra("discount_deals", ((ColorDrawable) tvDiscountDeals.getBackground()).getColor()==getResources().getColor(R.color.black)?1:0);
            resultIntent.putExtra("general_info", ((ColorDrawable) tvGeneralInfo.getBackground()).getColor()==getResources().getColor(R.color.black)?1:0);
            resultIntent.putExtra("important_updates", ((ColorDrawable) tvImportantUpdates.getBackground()).getColor()==getResources().getColor(R.color.black)?1:0);

        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_discount_deals:
                seletedMultipletab(tvDiscountDeals, -1);
                break;
            case R.id.tv_general_info:
                seletedMultipletab(tvGeneralInfo, -1);
                break;
            case R.id.tv_important_updates:
                seletedMultipletab(tvImportantUpdates, -1);
                break;
        }
    }

    //todo =======================  Reviews Filter Adapter ===========================

    class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

        @NonNull
        @Override
        public FilterAdapter.FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FilterAdapter.FilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile_filter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FilterAdapter.FilterViewHolder holder, int position) {
            FilterCatPayload filterCatPayload = filterCatList.get(position);
            holder.tvCatName.setText(TextUtils.isEmpty(filterCatPayload.getName()) ? "" : filterCatPayload.getName());
            if (filterCatList.get(position).isChecked()) {
                seletedMultipletab(holder.tvCatName, position);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seletedMultipletab(holder.tvCatName, position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return filterCatList.size();
        }

        class FilterViewHolder extends RecyclerView.ViewHolder {

            TextView tvCatName;

            public FilterViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_cat_name);
            }
        }
    }

    private void loadReportimgDialog(String url, ImageView img) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.ic_palceholder)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        img.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

}
