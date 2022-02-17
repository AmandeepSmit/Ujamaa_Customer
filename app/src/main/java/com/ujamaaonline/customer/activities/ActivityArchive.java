package com.ujamaaonline.customer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.ChatListAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.chat_models.ChatUserList;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.RecyclerViewSwipeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;

public class ActivityArchive extends AppCompatActivity{
    private SessionSecuredPreferences loginPreferences;
    private int customerId;
    private RecyclerView arRecyclerView;
    private List<ChatUserList> uList=new ArrayList<>();
    ChatListAdapter chatListAdapter;
    private GlobalProgressDialog progress;
    private DatabaseReference reference;
    private LinearLayout layout1;
    private TextView clrFilter,Title,centerTextView,topText;
    private String blockText;
    private ImageView filter;
    private String type,readStatus,archiveStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        initViews(ActivityArchive.this);
    }

    private void initViews(Context context){
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.customerId = this.loginPreferences.getInt(Constants.USER_ID, 0);
        progress=new GlobalProgressDialog(ActivityArchive.this);
        progress.showProgressBar();
        arRecyclerView=findViewById(R.id.aa_recyclerView);
        layout1=findViewById(R.id.ar_layout);
        clrFilter=findViewById(R.id.clr_Filter);
        filter=findViewById(R.id.ar_filter);
        centerTextView=findViewById(R.id.ar_centerText);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        arRecyclerView.setLayoutManager(linearLayoutManager);
        arRecyclerView.setHasFixedSize(true);
        Title=findViewById(R.id.ar_title);
        topText=findViewById(R.id.ar_topText);
        type=getIntent().getStringExtra("type");
        getChatUser(getApplicationContext(),type);

        if (type.equals("archive")){
            Title.setText("Archive");
            centerTextView.setText("We couldn't find any results for "+ "\"Archive"+"\"");
            topText.setVisibility(View.VISIBLE);
        }else if (type.equals("star")){
            topText.setVisibility(View.VISIBLE);
            Title.setText("Starred");
            centerTextView.setText("We couldn't find any results for "+ "\"Starred"+"\"");
        }else if (type.equals("unread")){
            topText.setVisibility(View.VISIBLE);
            Title.setText("Unread");
            centerTextView.setText("We couldn't find any results for "+ "\"Unread"+"\"");
        }

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(v.getContext());
                View sheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
                mBottomSheetDialog.setContentView(sheetView);
                ImageView tickMsg=sheetView.findViewById(R.id.fl_tickMsg);
                ImageView tickStarred=sheetView.findViewById(R.id.fl_tickStarred);
                ImageView tickArchive=sheetView.findViewById(R.id.fl_tickArchive);
                ImageView tickUnread=sheetView.findViewById(R.id.fl_tickUnread);
                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                mBottomSheetDialog.show();

                if (type.equals("archive")){
                    tickArchive.setVisibility(View.VISIBLE);
                    tickUnread.setVisibility(View.GONE);
                    tickStarred.setVisibility(View.GONE);
                    tickMsg.setVisibility(View.GONE);
                }else if (type.equals("star")){
                    tickArchive.setVisibility(View.GONE);
                    tickUnread.setVisibility(View.GONE);
                    tickStarred.setVisibility(View.VISIBLE);
                    tickMsg.setVisibility(View.GONE);
                }else if (type.equals("unread")){
                    tickArchive.setVisibility(View.GONE);
                    tickUnread.setVisibility(View.VISIBLE);
                    tickStarred.setVisibility(View.GONE);
                    tickMsg.setVisibility(View.GONE);
                }

                LinearLayout allMsg= sheetView.findViewById(R.id.fl_allMsgLayout);
                allMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        finish();
                    }
                });

                LinearLayout archiveLay=sheetView.findViewById(R.id.fl_archiveLayout);
                archiveLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        mBottomSheetDialog.dismiss();
                        topText.setVisibility(View.VISIBLE);
                        type="archive";
                        Title.setText("Archive");
                        centerTextView.setText("We couldn't find any results for "+ "\"Archive"+"\"");
                        tickArchive.setVisibility(View.VISIBLE);
                        tickUnread.setVisibility(View.GONE);
                        tickStarred.setVisibility(View.GONE);
                        getChatUser(getApplicationContext(),type);
                    }
                });

                LinearLayout starLay = sheetView.findViewById(R.id.fl_starLayout);
                starLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        topText.setVisibility(View.VISIBLE);
                        type="star";
                        Title.setText("Starred");
                        centerTextView.setText("We couldn't find any results for "+ "\"Starred"+"\"");
                        tickArchive.setVisibility(View.GONE);
                        tickUnread.setVisibility(View.GONE);
                        tickStarred.setVisibility(View.VISIBLE);
                        getChatUser(getApplicationContext(),type);
                    }
                });

                LinearLayout unreadLay = sheetView.findViewById(R.id.fl_unreadLayout);
                unreadLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        mBottomSheetDialog.dismiss();
                        topText.setVisibility(View.VISIBLE);
                        type="unread";
                        Title.setText("Unread");
                        centerTextView.setText("We couldn't find any results for "+ "\"Unread"+"\"");
                        tickArchive.setVisibility(View.GONE);
                        tickUnread.setVisibility(View.VISIBLE);
                        tickStarred.setVisibility(View.GONE);
                        getChatUser(getApplicationContext(),type);
                    }
                });
            }
        });

        clrFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //todo get chat user list
    private void getChatUser(Context context,String fromtype) {
        reference = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId));
        Query query=null;
        //Query query = reference.orderByChild("time_stamp");

        switch (fromtype)
        {
            case "archive":
                query=  reference.orderByChild("archive").equalTo("1");
                break;
            case "star":
                query=  reference.orderByChild("flag").equalTo("1");
                break;
            case "unread":
                query=  reference.orderByChild("markUnread").equalTo("1");
                break;
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                    uList.add(us);
                    chatListAdapter=new ChatListAdapter(uList,String.valueOf(customerId));
                    arRecyclerView.setAdapter(chatListAdapter);
                    chatListAdapter.notifyDataSetChanged();

                    blockText=us.getBlockByCustomer();
                    readStatus=us.getMarkUnread();
                    archiveStatus=us.getArchive();

                    if (us.getBlockByCustomer().equals("0")) {
                        blockText = "Block";
                    } else {
                        blockText = "Un-Block";
                    }

                }
                progress.hideProgressBar();
                if (uList.size()==0){
                    arRecyclerView.setVisibility(View.GONE);
                    layout1.setVisibility(View.VISIBLE);
                }else{
                    arRecyclerView.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                }

//                if(found){
//                    msg.setVisibility(View.GONE);
//                }else{
//                    msg.setVisibility(View.VISIBLE);
//                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}
