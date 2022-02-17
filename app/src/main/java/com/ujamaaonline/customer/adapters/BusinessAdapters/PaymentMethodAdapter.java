package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.PaymentMethod;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.models.business_data.PaymentSection;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PayViewHolder> {

    private List<PaymentSection> pmList;

    public PaymentMethodAdapter(List<PaymentSection> pmList) {
        this.pmList = pmList;
    }

    @NonNull
    @Override
    public PayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_method, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PayViewHolder holder, int position) {
        PaymentSection paymentSection = pmList.get(position);
        Picasso.get().load(paymentSection.getPayment().getPaymentIcon()).resize(400,400).into(holder.cardImage);
    }

    @Override
    public int getItemCount() {
        return pmList.size();
    }

    public class PayViewHolder extends RecyclerView.ViewHolder {
        private ImageView cardImage;

        public PayViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.rpm_image);
        }
    }
}
