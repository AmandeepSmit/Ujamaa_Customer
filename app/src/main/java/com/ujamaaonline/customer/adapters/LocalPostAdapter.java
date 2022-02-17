package com.ujamaaonline.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.BusinessProfile;
import com.ujamaaonline.customer.activities.MainActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.fragments.LocalFeedFragment;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;

public class LocalPostAdapter extends RecyclerView.Adapter<LocalPostAdapter.ViewHolder> {

    private List<PostPayload> feedList;
    private Context context;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private BottomSheetDialog mBottomSheetDialog;
    private View sheetView;
    private LocalFeedFragment localFeedFragment;

    public LocalPostAdapter(List<PostPayload> feedList, Context context, LocalFeedFragment localFeedFragment) {
        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        mBottomSheetDialog = new BottomSheetDialog(context);
        sheetView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.local_feed_bottom_sheet_dialog, null);
        sheetView.findViewById(R.id.layout_report).setVisibility(View.GONE);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        this.feedList = feedList;
        this.context = context;
        this.localFeedFragment = localFeedFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocalPostAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_local_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.businessName.setText(feedList.get(position).getBussinessName());
        holder.date.setText(feedList.get(position).getCreatedOn());
        holder.date.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_date));
        if (!TextUtils.isEmpty(feedList.get(position).getBusiness_logo())) {
            Picasso.get().load(feedList.get(position).getBusiness_logo()).placeholder(R.drawable.ic_user_two).error(R.drawable.ic_user_two).into(holder.feedImage);
        }
        holder.expireIn.setText(TextUtils.isEmpty(feedList.get(position).getExpire_in_text())?"":feedList.get(position).getExpire_in_text());
//        if (feedList.get(position).getExpireIn()>0)
//        {
//            holder.expireIn.setText("Expires in " + String.valueOf(feedList.get(position).getExpireIn()) + "Days");
//            holder.expireIn.setTextColor(context.getResources().getColor(R.color.gray_default));
//        }
//        else if (feedList.get(position).getExpireIn()==0)
//        {
//            holder.expireIn.setText("Expired");
//            holder.expireIn.setTextColor(context.getResources().getColor(R.color.darkRed));
//        }

        holder.postTitle.setText(TextUtils.isEmpty(feedList.get(position).getPostTitle())?"":feedList.get(position).getPostTitle());
        holder.postDesc.setText(feedList.get(position).getPost());
        holder.postDesc.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = holder.postDesc.getLineCount();
                if (lineCount > 2) {
                    holder.postDesc.setMaxLines(2);
                    holder.tvReadMore.setVisibility(View.VISIBLE);
                    holder.tvReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.postDesc.getLineCount() > 2) {
                                holder.tvReadMore.setText("read more");
                                holder.postDesc.setMaxLines(2);
                            } else {
                                holder.tvReadMore.setText("read less");
                                holder.postDesc.setMaxLines(Integer.MAX_VALUE);
                            }
                        }
                    });
                }
            }
        });

        if (feedList.get(position).getIs_count_revealed() == 1) {
            holder.imgSeeMore.setVisibility(View.GONE);
            holder.code.setBackground(context.getDrawable(R.drawable.back_row));
        } else {
            holder.imgSeeMore.setVisibility(View.VISIBLE);
            holder.code.setBackground(context.getDrawable(R.drawable.new_post_code));
        }

        if (!TextUtils.isEmpty(feedList.get(position).getCode())) {
            holder.layoutCode.setVisibility(View.VISIBLE);
            holder.code.setText(feedList.get(position).getCode());
        } else holder.layoutCode.setVisibility(View.GONE);


        if (!TextUtils.isEmpty(feedList.get(position).getHashtags()))
            holder.tvHashTag.setText(feedList.get(position).getHashtags());


        if (!TextUtils.isEmpty(feedList.get(position).getUpload())) {
            holder.postiMg.setVisibility(View.VISIBLE);
            loadImages(feedList.get(position).getUpload(), holder.postiMg);
        } else
            holder.postiMg.setVisibility(View.GONE);

//        Picasso.get().load(feedList.get(position).getUpload()).into(holder.feedImage);

        if (feedList.get(position).getIs_liked() == 1) {
            holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
        } else {
            holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
        }
        if (feedList.get(position).getIsDiscountPromotion() == 1) {
            holder.disTextView.setText("DISCOUNT");
            holder.disTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_light_green));
        } else if (feedList.get(position).getIsImportant() == 1) {
            holder.disTextView.setText("IMPORTANT");
            holder.disTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_dark_purple));
        } else if (feedList.get(position).getIsGeneral() == 1) {
            holder.disTextView.setText("GENERAL");
            holder.disTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_purple));
        } else {
            holder.disTextView.setText("EVENT");
            holder.disTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_yellow));
        }
        holder.imgSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeCode(feedList.get(position).getCode(), holder.imgSeeMore, holder.code,feedList.get(position).getId(),feedList.get(position).getUserId());
            }
        });

        if (!TextUtils.isEmpty(feedList.get(position).getLinkName())){
            holder.layoutLink.setVisibility(View.VISIBLE);
        }
        else {
            holder.layoutLink.setVisibility(View.GONE);
        }

        holder.linkName.setText(TextUtils.isEmpty(feedList.get(position).getLinkName())?"":feedList.get(position).getLinkName());
        holder.copyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(feedList.get(position).getLink()))
                setClipboard(context,feedList.get(position).getLink());
                linkClick(feedList.get(position).getId(), holder);

            }
        });
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedList.get(position).getIs_hidden_post() == 1) {
                    ((TextView) sheetView.findViewById(R.id.tv_post_type)).setText("Unhide Post");
                } else ((TextView) sheetView.findViewById(R.id.tv_post_type)).setText("Hide Post");
                mBottomSheetDialog.show();
                sheetView.findViewById(R.id.layout_hide_post).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callLikeEventApi(0, feedList.get(position).getId(), feedList.get(position).getIs_hidden_post()==1?0:1, position, null, true);
                        mBottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.layout_visit_profile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), BusinessProfile.class);
                        intent.putExtra("businessCatId",String.valueOf(feedList.get(position).getUserId()));
                        intent.putExtra("businessLogo", feedList.get(position).getBusiness_logo());
                        context.startActivity(intent);
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


        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedList.get(position).getIs_liked() == 0) {
                    holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
                    callLikeEventApi(1, feedList.get(position).getId(), 0, position, holder.imgLike, false);
                } else {
                    holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                    callLikeEventApi(0, feedList.get(position).getId(), 0, position, holder.imgLike, false);
                }
            }
        });
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Glide.with(context)
                .load(imagePath)
                .fitCenter()
                .error(R.drawable.app_logo)
                .placeholder(R.drawable.app_logo)
                .thumbnail(0.10f)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }




    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "link copied", Toast.LENGTH_SHORT).show();
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
        loginRequest.setIsEvent(0);
        loginRequest.setIsLocal(1);
        if (fromHide)
            loginRequest.setIs_hide(isHide);
        else
            loginRequest.setIsLike(isLIke);
        loginRequest.setIsShare(0);
        loginRequest.setPostId(postId);
        if (!ObjectUtil.isEmpty(loginRequest)) {
            apiInterface.likeEventPost(fromHide ? "hide_post" : "like_post", BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<GetEventsDateResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetEventsDateResponse> call, Response<GetEventsDateResponse> response) {
                    progress.hideProgressBar();
                    if (response.body().getStatus()) {
                        if (!fromHide) {
                            feedList.get(position).setIs_liked(isLIke);
                        } else {
                            localFeedFragment.getSubscriberFeed(1, 0, 0, 0);
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

    private void seeCode(String code, ImageView imgSeeMore, TextView tvSeeMore,int id,int businessId) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
            return;
        }
        progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setCode(code);
        loginRequest.setCode(code);
        loginRequest.setPostId(id);
        loginRequest.setBusiness_id(String.valueOf(businessId));

        if (!ObjectUtil.isEmpty(loginRequest)) {
            apiInterface.seeCode(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<GetEventsDateResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetEventsDateResponse> call, Response<GetEventsDateResponse> response) {
                    progress.hideProgressBar();
                    if (response.body().getStatus()) {
                        imgSeeMore.setVisibility(View.GONE);
                        tvSeeMore.setBackground(context.getDrawable(R.drawable.back_row));
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetEventsDateResponse> call, Throwable t) {
                    progress.hideProgressBar();
                    if (t.getMessage() != null) {
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView businessName, shareText, likesText, linkText, date, expireIn, postDesc, disTextView, code, tvHashTag,tvReadMore,linkName,copyLink,postTitle;
        private ImageView feedImage, imgLike, imgMenu, postiMg, imgSeeMore;
        private LinearLayout layoutCode,layoutLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            businessName = itemView.findViewById(R.id.rlf_businessName);
            date = itemView.findViewById(R.id.rlp_date);
            expireIn = itemView.findViewById(R.id.rlp_expireIn);
            postDesc = itemView.findViewById(R.id.rlp_postDesc);
            feedImage = itemView.findViewById(R.id.rlp_feedImage);
            disTextView = itemView.findViewById(R.id.rlp_discount);
            tvReadMore=itemView.findViewById(R.id.tv_read_more);
            postiMg = itemView.findViewById(R.id.post_img);
            tvHashTag = itemView.findViewById(R.id.tv_hash_tag);
            imgLike = itemView.findViewById(R.id.img_like);
            imgMenu = itemView.findViewById(R.id.img_menu);
            code = itemView.findViewById(R.id.rlp_code);
            linkName=itemView.findViewById(R.id.link_name);
            postTitle=itemView.findViewById(R.id.post_title);
            copyLink=itemView.findViewById(R.id.tv_copy_link);
            imgSeeMore = itemView.findViewById(R.id.see_more_img);
            layoutCode = itemView.findViewById(R.id.code_layout);
            layoutLink=itemView.findViewById(R.id.layout_link);
        }
    }

    private void linkClick(Integer id, ViewHolder holder){
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
