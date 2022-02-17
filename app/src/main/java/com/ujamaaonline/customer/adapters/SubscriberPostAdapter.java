package com.ujamaaonline.customer.adapters;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityChat;
import com.ujamaaonline.customer.activities.BusinessProfile;
import com.ujamaaonline.customer.activities.MainActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.fragments.FragmentSubscriberPost;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.feed_models.GetEventsDateResponse;
import com.ujamaaonline.customer.models.feed_models.LinkClickRequest;
import com.ujamaaonline.customer.models.feed_models.PostPayload;
import com.ujamaaonline.customer.models.feed_models.PostResponse;
import com.ujamaaonline.customer.models.filter_heading.HeadingFilterResponse;
import com.ujamaaonline.customer.models.get_business_reviews.AllReviewResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.BaseUtil;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;

public class SubscriberPostAdapter extends RecyclerView.Adapter<SubscriberPostAdapter.ViewHolder> {

    private List<PostPayload> fList;
    private Context context;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private BottomSheetDialog mBottomSheetDialog;
    private View sheetView;
    private FragmentSubscriberPost fragmentSubscriberPost;

    public SubscriberPostAdapter(List<PostPayload> fList, Context context, FragmentSubscriberPost fragmentSubscriberPost) {
        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.fragmentSubscriberPost = fragmentSubscriberPost;
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        mBottomSheetDialog = new BottomSheetDialog(context);
        sheetView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.local_feed_bottom_sheet_dialog, null);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        this.fList = fList;
        this.context = context;
    }

    @NonNull
    @Override
    public SubscriberPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subscriber_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriberPostAdapter.ViewHolder holder, int position) {
        if (fList.get(position).getIs_liked() == 1) {
            holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
        } else {
            holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
        }
        if (!TextUtils.isEmpty(fList.get(position).getBusiness_logo())) {
            Picasso.get().load(fList.get(position).getBusiness_logo()).placeholder(R.drawable.ic_user_two).error(R.drawable.ic_user_two).into(holder.feedImage);
        }

        if (fList.get(position).getIs_count_revealed() == 1) {
            holder.imgSeeMore.setVisibility(View.GONE);
            holder.code.setBackground(context.getDrawable(R.drawable.back_row));
        }

        else {
            holder.imgSeeMore.setVisibility(View.VISIBLE);
            holder.code.setBackground(context.getDrawable(R.drawable.new_post_code));
        }

        if (!TextUtils.isEmpty(fList.get(position).getCode())) {
            holder.layoutCode.setVisibility(View.VISIBLE);
            holder.code.setText(fList.get(position).getCode());
        }

        else holder.layoutCode.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(fList.get(position).getUpload())) {
            holder.postiMg.setVisibility(View.VISIBLE);
            loadImages(fList.get(position).getUpload(), holder.postiMg);
        } else
            holder.postiMg.setVisibility(View.GONE);
        holder.tvHashTag.setText(!TextUtils.isEmpty(fList.get(position).getHashtags()) ? fList.get(position).getHashtags().contains(",")?fList.get(position).getHashtags().replace(",",""):fList.get(position).getHashtags() : "");
        if (!TextUtils.isEmpty(fList.get(position).getLinkName()))
            holder.tvLinkName.setText(TextUtils.isEmpty(fList.get(position).getLinkName())?TextUtils.isEmpty(fList.get(position).getLink())?"":fList.get(position).getLink():fList.get(position).getLinkName());
        else
            holder.layoutLInk.setVisibility(View.GONE);

        holder.postDesc.setText(fList.get(position).getPost());
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

        holder.tvCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(holder.itemView.getContext(), fList.get(position).getLink());
                linkClick(fList.get(position).getId(), holder);
            }
        });

        holder.businessName.setText(fList.get(position).getBussinessName());
        holder.date.setText(fList.get(position).getCreatedOn());
        holder.date.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_date));

            holder.expireIn.setText(TextUtils.isEmpty(fList.get(position).getExpire_in_text())?"":fList.get(position).getExpire_in_text());

        if (fList.get(position).getIsImportant() == 1) {
            holder.impTextView.setText("IMPORTANT");
            holder.impTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_dark_purple));
        } else if (fList.get(position).getIsGeneral() == 1) {
            holder.impTextView.setText("GENERAL");
            holder.impTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_purple));
        } else if (fList.get(position).getIsDiscountPromotion() == 1) {
            holder.impTextView.setText("DISCOUNT");
            holder.impTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_light_green));
        } else {
            holder.impTextView.setText("EVENT");
            holder.impTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_yellow));
        }

        holder.imgSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeCode(fList.get(position).getCode(), holder.imgSeeMore, holder.code,fList.get(position).getId(),fList.get(position).getUserId());
            }
        });
        holder.postTitle.setText(TextUtils.isEmpty(fList.get(position).getPostTitle())?"":fList.get(position).getPostTitle());

        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fList.get(position).getIs_hidden_post() == 1) {
                    ((TextView) sheetView.findViewById(R.id.tv_post_type)).setText("Unhide Post");
                } else ((TextView) sheetView.findViewById(R.id.tv_post_type)).setText("Hide Post");
                mBottomSheetDialog.show();
                sheetView.findViewById(R.id.layout_hide_post).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fList.get(position).getIs_hidden_post() == 1) {
                            callLikeEventApi(0, fList.get(position).getId(), 0, position, null, true);
                        } else
                            callLikeEventApi(0, fList.get(position).getId(), 1, position, null, true);
                        mBottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.layout_visit_profile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), BusinessProfile.class);
                        intent.putExtra("businessCatId",String.valueOf(fList.get(position).getUserId()));
                        intent.putExtra("businessLogo", fList.get(position).getBusiness_logo());
                            context.startActivity(intent);
                        mBottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.layout_report).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscribeBusiness(String.valueOf(fList.get(position).getUserId()));
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
                if (fList.get(position).getIs_liked() == 0) {
                    holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
                    callLikeEventApi(1, fList.get(position).getId(), 0, position, holder.imgLike, false);
                } else {
                    holder.imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                    callLikeEventApi(0, fList.get(position).getId(), 0, position, holder.imgLike, false);
                }
            }
        });
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "link Copied", Toast.LENGTH_SHORT).show();
    }

    private void seeCode(String code, ImageView imgSeeMore, TextView tvSeeMore,int id,int businessId) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
            return;
        }
        progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();
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

    private void subscribeBusiness(String businessId) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest(businessId);
        progress.showProgressBar();
        apiInterface.subscribeBusiness(BEARER.concat(this.headerToken), request).enqueue(new Callback<AllReviewResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AllReviewResponse> call, Response<AllReviewResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    AllReviewResponse registerResponse = response.body();
                    if (!ObjectUtil.isEmpty(registerResponse)) {
                        if (registerResponse.getStatus() == RESPONSE_SUCCESS) {
                            Toast.makeText(context, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            fragmentSubscriberPost.getSubscriberFeed(1, 0, 0, 0);
                        } else {
                            Toast.makeText(context, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AllReviewResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(context, "failed : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callLikeEventApi(int isLIke, int postId, int isHide, int position, ImageView imgLike, boolean froHide) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
            return;
        }
        if (froHide)
            progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();

        loginRequest.setIsSubscriber(1);
        loginRequest.setIsEvent(0);
        loginRequest.setIsLocal(0);
        if (froHide)
            loginRequest.setIs_hide(isHide);
        else
            loginRequest.setIsLike(isLIke);
        loginRequest.setIsShare(0);
        loginRequest.setPostId(postId);

        if (!ObjectUtil.isEmpty(loginRequest)) {
            apiInterface.likeEventPost(froHide ? "hide_post" : "like_post", BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<GetEventsDateResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetEventsDateResponse> call, Response<GetEventsDateResponse> response) {
                    progress.hideProgressBar();
                    if (response.body().getStatus()) {

                        if (!froHide) {
                            fList.get(position).setIs_liked(isLIke);
                        } else {
                            fragmentSubscriberPost.getSubscriberFeed(1, 0, 0, 0);
                        }
                    } else {
                        if (!froHide)
                            if (isLIke == 0) {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_heart_anim));
                            } else {
                                imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                            }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetEventsDateResponse> call, Throwable t) {
                    progress.hideProgressBar();
                    if (t.getMessage() != null) {
                        if (froHide) {
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

    @Override
    public int getItemCount() {
        return fList.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView businessName, shareText, likesText, tvCopyLink, date, expireIn, postDesc, impTextView, tvReadMore, code, tvHashTag, tvLinkName,postTitle;
        private ImageView imgSeeMore;
        private ImageView feedImage, imgLike, imgMenu, postiMg;
        private LinearLayout layoutCode, layoutLInk;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            businessName = itemView.findViewById(R.id.rsp_businessName);
            date = itemView.findViewById(R.id.fsp_date);
            expireIn = itemView.findViewById(R.id.fsp_expireIn);
            postDesc = itemView.findViewById(R.id.fsp_post);
            feedImage = itemView.findViewById(R.id.fsp_feedImage);
            impTextView = itemView.findViewById(R.id.fsp_impText);
            imgLike = itemView.findViewById(R.id.img_like);
            postiMg = itemView.findViewById(R.id.post_img);
            imgMenu = itemView.findViewById(R.id.img_menu);
            tvReadMore = itemView.findViewById(R.id.tv_read_more);
            code = itemView.findViewById(R.id.rlp_code);
            imgSeeMore = itemView.findViewById(R.id.see_more_img);
            layoutCode = itemView.findViewById(R.id.code_layout);
            tvHashTag = itemView.findViewById(R.id.tv_hash_tag);
            postTitle=itemView.findViewById(R.id.fsp_title);
            tvLinkName = itemView.findViewById(R.id.tv_link_name);
            layoutLInk = itemView.findViewById(R.id.layout_link);
            tvCopyLink = itemView.findViewById(R.id.tv_copy_link);
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
