package com.ujamaaonline.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityEventDetail;
import com.ujamaaonline.customer.activities.MainActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.fragments.EventPostFragment;
import com.ujamaaonline.customer.models.event_filter.EventDetailResponse;
import com.ujamaaonline.customer.models.feed_models.EventPayload;
import com.ujamaaonline.customer.models.feed_models.FeedModel;
import com.ujamaaonline.customer.models.feed_models.GetEventsDateResponse;
import com.ujamaaonline.customer.models.feed_models.LinkClickRequest;
import com.ujamaaonline.customer.models.feed_models.PostPayload;
import com.ujamaaonline.customer.models.filter_heading.HeadingFilterResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;

public class EventPostAdapter extends RecyclerView.Adapter<EventPostAdapter.ViewHolder> {
    private List<EventPayload> eventFeedList;
    private Context context;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private BottomSheetDialog mBottomSheetDialog;
    private View sheetView;
    private EventPostFragment eventPostFragment;
    private SimpleDateFormat oldsdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date;
    private SimpleDateFormat newsdf=new SimpleDateFormat("EEE,MMM dd - hh:mm a");

    public EventPostAdapter(List<EventPayload> eventFeedList, Context context, EventPostFragment eventPostFragment) {
        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        mBottomSheetDialog = new BottomSheetDialog(context);
        sheetView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.local_feed_bottom_sheet_dialog, null);
        sheetView.findViewById(R.id.layout_report).setVisibility(View.GONE);
        sheetView.findViewById(R.id.layout_visit_profile).setVisibility(View.GONE);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        this.eventFeedList = eventFeedList;
        this.context = context;
        this.eventPostFragment = eventPostFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventPostAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(eventFeedList.get(position).getEventTitle());
        if (!TextUtils.isEmpty(eventFeedList.get(position).getImage())) {
            loadImages(eventFeedList.get(position).getImage(), holder.imgEvent);
        }


        if (eventFeedList.get(position).getIsPhysical()==1){
            holder.tvLocation.setVisibility(View.VISIBLE);
        }else{
            holder.tvLocation.setVisibility(View.GONE);
        }

        if (eventFeedList.get(position).getIsOnline()==1){
            holder.onlineText.setVisibility(View.VISIBLE);
        }else{
            holder.onlineText.setVisibility(View.GONE);
        }

        holder.tvHashtag.setText(TextUtils.isEmpty(eventFeedList.get(position).getHashtags()) ? "" : eventFeedList.get(position).getHashtags().contains(",")?eventFeedList.get(position).getHashtags().replace(","," "):eventFeedList.get(position).getHashtags());
        if (eventFeedList.get(position).getIsFreeEvent() == 0) {
            holder.tveventType.setVisibility(View.INVISIBLE);
        } else holder.tveventType.setVisibility(View.VISIBLE);
        if (eventFeedList.get(position).getIs_liked() == 1) {
            holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
        } else {
            holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
        }
        if (eventFeedList.get(position).getIsSubscriber() == 0) {
            holder.tvIsSubscriber.setVisibility(View.GONE);
            holder.imgShare.setVisibility(View.VISIBLE);
        } else
        {
            holder.imgShare.setVisibility(View.GONE);
            holder.tvIsSubscriber.setVisibility(View.VISIBLE);
        }

        if (eventFeedList.get(position).getAge()==0)
        {
            holder.tvMinAge.setVisibility(View.GONE);
        }
        else
        {
            holder.tvMinAge.setVisibility(View.VISIBLE);
            holder.tvMinAge.setText(eventFeedList.get(position).getAge()+"+");
        }
        try {
            date = oldsdf.parse(eventFeedList.get(position).getEventStarts());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvDateTime.setText(newsdf.format(date));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventDetail(eventFeedList.get(position).getId());
            }
        });
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventFeedList.get(position).getIs_hidden_post() == 1) {
                    ((TextView) sheetView.findViewById(R.id.tv_post_type)).setText("Unhide Event");
                } else ((TextView) sheetView.findViewById(R.id.tv_post_type)).setText("Hide Event");
                mBottomSheetDialog.show();
                sheetView.findViewById(R.id.layout_hide_post).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (eventFeedList.get(position).getIs_hidden_post() == 1) {
                            callLikeEventApi(0, eventFeedList.get(position).getId(), 0, position, null, true);
                        } else
                            callLikeEventApi(0, eventFeedList.get(position).getId(), 1, position, null, true);

                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.tv_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
            }
        });

        holder.tvDesc.setText(eventFeedList.get(position).getEventDescription());
        holder.tvDesc.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = holder.tvDesc.getLineCount();
                if (lineCount > 2) {
                    holder.tvDesc.setMaxLines(2);
                    holder.tvReadMore.setVisibility(View.VISIBLE);
                    holder.tvReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.tvDesc.getLineCount() > 2)
                            {
                                holder.tvReadMore.setText("read more");
                                holder.tvDesc.setMaxLines(2);
                            }
                            else
                            {
                                holder.tvReadMore.setText("read less");
                                holder.tvDesc.setMaxLines(lineCount);
                            }
                        }
                    });
                }
            }
        });
        if (!TextUtils.isEmpty(eventFeedList.get(position).getLocation()))
        holder.tvLocation.setText(eventFeedList.get(position).getLocation());
        else if (!TextUtils.isEmpty(eventFeedList.get(position).getExactLocation()))
            holder.tvLocation.setText(eventFeedList.get(position).getExactLocation());

//        holder.tvHashtag.setText(TextUtils.isEmpty(eventFeedList.get(position).getHashtags())?"":eventFeedList.get(position).getHashtags());

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventFeedList.get(position).getIs_liked() == 0) {
                    holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
                    callLikeEventApi(1, eventFeedList.get(position).getId(), 0, position, holder.imgLike, false);
                } else {
                    holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                    callLikeEventApi(0, eventFeedList.get(position).getId(), 0, position, holder.imgLike, false);
                }
            }
        });

    }

    private void callLikeEventApi(int isLIke, int postId, int isHide, int position, ImageView imgLike, boolean fromHide) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
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
                        if (!fromHide) {
                            eventFeedList.get(position).setIs_liked(isLIke);
                        } else {
                            eventPostFragment.getAllEvents(1, 0, 0,"","","", false);
                        }
                    } else {
                        if (!fromHide) {
                            if (isLIke == 0) {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
                            } else {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
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
                                imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
                            } else {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                            }
                        }
                    }
                }
            });
        }
    }

    private void getEventDetail(int eventId) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
            return;
        }
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setEvent_id(eventId);

        progress.showProgressBar();
        if (!ObjectUtil.isEmpty(loginRequest)) {

            try {
                apiInterface.getEventDetail(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<EventDetailResponse>() {
                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call<EventDetailResponse> call, Response<EventDetailResponse> response) {
                        progress.hideProgressBar();
                        if (response.body().getStatus()) {
                            if (response.body().getEventDetailPayload() != null) {
                                context.startActivity(new Intent(context, ActivityEventDetail.class).putExtra("data", response.body().getEventDetailPayload()));
                            }

                        } else {
                            Toast.makeText(context, "failed please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call<EventDetailResponse> call, Throwable t) {
                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        progress.hideProgressBar();
                        if (t.getMessage() != null) {
                        }
                    }
                });
            } catch (Exception e) {
                progress.hideProgressBar();
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventFeedList.size();
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_user_two)
                .error(R.drawable.ic_user_two)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tvDesc, tvLocation, tvHashtag, tveventType, tvReadMore,tvDateTime,tvMinAge,tvIsSubscriber,onlineText;
        ImageView imgEvent, imgLike, imgMenu, imgShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rev_title);
            imgEvent = itemView.findViewById(R.id.event_img);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvLocation = itemView.findViewById(R.id.tv_location);
            imgLike = itemView.findViewById(R.id.img_like);
            imgMenu = itemView.findViewById(R.id.img_menu);
            tveventType = itemView.findViewById(R.id.tv_price);
            tvReadMore = itemView.findViewById(R.id.tv_read_more);
            tvHashtag = itemView.findViewById(R.id.tv_hash_tag);
            imgShare = itemView.findViewById(R.id.img_share);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvMinAge=itemView.findViewById(R.id.tv_min_age);
            onlineText=itemView.findViewById(R.id.ref_online);
            tvIsSubscriber=itemView.findViewById(R.id.tv_is_subscriber);

        }
    }

    private void linkClick(Integer id, SubscriberPostAdapter.ViewHolder holder){
        LinkClickRequest request=new LinkClickRequest(id,0,1);
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
