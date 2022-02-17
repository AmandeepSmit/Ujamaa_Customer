package com.ujamaaonline.customer.adapters.BusinessAdapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.AllWorkingHour;
import com.ujamaaonline.customer.models.business_data.WorkingHours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OpeningHoursAdapter extends RecyclerView.Adapter<OpeningHoursAdapter.OpeningHolder> {

    private List<AllWorkingHour> oList;
    private SimpleDateFormat oldformat=new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat newformat=new SimpleDateFormat("hh:mm a");


    public OpeningHoursAdapter(List<AllWorkingHour> oList) {
        this.oList = oList;
    }

    @NonNull
    @Override
    public OpeningHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OpeningHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_opening_hours, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OpeningHolder holder, int position) {
        holder.day.setText(oList.get(position).getDayName());
        if (oList.get(position).getDayType().equals("1")) {
            holder.time.setText("Open 24 Hours");
        }
        if (position == 0) {
            holder.day.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.adobe_clean_bold));
            holder.time.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.adobe_clean_bold));
        }
        else
        {
            holder.day.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.adobe_clean_regular));
            holder.time.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.adobe_clean_regular));
        }

        if (oList.get(position).getDayType().equals("2")) {
            holder.time.setText("Closed");
        }
        if (oList.get(position).getDayType().equals("3")) {
            holder.time.setText(convertTime(oList.get(position).getStartTime()) + "-" + convertTime(oList.get(position).getEndTime()));
        }
        if (oList.get(position).getDayType().equals("4")) {
            holder.time.setText(convertTime(oList.get(position).getStartTime()) + "-" + convertTime(oList.get(position).getEndTime()) + " & " +
                    convertTime(oList.get(position).getSecondStartTime()) + "-" + convertTime(oList.get(position).getSecondEndTime()));
        }
        if (oList.get(position).getDayType().equals("5")) {
            holder.time.setText("No Opening Hours");
        }

    }

    private String convertTime(String time)
    {
        try {
            final Date dateObj = oldformat.parse(time);
            return newformat.format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public int getItemCount() {
        return oList.size();
    }

    public class OpeningHolder extends RecyclerView.ViewHolder {
        private TextView day, time;

        public OpeningHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.roh_day);
            time = itemView.findViewById(R.id.roh_time);
        }
    }
}
