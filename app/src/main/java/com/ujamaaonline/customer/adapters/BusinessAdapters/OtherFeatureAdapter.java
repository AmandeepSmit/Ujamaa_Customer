package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.OtherFeature;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.models.business_data.OtherFeatureSection;

import java.util.List;

public class OtherFeatureAdapter extends RecyclerView.Adapter<OtherFeatureAdapter.OtherViewHolder> {

    private List<OtherFeatureSection> oList;
    private boolean isViewAll;

    public OtherFeatureAdapter(List<OtherFeatureSection> oList, boolean isViewAll) {
        this.oList = oList;
        this.isViewAll = isViewAll;

    }

    @NonNull
    @Override
    public OtherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OtherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_other_feature, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OtherViewHolder holder, int position) {
        Picasso.get().load(oList.get(position).getOther_featur().getFeatureIcon()).resize(600,600).into(holder.img);
        holder.name.setText(oList.get(position).getOther_featur().getFeatureName());
        holder.img.setVisibility(View.VISIBLE);
    }

    public void setViewAll(boolean isViewAll) {
        this.isViewAll = isViewAll;
    }

    @Override
    public int getItemCount() {
        return !isViewAll ? oList.size() > 3 ? 3 : oList.size() : oList.size();
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView name;

        public OtherViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.rof_img);
            name = itemView.findViewById(R.id.rof_name);
        }

    }
}
