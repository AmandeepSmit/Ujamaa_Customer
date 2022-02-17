package com.ujamaaonline.customer.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityFilter;
import com.ujamaaonline.customer.activities.NearMeActivity;
import com.ujamaaonline.customer.activities.ProfileFilterActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.components.TimeAgo2;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.get_business_reviews.AllReviewResponse;
import com.ujamaaonline.customer.models.get_business_reviews.ReviewImage;
import com.ujamaaonline.customer.models.get_business_reviews.ReviewsPayload;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.signup.EORegisterResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.StringUtil;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ujamaaonline.customer.utils.UIUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static android.app.Activity.RESULT_OK;
import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;

public class MyReviewsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView recReviews;
    private TextView addReview, tvEmptyData, tvFilter, tvSortBy, tvRating, tvReviewsCount;
    private RatingImgAdapter ratingImgAdapter;
    private List<MultipartBody.Part> selectedImgList = new ArrayList();
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private String businessId = "";
    private LinearLayout imgLayout;
    private List<ReviewsPayload> reviewsList = new ArrayList<>();
    private ReviewsAdapter reviewsAdapter;
    private static BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    private LinearLayout layoutSort;
    private String sort = "0";
    private String filter = "0";
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatListTemp = new ArrayList<>();
    private List<Integer> selectedCatList = new ArrayList<>();
    private boolean fromProfile = false, isLogin = false;
    private RatingBar ratingBar;
    private Integer userid = 0;

    private DecimalFormat precision = new DecimalFormat("0.0");

    public MyReviewsFragment(String businessId) {
        this.businessId = businessId;
    }

    public MyReviewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_my_reviews, container, false);
        this.initView();
        return this.view;
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rating = intent.getStringExtra("rating");
            int reviewscount = intent.getIntExtra("review_count", 0);
            if (!TextUtils.isEmpty(rating)) {
                tvRating.setText(String.valueOf(precision.format(Float.parseFloat(rating))));
                tvReviewsCount.setText("Based on " + reviewscount + " reviews");
            } else tvRating.setText("0.0");
            if (rating != null)
                ratingBar.setRating(Float.parseFloat(rating));
        }
    };
    BroadcastReceiver receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAllReviews(filter, sort);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(receiver1);
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
            }
        });
    }

    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        ratingBar = view.findViewById(R.id.star_ratingbar);
        recReviews = view.findViewById(R.id.rec_reviews);
        recReviews.setHasFixedSize(true);
        isLogin = this.loginPreferences.getBoolean(IS_LOGGED_IN, false);
        tvReviewsCount = view.findViewById(R.id.tv_reviews_count);
        userid = this.loginPreferences.getInt(USER_ID, 0);
        tvEmptyData = view.findViewById(R.id.tv_empty_data);
        tvFilter = view.findViewById(R.id.tv_filter);
        addReview = view.findViewById(R.id.fmy_reviewBtn);
        tvFilter.setOnClickListener(this);
        layoutSort = view.findViewById(R.id.layout_sort);
        tvSortBy = view.findViewById(R.id.tv_sort_by);
        tvRating = view.findViewById(R.id.tv_rating);
        getActivity().registerReceiver(receiver, new IntentFilter("user_rating"));
        getActivity().registerReceiver(receiver1, new IntentFilter("update_reviews"));
        bottomSheet = view.findViewById(R.id.review_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        reviewsAdapter = new ReviewsAdapter();
        recReviews.setAdapter(reviewsAdapter);
        if (getArguments() != null && getArguments().containsKey("profile")) {
            view.findViewById(R.id.layout_rating).setVisibility(View.GONE);
            addReview.setVisibility(View.GONE);
            fromProfile = true;
            getFilterCat();
        }
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
                        getAllReviews("0", "0");
                        tvSortBy.setText("Most Recent");
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_hightest_rating).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "1";
                        tvSortBy.setText("Highest Rating");
                        getAllReviews("0", "1");
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_most_useful).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort = "2";
                        getAllReviews("0", "2");
                        tvSortBy.setText("Most Useful");
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
        ratingImgAdapter = new RatingImgAdapter(imgList);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        setOnClickListener();
        getAllReviews("0", "0");
    }

    private void getAllReviews(String filter, String sort) {

        progress.showProgressBar();
        Call<AllReviewResponse> call = null;
        if (!fromProfile) {
            EOLoginRequest loginRequest = new EOLoginRequest();
            loginRequest.setBusiness_id(businessId);
            if (isLogin)
            loginRequest.setCustomer_id(userid);
            call = apiInterface.getAllReviews( filter, sort, loginRequest);
        } else {
            EOLoginRequest loginRequest = new EOLoginRequest();
            loginRequest.setShorting(Integer.valueOf(sort));
            loginRequest.setCat_ids(selectedCatList);
            call = apiInterface.getUseFullReviews(BEARER.concat(this.headerToken), loginRequest);
        }
        call.enqueue(new Callback<AllReviewResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                progress.hideProgressBar();
                reviewsList.clear();
                tvFilter.setText("Filter");

                tvFilter.setText("Filter");

                if(response.body().getFilter_count()!=null)
                {
                    if (response.body().getFilter_count()>0)
                    {
                        tvFilter.setText("Filter("+response.body().getFilter_count()+")");
                    }
                }
                recReviews.setVisibility(View.GONE);
                tvEmptyData.setVisibility(View.VISIBLE);
                if (!ObjectUtil.isEmpty(response.body())) {
                    AllReviewResponse allReviewResponse = response.body();
                    if (!ObjectUtil.isEmpty(allReviewResponse)) {
                        if (allReviewResponse.getStatus() == RESPONSE_SUCCESS) {
                            if (!ObjectUtil.isEmpty(response.body())) {
                                if (allReviewResponse.getPayload().size() > 0) {
                                    recReviews.setVisibility(View.VISIBLE);
                                    tvEmptyData.setVisibility(View.GONE);
                                    reviewsList.addAll(allReviewResponse.getPayload());
                                    reviewsAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            //     Toast.makeText(getActivity(), allReviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AllReviewResponse> call, Throwable t) {
                reviewsList.clear();
                recReviews.setVisibility(View.GONE);
                tvEmptyData.setVisibility(View.VISIBLE);
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
//                    Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void reportReiviews(String reviewId, String comment, AlertDialog sDialog,boolean isReport) {
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setReview_id(reviewId);
        if (isReport)
        loginRequest.setReport(comment);
        if (!ObjectUtil.isEmpty(loginRequest)) {
            progress.showProgressBar();
            Call<AllReviewResponse> call=null;
            if (isReport)
            call=apiInterface.reportReviews(BEARER.concat(this.headerToken), loginRequest);
            else
                call=apiInterface.deleteReview(BEARER.concat(this.headerToken), loginRequest);

            call .enqueue(new Callback<AllReviewResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        AllReviewResponse allReviewResponse = response.body();
                        if (!ObjectUtil.isEmpty(allReviewResponse)) {
                            if (allReviewResponse.getStatus() == RESPONSE_SUCCESS) {
                                if (!ObjectUtil.isEmpty(response.body())) {
//                                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                    if(isReport) {
                                        sDialog.dismiss();
                                        submitedReviewDialog();
                                    }
                                    else
                                    {
                                        getAllReviews(filter, sort);
                                    }
                                }

                            } else {
//                                Toast.makeText(getActivity(), allReviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<AllReviewResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
//                        Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void usefulApi(String reviewId, LinearLayout layoutUsefull) {
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setReview_id(reviewId);
        loginRequest.setCustomer_id(userid);
        if (!ObjectUtil.isEmpty(loginRequest)) {
            progress.showProgressBar();
            apiInterface.usefull(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<AllReviewResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        AllReviewResponse allReviewResponse = response.body();
                        if (!ObjectUtil.isEmpty(allReviewResponse)) {
                            if (allReviewResponse.getStatus() == RESPONSE_SUCCESS) {
                                if (!ObjectUtil.isEmpty(response.body())) {
//                                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                    layoutUsefull.setBackgroundColor(getResources().getColor(R.color.yellow));
                                    getAllReviews(filter, sort);
                                }

                            } else {
//                                Toast.makeText(getActivity(), allReviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<AllReviewResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
//                        Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showReportDialog(ReviewsPayload reviewsPayload) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.diolog_report_review, null);
        AlertDialog sDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setCancelable(false).create();
        TextView tv_back = dialogView.findViewById(R.id.tv_btn_back);
        TextView tvReport = dialogView.findViewById(R.id.tv_btn_report);
        ImageView userImg = dialogView.findViewById(R.id.user_img);
        TextView tvName = dialogView.findViewById(R.id.tv_user_name);
        RatingBar userRating = dialogView.findViewById(R.id.user_rating);
        TextView tvReadMOre = dialogView.findViewById(R.id.tv_read_more);
        TextView tvDesc = dialogView.findViewById(R.id.tv_description);
        EditText etReason = dialogView.findViewById(R.id.et_reason);
        tvReadMOre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvDesc.getLineCount() > 2) {
                    tvReadMOre.setText("read more");
                    tvDesc.setMaxLines(2);
                } else {
                    tvReadMOre.setText("read less");
                    tvDesc.setMaxLines(Integer.MAX_VALUE);
                }
            }
        });

        tvDesc.post(new Runnable() {
            @Override
            public void run() {
                if (tvDesc.getLineCount() > 2) {
                    tvDesc.setMaxLines(2);
                    tvReadMOre.setVisibility(View.VISIBLE);
                } else {
                    tvReadMOre.setVisibility(View.GONE);
                }
            }
        });
        tvName.setText(reviewsPayload.getCustomerName());
        userRating.setRating(Float.parseFloat(reviewsPayload.getRateing()));
        tvDesc.setText(reviewsPayload.getComment());
        if (TextUtils.isEmpty(reviewsPayload.getCustomerProfile())) {
            loadReportimgDialog(reviewsPayload.getCustomerProfile(), userImg);
        }
        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        sDialog.show();

        tv_back.setOnClickListener(v -> sDialog.dismiss());
        tvReport.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etReason.getText().toString().trim())) {
//                Toast.makeText(getActivity(), "please enter reason", Toast.LENGTH_SHORT).show();
                return;
            }
            reportReiviews(String.valueOf(reviewsPayload.getId()), etReason.getText().toString(), sDialog,true);
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fmy_reviewBtn:
                showDialog();
                break;
            case R.id.tv_filter:
                if (!fromProfile)
                    startActivityForResult(new Intent(getActivity(), ActivityFilter.class).putExtra("filter", filter), 555);
                else
                    startActivityForResult(new Intent(getActivity(), ProfileFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList), 202);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ObjectUtil.isEmpty(data) && resultCode == Activity.RESULT_OK && requestCode == 101) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            assert resultArray != null;
            String path = resultArray.get(0);
            File file = new File(path);
            Uri imageUri = Uri.fromFile(new File(file.getAbsolutePath()));
            imgList.add(imageUri);
            ratingImgAdapter.notifyDataSetChanged();
            selectedImgList.add(convertMultipart(path, imageUri, "images[]"));
            if (imgLayout != null && selectedImgList.size() > 0) {
                imgLayout.setVisibility(View.VISIBLE);
            }
//            sendImage(convertMultipart(path, imageUri, "image"));
        } else if (resultCode == RESULT_OK && requestCode == 555) {
            boolean isChecked = data.getBooleanExtra("isChecked", false);
            if (isChecked) {
                filter = "1";
                getAllReviews(filter, sort);
            } else {
                filter = "0";
                getAllReviews(filter, sort);
            }
        } else if (resultCode == RESULT_OK && requestCode == 202) {
            if (data != null && data.hasExtra("filter_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList = (List<FilterCatPayload>) data.getSerializableExtra("filter_cat");
                if (filterCatList.size() > 0) {
                    for (FilterCatPayload filterCatPayload : filterCatList) {
                        if (filterCatPayload.isChecked()) {
                            selectedCatList.add(filterCatPayload.getId());
                        }
                    }
                    getAllReviews(filter, sort);
                }
            } else if (data.hasExtra("reset_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList.addAll(filterCatListTemp);
                getAllReviews(filter, sort);
            }

        }
    }

    List<Uri> imgList = new ArrayList<>();

    public void submitedReviewDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.dialog_compete_reviews, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllReviews(filter, sort);
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();

//        Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.setContentView(R.layout.dialog_compete_reviews);
//        TextView img = dialog.findViewById(R.id.btnBack);
//        img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    public void submitedReportDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.dialog_submit_report, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }

    public void showDialog() {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_rating);
        TextView tvBusinessName = dialog.findViewById(R.id.tv_business_name);
        tvBusinessName.setText(InformationFragment.businessName);
        RecyclerView recImg = dialog.findViewById(R.id.rec_imgs);
        LinearLayout img = dialog.findViewById(R.id.load_img_layout);
        RatingBar ratingBar = dialog.findViewById(R.id.rv_rating);
        EditText etComment = dialog.findViewById(R.id.et_comment);
        imgLayout = dialog.findViewById(R.id.img_layout);
        recImg.setHasFixedSize(true);
        recImg.setAdapter(ratingImgAdapter);
        img.setOnClickListener(dialogClicks);
        dialog.findViewById(R.id.btn_post_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(getActivity(), "Please select rating ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etComment.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Please enter comment", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if (selectedImgList.size() == 0) {
//                    Toast.makeText(getActivity(), "Please add at least one image", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                sendReviews(String.valueOf(ratingBar.getRating()), etComment.getText().toString(), dialog);
            }
        });
        dialog.findViewById(R.id.img_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgList.clear();
                selectedImgList.clear();
                dialog.dismiss();
            }
        });

//        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//        text.setText(msg);
//        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        dialog.show();
    }
    private void sendReviews(String rating, String comment, Dialog dialog) {
        RequestBody ratingbody = RequestBody.create(rating, MediaType.parse("text/plain"));
        RequestBody commentbody = RequestBody.create(comment, MediaType.parse("text/plain"));
        RequestBody businessIdbody = RequestBody.create(businessId, MediaType.parse("text/plain"));
        progress.showProgressBar();
        apiInterface.customerRating(BEARER.concat(this.headerToken), businessIdbody, ratingbody, commentbody, selectedImgList).enqueue(new Callback<EORegisterResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<EORegisterResponse> call, Response<EORegisterResponse> response) {
                progress.hideProgressBar();
//                Toast.makeText(getActivity(), "onresponse", Toast.LENGTH_SHORT).show();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EORegisterResponse registerResponse = response.body();
                    if (!ObjectUtil.isEmpty(registerResponse)) {
                        if (registerResponse.isStatus() == RESPONSE_SUCCESS) {
//                            Toast.makeText(getActivity(), registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            imgList.clear();
                            submitedReviewDialog();
                        } else {
//                            Toast.makeText(getActivity(), registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<EORegisterResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "on Error", Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
//                    Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private View.OnClickListener dialogClicks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.load_img_layout:
                    Options options = Options.init()
                            .setRequestCode(101)
                            .setCount(1)
                            .setFrontfacing(false)
                            .setExcludeVideos(true)
                            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                    Pix.start(getActivity(), options);
                    break;
            }
        }
    };

    private MultipartBody.Part convertMultipart(String path, Uri imageUri, String key) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
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
//                ivUserImage.setImageBitmap(rotatedBitmap);
            File userImage = convertFileFromBitmap(rotatedBitmap);

//            if (!ObjectUtil.isEmpty(userImage))
//                imageFile = userImage;

            RequestBody reqFile = RequestBody.create(userImage, MediaType.parse("image/*"));
            MultipartBody.Part logoPartBody = MultipartBody.Part.createFormData(key, userImage.getName(), reqFile);
            return logoPartBody;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private File convertFileFromBitmap(Bitmap bitmap) {
        String milliSeconds = String.valueOf(System.currentTimeMillis());
        File filesDir = getActivity().getFilesDir();
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

    private void setOnClickListener() {
        addReview.setOnClickListener(this);
    }

    //todo ======================= Rating images Adapter ===========================

    class RatingImgAdapter extends RecyclerView.Adapter<RatingImgAdapter.RatingImgViewHolder> {

        private List<Uri> imgList;

        public RatingImgAdapter(List<Uri> imgList) {
            this.imgList = imgList;
        }

        @NonNull
        @Override
        public RatingImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RatingImgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rating_img, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RatingImgViewHolder holder, int position) {
            holder.imageView.setImageURI(imgList.get(position));
        }

        @Override
        public int getItemCount() {
            return imgList.size();
        }

        class RatingImgViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public RatingImgViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.img_rating);
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

    //todo ======================= Reviews Adapter ===========================

    class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
        private boolean isLoaded = false;
        TimeAgo2 timeAgo2 = new TimeAgo2();

        @NonNull
        @Override
        public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReviewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review_profile, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
            ReviewsPayload reviewsPayload = reviewsList.get(position);
            holder.tvUserName.setText(reviewsPayload.getCustomerName());
            holder.userRating.setRating(Float.parseFloat(reviewsPayload.getRateing()));
            if (!TextUtils.isEmpty(reviewsPayload.getCustomerProfile())) {
                loadImage(reviewsPayload.getCustomerProfile(), holder.imgUser);
            }
            holder.tvTimeAgo.setText(timeAgo2.covertTimeToText(reviewsPayload.getDate()));
            if (reviewsPayload.getMarked_as_useful() == 1) {
                holder.layoutUseFul.setBackgroundColor(getResources().getColor(R.color.yellow));
            } else {
                holder.layoutUseFul.setBackgroundColor(getResources().getColor(R.color.color_gray_light));
            }
            holder.tvUseFul.setText("Useful " + reviewsPayload.getUsefull_count());
            if (fromProfile) {
                holder.layoutBusinessInfo.setVisibility(View.VISIBLE);
                holder.tvBusinessName.setText(TextUtils.isEmpty(reviewsPayload.getBusiness_name()) ? "" : reviewsPayload.getBusiness_name());
                holder.tvsummeryLine.setText(TextUtils.isEmpty(reviewsPayload.getSummery_line()) ? "" : reviewsPayload.getSummery_line());
                if (!TextUtils.isEmpty(reviewsPayload.getBusiness_logo()))
                    loadImage(reviewsPayload.getBusiness_logo(), holder.imgBusinessLogo);
            } else {
                holder.layoutBusinessInfo.setVisibility(View.GONE);
            }

            holder.dottedBtnImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
                    mBottomSheetDialog.setContentView(sheetView);
                    if (reviewsPayload.getCustomerId().equals(userid))
                        sheetView.findViewById(R.id.layout_delete_reviews).setVisibility(View.VISIBLE);
                    else
                        sheetView.findViewById(R.id.layout_delete_reviews).setVisibility(View.GONE);

                    sheetView.findViewById(R.id.layout_report).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showReportDialog(reviewsPayload);
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    sheetView.findViewById(R.id.layout_delete_reviews).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reportReiviews(String.valueOf(reviewsPayload.getId()),null,null ,false);
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

            holder.tvDesc.setText(TextUtils.isEmpty(reviewsPayload.getComment()) ? "" : reviewsPayload.getComment());
            if (!isLoaded) {
                holder.tvDesc.post(new Runnable() {
                    @Override
                    public void run() {
                        int lineCount = holder.tvDesc.getLineCount();
                        if (lineCount > 2) {
                            holder.tvDesc.setMaxLines(2);
                            holder.tvReadMore.setVisibility(View.VISIBLE);
                        } else {
                            holder.tvReadMore.setVisibility(View.GONE);
                        }
                        holder.tvReadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (holder.tvDesc.getLineCount() > 2) {
                                    holder.tvReadMore.setText("read more");
                                    holder.tvDesc.setMaxLines(2);
                                } else {
                                    holder.tvReadMore.setText("read less");
                                    holder.tvDesc.setMaxLines(Integer.MAX_VALUE);
                                }
                            }
                        });
                    }
                });
                if (position == reviewsList.size() - 1) {
                    isLoaded = true;
                }
            }
            holder.recGallery.setHasFixedSize(true);
            holder.recGallery.setAdapter(new RatingImgAdpater(reviewsPayload.getReviewImages()));

            holder.layoutUseFul.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLogin) {
                        usefulApi(String.valueOf(reviewsPayload.getId()), holder.layoutUseFul);
                    } else {
//                        Toast.makeText(getActivity(), "please login first", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void loadImage(String url, ImageView img) {
            Glide.with(getActivity())
                    .asBitmap()
                    .load(url)
                    .placeholder(R.drawable.ic_palceholder)
                    .override(500, 500)
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

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return reviewsList.size();
        }

        class ReviewsViewHolder extends RecyclerView.ViewHolder {
            ImageView imgUser, dottedBtnImg, imgBusinessLogo;
            TextView tvUserName, tvDesc, tvReadMore, tvUseFul, tvTimeAgo, tvBusinessName, tvsummeryLine;
            RatingBar userRating;
            LinearLayout layoutUseFul;
            RecyclerView recGallery;
            LinearLayout layoutBusinessInfo;

            public ReviewsViewHolder(@NonNull View itemView) {
                super(itemView);
                imgUser = itemView.findViewById(R.id.user_img);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
                userRating = itemView.findViewById(R.id.user_rating);
                tvDesc = itemView.findViewById(R.id.tv_description);
                imgBusinessLogo = itemView.findViewById(R.id.business_img);
                tvsummeryLine = itemView.findViewById(R.id.business_summery_line);
                recGallery = itemView.findViewById(R.id.rec_review_img);
                tvBusinessName = itemView.findViewById(R.id.tv_business_name);
                tvReadMore = itemView.findViewById(R.id.tv_read_more);
                layoutBusinessInfo = itemView.findViewById(R.id.layout_business_info);
                layoutUseFul = itemView.findViewById(R.id.useful_layout);
                dottedBtnImg = itemView.findViewById(R.id.doted_btn);
                tvUseFul = itemView.findViewById(R.id.tv_useful);
                tvTimeAgo = itemView.findViewById(R.id.tv_time_ago);
            }
        }
    }
    //todo ======================= Reviews Adapter ===========================

    class RatingImgAdpater extends RecyclerView.Adapter<RatingImgAdpater.RatingImgViewHolder> {

        List<ReviewImage> imgList;

        public RatingImgAdpater(List<ReviewImage> imgList) {
            this.imgList = imgList;
        }

        @NonNull
        @Override
        public RatingImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RatingImgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review_img, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RatingImgViewHolder holder, int position) {
            loadImage(imgList.get(position).getImage(), holder.ratingImg);
        }

        private void loadImage(String url, ImageView img) {
            Glide.with(getActivity())
                    .asBitmap()
                    .load(url)
                    .override(500,500)
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

        @Override
        public int getItemCount() {
            return imgList.size();
        }

        class RatingImgViewHolder extends RecyclerView.ViewHolder {
            ImageView ratingImg;


            public RatingImgViewHolder(@NonNull View itemView) {
                super(itemView);
                ratingImg = itemView.findViewById(R.id.img_review);

            }
        }
    }
}
