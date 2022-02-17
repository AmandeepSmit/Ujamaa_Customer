package com.ujamaaonline.customer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.event_filter.EventDataPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventFilterActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private List<EventDataPayload> filterCatList = new ArrayList<>();
    private List<EventDataPayload> filterTypeList = new ArrayList<>();
    private RecyclerView recCat, recType;
    private boolean isSeeType, isSeeCat, isMax,isChooseWhen=false;
    private TextView seeMoreCate, seeMoreType, tvMintv_choose_whenAge;
    private TextView preSortSelected, selectedFilterType, tvMinAge, selectedFilterCat, tvToday, tvTomorrow, tvThisWeek, tvWeekend, selectedEventFitler, tvChooseWhen, tvDate, tvRelevance, tvMaxSpend,tvReset;
    private SwitchCompat switchFreeEvent, switchOnlineEvent, switchhideEvent;
    private int freeEvent = 0, onlineEvent = 0, hideEvent = 0, selectedCatId = 0, filterType = 0, sortBy = 1;
    private String eventDay = "";
    private Calendar newCalendar;
    private DatePickerDialog datePickerDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);
        newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, datePickerListener, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        initView();
        if (getIntent().hasExtra("filter_cat") && getIntent().hasExtra("filter_types")) {
            filterCatList = (List<EventDataPayload>) getIntent().getSerializableExtra("filter_cat");
            filterTypeList = (List<EventDataPayload>) getIntent().getSerializableExtra("filter_types");
            if (filterCatList != null) {
                if (filterCatList.size() > 0) {
                    recCat.getAdapter().notifyDataSetChanged();
                }
                if (filterCatList.size() <= 6)
                    seeMoreCate.setVisibility(View.GONE);
            }
            if (filterTypeList != null) {
                if (filterTypeList.size() > 0) {
                    recCat.getAdapter().notifyDataSetChanged();
                }
                if (filterTypeList.size() <= 6)
                    seeMoreType.setVisibility(View.GONE);
            }
        }
    }

    private Calendar chooseWhenCalendar = Calendar.getInstance();
    private List<String> ageList = new ArrayList<>();
    private List<String> maxSpendLIst = new ArrayList<>();
    private Dialog dialog;

    DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            chooseWhenCalendar.set(Calendar.YEAR, year);
            chooseWhenCalendar.set(Calendar.MONTH, monthOfYear);
            chooseWhenCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updaeStartDate();
        }

    };

    DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            chooseWhenCalendar.set(Calendar.YEAR, year);
            chooseWhenCalendar.set(Calendar.MONTH, monthOfYear);
            chooseWhenCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updaeEndDate();
        }
    };

    private void ageDialog() {
        dialog = new Dialog(EventFilterActivity.this);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_age_select);
        RecyclerView ageRecyler = dialog.findViewById(R.id.ra_ageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EventFilterActivity.this);
        ageRecyler.setLayoutManager(linearLayoutManager);
        ageRecyler.setHasFixedSize(true);
        AgeAdapter ageAdapter = new AgeAdapter(ageList);
        ageRecyler.setAdapter(ageAdapter);
        dialog.show();
    }

    LayoutInflater li;
    View chooseWhenDialogView;
    AlertDialog chooseWhenDialog;
    TextView tvStartDateChooseWhen, tvEndDateChooseWhen;
    private void showDiscardDialog() {
        chooseWhenDialog.show();
    }

    private void addValue() {
        ageList.add("None");
        for (int i = 10; i < 100; i++) {
            ageList.add(String.valueOf(i));
        }
    }

    private void addMaxSpendValue() {
        maxSpendLIst.add("None");
        for (int i = 10; i < 200; i++) {
            if (i%10==0)
                maxSpendLIst.add(String.valueOf(i));
        }
    }

    private void maxSpendDialog() {
        dialog = new Dialog(EventFilterActivity.this);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_age_select);
        RecyclerView ageRecyler = dialog.findViewById(R.id.ra_ageRecycler);
        TextView tvTitle=dialog.findViewById(R.id.ageText);
        tvTitle.setText("Max Spend");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EventFilterActivity.this);
        ageRecyler.setLayoutManager(linearLayoutManager);
        ageRecyler.setHasFixedSize(true);
        AgeAdapter ageAdapter = new AgeAdapter(maxSpendLIst);
        ageRecyler.setAdapter(ageAdapter);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            eventTabSelection(tvChooseWhen,tvChooseWhen.getText().toString());
            tvChooseWhen.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);
        }
    };

    private void initView() {
        recCat = findViewById(R.id.rec_profile_filter);
        recType = findViewById(R.id.rec_event_type);
        seeMoreCate = findViewById(R.id.see_more_cat);
        seeMoreType = findViewById(R.id.see_more_type);
        tvMaxSpend = findViewById(R.id.tv_max_spend);
        tvMaxSpend.setOnClickListener(this);

        tvDate = findViewById(R.id.tv_discount_deals);
        tvDate.setOnClickListener(this);
        tvRelevance = findViewById(R.id.tv_general_info);
        tvRelevance.setOnClickListener(this);
        switchFreeEvent = findViewById(R.id.swch_free_events);


        switchOnlineEvent = findViewById(R.id.swch_online_events);




        switchhideEvent = findViewById(R.id.swch_hide_events);


        tvChooseWhen = findViewById(R.id.tv_choose_when);
        tvChooseWhen.setOnClickListener(this);
        tvToday = findViewById(R.id.tv_today);
        tvToday.setOnClickListener(this);
        tvTomorrow = findViewById(R.id.tv_tomorrow);
        tvTomorrow.setOnClickListener(this);
        tvThisWeek = findViewById(R.id.tv_this_week);
        tvThisWeek.setOnClickListener(this);
        tvWeekend = findViewById(R.id.tv_this_weekend);
        tvWeekend.setOnClickListener(this);
        switchFreeEvent.setOnCheckedChangeListener(this);
        switchOnlineEvent.setOnCheckedChangeListener(this);
        switchhideEvent.setOnCheckedChangeListener(this);
        tvMinAge = findViewById(R.id.tv_min_age);
        tvMinAge.setOnClickListener(this);
        seeMoreType.setOnClickListener(this);
        seeMoreCate.setOnClickListener(this);
        recType.setHasFixedSize(true);
        recCat.setHasFixedSize(true);
        recCat.setAdapter(new FilterAdapter());
        recType.setAdapter(new FilterTypeAdapter());
        createDialogView();
        addValue();
        addMaxSpendValue();
    }

    private void createDialogView() {
        li = LayoutInflater.from(EventFilterActivity.this);
        chooseWhenDialogView = li.inflate(R.layout.layout_choose_when, null);
        chooseWhenDialog = new AlertDialog.Builder(EventFilterActivity.this).setView(chooseWhenDialogView).setCancelable(false).create();
        tvStartDateChooseWhen = chooseWhenDialogView.findViewById(R.id.tv_start_date);
        tvEndDateChooseWhen = chooseWhenDialogView.findViewById(R.id.tv_end_date);
        tvStartDateChooseWhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventFilterActivity.this, startDate, chooseWhenCalendar
                        .get(Calendar.YEAR), chooseWhenCalendar.get(Calendar.MONTH),
                        chooseWhenCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tvEndDateChooseWhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStartDateChooseWhen.getText().toString().equalsIgnoreCase("Select Start Date")) {
                    new DatePickerDialog(EventFilterActivity.this, endDate, chooseWhenCalendar
                            .get(Calendar.YEAR), chooseWhenCalendar.get(Calendar.MONTH),
                            chooseWhenCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } else
                    Toast.makeText(EventFilterActivity.this, "please select start Date", Toast.LENGTH_SHORT).show();
            }
        });
        chooseWhenDialogView.findViewById(R.id.tv_discard_changes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvStartDateChooseWhen.getText().toString().equalsIgnoreCase("Select Start Date")) {
                    Toast.makeText(EventFilterActivity.this, "please select start date", Toast.LENGTH_SHORT).show();
                } else if (tvEndDateChooseWhen.getText().toString().equalsIgnoreCase("Select End Date")) {
                    Toast.makeText(EventFilterActivity.this, "please select end date", Toast.LENGTH_SHORT).show();
                } else {

                    tvChooseWhen.setText(tvStartDateChooseWhen.getText().toString()+" To "+tvEndDateChooseWhen.getText().toString());
                    eventTabSelection(tvChooseWhen,tvChooseWhen.getText().toString());
                    chooseWhenDialog.dismiss();
                }
            }
        });

        chooseWhenDialogView.findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseWhenDialog.dismiss();
            }
        });
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    private void updaeStartDate() {
        tvEndDateChooseWhen.setText("Select End Date");
        tvStartDateChooseWhen.setText(sdf.format(chooseWhenCalendar.getTime()));
    }

    private void updaeEndDate() {
        String myFormat = "MM/dd/yy"; //In which you need put here

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        Date endDate = null;
        try {
            strDate = sdf.parse(tvStartDateChooseWhen.getText().toString());
            endDate = sdf.parse(sdf.format(chooseWhenCalendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (strDate.after(endDate)) {
            Toast.makeText(this, "End date should be greater than start date", Toast.LENGTH_SHORT).show();
            return;
        }
        tvEndDateChooseWhen.setText(sdf.format(chooseWhenCalendar.getTime()));
    }

    public void resetBtnClick(View view) {

        Intent intent = new Intent();
        intent.putExtra("reset", true);
        setResult(RESULT_OK, intent);
        finish();

    }

    public void applyBtnClick(View view) {

        if (eventDay.contains(" To "))
        {
            String startDate=parseDateToddMMyyyy(eventDay.split("To")[0].trim());
            String endDate=parseDateToddMMyyyy(eventDay.split("To")[1].trim());
            eventDay=startDate+" To "+endDate;
        }

        Intent intent = new Intent();
        intent.putExtra("sort_by", sortBy);
        intent.putExtra("cat_id", selectedCatId);
        intent.putExtra("filter_type", filterType);
        intent.putExtra("event", TextUtils.isEmpty(eventDay) ? "" : eventDay);

        intent.putExtra("str_end_date", TextUtils.isEmpty(eventDay) ? "" : eventDay);
        if (tvMinAge.getText().toString().contains(":"))
            intent.putExtra("min_age", tvMinAge.getText().toString().split(":")[1]);
        if (tvMaxSpend.getText().toString().contains(":"))
            intent.putExtra("max_age", tvMaxSpend.getText().toString().split(":")[1]);
        intent.putExtra("free_event", freeEvent);
        intent.putExtra("online_event", onlineEvent);
        intent.putExtra("hide_event", hideEvent);
        intent.putExtra("apply", "");
        setResult(RESULT_OK, intent);
        finish();
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd/MM/yyyy";
        String outputPattern = " yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    private void eventTabSelection(TextView newTab,String selcted) {
        eventDay = selcted;
        if (selectedEventFitler == null) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            selectedEventFitler = newTab;
        } else if (selectedEventFitler.equals(newTab)){
            eventDay = "";
            selectedEventFitler = null;

        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            selectedEventFitler.setBackgroundColor(getResources().getColor(R.color.white));
            selectedEventFitler.setTextColor(getResources().getColor(R.color.dark_gray));
            selectedEventFitler = newTab;
        }
    }

    private void sortTabSelection(TextView newTab) {
        if (preSortSelected == null) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            preSortSelected = newTab;
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            preSortSelected.setBackgroundColor(getResources().getColor(R.color.white));
            preSortSelected.setTextColor(getResources().getColor(R.color.dark_gray));
            preSortSelected = newTab;
        }
    }

    private void typeSelection(TextView newTab, int position) {
        if (selectedFilterType == null) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            selectedFilterType = newTab;
            filterType = filterTypeList.get(position).getId();
        } else if (selectedFilterType.equals(newTab)) {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.dark_gray));
            filterType = 0;
            selectedFilterType = null;
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            selectedFilterType.setBackgroundColor(getResources().getColor(R.color.white));
            selectedFilterType.setTextColor(getResources().getColor(R.color.dark_gray));
            selectedFilterType = newTab;
            filterType = filterTypeList.get(position).getId();
        }
    }

    private void catSelection(TextView newTab, int position) {
        if (selectedFilterCat == null) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            selectedFilterCat = newTab;
        } else if (selectedFilterCat.equals(newTab)) {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.dark_gray));
            selectedCatId = 0;
            selectedFilterCat = null;
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
            selectedFilterCat.setBackgroundColor(getResources().getColor(R.color.white));
            selectedFilterCat.setTextColor(getResources().getColor(R.color.dark_gray));
            selectedFilterCat = newTab;
            selectedCatId = filterCatList.get(position).getId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.see_more_cat:
                if (isSeeCat) {
                    isSeeCat = false;
                    seeMoreCate.setText("SEE MORE");
                } else {
                    isSeeCat = true;
                    seeMoreCate.setText("SEE LESS");
                }
                recCat.getAdapter().notifyDataSetChanged();
                break;
            case R.id.see_more_type:
                if (isSeeType) {
                    isSeeType = false;
                    seeMoreType.setText("SEE MORE");
                } else {
                    isSeeType = true;
                    seeMoreType.setText("SEE LESS");
                }
                recType.getAdapter().notifyDataSetChanged();
                break;
            case R.id.tv_discount_deals:
                sortTabSelection(tvDate);
                break;
            case R.id.tv_general_info:
                sortTabSelection(tvRelevance);
                break;
            case R.id.tv_choose_when:
                showDiscardDialog();
                break;
            case R.id.tv_min_age:
                isMax = false;
                ageDialog();
                break;
            case R.id.tv_max_spend:
                isMax = true;
                maxSpendDialog();
                break;
            case R.id.tv_today:
                eventTabSelection(tvToday,tvToday.getText().toString());
                break;
            case R.id.tv_tomorrow:
                eventTabSelection(tvTomorrow,tvTomorrow.getText().toString());
                break;
            case R.id.tv_this_week:
                eventTabSelection(tvThisWeek,"this_week");
                break;
            case R.id.tv_this_weekend:
                eventTabSelection(tvWeekend,"this_weekend");
                break;
        }
    }


    private void selectedAge(TextView newTab, boolean isDeSelected) {
        if (isDeSelected) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void selectedMaxAge(TextView newTab, boolean isDeSelected) {
        if (isDeSelected) {
            newTab.setBackgroundColor(getResources().getColor(R.color.black));
            newTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            newTab.setBackgroundColor(getResources().getColor(R.color.white));
            newTab.setTextColor(getResources().getColor(R.color.black));
        }
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.swch_free_events:
                if (switchOnlineEvent.isChecked()||switchhideEvent.isChecked())
                {
                    switchFreeEvent.setChecked(false);
                    Toast.makeText(EventFilterActivity.this, "you can select only one option", Toast.LENGTH_SHORT).show();
                    return;
                }
                freeEvent = isChecked ? 1 : 0;
                break;
            case R.id.swch_online_events:
                if (switchFreeEvent.isChecked()||switchhideEvent.isChecked())
                {
                    switchOnlineEvent.setChecked(false);
                    Toast.makeText(EventFilterActivity.this, "you can select only one option", Toast.LENGTH_SHORT).show();
                    return;
                }
                onlineEvent = isChecked ? 1 : 0;
                break;
            case R.id.swch_hide_events:
                if (switchFreeEvent.isChecked()||switchOnlineEvent.isChecked())
                {
                    switchhideEvent.setChecked(false);
                    Toast.makeText(EventFilterActivity.this, "you can select only one option", Toast.LENGTH_SHORT).show();
                    return;
                }
                hideEvent = isChecked ? 1 : 0;
                break;
        }
    }

//todo =======================  Reviews Filter Adapter ===========================

    class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {
        @NonNull
        @Override
        public FilterAdapter.FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FilterAdapter.FilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile_filter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FilterAdapter.FilterViewHolder holder, int position) {
            EventDataPayload filterCatPayload = filterCatList.get(position);
            holder.tvCatName.setText(TextUtils.isEmpty(filterCatPayload.getName()) ? "" : filterCatPayload.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    catSelection(holder.tvCatName, position);

                }
            });
        }

        @Override
        public int getItemCount() {

            return filterCatList.size() < 6 ? filterCatList.size() : filterCatList.size() > 6 && !isSeeCat ? 6 : filterCatList.size() > 6 && isSeeCat ? filterCatList.size() : 0;
        }

        class FilterViewHolder extends RecyclerView.ViewHolder {

            TextView tvCatName;

            public FilterViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_cat_name);
            }
        }
    }

    private void loadReportimgDialog(String url, ImageView img) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.ic_palceholder)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        img.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    //todo =======================  Reviews Filter  Type Adapter ===========================

    class FilterTypeAdapter extends RecyclerView.Adapter<FilterTypeAdapter.FilterViewHolder> {
        @NonNull
        @Override
        public FilterTypeAdapter.FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FilterTypeAdapter.FilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile_filter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FilterTypeAdapter.FilterViewHolder holder, int position) {
            EventDataPayload filterCatPayload = filterTypeList.get(position);
            holder.tvCatName.setText(TextUtils.isEmpty(filterCatPayload.getName()) ? "" : filterCatPayload.getName());
            if (filterTypeList.get(position).isChecked()) {
//                seletedMultipletab(holder.tvCatName, position);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeSelection(holder.tvCatName, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filterTypeList.size() < 6 ? filterTypeList.size() : filterTypeList.size() > 6 && !isSeeType ? 6 : filterTypeList.size() > 6 && isSeeType ? filterTypeList.size() : 0;
        }

        class FilterViewHolder extends RecyclerView.ViewHolder {

            TextView tvCatName;

            public FilterViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_cat_name);
            }
        }
    }


    //todo==================================Age Adapter=========================
    public class AgeAdapter extends RecyclerView.Adapter<AgeAdapter.AgeHolder> {
        private List<String> ageModel;

        public AgeAdapter(List<String> ageModel) {
            this.ageModel = ageModel;
        }

        @NonNull
        @Override
        public AgeAdapter.AgeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AgeAdapter.AgeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_age, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AgeAdapter.AgeHolder holder, int position) {
            holder.age.setText(ageModel.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ageModel.get(position).equalsIgnoreCase("None")) {
                        if (!isMax) {
                            selectedAge(tvMinAge, false);
                            tvMinAge.setText("Age Restrictions...");
                        } else {
                            selectedMaxAge(tvMaxSpend, false);
                            tvMaxSpend.setText("Max Spend...");
                        }
                    } else {
                        if (!isMax) {
                            selectedAge(tvMinAge, true);
                            tvMinAge.setText("Age Restrictions:" + ageModel.get(position));
                        } else {
                            selectedAge(tvMaxSpend, true);
                            tvMaxSpend.setText("Max Spend:" + ageModel.get(position));
                        }
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return ageModel.size();
        }

        public class AgeHolder extends RecyclerView.ViewHolder {
            TextView age;

            public AgeHolder(@NonNull View itemView) {
                super(itemView);
                age = itemView.findViewById(R.id.ageText);
            }
        }
    }
}