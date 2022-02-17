package com.ujamaaonline.customer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.models.recent_search.RecentSearchResponse;
import com.ujamaaonline.customer.models.search_category.CatChild;
import com.ujamaaonline.customer.models.search_category.CatHeadingResponse;
import com.ujamaaonline.customer.models.search_category.Subcategory;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;
import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;
import static com.ujamaaonline.customer.utils.Constants.USER_ID;
import static com.ujamaaonline.customer.utils.Constants.USER_POST_CODE;

public class SearchCatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recHeadChild, recSelectedCat;
    private String headerToken, catId, type, latitude, longitude, previousPostCode,catName,reCentCatName;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private List<CatChild> catHeadingList = new ArrayList<>();
    private List<Subcategory> tempSelectedList = new ArrayList<>();
    private List<Subcategory> finalSelectedCatList = new ArrayList<>();
    private TextView tvCatName, tvNearMe;
    private EditText etPostalCode, etHashtag;
    private boolean isLogin = false;
    private Location location;
    private Integer userId=0;
    private String userPostCode="";
    private TextView tvRecentSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cat);
        registerReceiver(receiver, new IntentFilter("searchCat"));
        initView();
        viewSeOnClicklisteners();
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private void viewSeOnClicklisteners() {
        tvNearMe.setOnClickListener(this);
    }

    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        recHeadChild = findViewById(R.id.rec_heading_child);
        tvNearMe = findViewById(R.id.tv_near_me);
        tvRecentSearch=findViewById(R.id.tv_recent_search);
        userId=this.loginPreferences.getInt(USER_ID, 0);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        recSelectedCat = findViewById(R.id.rec_selected_cat);
        userPostCode=loginPreferences.getString(USER_POST_CODE, "");

//        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
//        layoutManager.setFlexDirection(FlexDirection.ROW);
//        layoutManager.setJustifyContent(JustifyContent.CENTER);
//
//        recSelectedCat.setLayoutManager(layoutManager);

        recSelectedCat.setHasFixedSize(true);
        etPostalCode = findViewById(R.id.et_postal_code);
        etHashtag = findViewById(R.id.et_hashtag);
        type = getIntent().getStringExtra("type");
        tvCatName = findViewById(R.id.tv_cat_name_search);
        recSelectedCat.setAdapter(new SelectedCatAdapter(finalSelectedCatList));
        recHeadChild.setHasFixedSize(true);
        recHeadChild.setAdapter(new CatHeadingAdapter());
        catId = getIntent().getStringExtra("catId");
        tvCatName.setText(getIntent().getStringExtra("cat_name"));
        catName = getIntent().getStringExtra("cat_name");
        etPostalCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getLatLong(etPostalCode.getText().toString(), false);
                    return true;
                }
                return false;
            }
        });
        getCatHeading();
        if(isLogin)
        {
            getRecentSearch();
        }
    }

    private void getLatLong(String postCode, Boolean isProgress) {
        if (!GlobalUtil.isNetworkAvailable(this)) {
            UIUtil.showNetworkDialog(this);
            return;
        }
        if (ObjectUtil.isNonEmptyStr(this.headerToken)) {
            if (isProgress)
                progress.showProgressBar();
            apiInterface.getLatLng("https://postcodes.io/postcodes/"+ postCode).enqueue(new Callback<GetLatLngResponse>() {
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
                                    if (isProgress) {
                                        getSelectedData(null);
                                    }
                                }
                            } else {
                                if (!TextUtils.isEmpty(editCatResponse.getError()))
                                    Toast.makeText(SearchCatActivity.this, editCatResponse.getError(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (!ObjectUtil.isEmpty(response.errorBody())) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String errorMessage = jsonObject.getString("error");
                            if (!TextUtils.isEmpty(errorMessage)) {
                                etPostalCode.setError(errorMessage);
                                Toast.makeText(SearchCatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPostalCode.getRootView().getWindowToken(), 0);
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetLatLngResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        if (progress.isShowing())
                            progress.hideProgressBar();
                        Toast.makeText(SearchCatActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etPostalCode.getRootView().getWindowToken(), 0);
                    }
                }
            });
        }
    }

    private void getCatHeading() {
        if (!GlobalUtil.isNetworkAvailable(SearchCatActivity.this)) {
            UIUtil.showNetworkDialog(SearchCatActivity.this);
            return;
        }
        GetBusinessRequest request = new GetBusinessRequest();
        request.setCat_id(catId);
        progress.showProgressBar();
        apiInterface.getCatHeading(BEARER.concat(this.headerToken), request).enqueue(new Callback<CatHeadingResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<CatHeadingResponse> call, Response<CatHeadingResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    CatHeadingResponse catHeadingResponse = response.body();
                    if (!ObjectUtil.isEmpty(catHeadingResponse)) {
                        if (catHeadingResponse.getStatus() == RESPONSE_SUCCESS) {
                            if (catHeadingResponse.getPayload().getCategoryData().getChildren().size() > 0) {
                                catHeadingList.addAll(catHeadingResponse.getPayload().getCategoryData().getChildren());
                                recHeadChild.getAdapter().notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(SearchCatActivity.this, catHeadingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<CatHeadingResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                }
            }
        });
    }
    private void getRecentSearch() {
        if (!GlobalUtil.isNetworkAvailable(SearchCatActivity.this)) {
            UIUtil.showNetworkDialog(SearchCatActivity.this);
            return;
        }
        progress.showProgressBar();
        apiInterface.getRecentSearch(BEARER.concat(this.headerToken)).enqueue(new Callback<RecentSearchResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<RecentSearchResponse> call, Response<RecentSearchResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    RecentSearchResponse catHeadingResponse = response.body();
                    if (!ObjectUtil.isEmpty(catHeadingResponse)) {
                        if (catHeadingResponse.getStatus() == RESPONSE_SUCCESS) {
                            if(!TextUtils.isEmpty(response.body().getPayload().getName()))
                            {
                                tvRecentSearch.setText(response.body().getPayload().getName());

                                tvRecentSearch.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getSelectedData(response.body().getPayload().getSearchData());
                                    }
                                });
                            }
                        } else {
//                            Toast.makeText(SearchCatActivity.this, catHeadingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<RecentSearchResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                }
            }
        });
    }
    private List<Integer> selectedCatIds = new ArrayList<>();

    private void getSelectedData(FilterBusinessRequest filterBusinessRequest2) {
        FilterBusinessRequest filterBusinessRequest=null;

        if (filterBusinessRequest2==null)
        {
            filterBusinessRequest  = new FilterBusinessRequest();
        if (!TextUtils.isEmpty(previousPostCode) && (!previousPostCode.equals(etPostalCode.getText().toString()))) {
            getLatLong(etPostalCode.getText().toString(), true);
            return;
        }
        if ((!TextUtils.isEmpty(etPostalCode.getText().toString())) && (TextUtils.isEmpty(latitude)) && (TextUtils.isEmpty(latitude)) && !etPostalCode.getText().toString().equalsIgnoreCase("Near Me")) {
            getLatLong(etPostalCode.getText().toString(), true);
            return;
        } else if ((!TextUtils.isEmpty(latitude)) && (!TextUtils.isEmpty(latitude))) {
            filterBusinessRequest.setCustomerLat(latitude);
            filterBusinessRequest.setCustomerLong(longitude);
            previousPostCode = etPostalCode.getText().toString();
        }
        else if (etPostalCode.getText().toString().equalsIgnoreCase("Near Me")&& location!=null)
        {
            filterBusinessRequest.setCustomerLat(String.valueOf(location.getLatitude()));
            filterBusinessRequest.setCustomerLong(String.valueOf(location.getLongitude()));
            previousPostCode = etPostalCode.getText().toString();
        }
        else if (etPostalCode.getText().toString().equalsIgnoreCase("Near Me")&& location==null)
        {
            getLatLong(userPostCode, true);
        }
        if (!TextUtils.isEmpty(etHashtag.getText().toString())) {
            filterBusinessRequest.setSearch(etHashtag.getText().toString());
        }
        selectedCatIds.clear();
        if (finalSelectedCatList.size() > 0) {
            for (Subcategory subcategory : finalSelectedCatList) {
                selectedCatIds.add(subcategory.getId());
            }
        }
        filterBusinessRequest.setSubCatIds(selectedCatIds);

        if (selectedCatIds.size() > 0)

            if(isLogin)
            {
                filterBusinessRequest.setStore_search(1);
                filterBusinessRequest.setCustomerId(userId);
            }
        filterBusinessRequest.setType(Integer.valueOf(type));
        filterBusinessRequest.setMainCatId(Integer.valueOf(catId));
        }
        else
        {
            filterBusinessRequest=filterBusinessRequest2;
        }
        startActivity(new Intent(SearchCatActivity.this, NearMeActivity.class).putExtra("businessId", catId).putExtra("type", type).putExtra("filter_data", filterBusinessRequest).putExtra("filter_pre", filterBusinessRequest).putExtra("cat_name", filterBusinessRequest2!=null?tvRecentSearch.getText().toString():catName).putExtra("post",etPostalCode.getText().toString()));
    }

    public void btnSearch(View view) {
        getSelectedData(null);
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_near_me:
                checkLocaionPermission();
                break;
        }
    }

    private void checkLocaionPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) != (PackageManager.PERMISSION_GRANTED)) {
                if (!showRationale)
                    ObjectUtil.showPermissionDialog(this);
                else
                    requestPermissions(new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            123);
            } else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                etPostalCode.setText("Near Me");
                location = ObjectUtil.getLocation(this);
            }
        } else {
            etPostalCode.setText("Near Me");
            location = ObjectUtil.getLocation(this);
        }
    }


    //todo ======================= Cat Heading Adapter ===========================

    class CatHeadingAdapter extends RecyclerView.Adapter<CatHeadingAdapter.CatHeadingViewHolder> {

        @NonNull
        @Override
        public CatHeadingAdapter.CatHeadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CatHeadingAdapter.CatHeadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_cat, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull CatHeadingAdapter.CatHeadingViewHolder holder, int position) {
            holder.tvCatName.setText(catHeadingList.get(position).getName());
            if (position == catHeadingList.size() - 1)
                holder.lineView.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(SearchCatActivity.this);
                    View sheetView = getLayoutInflater().inflate(R.layout.dialog_heading_sub_cat, null);
                    RecyclerView recSubCat = sheetView.findViewById(R.id.rec_sub_cat);
                    recSubCat.setHasFixedSize(true);
                    ImageView imgCross=sheetView.findViewById(R.id.iv_close);
                    imgCross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    TextView tvTitle = sheetView.findViewById(R.id.tv_title);
                    tvTitle.setText(catName);
                    recSubCat.setAdapter(new SubCatAdapter(catHeadingList.get(position).getSubcategory(), position));
                    mBottomSheetDialog.setContentView(sheetView);
                    sheetView.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tempSelectedList.size() > 0) {
                                for (int i = 0; i < catHeadingList.get(position).getSubcategory().size(); i++) {
                                    for (Subcategory subcat : tempSelectedList) {
                                        if (catHeadingList.get(position).getSubcategory().get(i).getId().equals(subcat.getId())) {
                                            catHeadingList.get(position).getSubcategory().get(i).setSelected(true);
                                        }
                                    }
                                }
                            }
                            addToList(position);
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    mBottomSheetDialog.show();
                }
            });
        }

        private void addToList(int position) {
            boolean isAdd = false;
            for (Subcategory subcategory : catHeadingList.get(position).getSubcategory()) {
                if (finalSelectedCatList.size() > 0) {
                    isAdd = false;
                    for (int i = 0; i < finalSelectedCatList.size(); i++) {
                        if ((subcategory.getId() == finalSelectedCatList.get(i).getId())) {
                            isAdd = true;
                            if (!subcategory.isSelected()) {
                                finalSelectedCatList.remove(i);
                            }
                        }
                    }
                    if (subcategory.isSelected() && (!isAdd))
                        finalSelectedCatList.add(subcategory);

                } else {
                    if (subcategory.isSelected())
                        finalSelectedCatList.add(subcategory);
                }
            }
            recSelectedCat.getAdapter().notifyDataSetChanged();
            tempSelectedList.clear();
        }

        @Override
        public int getItemCount() {
            return catHeadingList.size();
        }

        class CatHeadingViewHolder extends RecyclerView.ViewHolder {
            TextView tvCatName;
            View lineView;

            public CatHeadingViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_cat_name);
                lineView = itemView.findViewById(R.id.row_view);
            }
        }
    }

//todo ======================= Sub Category Adapter ===========================

    class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.SubCatViewHolder> {
        private List<Subcategory> subCatList;
        private int headingCatPos;

        public SubCatAdapter(List<Subcategory> subCatList, int headingCatPos) {
            this.subCatList = subCatList;
            this.headingCatPos = headingCatPos;
        }

        @NonNull
        @Override
        public SubCatAdapter.SubCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubCatAdapter.SubCatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_heading_sub_cat, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubCatAdapter.SubCatViewHolder holder, int position) {
            Subcategory subcategory = subCatList.get(position);
            holder.tvCatName.setText(subCatList.get(position).getName());

            if (subcategory.isSelected())
                holder.imgCheck.setVisibility(View.VISIBLE);
            else
                holder.imgCheck.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.imgCheck.getVisibility() == View.VISIBLE) {
                        holder.imgCheck.setVisibility(View.GONE);
                        tempSelectedList.remove(subcategory);
                        catHeadingList.get(headingCatPos).getSubcategory().get(position).setSelected(false);
                    } else {
                        holder.imgCheck.setVisibility(View.VISIBLE);
                        tempSelectedList.add(subcategory);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return subCatList.size();
        }

        class SubCatViewHolder extends RecyclerView.ViewHolder {
            TextView tvCatName;
            ImageView imgCheck;

            public SubCatViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_categories_name);
                imgCheck = itemView.findViewById(R.id.iv_check);
            }
        }
    }
    //todo ======================= Selected Category Adapter ===========================

    class SelectedCatAdapter extends RecyclerView.Adapter<SelectedCatAdapter.SelectedCatViewHolder> {

        List<Subcategory> subCatList;

        public SelectedCatAdapter(List<Subcategory> subCatList) {
            this.subCatList = subCatList;
        }

        @NonNull
        @Override
        public SelectedCatAdapter.SelectedCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SelectedCatAdapter.SelectedCatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_subcat, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SelectedCatAdapter.SelectedCatViewHolder holder, int position) {
            Subcategory subcategory = subCatList.get(position);
            holder.tvCatName.setText(subCatList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalSelectedCatList.get(position).setSelected(false);
                    finalSelectedCatList.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return finalSelectedCatList.size();
        }

        class SelectedCatViewHolder extends RecyclerView.ViewHolder {
            TextView tvCatName;
            ImageView imgRemoveCat;

            public SelectedCatViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_cat_name);
                imgRemoveCat = itemView.findViewById(R.id.img_remove_cat);
            }
        }
    }

}