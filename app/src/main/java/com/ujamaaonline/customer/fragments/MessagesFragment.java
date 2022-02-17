package com.ujamaaonline.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityArchive;
import com.ujamaaonline.customer.adapters.ChatListAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.chat_models.ChatUserList;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.RecyclerViewSwipeHelper;
import com.ujamaaonline.customer.utils.RecyclerViewSwipeToRight;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;

public class MessagesFragment extends Fragment implements View.OnClickListener {
    private SessionSecuredPreferences loginPreferences;
    private String customerId;

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    private RecyclerView cRecyclerView;
    private ImageView filterBtn;
    private String blockText = "Block";
    private List<ChatUserList> uList = new ArrayList<>();
    ChatListAdapter chatListAdapter;
    private GlobalProgressDialog progress;
    private ConstraintLayout noMsgLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.customerId = String.valueOf(this.loginPreferences.getInt(Constants.USER_ID, 0));
        filterBtn = view.findViewById(R.id.mf_filter);
        noMsgLayout=view.findViewById(R.id.mf_no_messageLayout);
        progress = new GlobalProgressDialog(view.getContext());
        progress.showProgressBar();
        cRecyclerView = view.findViewById(R.id.mf_recycler);
        bottomSheet = view.findViewById(R.id.filter_bottom_sheet);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        cRecyclerView.setLayoutManager(linearLayoutManager);
        cRecyclerView.setHasFixedSize(true);
        getChatUser(view.getContext());
        setOnClickListener();
    }

    //todo get chat user list
    private void getChatUser(Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId);
        Query query = reference.orderByChild("leaveByCustomer").equalTo("0");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                uList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    HashMap<String, String> data = (HashMap<String, String>) ds.getValue();
                    ChatUserList us = new ChatUserList(
                            data.get("businessName"),
                            data.get("businessId"),
                            data.get("businessLogo"),
                            data.get("message"),
                            data.get("senderType"),
                            data.get("timeStamp"),
                            data.get("archive"),
                            data.get("blockByBusiness"),
                            data.get("blockByCustomer"),
                            data.get("block"),
                            data.get("flag"),
                            data.get("leaveByBusiness"),
                            data.get("leaveByCustomer"),
                            data.get("markUnread"),
                            data.get("messageType"),
                            data.get("messagingStatus"));

                    if (data.get("archive") !=null){
                    if (data.get("archive").equals("0")){
                        uList.add(us);
                    }}

                    Collections.sort(uList, new Comparator<ChatUserList>() {
                        public int compare(ChatUserList o1, ChatUserList o2) {
                            return o1.getTimeStamp().compareTo(o2.getTimeStamp());
                        }
                    });

                    chatListAdapter = new ChatListAdapter(uList, customerId);
                    cRecyclerView.setAdapter(chatListAdapter);

                    Intent intent=new Intent("getStatus");
                    intent.putExtra("blockStatus",us.getBlockByBusiness());
                    intent.putExtra("leaveStatus",us.getLeaveByBusiness());
                    intent.putExtra("businessId",us.getBusinessId());
                    intent.putExtra("messagingStatus",us.getMessagingStatus());
                    intent.putExtra("statusType", "check");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    if (us.getBlockByCustomer().equals("0")) {
                        blockText = "Block";
                    } else {
                        blockText = "Un-Block";
                    }
                }

                if(snapshot.getValue()==null){
                    noMsgLayout.setVisibility(View.VISIBLE);
                    cRecyclerView.setVisibility(View.GONE);
                }else{
                    noMsgLayout.setVisibility(View.GONE);
                    cRecyclerView.setVisibility(View.VISIBLE);
                }

                if (chatListAdapter!=null) {
                    chatListAdapter.notifyDataSetChanged();
                }
                progress.hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setOnClickListener() {
        filterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.mf_filter:
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(v.getContext());
                View sheetView = this.getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
                mBottomSheetDialog.setContentView(sheetView);
                ImageView tickMsg=sheetView.findViewById(R.id.fl_tickMsg);
                ImageView tickStarred=sheetView.findViewById(R.id.fl_tickStarred);
                ImageView tickArchive=sheetView.findViewById(R.id.fl_tickArchive);
                ImageView tickUnread=sheetView.findViewById(R.id.fl_tickUnread);
                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                mBottomSheetDialog.show();

                tickMsg.setVisibility(View.VISIBLE);
                tickUnread.setVisibility(View.GONE);
                tickStarred.setVisibility(View.GONE);
                tickArchive.setVisibility(View.GONE);

                LinearLayout msgLay = sheetView.findViewById(R.id.fl_allMsgLayout);
                msgLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });

                LinearLayout archiveLay = sheetView.findViewById(R.id.fl_archiveLayout);
                archiveLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        Intent intent = new Intent(getActivity(), ActivityArchive.class);
                        intent.putExtra("type", "archive");
                        startActivity(intent);
                    }
                });

                LinearLayout starLay = sheetView.findViewById(R.id.fl_starLayout);
                starLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        Intent intent = new Intent(getActivity(), ActivityArchive.class);
                        intent.putExtra("type", "star");
                        startActivity(intent);
                    }
                });

                LinearLayout unreadLay = sheetView.findViewById(R.id.fl_unreadLayout);
                unreadLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        Intent intent = new Intent(getActivity(), ActivityArchive.class);
                        intent.putExtra("type", "unread");
                        startActivity(intent);
                    }
                });
                break;


        }
    }
}
