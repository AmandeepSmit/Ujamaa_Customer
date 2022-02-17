package com.ujamaaonline.customer.adapters;

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
import com.ujamaaonline.customer.activities.ActivityRecepient;
import com.ujamaaonline.customer.models.chat_models.QuoteModel;

import org.w3c.dom.Text;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

    private List<QuoteModel> qList;
    private String customerId;
    private String businessName;

    public QuoteAdapter(List<QuoteModel> qList,String businessName) {
        this.qList = qList;
        this.businessName=businessName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_quotes, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(qList.get(position).getRecipientName());
        holder.refNumber.setText(qList.get(position).getJobReferenceNumber());
        holder.jobTitle.setText(qList.get(position).getJobTitle());
        holder.quoteEstimate.setText(qList.get(position).getTypeOfCost());
        holder.workSummery.setText(qList.get(position).getSummaryOfWork());
        holder.date.setText(qList.get(position).getQouteDate());

        String excludeInclude;
        if (qList.get(position).getVatRegistered().equals("Exclude")){
            excludeInclude="Exc";
        }else {
            excludeInclude="Inc";
        }
        holder.price.setText(TextUtils.isEmpty(qList.get(position).getPrice())?"":"£ "+qList.get(position).getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(new Intent(holder.itemView.getContext(), ActivityRecepient.class));
                intent.putExtra("recipientName",qList.get(position).getRecipientName());
                intent.putExtra("refNumber",qList.get(position).getJobReferenceNumber());
                intent.putExtra("phone",TextUtils.isEmpty(qList.get(position).getBusinessTelephone())?"":qList.get(position).getBusinessTelephone());
                intent.putExtra("jobTitle",qList.get(position).getJobTitle());
                intent.putExtra("quoteEstimate",qList.get(position).getTypeOfCost());
                intent.putExtra("workSummery",qList.get(position).getSummaryOfWork());
                intent.putExtra("date",qList.get(position).getQouteDate());
                intent.putExtra("vatInfo",excludeInclude);
                intent.putExtra("key",qList.get(position).getKey());
                intent.putExtra("vatRegistered",qList.get(position).getVatRegistered());
                intent.putExtra("customerId",customerId);
                intent.putExtra("businessName",businessName);
                intent.putExtra("businessEmail", TextUtils.isEmpty(qList.get(position).getBusinessEmail())?"":qList.get(position).getBusinessEmail());
                intent.putExtra("businessAddress",qList.get(position).getBusinessAddress());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.vatInfo.setText(TextUtils.isEmpty(qList.get(position).getVATInformation())?"":"VAT "+qList.get(position).getVATInformation());
    }

    @Override
    public int getItemCount() {
        return qList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, quoteEstimate, refNumber, jobTitle, workSummery, price,date,vatInfo;
        private ImageView arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rq_resName);
            quoteEstimate = itemView.findViewById(R.id.rq_quoteEstimate);
            refNumber = itemView.findViewById(R.id.rq_refNumber);
            jobTitle = itemView.findViewById(R.id.rq_jobTitle);
            workSummery = itemView.findViewById(R.id.rq_workSummery);
            price = itemView.findViewById(R.id.rq_price);
            arrow = itemView.findViewById(R.id.rq_forArrow);
            date=itemView.findViewById(R.id.rq_date);
            vatInfo=itemView.findViewById(R.id.rq_vat_info);
        }
    }
}
