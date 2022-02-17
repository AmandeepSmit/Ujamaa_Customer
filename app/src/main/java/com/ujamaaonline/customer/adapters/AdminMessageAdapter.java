package com.ujamaaonline.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.AdminMessageModel;

import java.util.List;

public class AdminMessageAdapter extends RecyclerView.Adapter<AdminMessageAdapter.ViewHolder> {

    private List<AdminMessageModel> aList;

    public AdminMessageAdapter(List<AdminMessageModel> aList) {
        this.aList = aList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_messages,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.senderName.setText(aList.get(position).getSenderName());
        holder.message.setText(aList.get(position).getMessage());
        holder.subject.setText(aList.get(position).getSubject_line());
        String time=aList.get(position).getTimestamp();
        String[] mt=time.split(" ");
        String fTime=mt[4];
        holder.time.setText(fTime);
    }

    @Override
    public int getItemCount() {
        return aList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView senderName,message,time,subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName=itemView.findViewById(R.id.ram_sender);
            message=itemView.findViewById(R.id.ram_msg);
            time=itemView.findViewById(R.id.ram_time);
            subject=itemView.findViewById(R.id.ram_subject);
        }
    }
}
