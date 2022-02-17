package com.ujamaaonline.customer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.notification.GetNotificationResponse;
import com.ujamaaonline.customer.models.search_category.GetNotificationRequest;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class NotificationActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private SwitchCompat whenGetMessage, discountDeals, importantUpdate, generalUpdate, events, localDiscountDeals, localImportantUpdate, localGeneralUpdate, localEvents, follows, newSubscribers, foundSomeUseFull;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(NotificationActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        whenGetMessage = findViewById(R.id.nt_updatesForUs);
        whenGetMessage.setOnCheckedChangeListener(this);
        discountDeals = findViewById(R.id.nt_imageLikes);
        discountDeals.setOnCheckedChangeListener(this);
        importantUpdate = findViewById(R.id.nt_profileBookmark);
        importantUpdate.setOnCheckedChangeListener(this);
        generalUpdate = findViewById(R.id.nt_profileShare);
        generalUpdate.setOnCheckedChangeListener(this);
        events = findViewById(R.id.nt_event);
        events.setOnCheckedChangeListener(this);
        foundSomeUseFull = findViewById(R.id.nt_newSubscriber);
        foundSomeUseFull.setOnCheckedChangeListener(this);
        localDiscountDeals = findViewById(R.id.local_imageLikes);
        localDiscountDeals.setOnCheckedChangeListener(this);
        localImportantUpdate = findViewById(R.id.local_profileBookmark);
        localImportantUpdate.setOnCheckedChangeListener(this);
        localGeneralUpdate = findViewById(R.id.local_profileShare);
        localGeneralUpdate.setOnCheckedChangeListener(this);
        localEvents = findViewById(R.id.local_event);
        localEvents.setOnCheckedChangeListener(this);
        follows = findViewById(R.id.nt_postEvent);
        follows.setOnCheckedChangeListener(this);
        newSubscribers = findViewById(R.id.new_newSubscriber);
        newSubscribers.setOnCheckedChangeListener(this);
        updateUserProfile();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isLoaded) {
            SwitchCompat switchCompat = null;
            switch (buttonView.getId()) {
                case R.id.nt_updatesForUs:
                    switchCompat = whenGetMessage;
                    break;
                case R.id.nt_imageLikes:
                    switchCompat = discountDeals;
                    break;
                case R.id.nt_profileBookmark:
                    switchCompat = importantUpdate;
                    break;
                case R.id.nt_event:
                    switchCompat = events;
                    break;
                case R.id.local_imageLikes:
                    switchCompat = localDiscountDeals;
                    break;
                case R.id.local_profileBookmark:
                    switchCompat = localImportantUpdate;
                    break;
                case R.id.local_profileShare:
                    switchCompat = localGeneralUpdate;
                    break;
                case R.id.local_event:
                    switchCompat = localEvents;
                    break;
                case R.id.nt_postEvent:
                    switchCompat = follows;
                    break;
                case R.id.new_newSubscriber:
                    switchCompat = newSubscribers;
                    break;
                case R.id.nt_newSubscriber:
                    switchCompat = foundSomeUseFull;
                    break;
            }
            updateNotification(switchCompat);
        }
    }

    private void updateUserProfile() {
        if (!GlobalUtil.isNetworkAvailable(NotificationActivity.this)) {
            UIUtil.showNetworkDialog(NotificationActivity.this);
            return;
        }
        progress.showProgressBar();

        apiInterface.getNotification(BEARER.concat(this.headerToken)).enqueue(new Callback<GetNotificationResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GetNotificationResponse> call, Response<GetNotificationResponse> response) {
                progress.hideProgressBar();
                if (response.body().getStatus()) {
                    whenGetMessage.setChecked(response.body().getPayload().getGetMessageNoti() == 0 ? false : true);
                    discountDeals.setChecked(response.body().getPayload().getSubscribeDisDeals() == 0 ? false : true);
                    importantUpdate.setChecked(response.body().getPayload().getSubscribeImpUpdates() == 0 ? false : true);
                    generalUpdate.setChecked(response.body().getPayload().getSubscribeGenrlUpdates() == 0 ? false : true);
                    events.setChecked(response.body().getPayload().getSubscribeEvents() == 0 ? false : true);
                    localDiscountDeals.setChecked(response.body().getPayload().getLocalfeedDisDeals() == 0 ? false : true);
                    localImportantUpdate.setChecked(response.body().getPayload().getLocalfeedImpUpdates() == 0 ? false : true);
                    localGeneralUpdate.setChecked(response.body().getPayload().getLocalfeedGenrlUpdates() == 0 ? false : true);
                    localEvents.setChecked(response.body().getPayload().getLocalfeedEvents() == 0 ? false : true);
                    foundSomeUseFull.setChecked(response.body().getPayload().getSomeoneFoundUseful() == 0 ? false : true);
                    isLoaded = true;
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GetNotificationResponse> call, Throwable t){
                if (t.getMessage() != null) {
                    progress.hideProgressBar();


                }
            }
        });
    }

    private void updateNotification(SwitchCompat switchCompat) {
        if (!GlobalUtil.isNetworkAvailable(NotificationActivity.this)) {
            UIUtil.showNetworkDialog(NotificationActivity.this);
            return;
        }
        progress.showProgressBar();
        GetNotificationRequest loginRequest = new GetNotificationRequest();
        loginRequest.setGetMessageNoti(whenGetMessage.isChecked() ? 1 : 0);
        loginRequest.setSubscribeDisDeals(discountDeals.isChecked() ? 1 : 0);
        loginRequest.setSubscribeImpUpdates(importantUpdate.isChecked() ? 1 : 0);
        loginRequest.setSubscribeGenrlUpdates(generalUpdate.isChecked() ? 1 : 0);
        loginRequest.setSubscribeEvents(events.isChecked() ? 1 : 0);
        loginRequest.setLocalfeedDisDeals(localDiscountDeals.isChecked() ? 1 : 0);
        loginRequest.setLocalfeedImpUpdates(localImportantUpdate.isChecked() ? 1 : 0);
        loginRequest.setLocalfeedGenrlUpdates(localGeneralUpdate.isChecked() ? 1 : 0);
        loginRequest.setLocalfeedEvents(localEvents.isChecked() ? 1 : 0);
        loginRequest.setSomeoneFoundUseful(foundSomeUseFull.isChecked() ? 1 : 0);

        apiInterface.updateNotificationSetting(BEARER.concat(this.headerToken), loginRequest).enqueue(new Callback<BusinessListResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<BusinessListResponse> call, Response<BusinessListResponse> response) {
                progress.hideProgressBar();
                if (response.body().getStatus()) {
                    Toast.makeText(NotificationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    switchCompat.setChecked(!switchCompat.isChecked());
                    Toast.makeText(NotificationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<BusinessListResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    switchCompat.setChecked(!switchCompat.isChecked());
                    Toast.makeText(NotificationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void backBtnCick(View view) {
        this.onBackPressed();
    }
}