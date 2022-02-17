package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityGalleryDetail;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.models.business_data.GalleryImage;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.gallery_images.GalleryImagesResponse;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {

    private List<SearchGalleryPayload> gList;
    private boolean isLogin = false;
    private boolean isLiked = false;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    public GalleryAdapter(List<SearchGalleryPayload> gList) {
        this.gList = gList;
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
    }
    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {
        Picasso.get().load(gList.get(position).getImg()).resize(600,600).into(holder.imgs);


        if (gList.get(position).getLikedImg()==1)
        {
            holder.imgLike.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.like_heart_anim));
        }
        else
        {
            holder.imgLike.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_heart));
        }


        holder.imgLike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    if (holder.imgLike.getDrawable().getConstantState().equals(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_heart).getConstantState())) {
                        holder.imgLike.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.like_heart_anim));
                        isLiked = true;
                    } else {
                        holder.imgLike.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_heart));
                        isLiked = false;
                    }
                    likeImage(holder.itemView.getContext(),gList.get(position).getImgId(), holder.imgLike, isLiked,position);
                    holder.imgAnim.setVisibility(View.VISIBLE);
                    animateHeart(holder.imgAnim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.imgAnim.setVisibility(View.GONE);
                        }
                    }, 400);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "please login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ActivityGalleryDetail.class);
                intent.putExtra("img_payload", gList.get(position));
                intent.putExtra("business","");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), holder.imgs, ViewCompat.getTransitionName(holder.imgs));
                holder.itemView.getContext().startActivity(intent, options.toBundle());
            }
        });



    }
    private void likeImage(Context context,Integer imgId, ImageView img, boolean isLIked, int position) {
        if (!GlobalUtil.isNetworkAvailable(context)) {
            UIUtil.showNetworkDialog(context);
            changeView(context,isLIked, img);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest();
        request.setImg_id(imgId);

        apiInterface.likeImage(BEARER.concat(headerToken), request).enqueue(new Callback<GalleryImagesResponse>() {
            @Override
            public void onResponse(Call<GalleryImagesResponse> call, Response<GalleryImagesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        changeView(context,isLIked, img);
                    }
                } else {
                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                    changeView(context,isLIked, img);
                }
            }

            @Override
            public void onFailure(Call<GalleryImagesResponse> call, Throwable t) {
                changeView(context,isLIked, img);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeView(Context context,boolean isLIked, ImageView img) {
        if (isLIked)
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart));
        else
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.like_heart_anim));
    }

    public void animateHeart(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(400);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }
    private Animation prepareAnimation(Animation animation) {
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
    @Override
    public int getItemCount() {
        return gList.size();
    }

    public class GalleryHolder extends RecyclerView.ViewHolder{
        private ImageView imgs,imgLike, imgAnim;
        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            imgs=itemView.findViewById(R.id.rg_Img);
            imgLike = itemView.findViewById(R.id.img_like);
            imgAnim = itemView.findViewById(R.id.img_heart);
        }
    }
}
