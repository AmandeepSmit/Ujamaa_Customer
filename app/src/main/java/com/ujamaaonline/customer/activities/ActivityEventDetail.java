package com.ujamaaonline.customer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.LocalPostAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.fragments.EventPostFragment;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.event_filter.EventDetailPayload;
import com.ujamaaonline.customer.models.feed_models.GetEventsDateResponse;
import com.ujamaaonline.customer.models.feed_models.LinkClickRequest;
import com.ujamaaonline.customer.models.filter_heading.HeadingFilterResponse;
import com.ujamaaonline.customer.models.get_business_reviews.AllReviewResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;

public class ActivityEventDetail extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgLike, imgShare, imgMenu, imgPost, imgBusiness, imgBusiness2;
    private TextView tvEventType, tvEventTitle, tvBusinessName, tvGetTicket, tvEventTime, tvServiceLocation, tvEventPrice, tvBusinessName2, tvEventTitle2, tvEventDesc, tvSubscribe, tvBusinessDesc;
    private EventDetailPayload eventDetailPayload;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private DecimalFormat formater = new DecimalFormat("00.00");
    private EventPostFragment eventPostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        initView();
        if (getIntent().hasExtra("data")) {
            eventDetailPayload = (EventDetailPayload) getIntent().getSerializableExtra("data");
            loadData();
        }
    }
    private void loadData() {
        tvEventTitle.setText(TextUtils.isEmpty(eventDetailPayload.getEventTitle()) ? "" : eventDetailPayload.getEventTitle());
        if (!TextUtils.isEmpty(eventDetailPayload.getImage())) {
            loadImages(eventDetailPayload.getImage(), imgPost, R.drawable.no_image_business);
        }
        if (eventDetailPayload.getIsLiked() == 1)
            imgLike.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim_black));
        tvBusinessName.setText(TextUtils.isEmpty(eventDetailPayload.getBussinessName()) ? "" : eventDetailPayload.getBussinessName());
        tvBusinessName2.setText(TextUtils.isEmpty(eventDetailPayload.getBussinessName()) ? "" : eventDetailPayload.getBussinessName());
        tvServiceLocation.setText(TextUtils.isEmpty(eventDetailPayload.getLocation()) ? "" : eventDetailPayload.getLocation());
        tvEventTitle2.setText(TextUtils.isEmpty(eventDetailPayload.getEventTitle()) ? "" : eventDetailPayload.getEventTitle());
        if (!TextUtils.isEmpty(eventDetailPayload.getBusiness_logo())) {
            loadImages(eventDetailPayload.getBusiness_logo(), imgBusiness, R.drawable.ic_placeholder_user);
            loadImages(eventDetailPayload.getBusiness_logo(), imgBusiness2, R.drawable.ic_placeholder_user);
        }
      if (eventDetailPayload.getIsFreeEvent()==0)
      {
          if ((!TextUtils.isEmpty(eventDetailPayload.getTicketStartPrice())) && (!TextUtils.isEmpty(eventDetailPayload.getTicketEndPrice()))) {
              tvEventPrice.setText(formater.format(Double.parseDouble(eventDetailPayload.getTicketStartPrice()))+" - "+formater.format(Double.parseDouble(eventDetailPayload.getTicketEndPrice())));
          }
      }
      else
      {
          tvEventPrice.setText("FREE");
      }
        if (eventDetailPayload.getIsSubscriber() == 0) {
            imgShare.setVisibility(View.VISIBLE);
        } else imgShare.setVisibility(View.GONE);

        tvBusinessDesc.setText(!TextUtils.isEmpty(eventDetailPayload.getSummryLine()) ? eventDetailPayload.getSummryLine() : "");
        if (eventDetailPayload.getIsFreeEvent() == 0)
            tvEventType.setText("Paid event");
        else tvEventType.setText("Free event");
        tvEventDesc.setText(TextUtils.isEmpty(eventDetailPayload.getEventDescription()) ? "" : eventDetailPayload.getEventDescription());

        if (!TextUtils.isEmpty(eventDetailPayload.getEventStarts()) && !TextUtils.isEmpty(eventDetailPayload.getEventEnds())) {
            tvEventTime.setText(eventDetailPayload.getEventStarts().split(" ")[0] + " To " + eventDetailPayload.getEventEnds().split(" ")[0] + "\n" + eventDetailPayload.getEventStarts().split(" ")[1] + " To " + eventDetailPayload.getEventEnds().split(" ")[1]);
        }

        if (eventDetailPayload.getIs_bussiness_subscribed() == 1) {
            tvSubscribe.setText("Unsubscribe");
        } else tvSubscribe.setText("Subscribe");
    }

    private void loadImages(String imagePath, ImageView imageView, int no_image_business) {
        Glide.with(this)
                .load(imagePath)
                .error(R.drawable.no_image_business)
                .placeholder(no_image_business)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        imgLike = findViewById(R.id.bp_bookmarks);
        imgLike.setOnClickListener(this);
        imgPost = findViewById(R.id.post_img);
        imgShare = findViewById(R.id.bp_share);
        tvEventType = findViewById(R.id.tv_event_type);
        tvEventTitle = findViewById(R.id.tv_event_title);
        imgBusiness = findViewById(R.id.img_busness);
        tvBusinessDesc = findViewById(R.id.tv_business_desc);
        tvBusinessName2 = findViewById(R.id.tv_business_name2);
        imgBusiness2 = findViewById(R.id.event_img);
        tvBusinessName = findViewById(R.id.tv_business_name);
        tvGetTicket = findViewById(R.id.btn_get_ticket);
        tvGetTicket.setOnClickListener(this);
        tvSubscribe = findViewById(R.id.tv_subscribe);
        tvSubscribe.setOnClickListener(this);
        tvEventDesc = findViewById(R.id.tv_event_desc);
        tvEventTime = findViewById(R.id.tv_event_time);
        tvEventTitle2 = findViewById(R.id.tv_event_title_secnd);
        tvServiceLocation = findViewById(R.id.service_location);
        tvEventPrice = findViewById(R.id.tv_event_price);
    }

    public void visitProfileClick(View view) {
        if (eventDetailPayload != null){
            Intent intent = new Intent(ActivityEventDetail.this, BusinessProfile.class);
            intent.putExtra("businessCatId", String.valueOf(eventDetailPayload.getUserId()));
            intent.putExtra("businessLogo", eventDetailPayload.getBusiness_logo());
            startActivity(intent);
        }
    }

    private void callLikeEventApi(int isLIke, int postId, int isHide, ImageView imgLike, boolean fromHide) {
        if (!GlobalUtil.isNetworkAvailable(ActivityEventDetail.this)) {
            UIUtil.showNetworkDialog(ActivityEventDetail.this);
            return;
        }
        if (isHide == 1)
            progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setIsSubscriber(0);
        loginRequest.setIsEvent(1);
        loginRequest.setIsLocal(0);
        if (fromHide)
            loginRequest.setIs_hide(isHide);
        else
            loginRequest.setIsLike(isLIke);
        loginRequest.setIsShare(0);
        loginRequest.setPostId(postId);
//        loginRequest.setMonth("4");
//        loginRequest.setYear("2021");

        if (!ObjectUtil.isEmpty(loginRequest)) {

            apiInterface.likeEventPost(fromHide ? "hide_post" : "like_post", BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<GetEventsDateResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetEventsDateResponse> call, Response<GetEventsDateResponse> response) {
                    progress.hideProgressBar();
                    if (response.body().getStatus()) {
                       sendBroadcast(new Intent("update_event"));
                    } else {
                        if (!fromHide) {
                            if (isLIke == 0) {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(ActivityEventDetail.this, R.drawable.like_heart_anim));
                            } else {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(ActivityEventDetail.this, R.drawable.ic_heart));
                            }
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetEventsDateResponse> call, Throwable t) {
                    progress.hideProgressBar();
                    if (t.getMessage() != null) {
                        if (!fromHide) {
                            if (isLIke == 0) {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(ActivityEventDetail.this, R.drawable.like_heart_anim));
                            } else {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(ActivityEventDetail.this, R.drawable.ic_heart));
                            }
                        }
                    }
                }
            });
        }
    }

    private void subscribeBusiness(String businessId) {
        if (!GlobalUtil.isNetworkAvailable(ActivityEventDetail.this)) {
            UIUtil.showNetworkDialog(ActivityEventDetail.this);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest(businessId);
        progress.showProgressBar();
        apiInterface.subscribeBusiness(BEARER.concat(this.headerToken), request).enqueue(new Callback<AllReviewResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                progress.hideProgressBar();
                Toast.makeText(ActivityEventDetail.this, "onresponse", Toast.LENGTH_SHORT).show();
                if (!ObjectUtil.isEmpty(response.body())) {
                    AllReviewResponse registerResponse = response.body();
                    if (!ObjectUtil.isEmpty(registerResponse)) {
                        if (registerResponse.getStatus() == RESPONSE_SUCCESS) {
                            Toast.makeText(ActivityEventDetail.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            if (tvSubscribe.getText().toString().equalsIgnoreCase("subscribe")) {
                                tvSubscribe.setText("Unsubscribe");
                            } else {
                                tvSubscribe.setText("Subscribe");
                            }
                        } else {
                            Toast.makeText(ActivityEventDetail.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AllReviewResponse> call, Throwable t) {
                Toast.makeText(ActivityEventDetail.this, "on Error", Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityEventDetail.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_subscribe:
                subscribeBusiness(String.valueOf(eventDetailPayload.getUserId()));
                break;
            case R.id.btn_get_ticket:
                if (!TextUtils.isEmpty(eventDetailPayload.getEventLink())) {
                    String url = eventDetailPayload.getEventLink();
                    if (!eventDetailPayload.getEventLink().startsWith("http://") && !eventDetailPayload.getEventLink().startsWith("https://"))
                        url = "http://" + url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    linkClick();
                }
                break;
            case R.id.bp_bookmarks:
                if (eventDetailPayload.getIsLiked() == 0) {
                    imgLike.setImageDrawable(ContextCompat.getDrawable(ActivityEventDetail.this, R.drawable.like_heart_anim));
                    callLikeEventApi(1, eventDetailPayload.getId(), 0, imgLike, false);
                } else {
                    imgLike.setImageDrawable(ContextCompat.getDrawable(ActivityEventDetail.this, R.drawable.ic_heart));
                    callLikeEventApi(0, eventDetailPayload.getId(), 0,imgLike, false);
                }
                break;
        }
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }

    private void linkClick(){
        LinkClickRequest request=new LinkClickRequest(eventDetailPayload.getId(),1,0);
        apiInterface.linkClick(Constants.BEARER.concat(this.headerToken), request).enqueue(new Callback<HeadingFilterResponse>() {
            @Override
            public void onResponse(Call<HeadingFilterResponse> call, Response<HeadingFilterResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()){

                    }else{

                    }
                }
            }
            @Override
            public void onFailure(Call<HeadingFilterResponse> call, Throwable t) {

            }
        });
    }
}