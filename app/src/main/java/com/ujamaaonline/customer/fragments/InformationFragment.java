package com.ujamaaonline.customer.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityChat;
import com.ujamaaonline.customer.activities.BusinessProfile;
import com.ujamaaonline.customer.activities.LoginActivity;
import com.ujamaaonline.customer.activities.NearMeActivity;
import com.ujamaaonline.customer.activities.SearchCatActivity;
import com.ujamaaonline.customer.activities.ViewPagerActivity;
import com.ujamaaonline.customer.adapters.BusinessAdapters.GalleryAdapter;
import com.ujamaaonline.customer.adapters.BusinessAdapters.MeetTeamAdapter;
import com.ujamaaonline.customer.adapters.BusinessAdapters.OpeningHoursAdapter;
import com.ujamaaonline.customer.adapters.BusinessAdapters.OtherFeatureAdapter;
import com.ujamaaonline.customer.adapters.BusinessAdapters.PaymentMethodAdapter;
import com.ujamaaonline.customer.adapters.BusinessAdapters.ProductServiceAdapter;
import com.ujamaaonline.customer.adapters.BusinessAdapters.ReputationalCredAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.business_data.DisabledFacility;
import com.ujamaaonline.customer.models.business_data.GalleryImage;
import com.ujamaaonline.customer.models.business_data.GetBusinessData;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.business_data.MeetTheTeam;
import com.ujamaaonline.customer.models.business_data.OtherFeatureSection;
import com.ujamaaonline.customer.models.business_data.PaymentSection;
import com.ujamaaonline.customer.models.business_data.ProductService;
import com.ujamaaonline.customer.models.business_data.ReputationCredential;
import com.ujamaaonline.customer.models.business_data.SocialMedia;
import com.ujamaaonline.customer.models.business_data.AllWorkingHour;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryPayload;
import com.ujamaaonline.customer.models.signup.EORegisterResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.StringUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

public class InformationFragment extends Fragment implements View.OnClickListener {
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private RatingBar ratingBar;
    private String number, email;
    public static String businessName;
    private LinearLayout imgLayout;
    private List<Uri> imgList = new ArrayList<>();
    private TextView phoNumber,orderNow,tvBookNow, busDescription, glViewAll, avgRating, revCount, tvStatus, tvOpningTime, tvDeViewAll, tvAmenityViewAll, tvViewAllProducts, msgBtn;
    private RecyclerView whRecyclerView, crRecyclerView, prRecyclerView, mtRecyclerView, pmRecyclerView, ofRecyclerView, gRecyclerView, recSocialMedia, recDesableFacility;
    private ImageView smArrow, ohArrow;
    private LinearLayout socialMediaBtn, ohLinear, mtLayout;
    private List<SocialMedia> socialMediaList = new ArrayList<>();
    private List<AllWorkingHour> whList = new ArrayList<>();
    private OpeningHoursAdapter openingHoursAdapter;
    private RatingImgAdapter ratingImgAdapter;
    private List<MultipartBody.Part> selectedImgList = new ArrayList();
    private List<ReputationCredential> rList = new ArrayList<>();
    private ReputationalCredAdapter reputationalCredAdapter;
    private List<ProductService> pList = new ArrayList<>();
    private ProductServiceAdapter productServiceAdapter;
    private List<MeetTheTeam> mList = new ArrayList<>();
    private MeetTeamAdapter meetTeamAdapter;
    private List<PaymentSection> pmList = new ArrayList<>();
    private PaymentMethodAdapter paymentMethodAdapter;
    private List<OtherFeatureSection> oList = new ArrayList<>();
    private OtherFeatureAdapter otherFeatureAdapter;
    private int imgShowCount = 3;
    private List<SearchGalleryPayload> gList = new ArrayList<>();
    private List<GalleryImage> selectedGalleryList = new ArrayList<>();
    private List<DisabledFacility> disableFacilityList = new ArrayList<>();
    private GalleryAdapter galleryAdapter;
    private TextView tvBtnWriteReview, tvReadMore;
    private View view;
    private boolean isViewAllDesablily = false, isOtherAmenities = false, isViewAllProducts = false, isLogin = false;
    private Integer count = 3, lastInsert = 0;
    private List<MeetTheTeam> displayList = new ArrayList<>();
    private String businessId = "", businessLogo,bLat,blong;
    private int BusinessUserId, customerId;
    private int msgStatus;
    private LinearLayout layoutCopyLink;
    private String businessLink="",orderStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information, container, false);
        initViews(view);
        return view;
    }

    public InformationFragment(String businessId) {
        this.businessId = businessId;
    }

    private void initViews(View view) {
        this.progress = new GlobalProgressDialog(view.getContext());
        this.apiInterface = APIClient.getClient();
        tvOpningTime = view.findViewById(R.id.tv_start_time);
        tvStatus = view.findViewById(R.id.tv_end_time);
        tvBookNow=view.findViewById(R.id.in_book_now);
        tvBookNow.setOnClickListener(this);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        phoNumber = view.findViewById(R.id.in_phone);
        phoNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(phoNumber.getText().toString());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("text label",phoNumber.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(view.getContext(),phoNumber.getText().toString()  +" Copied",Toast.LENGTH_SHORT).show();
            }
        });
        customerId = loginPreferences.getInt(USER_ID, 0);
        tvBtnWriteReview = view.findViewById(R.id.fmy_reviewBtn);
        tvBtnWriteReview.setOnClickListener(this);
        msgBtn = view.findViewById(R.id.in_msgBtn);
        orderNow=view.findViewById(R.id.in_order);
        orderNow.setOnClickListener(this);
        layoutCopyLink = view.findViewById(R.id.layout_copy_link);
        layoutCopyLink.setOnClickListener(this);
        msgBtn.setOnClickListener(this);
        busDescription = view.findViewById(R.id.in_bus_desc);
        tvReadMore = view.findViewById(R.id.tv_read_more);
        recSocialMedia = view.findViewById(R.id.rec_social_media);
        recSocialMedia.setHasFixedSize(true);
        tvAmenityViewAll = view.findViewById(R.id.tv_view_all_o_a);
        recSocialMedia.setAdapter(new SocialMediaAdapter());
        socialMediaBtn = view.findViewById(R.id.in_socialMeida);
        smArrow = view.findViewById(R.id.in_smArrow);
        tvDeViewAll = view.findViewById(R.id.tv_de_view_all);
        tvViewAllProducts = view.findViewById(R.id.tv_view_all_products);
        ohArrow = view.findViewById(R.id.in_ohArrow);
        recDesableFacility = view.findViewById(R.id.in_deRecycler);
        recDesableFacility.setHasFixedSize(true);
        recDesableFacility.setAdapter(new DesableFacilityAdapter());
        ohLinear = view.findViewById(R.id.in_ohLinear);
        mtLayout = view.findViewById(R.id.mt_linear);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        glViewAll = view.findViewById(R.id.in_glViewAll);
        avgRating = view.findViewById(R.id.in_avgRating);
        revCount = view.findViewById(R.id.in_revCount);
        ratingBar = view.findViewById(R.id.in_ratingBar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        whRecyclerView = view.findViewById(R.id.whRecycler);
        whRecyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        crRecyclerView = view.findViewById(R.id.in_crRecycler);
        crRecyclerView.setLayoutManager(linearLayoutManager1);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        prRecyclerView = view.findViewById(R.id.in_prRecycler);
        prRecyclerView.setLayoutManager(linearLayoutManager2);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mtRecyclerView = view.findViewById(R.id.mtRecycler);
        mtRecyclerView.setLayoutManager(linearLayoutManager3);
        GridLayoutManager linearLayoutManager4 = new GridLayoutManager(getActivity(), 5);
        pmRecyclerView = view.findViewById(R.id.in_pmRecycler);
        pmRecyclerView.setLayoutManager(linearLayoutManager4);
        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(getActivity());
        ofRecyclerView = view.findViewById(R.id.in_ofRecycler);
        view.findViewById(R.id.layout_location).setOnClickListener(this);
        meetTeamAdapter = new MeetTeamAdapter(displayList);
        ofRecyclerView.setLayoutManager(linearLayoutManager5);
        LinearLayoutManager linearLayoutManager6 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        gRecyclerView = view.findViewById(R.id.in_gRecycler);
        gRecyclerView.setLayoutManager(linearLayoutManager6);
        ratingImgAdapter = new RatingImgAdapter(imgList);
        if (msgStatus == 0) {
            msgBtn.setBackground(getResources().getDrawable(R.drawable.green_rect));
            msgBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            msgBtn.setBackground(getResources().getDrawable(R.drawable.gray_rect));
            msgBtn.setTextColor(getResources().getColor(R.color.black));
        }
        setOnClickListener();
        getBusinessData(true);
    }

    private void getLatLong(String postCode, Boolean isProgress) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        if (ObjectUtil.isNonEmptyStr(this.headerToken)) {
            if (isProgress)
                progress.showProgressBar();
            apiInterface.getLatLng("https://postcodes.io/postcodes/"+postCode).enqueue(new Callback<GetLatLngResponse>() {
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
                                    bLat = String.valueOf(editCatResponse.getResult().getLatitude());
                                    blong = String.valueOf(editCatResponse.getResult().getLongitude());
                                }
                            } else {
//                                if (!TextUtils.isEmpty(editCatResponse.getError()))
//                                    Toast.makeText(getActivity(), editCatResponse.getError(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (!ObjectUtil.isEmpty(response.errorBody())) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String errorMessage = jsonObject.getString("error");
                            if (!TextUtils.isEmpty(errorMessage)) {

                            }
                         } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetLatLngResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        if (progress.isShowing())
                            progress.hideProgressBar();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getBusinessData(false);
    }

    public void reLoadData() {
        getBusinessData(false);
    }

    private void getBusinessData(boolean isProgress) {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }
        if (isProgress)
        progress.showProgressBar();
        GetBusinessRequest request = new GetBusinessRequest(businessId);
        if (isLogin)
            request.setCustomer_id(String.valueOf(customerId));
        apiInterface.getBusinessData(request).enqueue(new Callback<GetBusinessData>() {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            public void onResponse(Call<GetBusinessData> call, Response<GetBusinessData> response) {
                pmList.clear();
                whList.clear();
                imgList.clear();
                mList.clear();
                gList.clear();
                oList.clear();
                rList.clear();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (!TextUtils.isEmpty(response.body().getPayload().getEmail())) {
                            email = response.body().getPayload().getEmail();
                            view.findViewById(R.id.layout_email).setVisibility(View.VISIBLE);
                        } else view.findViewById(R.id.layout_email).setVisibility(View.GONE);

                        if (response.body().getPayload().getContactInfo() != null) {
                            if (!TextUtils.isEmpty(response.body().getPayload().getContactInfo().getContactNumber())) {
                                number = response.body().getPayload().getContactInfo().getContactNumber();
                                view.findViewById(R.id.layout_call).setVisibility(View.VISIBLE);
                                phoNumber.setText(response.body().getPayload().getContactInfo().getContactNumber());
                            } else {
                                view.findViewById(R.id.layout_call).setVisibility(View.GONE);
                                phoNumber.setText("N/A");
                            }
                            if (!TextUtils.isEmpty(response.body().getPayload().getContactInfo().getWebsite())) {
                               businessLink=response.body().getPayload().getContactInfo().getWebsite();
                            }
                            if (!TextUtils.isEmpty(response.body().getPayload().getContactInfo().getLat()) && !TextUtils.isEmpty(response.body().getPayload().getContactInfo().getBlong()) )
                            {
                                bLat=response.body().getPayload().getContactInfo().getLat();
                                blong=response.body().getPayload().getContactInfo().getBlong();
                            }
                            else if ((TextUtils.isEmpty(response.body().getPayload().getContactInfo().getLat())) && (TextUtils.isEmpty(response.body().getPayload().getContactInfo().getBlong())) && ((!TextUtils.isEmpty(response.body().getPayload().getContactInfo().getPostcode()))))
                            {
                                getLatLong(response.body().getPayload().getContactInfo().getPostcode(),false);
                            }
                            if (!TextUtils.isEmpty(response.body().getPayload().getContactInfo().getPostcode())){
                                view.findViewById(R.id.layout_location).setVisibility(View.VISIBLE);
                            }else{
                                view.findViewById(R.id.layout_location).setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(response.body().getPayload().getContactInfo().getEmail())){
                                view.findViewById(R.id.layout_email).setVisibility(View.VISIBLE);
                            }else{
                                view.findViewById(R.id.layout_email).setVisibility(View.GONE);
                            }
                            if (response.body().getPayload().getBusiness_logo() != null) {
                                businessLogo = response.body().getPayload().getBusiness_logo();
                            }
                            BusinessUserId = response.body().getPayload().getBusinessData().getUserId();
                            businessName = response.body().getPayload().getBusinessData().getBusinessName();
                            msgStatus = response.body().getPayload().getBusinessData().getMessage_status();

                            if (msgStatus == 0) {
                                msgBtn.setVisibility(View.VISIBLE);
                                msgBtn.setBackground(getResources().getDrawable(R.drawable.green_rect));
                                msgBtn.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                msgBtn.setBackground(getResources().getDrawable(R.drawable.gray_rect));
                                msgBtn.setTextColor(getResources().getColor(R.color.black));
                                msgBtn.setVisibility(View.GONE);
                            }
                            orderStatus=response.body().getPayload().getBusinessData().getOrderNow();
                            if (response.body().getPayload().getBusinessData().getOrderNow()==null) {
                                orderNow.setVisibility(View.GONE);
                                orderNow.setBackground(getResources().getDrawable(R.drawable.gray_rect));
                                orderNow.setTextColor(getResources().getColor(R.color.black));

                                tvBookNow.setVisibility(View.GONE);
                                tvBookNow.setBackground(getResources().getDrawable(R.drawable.gray_rect));
                                tvBookNow.setTextColor(getResources().getColor(R.color.black));

                            } else {

                                orderNow.setVisibility(View.VISIBLE);
                                orderNow.setBackground(getResources().getDrawable(R.drawable.green_rect));
                                orderNow.setTextColor(getResources().getColor(R.color.white));


                                tvBookNow.setVisibility(View.VISIBLE);
                                tvBookNow.setBackground(getResources().getDrawable(R.drawable.green_rect));
                                tvBookNow.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                        if (response.body().getPayload().getAvrgRateing() == null) {
                            avgRating.setText("0.0");
                        } else {
                            avgRating.setText(String.valueOf(response.body().getPayload().getAvrgRateing()).substring(0, 3));
                            ratingBar.setRating(Float.parseFloat(String.valueOf(response.body().getPayload().getAvrgRateing())));
                        }
                        socialMediaList.clear();
                        displayList.clear();
                        disableFacilityList.clear();

                        revCount.setText(response.body().getPayload().getReviewsCount() + " Reviews");
                        ((BusinessProfile) getActivity()).userData(response.body().getPayload().getBusinessData().getBusinessName(), response.body().getPayload().getSubscribed(), response.body().getPayload().getBookmarked());
                        getActivity().sendBroadcast(new Intent("user_rating").putExtra("rating", response.body().getPayload().getAvrgRateing()).putExtra("review_count", response.body().getPayload().getReviewsCount()));

                        if (response.body().getPayload().getBusinessDesc() != null) {
                            view.findViewById(R.id.business_desc_layout).setVisibility(View.VISIBLE);
                            busDescription.setText(response.body().getPayload().getBusinessDesc().getBusinessDesc());
                            busDescription.post(new Runnable() {
                                @Override
                                public void run() {
                                    int lineCount = busDescription.getLineCount();
                                    if (lineCount >= 2) {
                                        busDescription.setMaxLines(2);
                                        busDescription.setVisibility(View.VISIBLE);
                                    } else {
                                        tvReadMore.setVisibility(View.GONE);
                                    }
                                }
                            });

                        } else
                            view.findViewById(R.id.business_desc_layout).setVisibility(View.GONE);
                        if (response.body().getPayload().getDisabledFacilities().size() > 0) {
                            view.findViewById(R.id.disable_facilities_layout).setVisibility(View.VISIBLE);
                            disableFacilityList.addAll(response.body().getPayload().getDisabledFacilities());
                            recDesableFacility.setVisibility(View.VISIBLE);
                            recDesableFacility.getAdapter().notifyDataSetChanged();
                            if (response.body().getPayload().getDisabledFacilities().size() > 3) {
                                tvDeViewAll.setVisibility(View.VISIBLE);
                            } else
                                tvDeViewAll.setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.disable_facilities_layout).setVisibility(View.GONE);
                            tvDeViewAll.setVisibility(View.GONE);
                        }

                        if (response.body().getPayload().getSocialMedia().size() > 0) {
                            view.findViewById(R.id.social_media_layout).setVisibility(View.VISIBLE);
                            socialMediaList.addAll(response.body().getPayload().getSocialMedia());
                            smArrow.setVisibility(View.VISIBLE);
                            socialMediaBtn.setEnabled(true);
                            recSocialMedia.getAdapter().notifyDataSetChanged();
                        } else {
                            view.findViewById(R.id.social_media_layout).setVisibility(View.GONE);
                            smArrow.setVisibility(View.GONE);
                            socialMediaBtn.setEnabled(false);
                        }
                        whList.clear();

//                        int intdex=0;
//                        for (WorkingHours workingHour:response.body().getPayload().getWorkingHours())
//                        {
//                            if (whList.size()>0)
//                            {
//                                whList.add(workingHour);
//                            }
//                            if (workingHour.getDayName().equalsIgnoreCase(getCurrentDayName()))
//                            {
//                                intdex=response.body().getPayload().getWorkingHours().indexOf(workingHour);
//                                whList.add(workingHour);
//                            }
//                        }
//                        for (int i=0;i<intdex;i++)
//                        {
//                            whList.add(response.body().getPayload().getWorkingHours().get(i));
//                        }
                        if (!TextUtils.isEmpty(response.body().getPayload().getBackground_image()))
                            ((BusinessProfile) getActivity()).displayProfileImages(response.body().getPayload().getBackground_image(), 1);

                        if (!TextUtils.isEmpty(response.body().getPayload().getBusiness_logo()))
                            ((BusinessProfile) getActivity()).displayProfileImages(response.body().getPayload().getBusiness_logo(), 2);

                        if (response.body().getPayload().getSummryLine() != null) {
                            if (!TextUtils.isEmpty(response.body().getPayload().getSummryLine().getSummeryLine()))
                                ((BusinessProfile) getActivity()).displayProfileImages(response.body().getPayload().getSummryLine().getSummeryLine(), 3);
                        }
                        if (response.body().getPayload().getWorkingHours() != null) {
                            if (response.body().getPayload().getWorkingHours().getToday().getStatus().equalsIgnoreCase("Closed")) {
                                tvStatus.setText(response.body().getPayload().getWorkingHours().getToday().getStatus());
                                tvStatus.setTextColor(getResources().getColor(R.color.darkRed));
                            } else if (response.body().getPayload().getWorkingHours().getToday().getStatus().equalsIgnoreCase("Open")) {
                                tvStatus.setText(response.body().getPayload().getWorkingHours().getToday().getStatus());
                                tvStatus.setTextColor(getResources().getColor(R.color.green));
                            }
                            tvOpningTime.setText(response.body().getPayload().getWorkingHours().getToday().getOpen_at());
                            if (response.body().getPayload().getWorkingHours().getAll_working_hours().size() > 0) {
                                view.findViewById(R.id.working_hours_layout).setVisibility(View.VISIBLE);
                                whList.addAll(response.body().getPayload().getWorkingHours().getAll_working_hours());
                                openingHoursAdapter = new OpeningHoursAdapter(whList);
                                whRecyclerView.setAdapter(openingHoursAdapter);
                            } else
                                view.findViewById(R.id.working_hours_layout).setVisibility(View.GONE);
                        } else
                            view.findViewById(R.id.working_hours_layout).setVisibility(View.GONE);

                        crRecyclerView.setVisibility(View.VISIBLE);
                        if (response.body().getPayload().getReputationCredential().size() > 0) {
                            rList = response.body().getPayload().getReputationCredential();
                        } else {
                            view.findViewById(R.id.credential_layout).setVisibility(View.GONE);
                        }
                        reputationalCredAdapter = new ReputationalCredAdapter(rList);
                        crRecyclerView.setAdapter(reputationalCredAdapter);
                        prRecyclerView.setVisibility(View.VISIBLE);
                        if (response.body().getPayload().getProductServices().size() > 0) {
                            view.findViewById(R.id.product_service_layout).setVisibility(View.VISIBLE);
                            pList = response.body().getPayload().getProductServices();
                            if (response.body().getPayload().getProductServices().size() > 3)
                                tvViewAllProducts.setVisibility(View.VISIBLE);
                            else tvViewAllProducts.setVisibility(View.GONE);
                        } else
                            view.findViewById(R.id.product_service_layout).setVisibility(View.GONE);
                        productServiceAdapter = new ProductServiceAdapter(pList, isViewAllProducts);
                        prRecyclerView.setAdapter(productServiceAdapter);
                        if (response.body().getPayload().getMeetTheTeam().size() > 0) {
                            mtLayout.setVisibility(View.VISIBLE);
                            mList = response.body().getPayload().getMeetTheTeam();
                            if (mList.size() > 3) {
                                view.findViewById(R.id.mt_next_img).setVisibility(View.VISIBLE);
                                for (int i = 0; i < 3; i++) {
                                    displayList.add(mList.get(i));
                                }
                            } else
                                displayList.addAll(mList);
                            mtRecyclerView.setAdapter(meetTeamAdapter);
                            view.findViewById(R.id.meet_the_team_layout).setVisibility(View.VISIBLE);
                        } else {
                            mtLayout.setVisibility(View.GONE);
                            view.findViewById(R.id.meet_the_team_layout).setVisibility(View.GONE);
                        }
                        pmRecyclerView.setVisibility(View.VISIBLE);
                        if (response.body().getPayload().getPaymentMethods().size() > 0) {
                            view.findViewById(R.id.payment_method_layout).setVisibility(View.VISIBLE);
                            pmList.addAll(response.body().getPayload().getPaymentMethods());
                        } else
                            view.findViewById(R.id.payment_method_layout).setVisibility(View.GONE);
                        paymentMethodAdapter = new PaymentMethodAdapter(pmList);
                        pmRecyclerView.setAdapter(paymentMethodAdapter);
                        ofRecyclerView.setVisibility(View.VISIBLE);
                        if (response.body().getPayload().getOtherFeatures().size() > 0) {
                            view.findViewById(R.id.other_features_amenities_layout).setVisibility(View.VISIBLE);
                            oList = response.body().getPayload().getOtherFeatures();
                            otherFeatureAdapter = new OtherFeatureAdapter(oList, isOtherAmenities);
                            ofRecyclerView.setAdapter(otherFeatureAdapter);
                            if (response.body().getPayload().getOtherFeatures().size() > 3) {
                                tvAmenityViewAll.setVisibility(View.VISIBLE);
                            } else tvAmenityViewAll.setVisibility(View.GONE);
                        } else
                            view.findViewById(R.id.other_features_amenities_layout).setVisibility(View.GONE);
                        gRecyclerView.setVisibility(View.VISIBLE);
                        if (response.body().getPayload().getGalleryImages().size() > 0) {
                            view.findViewById(R.id.gallery_layout).setVisibility(View.VISIBLE);
                            gList.addAll(response.body().getPayload().getGalleryImages());
                            galleryAdapter = new GalleryAdapter(gList);
                            gRecyclerView.setAdapter(galleryAdapter);
                        } else view.findViewById(R.id.gallery_layout).setVisibility(View.GONE);

                    } else {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                if (progress.isShowing())
                progress.hideProgressBar();
            }

            @Override
            public void onFailure(Call<GetBusinessData> call, Throwable t) {
                if (progress.isShowing())
                    progress.hideProgressBar();
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ObjectUtil.isEmpty(data) && resultCode == Activity.RESULT_OK && requestCode == 101) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            assert resultArray != null;
            String path = resultArray.get(0);
            File file = new File(path);
            Uri imageUri = Uri.fromFile(new File(file.getAbsolutePath()));
            imgList.add(imageUri);
            ratingImgAdapter.notifyDataSetChanged();
            selectedImgList.add(convertMultipart(path, imageUri, "images[]"));
            if (imgLayout != null && selectedImgList.size() > 0) {
                imgLayout.setVisibility(View.VISIBLE);
            }
//            sendImage(convertMultipart(path, imageUri, "image"));
        } else if (resultCode == Activity.RESULT_OK && requestCode == 110) {
            isLogin = true;
            this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        }
    }

    private MultipartBody.Part convertMultipart(String path, Uri imageUri, String key) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
            //TODO for image rotation issue in android 10
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap rotatedBitmap;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bmp, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bmp, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bmp, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bmp;
            }
//                ivUserImage.setImageBitmap(rotatedBitmap);
            File userImage = convertFileFromBitmap(rotatedBitmap);

//            if (!ObjectUtil.isEmpty(userImage))
//                imageFile = userImage;

            RequestBody reqFile = RequestBody.create(userImage, MediaType.parse("image/*"));
            MultipartBody.Part logoPartBody = MultipartBody.Part.createFormData(key, userImage.getName(), reqFile);
            return logoPartBody;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private File convertFileFromBitmap(Bitmap bitmap) {
        String milliSeconds = String.valueOf(System.currentTimeMillis());
        File filesDir = getActivity().getFilesDir();
        File imageFile = new File(filesDir, StringUtil.getStringForID(R.string.app_name) + milliSeconds + ".jpg");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    private void getNextImages() {
        displayList.clear();
        view.findViewById(R.id.mt_previous_img).setVisibility(View.VISIBLE);
        if (count + 3 < mList.size()) {
            for (int i = count; i <= count + 2; i++) {
                if (count <= i) {
                    displayList.add(mList.get(i));
                }
            }
            count = count + 3;
            lastInsert = 3;
        } else {
            for (int i = 0; i < mList.size(); i++) {
                if (count <= i) {
                    displayList.add(mList.get(i));
                }
            }
            view.findViewById(R.id.mt_next_img).setVisibility(View.INVISIBLE);
            lastInsert = mList.size() - count;
            count = mList.size();
        }
        meetTeamAdapter.notifyDataSetChanged();
    }

    private void getPreviousImages() {
        displayList.clear();
        view.findViewById(R.id.mt_next_img).setVisibility(View.VISIBLE);
        if ((count > 3)) {
            count = count - lastInsert;
            for (int i = 0; i < mList.size(); i++) {
                if ((i >= count - 3) && i < count) {
                    displayList.add(mList.get(i));
                }
                if (i == mList.size() - 1) {
                    lastInsert = 3;
                }
            }
            if (count == 3) {
                lastInsert = 0;
                view.findViewById(R.id.mt_previous_img).setVisibility(View.INVISIBLE);
//                Toast.makeText(getActivity(), "first position", Toast.LENGTH_SHORT).show();
            }
        }
        meetTeamAdapter.notifyDataSetChanged();
    }

    public void showDialog() {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_rating);
        TextView tvBusinessName = dialog.findViewById(R.id.tv_business_name);
        tvBusinessName.setText(businessName);
        RecyclerView recImg = dialog.findViewById(R.id.rec_imgs);
        LinearLayout img = dialog.findViewById(R.id.load_img_layout);
        RatingBar ratingBar = dialog.findViewById(R.id.rv_rating);

        EditText etComment = dialog.findViewById(R.id.et_comment);
        imgLayout = dialog.findViewById(R.id.img_layout);
        recImg.setHasFixedSize(true);
        recImg.setAdapter(ratingImgAdapter);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(101)
                        .setCount(1)
                        .setFrontfacing(false)
                        .setExcludeVideos(true)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start(getActivity(), options);
            }
        });
        dialog.findViewById(R.id.btn_post_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(getActivity(), "Please select the rating ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etComment.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Please enter comment", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if (selectedImgList.size() == 0) {
//                    Toast.makeText(getActivity(), "please add at least one image", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                sendReviews(String.valueOf(ratingBar.getRating()), etComment.getText().toString(), dialog);
            }
        });
        dialog.findViewById(R.id.img_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgList.clear();
                selectedImgList.clear();
                dialog.dismiss();
            }
        });
//        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//        text.setText(msg);
//        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

    private void sendReviews(String rating, String comment, Dialog dialog) {
        RequestBody ratingbody = RequestBody.create(rating, MediaType.parse("text/plain"));
        RequestBody commentbody = RequestBody.create(comment, MediaType.parse("text/plain"));
        RequestBody businessIdbody = RequestBody.create(businessId, MediaType.parse("text/plain"));

        progress.showProgressBar();
        apiInterface.customerRating(BEARER.concat(this.headerToken), businessIdbody, ratingbody, commentbody, selectedImgList).enqueue(new Callback<EORegisterResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<EORegisterResponse> call, Response<EORegisterResponse> response) {
                progress.hideProgressBar();
//                Toast.makeText(getActivity(), "onresponse", Toast.LENGTH_SHORT).show();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EORegisterResponse registerResponse = response.body();
                    if (!ObjectUtil.isEmpty(registerResponse)) {
                        if (registerResponse.isStatus() == RESPONSE_SUCCESS) {
//                            Toast.makeText(getActivity(), registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            imgList.clear();
                            submitedReviewDialog();
                        } else {
//                            Toast.makeText(getActivity(), registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<EORegisterResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "on Error", Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
//                    Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void submitedReviewDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.dialog_compete_reviews, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                getActivity().sendBroadcast(new Intent("update_reviews"));
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();

//        Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.setContentView(R.layout.dialog_compete_reviews);
//        TextView img = dialog.findViewById(R.id.btnBack);
//        img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.in_socialMeida:
                if (recSocialMedia.getVisibility() == View.VISIBLE) {
                    recSocialMedia.setVisibility(View.GONE);
                    smArrow.setImageResource(R.drawable.ic_arrow_forward);
                } else {
                    recSocialMedia.setVisibility(View.VISIBLE);
                    smArrow.setImageResource(R.drawable.ic_arrow_down);
                }
                break;
            case R.id.in_msgBtn:
                if (msgStatus == 0) {
                    if (!isLogin)
                    {
                        askLoginDialog();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), ActivityChat.class);
                    intent.putExtra("userName", businessName);
                    intent.putExtra("businessId", String.valueOf(BusinessUserId));
                    intent.putExtra("businessLogo", businessLogo);
                    startActivity(intent);
                } else {
//                    Toast.makeText(getActivity(), "Messaging disabled by business", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_location:
              if (!TextUtils.isEmpty(bLat) && !TextUtils.isEmpty(blong))
              {
                    boolean isAppInstalled = appInstalledOrNot("com.google.android.apps.maps");
                if (isAppInstalled){
                    // Create a Uri from an intent string. Use the result to create an Intent.

                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", Double.parseDouble(bLat),Double.parseDouble(blong));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en_IN&gl=US"));
                    startActivity(intent);
                }
              }
                break;
            case R.id.in_ohLinear:
                if (whRecyclerView.getVisibility() == View.VISIBLE) {
                    whRecyclerView.setVisibility(View.GONE);
                    ohArrow.setImageResource(R.drawable.ic_arrow_forward);
                } else {
                    whRecyclerView.setVisibility(View.VISIBLE);
                    ohArrow.setImageResource(R.drawable.ic_arrow_down);
                }
                break;
            case R.id.fmy_reviewBtn:
                if (isLogin)
                    showDialog();
                else
                    askLoginDialog();
                break;
            case R.id.layout_website:
                String url = "http://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.layout_email:
                if (!TextUtils.isEmpty(email)) {
                    Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                    intent1.setData(Uri.parse("mailto:" + email+"?body=" + Uri.encode("Hello")));
                    intent1.putExtra(Intent.EXTRA_TEXT, "Hello");
                    if (intent1.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent1);
                    }
                }
                break;
            case R.id.layout_call:
                if (!TextUtils.isEmpty(number)) {
                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(intent1);
                }
                break;
            case R.id.tv_read_more:
                if (busDescription.getLineCount() > 2) {
                    tvReadMore.setText("Read More");
                    busDescription.setMaxLines(2);
                } else {
                    tvReadMore.setText("Read Less");
                    busDescription.setMaxLines(Integer.MAX_VALUE);
                }
                break;
            case R.id.view_all_reviews:
                ((BusinessProfile) getActivity()).changeViewpager(2);
                break;
            case R.id.in_glViewAll:
                ((BusinessProfile) getActivity()).changeViewpager(1);
                break;
            case R.id.mt_previous_img:
                getPreviousImages();
                break;
            case R.id.mt_next_img:
                getNextImages();
                break;
            case R.id.tv_de_view_all:
                if (isViewAllDesablily) {
                    isViewAllDesablily = false;
                    tvDeViewAll.setText("View All");
                } else {
                    tvDeViewAll.setText("View Less");
                    isViewAllDesablily = true;
                }
                recDesableFacility.getAdapter().notifyDataSetChanged();
                break;
            case R.id.tv_view_all_o_a:
                if (isOtherAmenities) {
                    isOtherAmenities = false;
                    tvAmenityViewAll.setText("View All");
                } else {
                    tvAmenityViewAll.setText("View Less");
                    isOtherAmenities = true;
                }
                ((OtherFeatureAdapter) ofRecyclerView.getAdapter()).setViewAll(isOtherAmenities);
                ofRecyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.in_book_now:
            case R.id.in_order:
                if (!TextUtils.isEmpty(orderStatus)) {
                    String url2 = orderStatus;
                    if (!orderStatus.startsWith("http://") && !orderStatus.startsWith("https://"))
                        url2 = "http://" + url2;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                    startActivity(browserIntent);
                }
                break;
            case R.id.tv_view_all_products:
                if (isViewAllProducts) {
                    isViewAllProducts = false;
                    tvViewAllProducts.setText("View All");
                } else {
                    tvViewAllProducts.setText("View Less");
                    isViewAllProducts = true;
                }
                ((ProductServiceAdapter) prRecyclerView.getAdapter()).setViewAll(isViewAllProducts);
                prRecyclerView.getAdapter().notifyDataSetChanged();
                break;

            case R.id.layout_copy_link:
                String app_url1 = "https://dev.theappkit.co.uk/Ujamaa/public/deep-linking/" + businessId;
                setClipboard(getActivity(), app_url1);
                break;
            case R.id.layout_more:
                ((BusinessProfile)getActivity()).shareProfile();
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share via");
                String app_url = "https://www.ujamaaonline.co.uk/download?business_id=" + businessId;
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                break;
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
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
        desc.setText("You have to be logged in for write review");
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
                startActivityForResult(new Intent(getActivity(), ViewPagerActivity.class).putExtra("fromFilter", true), 110);
                sDialog.dismiss();
            }
        });
    }


    private void setClipboard(Context context, String text){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    private void setOnClickListener() {
        view.findViewById(R.id.layout_more).setOnClickListener(this);
        socialMediaBtn.setOnClickListener(this);
        ohLinear.setOnClickListener(this);
        view.findViewById(R.id.layout_website).setOnClickListener(this);
        view.findViewById(R.id.layout_email).setOnClickListener(this);
        view.findViewById(R.id.layout_call).setOnClickListener(this);
        tvReadMore.setOnClickListener(this);
        glViewAll.setOnClickListener(this);
        tvAmenityViewAll.setOnClickListener(this);
        view.findViewById(R.id.mt_previous_img).setOnClickListener(this);
        view.findViewById(R.id.mt_next_img).setOnClickListener(this);
        view.findViewById(R.id.view_all_reviews).setOnClickListener(this);
        tvDeViewAll.setOnClickListener(this);
        tvViewAllProducts.setOnClickListener(this);
    }

    private void openPageFromUrl(String url) {
        if ((!url.contains("http")) && (!url.contains("www.")))
            url = "https://www." + url;
        else if (((!url.contains("http"))) && (url.contains("www.")))
            url = "https://" + url;
        if (!GlobalUtil.isValidUrl(url)) {
//            Toast.makeText(getActivity(), "Please enter valid website link", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //todo======================== Social Media Adapter =================================

    class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.SocialMediaViewHolder> {
        @NonNull
        @Override
        public SocialMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SocialMediaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_social_media, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SocialMediaViewHolder holder, int position) {
            SocialMedia socialMedia = socialMediaList.get(position);
            Picasso.get().load(socialMedia.getIcon()).resize(300, 300).into(holder.imgSocial);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPageFromUrl(socialMedia.getUrl());
                }
            });
        }

        @Override
        public int getItemCount() {
            return socialMediaList.size();
        }

        class SocialMediaViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSocial;

            public SocialMediaViewHolder(@NonNull View itemView) {
                super(itemView);
                imgSocial = itemView.findViewById(R.id.image_social);
            }
        }
    }


    //todo======================== Disabled Functionality Adapter =================================

    class DesableFacilityAdapter extends RecyclerView.Adapter<DesableFacilityAdapter.DesableFacilityViewHolder> {
        @NonNull
        @Override
        public DesableFacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DesableFacilityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_other_feature, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DesableFacilityViewHolder holder, int position) {
            DisabledFacility disabledFacility = disableFacilityList.get(position);
            holder.tvNames.setText(disabledFacility.getDisable_data().getFacilitie_name());


        }

        @Override
        public int getItemCount() {
            return !isViewAllDesablily ? disableFacilityList.size() > 3 ? 3 : disableFacilityList.size() : disableFacilityList.size();
        }

        class DesableFacilityViewHolder extends RecyclerView.ViewHolder {
            TextView tvNames;

            public DesableFacilityViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNames = itemView.findViewById(R.id.rof_name);
            }
        }
    }

    //todo ======================= Rating images Adapter ===========================

    class RatingImgAdapter extends RecyclerView.Adapter<RatingImgAdapter.RatingImgViewHolder> {

        private List<Uri> imgList;

        public RatingImgAdapter(List<Uri> imgList) {
            this.imgList = imgList;
        }

        @NonNull
        @Override
        public RatingImgAdapter.RatingImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RatingImgAdapter.RatingImgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rating_img, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RatingImgAdapter.RatingImgViewHolder holder, int position) {
            holder.imageView.setImageURI(imgList.get(position));
        }

        @Override
        public int getItemCount() {
            return imgList.size();
        }

        class RatingImgViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public RatingImgViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.img_rating);
            }
        }
    }

    private void loadReportimgDialog(String url, ImageView img) {
        Glide.with(getActivity())
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().override(600, 600))
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
}
