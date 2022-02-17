package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.MeetTheTeam;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MeetTeamAdapter extends RecyclerView.Adapter<MeetTeamAdapter.MeetViewHolder> {

    private List<MeetTheTeam> mList;

    public MeetTeamAdapter(List<MeetTheTeam> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public MeetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MeetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_meet_team, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MeetViewHolder holder, int position) {
        holder.name.setText(mList.get(position).getMemberName());
        holder.role.setText(mList.get(position).getMemberRole());
        Picasso.get().load(mList.get(position).getMemberProfile()).resize(500,500).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MeetViewHolder extends RecyclerView.ViewHolder {
        private TextView name, role;
        private ImageView img;

        public MeetViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rmt_name);
            role = itemView.findViewById(R.id.rmt_role);
            img = itemView.findViewById(R.id.rmt_image);
        }
    }
}
