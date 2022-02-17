package com.ujamaaonline.customer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.BusinessProfile;
import com.ujamaaonline.customer.activities.MainActivity;
import com.ujamaaonline.customer.activities.NearMeActivity;
import com.ujamaaonline.customer.activities.SearchCatActivity;
import com.ujamaaonline.customer.activities.ViewPagerActivity;
import com.ujamaaonline.customer.adapters.SliderAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.BannerPayload;
import com.ujamaaonline.customer.models.BannerResponse;
import com.ujamaaonline.customer.models.CategoryPayload;
import com.ujamaaonline.customer.models.CategoryResponse;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.BaseUtil;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tab_location, tvTabUpcomingEvents;
    private List<BannerPayload> bannerList = new ArrayList<>();
    private List<CategoryPayload> categoryList = new ArrayList<>();
    private ViewPager2 viewPager2;
    private RecyclerView recShops;
    private EditText etSearch, etPostalCode;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private SliderAdapter sliderAdapter;
    private ShopsAdapter shopsAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private int count = 0;
    private String latitude, longitude;
    private boolean isLogin = false;
    private FilterBusinessRequest filterBusinessRequest = new FilterBusinessRequest();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home, container, false);

        this.initView();
        this.viewPagerSetUp();
        this.getCategory();
        return this.view;
    }

    private void initView() {
        BaseUtil.putSenderAccount(getActivity(),"");
        this.progress = new GlobalProgressDialog(view.getContext());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.viewPager2 = view.findViewById(R.id.view_pager);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        this.recShops = view.findViewById(R.id.rec_shops);
        this.pullToRefresh = this.view.findViewById(R.id.pullToRefresh);
        this.tvTabUpcomingEvents = view.findViewById(R.id.tab_upcoming_event);
        this.tab_location = view.findViewById(R.id.tab_location);
        this.recShops.setHasFixedSize(true);
        etSearch = view.findViewById(R.id.et_search);
        etSearch.setOnClickListener(this);
        etPostalCode = view.findViewById(R.id.et_postal_code);
        this.shopsAdapter = new ShopsAdapter();
        this.recShops.setAdapter(shopsAdapter);
        tvTabUpcomingEvents.setOnClickListener(this);
        tab_location.setOnClickListener(this);
        tvTabUpcomingEvents.setBackgroundColor(getResources().getColor(R.color.yellow_dark));

        this.pullToRefresh.setOnRefreshListener(() -> {
            this.getBanners();
            this.getCategory();
            pullToRefresh.setRefreshing(false);
        });
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                   // etPostalCode.setVisibility(View.VISIBLE);
                }
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(etSearch.getText().toString().trim()))
                        if (!TextUtils.isEmpty(etPostalCode.getText().toString().trim()))
                            getLatLong(etPostalCode.getText().toString(), true);
                        else {
                            filterBusinessRequest.setSearch(etSearch.getText().toString());
                            startActivity(new Intent(getActivity(), NearMeActivity.class).putExtra("type", "1").putExtra("filter_data", filterBusinessRequest).putExtra("filter_pre", filterBusinessRequest).putExtra("cat_name", etSearch.getText().toString().trim()));
                        }
                    else {
                        Toast toast = Toast.makeText(getActivity(), "please enter search keyword", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
        });

        etPostalCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(etSearch.getText().toString().trim()) && !TextUtils.isEmpty(etPostalCode.getText().toString().trim()))
                        getLatLong(etPostalCode.getText().toString(), true);
                    else if (!TextUtils.isEmpty(etSearch.getText().toString().trim()) && TextUtils.isEmpty(etPostalCode.getText().toString().trim())) {
                        filterBusinessRequest.setSearch(etSearch.getText().toString());
                        startActivity(new Intent(getActivity(), NearMeActivity.class).putExtra("type", "1").putExtra("filter_data", filterBusinessRequest).putExtra("filter_pre", filterBusinessRequest).putExtra("cat_name", etSearch.getText().toString().trim()));

                    } else {
                        Toast toast = Toast.makeText(getActivity(), "please enter search keyword", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

//    private void viewPagerTimer() {
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            if (count == bannerList.size()) {
//                count = 0;
//                viewPager2.setCurrentItem(count);
//                viewPagerTimer();
//            } else {
//                count++;
//                viewPager2.setCurrentItem(count);
//                viewPagerTimer();
//            }
//
//        }, 4000);
//    }

    private void getLatLong(String postCode, Boolean isProgress) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
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
                                startActivity(new Intent());
                            }
                        } else {
                            if (!TextUtils.isEmpty(editCatResponse.getError())) {
                                Toast toast = Toast.makeText(getActivity(), editCatResponse.getError(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }
                } else if (!ObjectUtil.isEmpty(response.errorBody())) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        String errorMessage = jsonObject.getString("error");
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etPostalCode.getRootView().getWindowToken(), 0);
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GetLatLngResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    if (progress.isShowing())
                        progress.hideProgressBar();
                    Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPostalCode.getRootView().getWindowToken(), 0);
                }
            }
        });
    }

    private void viewPagerSetUp() {
        sliderAdapter = new SliderAdapter(view.getContext(), bannerList);
        viewPager2.setAdapter(sliderAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
//        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
//            @Override
//            public void transformPage(@NonNull View page, float position) {
//                float r = 1 - Math.abs(position);
//                page.setScaleY(0.85f + r * 0.15f);
//            }
//        });
        viewPager2.setPageTransformer(compositePageTransformer);

        this.getBanners();
    }


    private void getBanners() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }

        progress.showProgressBar();
        apiInterface.getBanner().enqueue(new Callback<BannerResponse>() {

            @EverythingIsNonNull
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                //progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    BannerResponse bannerResponse = response.body();
                    if (!ObjectUtil.isEmpty(bannerResponse)) {
                        if (bannerResponse.getStatus() == RESPONSE_SUCCESS) {
                            if (bannerResponse.getPayload().size() > 0) {
                                bannerList.clear();
                                bannerList.addAll(bannerResponse.getPayload());
                                sliderAdapter.notifyDataSetChanged();

//                                viewPagerTimer();
                            }
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(view.getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCategory() {
        //progress.showProgressBar();
        apiInterface.getCategories().enqueue(new Callback<CategoryResponse>() {

            @EverythingIsNonNull
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    CategoryResponse bannerResponse = response.body();
                    if (!ObjectUtil.isEmpty(bannerResponse)) {
                        if (bannerResponse.getStatus() == RESPONSE_SUCCESS) {
                            if (bannerResponse.getPayload().size() > 0) {
                                categoryList.clear();
                                categoryList.addAll(bannerResponse.getPayload());
                                shopsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(view.getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_location:
                
                tab_location.setBackgroundColor(getResources().getColor(R.color.yellow_dark));
                tvTabUpcomingEvents.setBackgroundColor(getResources().getColor(R.color.yellow));
                if (isLogin)
                    ( (MainActivity) getActivity()).loadFragmentfromOther("second",new FeedFragment());

                else askLoginDialog();


                break;
            case R.id.tab_upcoming_event:
                tab_location.setBackgroundColor(getResources().getColor(R.color.yellow_dark));
                tvTabUpcomingEvents.setBackgroundColor(getResources().getColor(R.color.yellow));
                if (isLogin)
                    ( (MainActivity) getActivity()).loadFragmentfromOther("first",new FeedFragment());

                else askLoginDialog();
                break;

        }
    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dashboard_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    private void askLoginDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        AlertDialog sDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setCancelable(false).create();
        TextView title = dialogView.findViewById(R.id.dr_title);
        TextView desc = dialogView.findViewById(R.id.dr_desc);
        TextView back = dialogView.findViewById(R.id.tv_cancel);
        TextView block = dialogView.findViewById(R.id.tv_reset);
        back.setText("Cancel");
        block.setText("Login Now");
        title.setText("Login");
        desc.setText("You have to be logged in to use this");
        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        sDialog.show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDialog.dismiss();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ViewPagerActivity.class));
                sDialog.dismiss();
            }
        });
    }

    //todo ====================== items Adapter ============================
    class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ShopsViewHolder> {
        @NonNull
        @Override
        public ShopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ShopsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ShopsViewHolder holder, int position) {
            CategoryPayload categoryPayload = categoryList.get(position);
            if (!TextUtils.isEmpty(categoryPayload.getImage())) {
                //Picasso.get().load(categoryPayload.getImage()).error(R.drawable.dummy).into(holder.ivBanner);
                loadImages(categoryPayload.getImage(), holder.ivBanner);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SearchCatActivity.class).putExtra("catId", String.valueOf(categoryPayload.getId())).putExtra("cat_name", categoryPayload.getName()).putExtra("type", "2"));
                }
            });
            holder.tvName.setText(TextUtils.isEmpty(categoryPayload.getName()) ? "" : categoryPayload.getName());
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class ShopsViewHolder extends RecyclerView.ViewHolder {

            ImageView ivBanner;
            TextView tvName;

            public ShopsViewHolder(@NonNull View itemView) {
                super(itemView);
                ivBanner = itemView.findViewById(R.id.iv_banner);
                tvName = itemView.findViewById(R.id.tv_name);

            }
        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Glide.with(this)
                .load(imagePath)
                .error(R.mipmap.ic_launcher)
                .apply(new RequestOptions().override(600, 600))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }
}