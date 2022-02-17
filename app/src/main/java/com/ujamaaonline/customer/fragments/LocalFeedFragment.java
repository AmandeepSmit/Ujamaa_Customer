package com.ujamaaonline.customer.fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ProfileFilterActivity;
import com.ujamaaonline.customer.activities.SearchCatActivity;
import com.ujamaaonline.customer.adapters.LocalPostAdapter;
import com.ujamaaonline.customer.adapters.SubscriberPostAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.feed_models.FeedLocationFilterRequest;
import com.ujamaaonline.customer.models.feed_models.GetPostRequest;
import com.ujamaaonline.customer.models.feed_models.PostPayload;
import com.ujamaaonline.customer.models.feed_models.PostResponse;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static android.app.Activity.RESULT_OK;

public class LocalFeedFragment extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private RecyclerView lpRecyclerView;
    private List<PostPayload> feedModels = new ArrayList<>();
    private LocalPostAdapter adapter;
    private Spinner spSpinner;
    private int isAll, expireAll;
    private EditText etPostCode, etMiles, etSearch;
    private String prePostCode = "";
    private TextView tvFilter, tvChange, btnResetPostCode, btnResetMiles, btnSearch;
    private List<Integer> selectedCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatListTemp = new ArrayList<>();
    private String latitude, longitude;
    private String userPostCode = "";
    private String preMiles = "5";
    private boolean isLocationAdded=false;
    private ImageView crossIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_local_feed, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        this.userPostCode = this.loginPreferences.getString(Constants.USER_POST_CODE, "");
        prePostCode = userPostCode;
        spSpinner = view.findViewById(R.id.flp_spinner);
        etPostCode = view.findViewById(R.id.et_postal_code);
        tvFilter = view.findViewById(R.id.tv_filter);
        crossIcon=view.findViewById(R.id.lep_cross);
        crossIcon.setOnClickListener(this);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        btnResetMiles = view.findViewById(R.id.btn_rest_miles);
        btnResetMiles.setOnClickListener(this);
        etSearch = view.findViewById(R.id.et_search);
        tvFilter.setOnClickListener(this);
        etMiles = view.findViewById(R.id.et_miles);
        btnResetPostCode = view.findViewById(R.id.btn_reset_post_code);
        btnResetPostCode.setOnClickListener(this);
        tvChange = view.findViewById(R.id.tv_change);
        tvChange.setOnClickListener(this);
        lpRecyclerView = view.findViewById(R.id.flf_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lpRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new LocalPostAdapter(feedModels, getActivity(),this);
        lpRecyclerView.setAdapter(adapter);
        String[] arraySpinner = new String[]{"All Posts", "Liked Posts", "Hidden Posts"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.custome_spinner, arraySpinner);
        adapter.setDropDownViewResource(R.layout.custome_spinner);
        if (!TextUtils.isEmpty(userPostCode)){
            etPostCode.setText(userPostCode);
        }
        spSpinner.setAdapter(adapter);
        spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feedModels.clear();
                switch (position) {
                    case 0:
                        if(isLocationAdded)
                        getSubscriberFeed(1, 0, 0, 0);
                        break;
                    case 1:
                        getSubscriberFeed(0, 1, 0, 0);
                        break;
                    case 2:
                        getSubscriberFeed(0, 0, 1, 0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getFilterCat();
        if(!TextUtils.isEmpty(etPostCode.getText().toString().trim()))
        getLatLong(etPostCode.getText().toString(), true,true);
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
                                    if (isFirst && !TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude))
                                        getSubscriberFeed(1,0,0,0);
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

    private void getFilterCat() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        apiInterface.getFilterCat().enqueue(new Callback<FilterCatResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<FilterCatResponse> call, Response<FilterCatResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0) {
                            filterCatList.addAll(response.body().getPayload());
                            filterCatListTemp.addAll(response.body().getPayload());
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<FilterCatResponse> call, Throwable t) {

            }
        });
    }
    private void getLocationFilter(int miles, String lat, String lng) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        FeedLocationFilterRequest locationFilterRequest = new FeedLocationFilterRequest(lat, lng, miles, 0, 1);
        apiInterface.getLocationFilter(Constants.BEARER.concat(this.headerToken), locationFilterRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                feedModels.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        feedModels.addAll(response.body().getPostPayload());
                    } else {
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                lpRecyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSubscriberFeed(int isall, int isliked, int ishidden, int yourSubscriptions) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetPostRequest getFeedRequest = new GetPostRequest(isall, 1, 0, isliked, ishidden, yourSubscriptions,latitude,longitude,preMiles);
        apiInterface.getLocalPost(Constants.BEARER.concat(this.headerToken), getFeedRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                feedModels.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        feedModels.addAll(response.body().getPostPayload());
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

                }
                progress.hideProgressBar();
                lpRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 202) {
            if (data != null && data.hasExtra("filter_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList = (List<FilterCatPayload>) data.getSerializableExtra("filter_cat");
                if (filterCatList.size() > 0) {
                    for (FilterCatPayload filterCatPayload : filterCatList) {
                        if (filterCatPayload.isChecked()) {
                            selectedCatList.add(filterCatPayload.getId());
                        }
                    }
                    if (data.hasExtra("discount_deals"))
                        getFilterEvents(data.getIntExtra("discount_deals", 0), data.getIntExtra("important_updates", 0), selectedCatList.size()>0?selectedCatList.toString().replace("[","").replace("]",""):"", data.getIntExtra("general_info", 0));
                }
            } else if (data.hasExtra("reset_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList.addAll(filterCatListTemp);
                getSubscriberFeed(1, 0, 0, 0);
            }
        }
    }

    private void getFilterEvents(int discount, int important, String catId, int general) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetBusinessRequest request = new GetBusinessRequest();
        request.setIsLocal(1);
        request.setIsSubscriber(0);
        request.setIsEvent(0);
        request.setIs_liked(0);
        request.setIsDiscount(discount);
        request.setIsImportant(important);
        request.setIsGeneral(general);
        request.setCategoryId(catId);
        request.setIs_hidden(0);

        apiInterface.getFilterEvents(Constants.BEARER.concat(this.headerToken), request).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                feedModels.clear();
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
                        if (response.body().getPostPayload().size() > 0) {
                            feedModels.addAll(response.body().getPostPayload());
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                lpRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter:
                startActivityForResult(new Intent(getActivity(), ProfileFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList).putExtra("from_feed", ""), 202);
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
            case R.id.lep_cross:
                etSearch.setText(null);
                break;
        }
    }

    private void getLocationFilter()
    {
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

    private void searchHashTag(String searchKeyword){
        if (!GlobalUtil.isNetworkAvailable(getActivity())){
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetPostRequest getFeedRequest = new GetPostRequest(1, 0, 0, searchKeyword);
        apiInterface.searchHashtagFeed(Constants.BEARER.concat(this.headerToken), getFeedRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                feedModels.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        feedModels.addAll(response.body().getPostPayload());
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

                }
                progress.hideProgressBar();
                lpRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
