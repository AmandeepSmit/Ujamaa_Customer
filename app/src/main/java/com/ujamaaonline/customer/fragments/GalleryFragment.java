package com.ujamaaonline.customer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityGalleryDetail;
import com.ujamaaonline.customer.activities.ProfileImageDialog;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.gallery_images.GalleryImages;
import com.ujamaaonline.customer.models.gallery_images.GalleryImagesResponse;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class GalleryFragment extends Fragment {

    private RecyclerView gRecyclerView;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken, businessId;
    private List<SearchGalleryPayload> gaList = new ArrayList<>();
    private GalleryImagesAdapter galleryImagesAdapter;
    private TextView tvEmptyGallery;
    private boolean isLogin = false;

    public GalleryFragment(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.progress = new GlobalProgressDialog(view.getContext());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        gRecyclerView = view.findViewById(R.id.gl_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gRecyclerView.setLayoutManager(gridLayoutManager);
        tvEmptyGallery = view.findViewById(R.id.tv_empty_gallery);
        getGalleryImages(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        getGalleryImages(false);
    }
    private void getGalleryImages(boolean isProgress) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        if (isProgress)
        progress.showProgressBar();
        GetBusinessRequest request = new GetBusinessRequest(businessId);
        request.setCustomer_id(String.valueOf(this.loginPreferences.getInt(Constants.USER_ID, 0)));
        apiInterface.getGalleryData(BEARER.concat(this.headerToken), request).enqueue(new Callback<GalleryImagesResponse>() {
            @Override
            public void onResponse(Call<GalleryImagesResponse> call, Response<GalleryImagesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().getGalleryImages().size() > 0) {
                            gaList.clear();
                            gaList.addAll(response.body().getPayload().getGalleryImages());
                            galleryImagesAdapter = new GalleryImagesAdapter(gaList);
                            gRecyclerView.setAdapter(galleryImagesAdapter);
                            gRecyclerView.setVisibility(View.VISIBLE);
                            tvEmptyGallery.setVisibility(View.GONE);
                        }

                    } else {
//                         Toast.makeText(getActivity(),response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                     Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                if (progress.isShowing())
                progress.hideProgressBar();
            }

            @Override
            public void onFailure(Call<GalleryImagesResponse> call, Throwable t) {
                gRecyclerView.setVisibility(View.GONE);
                tvEmptyGallery.setVisibility(View.VISIBLE);
                if (progress.isShowing())
                progress.hideProgressBar();
//                 Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void changeView(boolean isLIked, ImageView img) {
        if (isLIked)
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
        else
            img.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim));
    }


    private void likeImage(Integer imgId, ImageView img, boolean isLIked, int position) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
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

//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        changeView(isLIked, img);
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                    changeView(isLIked, img);
                }
            }

            @Override
            public void onFailure(Call<GalleryImagesResponse> call, Throwable t) {
                changeView(isLIked, img);
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //todo===============================Gallery Adapter====================================
    public class GalleryImagesAdapter extends RecyclerView.Adapter<GalleryImagesAdapter.GallryHolder> {

        private List<SearchGalleryPayload> gList;
        private boolean isLiked = false;
        private List<String> mdeiaList = new ArrayList<>();
        public GalleryImagesAdapter(List<SearchGalleryPayload> gList) {
            this.gList = gList;
        }

        @NonNull
        @Override
        public GalleryImagesAdapter.GallryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GallryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_images, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryImagesAdapter.GallryHolder holder, int position) {
            Picasso.get().load(gList.get(position).getImg()).resize(500, 500).into(holder.image);
            if (gList.get(position).getLikedImg()==1)
            {
                holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim));
            }
            else
            {
                holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
            }

            holder.imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLogin) {
                        if (holder.imgLike.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_heart).getConstantState())) {
                            holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.like_heart_anim));
                            isLiked = true;
                        } else {
                            holder.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
                            isLiked = false;
                        }
                        likeImage(gList.get(position).getImgId(), holder.imgLike, isLiked,position);
                        holder.imgAnim.setVisibility(View.VISIBLE);
                        animateHeart(holder.imgAnim);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.imgAnim.setVisibility(View.GONE);
                            }
                        }, 400);
                    } else {
                        Toast.makeText(getActivity(), "please login first", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActivityGalleryDetail.class);
                    intent.putExtra("img_payload", gList.get(position));
                    intent.putExtra("business","");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), holder.image, ViewCompat.getTransitionName(holder.image));
                    startActivity(intent, options.toBundle());
                }
            });

        }


        @Override
        public int getItemCount() {
            return gList.size();
        }

        public class GallryHolder extends RecyclerView.ViewHolder {
            private ImageView image, imgLike, imgAnim;

            public GallryHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.glr_Img);
                imgLike = itemView.findViewById(R.id.img_like);
                imgAnim = itemView.findViewById(R.id.img_heart);
            }
        }
    }
}
