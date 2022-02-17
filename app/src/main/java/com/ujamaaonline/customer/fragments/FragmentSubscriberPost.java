package com.ujamaaonline.customer.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.BusinessProfile;
import com.ujamaaonline.customer.activities.NearMeActivity;
import com.ujamaaonline.customer.activities.ProfileFilterActivity;
import com.ujamaaonline.customer.adapters.SubscriberPostAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.OnOpenListener;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListData;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.business_data.OtherFeature;
import com.ujamaaonline.customer.models.feed_models.GetPostRequest;
import com.ujamaaonline.customer.models.feed_models.PostPayload;
import com.ujamaaonline.customer.models.feed_models.PostResponse;
import com.ujamaaonline.customer.models.filter_cat.FilterCatPayload;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static android.app.Activity.RESULT_OK;
import static com.ujamaaonline.customer.utils.Constants.BEARER;

public class FragmentSubscriberPost extends Fragment implements View.OnClickListener {

    private DecimalFormat formater = new DecimalFormat("0.0");
    private RecyclerView spRecyclerView, subsBusiness;
    private List<PostPayload> fList = new ArrayList<>();
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private Spinner spSpinner;
    private SubscriberPostAdapter subscriberPostAdapter;
    private int isAll, expireAll;
    private TextView tvSearch, tvFilter, tv_empty_data;
    private EditText etSearch;
    private List<Integer> selectedCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatList = new ArrayList<>();
    private List<FilterCatPayload> filterCatListTemp = new ArrayList<>();
    private List<BusinessListData> bList = new ArrayList<>();
    TextView tvDummy;
    private ImageView crossIcon;
    Calendar start_calendar = Calendar.getInstance();
    Calendar end_calendar = Calendar.getInstance();


    long start_millis = start_calendar.getTimeInMillis(); //get the start time in milliseconds
    long end_millis = end_calendar.getTimeInMillis(); //get the end time in milliseconds
    long total_millis = (end_millis - start_millis); //total time in milliseconds

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscriber_post, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(Constants.HEADER_TOKEN, "");
        spSpinner = view.findViewById(R.id.fsp_spinner);
        spRecyclerView = view.findViewById(R.id.fsp_recycler);
        etSearch = view.findViewById(R.id.et_search);
        etSearch.setOnClickListener(this);
        tv_empty_data = view.findViewById(R.id.tv_empty_data);
        subsBusiness = view.findViewById(R.id.sub_business_recycler);
        subsBusiness.setHasFixedSize(true);
        subsBusiness.setAdapter(new BusinessCategoryAdapter(bList));
        tvFilter = view.findViewById(R.id.tv_filter);
        tvFilter.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        spRecyclerView.setLayoutManager(linearLayoutManager);
        subscriberPostAdapter = new SubscriberPostAdapter(fList, getActivity(), FragmentSubscriberPost.this);
        spRecyclerView.setAdapter(subscriberPostAdapter);
        tvSearch = view.findViewById(R.id.btn_search);
        tvSearch.setOnClickListener(this);
        crossIcon=view.findViewById(R.id.spf_cross);
        crossIcon.setOnClickListener(this);

        String[] arraySpinner = new String[]{"All Posts", "Liked Posts", "Hidden Posts", "Your Subscriptions"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.custome_spinner, arraySpinner);
        adapter.setDropDownViewResource(R.layout.custome_spinner);
        spSpinner.setAdapter(adapter);
        spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fList.clear();
                tv_empty_data.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        getSubscriberFeed(1, 0, 0, 0);
                        break;
                    case 1:
                        getSubscriberFeed(0, 1, 0, 0);
                        break;
                    case 2:
                        getSubscriberFeed(0, 0, 1, 0);
                        break;
                    case 3:
                        spRecyclerView.setVisibility(View.GONE);
                        getBusinessList();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getFilterCat();
    }

    public void getSubscriberFeed(int isall, int isliked, int ishidden, int yourSubscriptions) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        subsBusiness.setVisibility(View.GONE);
        tv_empty_data.setVisibility(View.GONE);
        GetPostRequest getFeedRequest = new GetPostRequest(isall, 0, 1, isliked, ishidden, yourSubscriptions);
        apiInterface.getFeedList(Constants.BEARER.concat(this.headerToken), getFeedRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                fList.clear();
                tvFilter.setText("Filter");
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        spRecyclerView.setVisibility(View.VISIBLE);
                        fList.addAll(response.body().getPostPayload());
                    } else {
                        Toast.makeText(getActivity(), "No Post available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                spRecyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBusinessList() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        apiInterface.getBookmarkedBusinesses(BEARER.concat(this.headerToken)).enqueue(new Callback<BusinessListResponse>() {
            @Override
            public void onResponse(Call<BusinessListResponse> call, Response<BusinessListResponse> response) {
                tv_empty_data.setVisibility(View.VISIBLE);
                subsBusiness.setVisibility(View.GONE);
                bList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0){
                            bList.addAll(response.body().getPayload());
                            subsBusiness.getAdapter().notifyDataSetChanged();
                            subsBusiness.setVisibility(View.VISIBLE);
                            tv_empty_data.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }

            @Override
            public void onFailure(Call<BusinessListResponse> call, Throwable t) {
                tv_empty_data.setVisibility(View.VISIBLE);
                subsBusiness.setVisibility(View.GONE);
                progress.hideProgressBar();
                Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHashTag(String searchKeyword) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetPostRequest getFeedRequest = new GetPostRequest(0, 1, 0, searchKeyword);
        apiInterface.searchHashtagFeed(Constants.BEARER.concat(this.headerToken), getFeedRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                fList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        fList.addAll(response.body().getPostPayload());
                        subscriberPostAdapter = new SubscriberPostAdapter(fList, getActivity(), FragmentSubscriberPost.this);

                    } else {
                        Toast.makeText(getActivity(), "No Post available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
                spRecyclerView.setAdapter(subscriberPostAdapter);
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
                        getFilterEvents(data.getIntExtra("discount_deals", 0), data.getIntExtra("important_updates", 0), selectedCatList.size() > 0 ? selectedCatList.toString().replace("[","").replace("]","") : "", data.getIntExtra("general_info", 0));
                }
            } else if (data.hasExtra("reset_cat")) {
                filterCatList.clear();
                selectedCatList.clear();
                filterCatList.addAll(filterCatListTemp);
                getSubscriberFeed(1, 0, 0, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                    searchHashTag(etSearch.getText().toString());
                }
                break;
            case R.id.tv_filter:
                startActivityForResult(new Intent(getActivity(), ProfileFilterActivity.class).putExtra("filter_cat", (Serializable) filterCatList).putExtra("from_feed", ""), 202);
                break;
            case R.id.spf_cross:
                etSearch.setText(null);
                break;
        }
    }

    private void getFilterEvents(int discount, int important, String catId, int general) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        progress.showProgressBar();
        GetBusinessRequest request = new GetBusinessRequest();
        request.setIsLocal(0);
        request.setIsSubscriber(1);
        request.setIsEvent(0);
        request.setIsDiscount(discount);
        request.setIsImportant(important);
        request.setIsGeneral(general);
        request.setCategoryId(catId);
        request.setIs_liked(0);
        request.setIs_hidden(0);
        apiInterface.getFilterEvents(Constants.BEARER.concat(this.headerToken), request).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                fList.clear();
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
                            fList.addAll(response.body().getPostPayload());
                        }
                    } else {
                        Toast.makeText(getActivity(), "No Post available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

                }
                progress.hideProgressBar();
                spRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
    //todo============================== Business Adapter ==============================

    public class BusinessCategoryAdapter extends RecyclerView.Adapter<BusinessCategoryAdapter.ViewHolder> {
        private List<BusinessListData> mList;

        public BusinessCategoryAdapter(List<BusinessListData> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public BusinessCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BusinessCategoryAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_business, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BusinessCategoryAdapter.ViewHolder holder, int position) {
            holder.bName.setText(mList.get(position).getBusinessName());
            Picasso.get().load(mList.get(position).getBusiness_logo()).into(holder.bImage);
            holder.tvSummaryLine.setText(TextUtils.isEmpty(mList.get(position).getSummery_line()) ? "" : mList.get(position).getSummery_line());
            holder.tvContact.setText(TextUtils.isEmpty(mList.get(position).getBusiness_contact()) ? "" : mList.get(position).getBusiness_contact());

            if (mList.get(position).getMessageStatus() == 1)
                holder.cardViewMessage.setVisibility(View.VISIBLE);
            else holder.cardViewMessage.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mList.get(position).getAvrg_rateing())) {
                holder.ratingBar.setRating(Float.parseFloat(mList.get(position).getAvrg_rateing()));
                holder.tvRatingCount.setText(formater.format(Float.parseFloat(mList.get(position).getAvrg_rateing())));
                holder.tvReviewCount.setText(mList.get(position).getReviews_count() + " Reviews");
            }
            if (mList.get(position).getCurrent_working_status() != null) {
                if (!TextUtils.isEmpty(mList.get(position).getCurrent_working_status().getStatus())) {
                    if (mList.get(position).getCurrent_working_status().getStatus().equalsIgnoreCase("Closed")) {
                        holder.tvOpenStatus.setText(mList.get(position).getCurrent_working_status().getStatus());
                        holder.tvOpenStatus.setTextColor(getResources().getColor(R.color.darkRed));
                    } else if (mList.get(position).getCurrent_working_status().getStatus().equalsIgnoreCase("Open")) {
                        holder.tvOpenStatus.setText(mList.get(position).getCurrent_working_status().getStatus());
                        holder.tvOpenStatus.setTextColor(getResources().getColor(R.color.green));
                    }
                }
                holder.tvTime.setText(TextUtils.isEmpty(mList.get(position).getCurrent_working_status().getOpenCloseAt()) ? "" : mList.get(position).getCurrent_working_status().getOpenCloseAt());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), BusinessProfile.class);
                    intent.putExtra("businessCatId", String.valueOf(mList.get(position).getId()));
                    intent.putExtra("businessLogo", mList.get(position).getBusiness_long());
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView bImage;
            private TextView bName, tvSummaryLine, tvRatingCount, tvContact, tvOpenStatus, tvTime, tvReviewCount;
            private RatingBar ratingBar;
            private CardView cardViewMessage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bName = itemView.findViewById(R.id.rcb_bName);
                bImage = itemView.findViewById(R.id.rcb_bImage);
                tvSummaryLine = itemView.findViewById(R.id.tv_summary_line);
                ratingBar = itemView.findViewById(R.id.star_ratingbar);
                tvRatingCount = itemView.findViewById(R.id.tv_rating_count);
                tvContact = itemView.findViewById(R.id.tv_contact);
                tvTime = itemView.findViewById(R.id.tvtime);
                tvOpenStatus = itemView.findViewById(R.id.tvstatus);
                tvReviewCount = itemView.findViewById(R.id.tv_review_cout);
                cardViewMessage = itemView.findViewById(R.id.card_view_msg);
            }
        }
    }


}
