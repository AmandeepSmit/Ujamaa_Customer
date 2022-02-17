package com.ujamaaonline.customer.adapters;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ShareAndEarnPointActivity;
import com.ujamaaonline.customer.models.BannerPayload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<BannerPayload> itemLIst;

    public SliderAdapter(Context context, List<BannerPayload> itemLIst) {
        this.context = context;
        this.itemLIst = itemLIst;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.row_home_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
                BannerPayload bannerPayload=itemLIst.get(position);
                if(!TextUtils.isEmpty(bannerPayload.getBanner()))
                {
                    Picasso.get().load(bannerPayload.getBanner()).error(R.drawable.dummy).resize(1080,720).into(holder.ivBanner);
                }
                holder.tvName.setText(TextUtils.isEmpty(bannerPayload.getTitle())?"":bannerPayload.getTitle());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ShareAndEarnPointActivity.class));
                    }
                });

    }

    @Override
    public int getItemCount(){
        return itemLIst.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{

        ImageView ivBanner;
        TextView tvName;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner=itemView.findViewById(R.id.iv_banner);
            tvName=itemView.findViewById(R.id.tv_name);
        }
    }
}
