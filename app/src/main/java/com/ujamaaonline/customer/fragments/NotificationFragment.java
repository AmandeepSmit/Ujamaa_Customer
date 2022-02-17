package com.ujamaaonline.customer.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ChangePasswordActivity;
import com.ujamaaonline.customer.adapters.NotificationListAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.NotificationListResponse;
import com.ujamaaonline.customer.models.NotificationPayload;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;
import static com.ujamaaonline.customer.utils.Constants.RESPONSE_SUCCESS;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private TextView noNotification;
    private String headerToken;
    private SessionSecuredPreferences loginPreferences;
    private List<NotificationPayload> nList=new ArrayList<>();
    private NotificationListAdapter notificationListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notification, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        apiInterface= APIClient.getClient();
        progress=new GlobalProgressDialog(getActivity());
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        recyclerView=view.findViewById(R.id.nf_RecyeclerView);
        noNotification=view.findViewById(R.id.nf_noMessgage);
        getNotificationList();
    }

    private void getNotificationList(){
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }

        apiInterface.getNotificationList(Constants.BEARER.concat(this.headerToken)).enqueue(new Callback<NotificationListResponse>() {
            @Override
            public void onResponse(Call<NotificationListResponse> call, Response<NotificationListResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()){
                     nList.addAll(response.body().getNotificationPayload());
                     if (nList.size()>0) {
                         notificationListAdapter = new NotificationListAdapter(nList);
                         recyclerView.setAdapter(notificationListAdapter);
                     }
                     else{
                         recyclerView.setVisibility(View.GONE);
                         noNotification.setVisibility(View.VISIBLE);
                     }
                    }else{
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
//                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificationListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}