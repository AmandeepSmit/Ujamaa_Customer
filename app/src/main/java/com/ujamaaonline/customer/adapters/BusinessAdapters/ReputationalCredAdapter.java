package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.ReputationCredential;

import java.util.List;

public class ReputationalCredAdapter extends RecyclerView.Adapter<ReputationalCredAdapter.ViewHolder> {

    private List<ReputationCredential> rList;

    public ReputationalCredAdapter(List<ReputationCredential> rList) {
        this.rList = rList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_repu_cred,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.credential.setText(rList.get(position).getCredentialName());
    }

    @Override
    public int getItemCount() {
        return rList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        private TextView credential;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            credential=itemView.findViewById(R.id.rpc_text);
        }
    }
}
