package com.ujamaaonline.customer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ProfileFilterActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.components.TimeAgo2;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.customer_own_reviews.CustomerAllReviewe;
import com.ujamaaonline.customer.models.customer_own_reviews.CustomerOwnReviews;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.get_business_reviews.AllReviewResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;


public class YourReviewsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private RecyclerView recReviews;
    private String headerToken;
    private TextView imageReviews;
    private TextView tvEmptyData, tvPostedCount, tvUseFullCount, tvFilter,tvSortBy;
    private List<CustomerAllReviewe> reviewList = new ArrayList<>();
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatListTemp = new ArrayList<>();
    private List<Integer> selectedCatList = new ArrayList<>();
    private LinearLayout layoutSort;
    private String sort="0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_your_reviews, container, false);
        initView();
        clickListeners();
        return view;
    }

    private void clickListeners() {


    }
    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        tvEmptyData = view.findViewById(R.id.tv_empty_data);
        recReviews = view.findViewById(R.id.rec_reviews);
        recReviews.setHasFixedSize(true);
        imageReviews=view.findViewById(R.id.tv_img_reviews);
        tvFilter = view.findViewById(R.id.tv_filter);
        tvFilter.setOnClickListener(this);
        tvUseFullCount = view.findViewById(R.id.usefull_count);
        tvPostedCount = view.findViewById(R.id.posted_count);
        recReviews.setAdapter(new ReviewsAdapter());
        tvSortBy=view.findViewById(R.id.tv_sort_by);
        layoutSort=view.findViewById(R.id.layout_sort);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        layoutSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.dialog_sort_reviews, null);
                mBottomSheetDialog.setContentView(sheetView);
                sheetView.findViewById(R.id.layout_most_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "0";
                        tvSortBy.setText("Most Recent");
                        getAllReviews();
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_hightest_rating).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "1";
                        tvSortBy.setText("Highest Rating");
                        getAllReviews();
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_most_useful).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "2";
                        tvSortBy.setText("Most Useful");
                        getAllReviews();
                        mBottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.tv_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });

                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                mBottomSheetDialog.show();
            }
        });
        getAllReviews();
        getFilterCat();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter:
                startActivityForResult(new Intent(getActivity(), ProfileFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList), 202);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode== Activity.RESULT_OK )
       {
           if (data!=null && data.hasExtra("filter_cat")) {
               filterCatList.clear();
               selectedCatList.clear();
               filterCatList = (List<FilterCatPayload>) data.getSerializableExtra("filter_cat");
               if (filterCatList.size() > 0) {
                   for (FilterCatPayload filterCatPayload : filterCatList) {
                       if (filterCatPayload.isChecked()) {
                           selectedCatList.add(filterCatPayload.getId());
                       }
                   }
                   getAllReviews();
               }
           }
           else if (data.hasExtra("reset_cat"))
           {
               filterCatList.clear();
               selectedCatList.clear();
               filterCatList.addAll(filterCatListTemp);
               getAllReviews();
           }
       }
    }

    private void getAllReviews() {
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setShorting(Integer.valueOf(sort));
        loginRequest.setCat_ids(selectedCatList);
        if (!ObjectUtil.isEmpty(loginRequest)) {
            progress.showProgressBar();
            apiInterface.getCustomerReviews(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<CustomerOwnReviews>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<CustomerOwnReviews> call, Response<CustomerOwnReviews> response) {
                    progress.hideProgressBar();
                    reviewList.clear();
                    recReviews.setVisibility(View.GONE);
                    tvEmptyData.setVisibility(View.VISIBLE);
                    if (!ObjectUtil.isEmpty(response.body())) {
                        CustomerOwnReviews allReviewResponse = response.body();
                        tvFilter.setText("Filter");

                        if(response.body().getFilter_count()!=null)
                        {
                            if (response.body().getFilter_count()>0)
                            {
                                tvFilter.setText("Filter("+response.body().getFilter_count()+")");
                            }
                        }
                        if (!ObjectUtil.isEmpty(allReviewResponse)) {
                            if (allReviewResponse.getStatus() == RESPONSE_SUCCESS) {
                                if (!ObjectUtil.isEmpty(response.body())) {
                                    tvPostedCount.setText(String.valueOf(allReviewResponse.getPayload().getTotalPostedCount()));
                                    imageReviews.setText(String.valueOf(allReviewResponse.getPayload().getReviewWithImgCount()));
                                    String usefulCount=String.valueOf(allReviewResponse.getPayload().getReviewWithImgCount());
                                    String count="<font color='#063AF1'>"+usefulCount+"</font>";
                                    tvUseFullCount.setText(Html.fromHtml("Combining all of your reviews "+(response.body().getPayload().getUsefullPeopleCount()==null?0:response.body().getPayload().getUsefullPeopleCount())+" people have found them useful."));
                                    if (allReviewResponse.getPayload().getAllReviewes().size() > 0) {
                                        recReviews.setVisibility(View.VISIBLE);
                                        tvEmptyData.setVisibility(View.GONE);
                                        reviewList.addAll(allReviewResponse.getPayload().getAllReviewes());
                                        recReviews.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            } else {
//                                     Toast.makeText(getActivity(), allReviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<CustomerOwnReviews> call, Throwable t) {
                    reviewList.clear();
                    recReviews.setVisibility(View.GONE);
                    tvEmptyData.setVisibility(View.VISIBLE);
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                    }
                }
            });
        }
    }

    private void getFilterCat() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        apiInterface.getFilterCat().enqueue(new Callback<FilterCatResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<FilterCatResponse> call, Response<FilterCatResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0) {
                            filterCatList.addAll(response.body().getPayload());
                            filterCatListTemp.addAll(response.body().getPayload());
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<FilterCatResponse> call, Throwable t) {
//
            }
        });
    }


    //todo ======================= Reviews Adapter ===========================

    class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
        private TimeAgo2 timeAgo2 = new TimeAgo2();
        @NonNull
        @Override
        public ReviewsAdapter.ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReviewsAdapter.ReviewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_your_reviews, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewsViewHolder holder, int position) {
            CustomerAllReviewe reviewe = reviewList.get(position);
            holder.businessName.setText(TextUtils.isEmpty(reviewe.getBusinessName()) ? "" : reviewe.getBusinessName());
            holder.businessSummeryLine.setText(TextUtils.isEmpty(reviewe.getSummeryLine()) ? "" : reviewe.getSummeryLine());
            holder.userName.setText(TextUtils.isEmpty(reviewe.getCustomerName()) ? "" : reviewe.getCustomerName());
            holder.userDesc.setText(TextUtils.isEmpty(reviewe.getComment()) ? "" : reviewe.getComment());
            holder.userRating.setRating(Float.valueOf(reviewe.getRateing()));
            holder.tvTimeAgo.setText(timeAgo2.covertTimeToText(reviewe.getDate()));
            holder.tvUseFullCount.setText(reviewe.getUsefullCount() + " people found this review useful");
            if (!TextUtils.isEmpty(reviewe.getBusinessLogo())) {
                loadReportimgDialog(reviewe.getBusinessLogo(), holder.imgBusiness);
            }
            if (!TextUtils.isEmpty(reviewe.getCustomerProfile())) {
                loadReportimgDialog(reviewe.getCustomerProfile(), holder.imgUser);
                holder.tvUserNameChar.setVisibility(View.GONE);
            } else {
                holder.tvUserNameChar.setText(reviewe.getCustomerName().substring(0, 1), TextView.BufferType.EDITABLE);
                holder.tvUserNameChar.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public int getItemCount() {
            return reviewList.size();
        }

        class ReviewsViewHolder extends RecyclerView.ViewHolder {
            ImageView imgUser, imgBusiness;
            TextView businessName, businessSummeryLine, userName, userDesc, tvUserNameChar, tvUseFullCount,tvTimeAgo;
            RatingBar userRating;

            public ReviewsViewHolder(@NonNull View itemView) {
                super(itemView);
                imgUser = itemView.findViewById(R.id.img_user);
                imgBusiness = itemView.findViewById(R.id.user_img);
                businessName = itemView.findViewById(R.id.tv_user_name);
                businessSummeryLine = itemView.findViewById(R.id.business_summery_line);
                userName = itemView.findViewById(R.id.tv_customer_name);
                userDesc = itemView.findViewById(R.id.customer_desc);
                tvTimeAgo=itemView.findViewById(R.id.tv_time_ago);
                tvUserNameChar = itemView.findViewById(R.id.tv_name);
                userRating = itemView.findViewById(R.id.user_rating);
                tvUseFullCount = itemView.findViewById(R.id.tv_useful);

            }
        }
    }

    private void loadReportimgDialog(String url, ImageView img) {
        Glide.with(getActivity())
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