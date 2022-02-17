package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.ProductService;

import java.util.List;

public class ProductServiceAdapter extends RecyclerView.Adapter<ProductServiceAdapter.ProductViewHolder>{

    private List<ProductService> pList;
    private boolean isViewAll;

    public ProductServiceAdapter(List<ProductService> pList,boolean isViewAll) {
        this.pList = pList;
        this.isViewAll = isViewAll;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_services,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.proName.setText(pList.get(position).getName());


        if (!TextUtils.isEmpty(pList.get(position).getPrice())){
            holder.price.setText(String.valueOf("£"+pList.get(position).getPrice()));

        }
        else
        {
            holder.price.setText("£0.0");
        }
    }
    public void setViewAll(boolean isViewAll) {
        this.isViewAll = isViewAll;
    }
    @Override
    public int getItemCount() {
        return !isViewAll ? pList.size() > 3 ? 3 : pList.size() : pList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private TextView proName,price;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            proName=itemView.findViewById(R.id.rps_product);
            price=itemView.findViewById(R.id.rps_price);
        }
    }
}
