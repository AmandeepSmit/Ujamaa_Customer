package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.BuildConfig;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.gallery_images.GalleryImagesResponse;
import com.ujamaaonline.customer.models.gallery_images.SingleImgDetailResponse;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;

public class ActivityGalleryDetail extends AppCompatActivity implements View.OnClickListener {
    //

    private SearchGalleryPayload searchGalleryPayload;
    private ImageView imgView;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private String headerToken;
    private boolean isLogin = false, isLiked;
    private EditText setSearch;
    private TextView tvBusinessName, tvHashTag, tvVisit;
    private SessionSecuredPreferences loginPreferences;
    private Integer userId, businessId;
    private ImageView imgHeart, likeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);
        animateView();
        initView();
    }

    private void initView() {
        searchGalleryPayload = (SearchGalleryPayload) getIntent().getSerializableExtra("img_payload");
        imgView = findViewById(R.id.img_view);
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        userId = this.loginPreferences.getInt(USER_ID, 0);
        likeAnim = findViewById(R.id.img_anim);
        tvBusinessName = findViewById(R.id.tv_business_name);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        imgHeart = findViewById(R.id.img_heart);
        tvHashTag = findViewById(R.id.tv_hash_tag);
        tvVisit = findViewById(R.id.tv_visit);
        tvVisit.setOnClickListener(this);
        imgHeart.setOnClickListener(this);

//        imgView.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector = new GestureDetector(ActivityGalleryDetail.this, new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//                    likeImageTap();
//                    return super.onDoubleTap(e);
//                }
//            });
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });

        if (!TextUtils.isEmpty(searchGalleryPayload.getImg())) {
            Picasso.get().load(searchGalleryPayload.getImg()).resize(700, 700).into(imgView);
        }
        getImgDetail(searchGalleryPayload.getImgId());
    }

    private void likeImage(Integer imgId, ImageView img, boolean isLIked) {
        if (!GlobalUtil.isNetworkAvailable(this)) {
            UIUtil.showNetworkDialog(this);
            changeView(isLIked, img);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest();
        request.setImg_id(imgId);

        apiInterface.likeImage(BEARER.concat(this.headerToken), request).enqueue(new Callback<GalleryImagesResponse>() {
            @Override
            public void onResponse(Call<GalleryImagesResponse> call, Response<GalleryImagesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
//                        Toast.makeText(ActivityGalleryDetail.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(ActivityGalleryDetail.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        changeView(isLIked, img);
                    }
                } else {
//                    Toast.makeText(ActivityGalleryDetail.this, "Server Error", Toast.LENGTH_SHORT).show();
                    changeView(isLIked, img);
                }
            }

            @Override
            public void onFailure(Call<GalleryImagesResponse> call, Throwable t) {
                changeView(isLIked, img);
//                Toast.makeText(ActivityGalleryDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeView(boolean isLIked, ImageView img) {
        if (isLIked)
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_black));
        else img.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim_black));
    }

    private void animateView() {
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    public void animateHeart(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(600);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    private Animation prepareAnimation(Animation animation) {
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    private void getImgDetail(Integer imgId) {
        if (!GlobalUtil.isNetworkAvailable(this)) {
            UIUtil.showNetworkDialog(this);
            return;
        }

        GetBusinessRequest request = new GetBusinessRequest();
        request.setImg_id(imgId);
        request.setCustomer_id(String.valueOf(userId));

        apiInterface.getSingleImgDetail(BEARER.concat(this.headerToken), request).enqueue(new Callback<SingleImgDetailResponse>() {
            @Override
            public void onResponse(Call<SingleImgDetailResponse> call, Response<SingleImgDetailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        SingleImgDetailResponse singleImgDetailResponse = response.body();
                        if (singleImgDetailResponse.getPayload() != null) {
                            tvBusinessName.setText(TextUtils.isEmpty(singleImgDetailResponse.getPayload().getBusinessName()) ? "" : singleImgDetailResponse.getPayload().getBusinessName());
                            if (singleImgDetailResponse.getPayload().likedImg == 1) {
                                imgHeart.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim_black));
                            }
                            businessId = singleImgDetailResponse.getPayload().businessId;
                            if (singleImgDetailResponse.getPayload().getImg().getImageHastags().size() > 0)
                                tvHashTag.setText(singleImgDetailResponse.getPayload().getImg().getImageHastags().toString().replace(",", " ").replace("[", "").replace("]", ""));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleImgDetailResponse> call, Throwable t) {
//                changeView(isLIked, img);
//                Toast.makeText(ActivityGalleryDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void backBtnCick(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_heart:
                if (!getIntent().hasExtra("profile"))
                    likeImageTap();
                break;
            case R.id.tv_visit:
                if (getIntent().hasExtra("business"))
                    finish();
                else
                startActivity(new Intent(ActivityGalleryDetail.this, BusinessProfile.class).putExtra("businessCatId", String.valueOf(businessId)));
                break;


        }
    }

    private void likeImageTap() {
        if (isLogin) {
            if (imgHeart.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_heart_black).getConstantState())) {
                imgHeart.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim_black));
                isLiked = true;
            } else {
                imgHeart.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_black));
                isLiked = false;
            }
            likeImage(searchGalleryPayload.getImgId(), imgHeart, isLiked);
            likeAnim.setVisibility(View.VISIBLE);
            animateHeart(likeAnim);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    likeAnim.setVisibility(View.GONE);
                }
            }, 400);
        } else {
//            Toast.makeText(ActivityGalleryDetail.this, "please login first", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareBtnClick(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                searchGalleryPayload.getImg());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }
}