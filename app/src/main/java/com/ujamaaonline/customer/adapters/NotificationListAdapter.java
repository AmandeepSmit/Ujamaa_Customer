package com.ujamaaonline.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.NotificationPayload;

import java.util.List;

public class NotificationListAdapter  extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private List<NotificationPayload> nList;

    public NotificationListAdapter(List<NotificationPayload> nList) {
        this.nList = nList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notifications,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(nList.get(position).getTitle());
        holder.time.setText(nList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return nList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title, desc,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.naf_title);
            //desc=itemView.findViewById(R.id.naf_desc);
            time=itemView.findViewById(R.id.naf_time);
        }
    }
}
