package com.ujamaaonline.customer.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.EventFilterActivity;
import com.ujamaaonline.customer.activities.ProfileFilterActivity;
import com.ujamaaonline.customer.adapters.EventPostAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.DaysModel;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.event_filter.EventDataPayload;
import com.ujamaaonline.customer.models.event_filter.EventFilterCatResponse;
import com.ujamaaonline.customer.models.event_filter.FilterEventRequest;
import com.ujamaaonline.customer.models.feed_models.EventPayload;
import com.ujamaaonline.customer.models.feed_models.FeedLocationFilterRequest;
import com.ujamaaonline.customer.models.feed_models.FeedModel;
import com.ujamaaonline.customer.models.feed_models.GetEventsDateResponse;
import com.ujamaaonline.customer.models.feed_models.GetPostRequest;
import com.ujamaaonline.customer.models.feed_models.PostPayload;
import com.ujamaaonline.customer.models.feed_models.PostResponse;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static android.app.Activity.RESULT_OK;
import static com.ujamaaonline.customer.utils.Constants.BEARER;

public class EventPostFragment extends Fragment implements View.OnClickListener {
    private RecyclerView epRecyclerView;
    private List<DaysModel> dateList = new ArrayList<>();
    private List<EventPayload> eventFeedList = new ArrayList<>();
    private RecyclerView recCalendar;
    private Calendar calendar;
    private Calendar nextMonthCalender;
    private TextView tvMonthName, tvNextMonth, tvPreMonth, tvChange, btnResetPostCode, btnResetMiles, btnSearch,tvFilter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE M/d/yyyy");
    private SimpleDateFormat displaysdf = new SimpleDateFormat("MMM/d/yyyy");
    private String currentMonth;
    private ImageView imgPre, imgRight, imgCal,crossIcon;
    private int tapCount = 0;
    private LinearLayout calendarLayout;
    private String currenDate;
    private EditText etPostCode, etMiles, etSearch;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private Spinner spSpinner;
    private List<Integer> selectedCatList = new ArrayList<>();
    private List<EventDataPayload> filterCatList = new ArrayList<>();
    private List<EventDataPayload> filterCatListTemp = new ArrayList<>();
    private List<EventDataPayload> filterTypeList = new ArrayList<>();
    private List<EventDataPayload> filterTypeListTemp = new ArrayList<>();
    private String sort = "0";
    private String filter = "0";
    private String prePostCode = "";
    private String latitude, longitude;
    private String userPostCode = "";
    private String preMiles = "5";
    private boolean isLocationAdded=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_post, container, false);
        initViews(view);
        return view;
    }
    private void initViews(View view) {
        getActivity().registerReceiver(receiver, new IntentFilter("update_event"));
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        this.userPostCode = this.loginPreferences.getString(Constants.USER_POST_CODE, "");
        prePostCode = userPostCode;
        epRecyclerView = view.findViewById(R.id.fep_recycler);
        spSpinner = view.findViewById(R.id.flp_spinner);
        view.findViewById(R.id.tv_change).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        epRecyclerView.setLayoutManager(linearLayoutManager);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        etPostCode = view.findViewById(R.id.et_postal_code);
        btnResetMiles = view.findViewById(R.id.btn_rest_miles);
        btnResetMiles.setOnClickListener(this);
        etSearch = view.findViewById(R.id.et_search);
        tvPreMonth = view.findViewById(R.id.tv_pre);
        etMiles = view.findViewById(R.id.et_miles);
        imgPre = view.findViewById(R.id.img_pre);
        crossIcon=view.findViewById(R.id.fep_cross);
        crossIcon.setOnClickListener(this);
        imgRight = view.findViewById(R.id.right_arrow);
        imgRight.setOnClickListener(this);
        imgPre.setOnClickListener(this);
        imgCal = view.findViewById(R.id.img_cal);
        imgCal.setOnClickListener(this);
        calendar = Calendar.getInstance();
        nextMonthCalender = Calendar.getInstance();
        currenDate = simpleDateFormat.format(new Date());
        currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        calendarLayout = view.findViewById(R.id.calendar_layout);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        recCalendar = view.findViewById(R.id.rec_calendar_view);
        tvNextMonth = view.findViewById(R.id.tv_next_month);
        tvMonthName = view.findViewById(R.id.tv_month_name);
        recCalendar.setHasFixedSize(true);
        recCalendar.setAdapter(new CalendarAdapter());
        tvFilter=view.findViewById(R.id.tv_filter);
        tvFilter.setOnClickListener(this);
        loadData();
        EventPostAdapter adapter = new EventPostAdapter(eventFeedList, getActivity(),this);
        String[] arraySpinner = new String[]{"All Events", "Liked Events", "Hidden Events"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.custome_spinner, arraySpinner);
        adapter1.setDropDownViewResource(R.layout.custome_spinner);

        spSpinner.setAdapter(adapter1);
        spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventFeedList.clear();
                switch (position){
                    case 0:
                        getAllEvents(1,0,0,"","","",false);
                        break;
                    case 1:
                        getAllEvents(0,1,0,"","","",false);
                        break;
                    case 2:
                        getAllEvents(0,0,1,"","","",false);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        epRecyclerView.setAdapter(adapter);
        if (!TextUtils.isEmpty(userPostCode)){
            etPostCode.setText(userPostCode);
        }
        eventCat();
        getType();
        getLatLong(etPostCode.getText().toString(), true,false);
//        getLatLong(etPostCode.getText().toString(),true,true);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAllEvents(1,0,0,"","","",false);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private void getType() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        apiInterface.getEventFilterCat("event_type").enqueue(new Callback<EventFilterCatResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<EventFilterCatResponse> call, Response<EventFilterCatResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()){
                        if (response.body().getDataPayload().size() > 0) {
                            filterTypeList.addAll(response.body().getDataPayload());
                            filterTypeListTemp.addAll(response.body().getDataPayload());
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<EventFilterCatResponse> call, Throwable t) {
            }
        });
    }

    private void eventCat(){
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        apiInterface.getEventFilterCat("event_cat").enqueue(new Callback<EventFilterCatResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<EventFilterCatResponse> call, Response<EventFilterCatResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getDataPayload().size() > 0) {
                            filterCatList.addAll(response.body().getDataPayload());
                            filterCatListTemp.addAll(response.body().getDataPayload());
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<EventFilterCatResponse> call, Throwable t) {
            }
        });
    }

    private void loadData() {
        dateList.clear();
        String date = new SimpleDateFormat("EEE MM/dd/yyyy").format(calendar.getTime());
        getEventsDate(true, date);
        String display = displaysdf.format(calendar.getTime());
        nextMonthCalender.set(Calendar.MONTH, Integer.valueOf(currentMonth));
        tvMonthName.setText(display.split("/")[0] + " " + display.split("/")[2]);
        tvNextMonth.setText(displaysdf.format(nextMonthCalender.getTime()).split("/")[0]);
        nextMonthCalender.set(Calendar.MONTH, Integer.valueOf(currentMonth) - 2);
        tvPreMonth.setText(displaysdf.format(nextMonthCalender.getTime()).split("/")[0]);
        addWeekDays();
        if (startDate(date.split(" ")[0]) > 0) {
            for (int i = 1; i <= startDate(getFirstDayOfMonth()); i++) {
                dateList.add(new DaysModel("", ""));
            }
        }
        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            dateList.add(new DaysModel(getFirstDayOfMonth(), simpleDateFormat.format(calendar.getTime()).split(" ")[1]));
        }
        int lastDate = dateList.size() - 1;
        if (getLastWeekDayName(dateList.get(lastDate).dayName) > 0) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);

            for (int i = 1; i <= getLastWeekDayName(dateList.get(lastDate).dayName); i++) {
                calendar.set(Calendar.DAY_OF_MONTH, i);
                dateList.add(new DaysModel(getFirstDayOfMonth(), simpleDateFormat.format(calendar.getTime()).split(" ")[1]));
            }
        }
        recCalendar.setAdapter(new CalendarAdapter());
    }
    private String getFirstDayOfMonth() {
        return simpleDateFormat.format(calendar.getTime()).split(" ")[0];
    }
    private int startDate(String day) {
        switch (day) {
            case "Sun":
                return 0;
            case "Mon":
                return 1;
            case "Tue":
                return 2;
            case "Wed":
                return 3;
            case "Thu":
                return 4;
            case "Fri":
                return 5;
            case "Sat":
                return 6;
        }
        return 0;
    }
    private int getLastWeekDayName(String day) {
        switch (day) {
            case "Sun":
                return 6;
            case "Mon":
                return 5;
            case "Tue":
                return 4;
            case "Wed":
                return 3;
            case "Thu":
                return 2;
            case "Fri":
                return 1;
        }
        return 0;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 202) {
           if (data.hasExtra("reset"))
           {
               getAllEvents(1,0,0,"","","",false);
           }
           else if (data.hasExtra("apply"))
           {
               getAllEvents(0,0,0,"","","",true,new  FilterEventRequest(data.getIntExtra("cat_id",0),
                       data.hasExtra("min_age")?Integer.valueOf(data.getStringExtra("min_age")):0,data.hasExtra("max_age")?Integer.valueOf(data.getStringExtra("max_age")):0,
                       data.getStringExtra("event").contains(" To ")?data.getStringExtra("event").split("To")[0].trim():data.getStringExtra("event").toLowerCase(),data.getIntExtra("filter_type",0),data.getIntExtra("free_event",0),data.getIntExtra("hide_event",0),data.getIntExtra("online_event",0), data.getStringExtra("event").contains(" To ")?data.getStringExtra("event").split("To")[1].trim():null
                       , data.getIntExtra("sort_by",1)));
           }
        }
    }
    private void addWeekDays() {
        dateList.add(new DaysModel("", "S"));
        dateList.add(new DaysModel("", "M"));
        dateList.add(new DaysModel("", "T"));
        dateList.add(new DaysModel("", "W"));
        dateList.add(new DaysModel("", "T"));
        dateList.add(new DaysModel("", "F"));
        dateList.add(new DaysModel("", "S"));
    }
    private void getNextMonth() {
        imgPre.setVisibility(View.VISIBLE);
        tvPreMonth.setVisibility(View.VISIBLE);
        tapCount = tapCount + 1;
        if (Integer.valueOf(currentMonth).equals(calendar.get(Calendar.MONTH) + 1)) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        tvMonthName.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        loadData();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_pre:
                tapCount = tapCount - 1;
                if (tapCount == 0) {
                    imgPre.setVisibility(View.INVISIBLE);
                    tvPreMonth.setVisibility(View.INVISIBLE);
                }
                if (calendar.get(Calendar.MONTH) + 1 > Integer.valueOf(currentMonth) || calendar.get(Calendar.MONTH) == 0 && Integer.valueOf(currentMonth) == 12)
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 2);
                else if (Integer.valueOf(currentMonth).equals(calendar.get(Calendar.MONTH) + 1))
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                loadData();
                break;
            case R.id.right_arrow:
                getNextMonth();
                break;
            case R.id.img_cal:
                if (calendarLayout.getVisibility() == View.VISIBLE)
                    calendarLayout.setVisibility(View.GONE);
                else calendarLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_filter:
                startActivityForResult(new Intent(getActivity(), EventFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList).putExtra("filter_types", (Serializable) filterTypeList).putExtra("from_feed", ""), 202);
                break;
            case R.id.tv_change:
                getLocationFilter();
                break;
            case R.id.btn_reset_post_code:
                etPostCode.setText(userPostCode);
                getLocationFilter();
                break;
            case R.id.btn_rest_miles:
                etMiles.setText("5");
                getLocationFilter();
                break;
            case R.id.btn_search:
                if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                    searchHashTag(etSearch.getText().toString());
                }
                break;
            case R.id.fep_cross:
                etSearch.setText(null);
                break;
        }
    }
    private void searchHashTag(String searchKeyword) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetPostRequest getFeedRequest = new GetPostRequest(0, 0, 1, searchKeyword);
        apiInterface.searchEvents(Constants.BEARER.concat(this.headerToken), getFeedRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                eventFeedList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if(response.body().getEventPayload()!=null)
                        eventFeedList.addAll(response.body().getEventPayload());
                    } else {
//                        Toast.makeText(getActivity(), "No Post available", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

                }
                progress.hideProgressBar();
                epRecyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getLocationFilter() {
        if (TextUtils.isEmpty(etPostCode.getText().toString().trim())) {
            Toast.makeText(getActivity(), "please enter post code", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(etMiles.getText().toString().trim())) {
            Toast.makeText(getActivity(), "please enter miles", Toast.LENGTH_SHORT).show();
            return;
        } else if (!prePostCode.equalsIgnoreCase(etPostCode.getText().toString()) || !preMiles.equalsIgnoreCase(etMiles.getText().toString())) {
            getLatLong(etPostCode.getText().toString(), true,false);
        }
    }

    private void getLocationFilter(int miles, String lat, String lng) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        FeedLocationFilterRequest locationFilterRequest = new FeedLocationFilterRequest(lat, lng, miles, 1, 0);
        apiInterface.getLocationFilter(Constants.BEARER.concat(this.headerToken), locationFilterRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                eventFeedList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        eventFeedList.addAll(response.body().getEventPayload());
                    } else {
                        Toast.makeText(getActivity(), "No Post available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                epRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLatLong(String postCode, Boolean isProgress,boolean isFirst) {
        isLocationAdded=true;
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        if (ObjectUtil.isNonEmptyStr(this.headerToken)) {
            if (isProgress)
                progress.showProgressBar();
            apiInterface.getLatLng("https://postcodes.io/postcodes/" + postCode).enqueue(new Callback<GetLatLngResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetLatLngResponse> call, Response<GetLatLngResponse> response) {
                    if (progress.isShowing())
                        progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        GetLatLngResponse editCatResponse = response.body();
                        if (!ObjectUtil.isEmpty(editCatResponse)) {
                            if (editCatResponse.getStatus() == 200) {
                                if ((editCatResponse.getResult().getLatitude() != null) && (editCatResponse.getResult().getLongitude() != null)) {
                                    latitude = String.valueOf(editCatResponse.getResult().getLatitude());
                                    longitude = String.valueOf(editCatResponse.getResult().getLongitude());
                                    prePostCode = etPostCode.getText().toString();
                                    preMiles = etMiles.getText().toString();
                                    if (isFirst)
                                        getAllEvents(1,0,0,latitude,longitude,preMiles,false);
                                    else
                                    getLocationFilter(Integer.valueOf(etMiles.getText().toString().trim()), latitude, longitude);
                                }
                            } else {
                                if (!TextUtils.isEmpty(editCatResponse.getError()))
                                    Toast.makeText(getActivity(), editCatResponse.getError(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (!ObjectUtil.isEmpty(response.errorBody())) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String errorMessage = jsonObject.getString("error");
                            if (!TextUtils.isEmpty(errorMessage)) {
                                etPostCode.setError(errorMessage);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPostCode.getRootView().getWindowToken(), 0);
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetLatLngResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        if (progress.isShowing())
                            progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etPostCode.getRootView().getWindowToken(), 0);
                    }
                }
            });
        }
    }
    private void getEventsDate(boolean isProgress, String date) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setMonth(date.split("/")[0].split(" ")[1]);
        loginRequest.setYear(date.split("/")[2]);
//        loginRequest.setMonth("4");
//        loginRequest.setYear("2021");

        if (!ObjectUtil.isEmpty(loginRequest)) {
            if (isProgress)
                progress.showProgressBar();
            apiInterface.getEventsDate(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<GetEventsDateResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetEventsDateResponse> call, Response<GetEventsDateResponse> response) {
                    if (isProgress)
                        progress.hideProgressBar();
                   if(response.isSuccessful())
                   {
                        if (response.body().getStatus()) {
                        if (response.body().getEventDatePayload() != null) {
                            if (response.body().getEventDatePayload().size() > 0) {
                                for (String date : response.body().getEventDatePayload()) {
                                    for (int i = 0; i < dateList.size(); i++) {
                                        if (dateList.get(i).date.contains("/")) {
                                            if ((date.split("-")[2].equalsIgnoreCase(dateList.get(i).date.split("/")[1].length() == 1 ? "0" + dateList.get(i).date.split("/")[1] : dateList.get(i).date.split("/")[1])) &&
                                                    date.split("-")[1].equalsIgnoreCase(dateList.get(i).date.split("/")[0].length() == 1 ? "0" + dateList.get(i).date.split("/")[0] : dateList.get(i).date.split("/")[0])) {
                                                dateList.get(i).setEvent(true);
                                            }
                                        }
                                    }
                                }
                                recCalendar.getAdapter().notifyDataSetChanged();
                            }
                        }
                    }
                   }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetEventsDateResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        if (isProgress)
                            progress.hideProgressBar();
                    }
                }
            });
        }
    }
    public void getAllEvents(int isAll,int isLiked, int isHidden,String lat, String lng, String miles,boolean isFilter,FilterEventRequest... filterEventRequests) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();

        FilterEventRequest filterEventRequest=null;
        if (!isFilter)
            filterEventRequest= new FilterEventRequest(isAll,isLiked,isHidden,latitude,longitude,preMiles);
        else
            filterEventRequest=filterEventRequests[0];

        apiInterface.getEvents(!isFilter?"customer_events":"event_filter_customer",Constants.BEARER.concat(this.headerToken), filterEventRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                eventFeedList.clear();
                if (response.isSuccessful()) {
                    tvFilter.setText("Filter");
                    if(response.body().getFilter_count()!=null)
                    {
                        if (response.body().getFilter_count()>0)
                        {
                            tvFilter.setText("Filter("+response.body().getFilter_count()+")");
                        }
                    }

                    if (response.body().getStatus()) {
                        if (response.body().getEventPayload().size() > 0) {
                            eventFeedList.addAll(response.body().getEventPayload());
                        }
                    } else {
//                        Toast.makeText(getActivity(), "No Post available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

                }
                progress.hideProgressBar();
                epRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFilterEvents(int discount, int important, String catId, int general) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetBusinessRequest request = new GetBusinessRequest();
        request.setIsLocal(0);
        request.setIsSubscriber(0);
        request.setIsEvent(1);
        request.setIsDiscount(discount);
        request.setIsImportant(important);
        request.setIsGeneral(general);
        request.setCategoryId(catId);

        apiInterface.getFilterEvents(Constants.BEARER.concat(this.headerToken), request).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                eventFeedList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getEventPayload().size() > 0) {
                            eventFeedList.addAll(response.body().getEventPayload());
                        }
                    } else {
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                epRecyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getEventByDate(String date) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setDate(date);
        loginRequest.setIs_liked(0);
        loginRequest.setIs_hidden(0);
        loginRequest.setIs_all(1);

        apiInterface.getEventByDate(Constants.BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                eventFeedList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getEventPayload().size() > 0) {
                            eventFeedList.addAll(response.body().getEventPayload());
                        }

                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

                }
                progress.hideProgressBar();
                epRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //todo ================= calendar Adapter =================

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

        CardView previousCard;
        TextView previusText;
        int previousPosition;
        private boolean isCurrentdate=false;

        @NonNull
        @Override
        public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CalendarViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calendar, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

                if(currenDate.split(" ")[1].equalsIgnoreCase(dateList.get(position).date))
                    isCurrentdate=true;

           if (isCurrentdate)
           {
                if (dateList.get(position).isEvent()) {
                holder.eventView.setVisibility(View.VISIBLE);
            } else holder.eventView.setVisibility(View.INVISIBLE);
           }

            if ((currentMonth.equalsIgnoreCase(dateList.get(position).date.split("/")[0])) && (dateList.get(position).date.contains("/"))) {
                holder.tvDate.setTextColor(getResources().getColor(R.color.black));
            } else holder.tvDate.setTextColor(getResources().getColor(R.color.gray_light));

            if (currenDate.split(" ")[1].equalsIgnoreCase(dateList.get(position).date)) {
                holder.tvDate.setTextColor(getResources().getColor(R.color.yellow_dark));
            }

            holder.tvDate.setText(dateList.get(position).date.contains("/") ? dateList.get(position).date.split("/")[1] : dateList.get(position).date);
            if (isCurrentdate)
            {
                if (!TextUtils.isEmpty(dateList.get(position).date) && dateList.get(position).date.contains("/")) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (previousCard == null) {
                                holder.cardBg.setCardBackgroundColor(getResources().getColor(R.color.black));
                                holder.tvDate.setTextColor(getResources().getColor(R.color.white));
                                previousCard = holder.cardBg;
                                previusText = holder.tvDate;
                                previousPosition = position;
                            } else {

                                holder.cardBg.setCardBackgroundColor(getResources().getColor(R.color.black));
                                holder.tvDate.setTextColor(getResources().getColor(R.color.white));
                                previousCard.setCardBackgroundColor(getResources().getColor(R.color.gray_bg));
                                if ((currentMonth.equalsIgnoreCase(dateList.get(previousPosition).date.split("/")[0]))) {
                                    previusText.setTextColor(getResources().getColor(R.color.black));
                                    if (currenDate.split(" ")[1].equalsIgnoreCase(dateList.get(previousPosition).date)) {
                                        previusText.setTextColor(getResources().getColor(R.color.yellow_dark));
                                    }
                                } else
                                    previusText.setTextColor(getResources().getColor(R.color.gray_light));
                                previousCard = holder.cardBg;
                                previusText = holder.tvDate;
                                previousPosition = position;
                            }
                            if ((!currentMonth.equalsIgnoreCase(dateList.get(position).date.split("/")[0])) && (dateList.get(position).date.contains("/"))) {
                                previousCard = null;
                                previusText = null;
                                previousPosition = -1;
                                getNextMonth();
                            }
                            getEventByDate(dateList.get(position).date);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return dateList.size();
        }

        class CalendarViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            CardView cardBg, eventView;

            public CalendarViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                cardBg = itemView.findViewById(R.id.card_bg);
                eventView = itemView.findViewById(R.id.card_event_view);
            }
        }
    }
}