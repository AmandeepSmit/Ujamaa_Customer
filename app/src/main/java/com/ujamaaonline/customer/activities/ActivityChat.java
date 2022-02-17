package com.ujamaaonline.customer.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.MessageAdapter;
import com.ujamaaonline.customer.adapters.QuoteAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.chat_models.ChatModel;
import com.ujamaaonline.customer.models.chat_models.ChatUserList;
import com.ujamaaonline.customer.models.chat_models.DataModel;
import com.ujamaaonline.customer.models.chat_models.NotificationModel;
import com.ujamaaonline.customer.models.chat_models.QuoteModel;
import com.ujamaaonline.customer.models.chat_models.RootModel;
import com.ujamaaonline.customer.models.chat_models.sendImageResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.BaseUtil;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ujamaaonline.customer.utils.Constants.BEARER;
import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;

public class ActivityChat extends AppCompatActivity implements View.OnClickListener {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private ImageView add, more, quotes, closeQuotes;
    private EditText enterMsg;
    private ImageView sendMsg, back;
    private String customerId;
    private String businessId, businessName, businessLogo, userName, isSeen;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    private SessionSecuredPreferences loginPreferences;
    private APIClient.APIInterface apiInterface;
    private String headerToken;
    private GlobalProgressDialog progress;
    private RecyclerView msgRecyclerView, quRecyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatModel> mList = new ArrayList<>();
    private String disableStatus, leaveByCustomer, leaveByBusiness, blockByBusiness, blockByCustomer, readStatus, archiveStatus, blockStatus;
    private TextView leaveUserName, userNameTextView, leaveTextView, archiveTextView, lastSeenTextView, quateSeenTextView;
    private LinearLayout leaveLayout, msgLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double lat, lng;
    private boolean isActive = false;
    private List<QuoteModel> qList = new ArrayList<>();
    private ChatUserList us;
    private String userImgUrl;
    private String token, type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    private void initViews() {
        isActive = true;
        progress = new GlobalProgressDialog(ActivityChat.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        this.customerId = String.valueOf(this.loginPreferences.getInt(Constants.USER_ID, 0));
        userName = loginPreferences.getString(Constants.USER_NAME, "");
        add = findViewById(R.id.ac_add);
        more = findViewById(R.id.ac_more);
        quotes = findViewById(R.id.ac_quotes);
        enterMsg = findViewById(R.id.ac_msg);
        sendMsg = findViewById(R.id.ac_send);
        lastSeenTextView = findViewById(R.id.ac_lastSeen);
        bottomSheet = findViewById(R.id.qt_bottom_sheets);
        leaveLayout = findViewById(R.id.ac_leaveLayout);
        leaveUserName = findViewById(R.id.ac_leaveUserName);
        msgLayout = findViewById(R.id.ac_msgBar);
        back = findViewById(R.id.ac_back);
        userNameTextView = findViewById(R.id.ac_userName);
        leaveTextView = findViewById(R.id.ac_leave);
        archiveTextView = findViewById(R.id.ac_archive);
        quRecyclerView = findViewById(R.id.qb_recycler);
        quateSeenTextView = findViewById(R.id.tv_quate_seen);
        closeQuotes = findViewById(R.id.qbs_cross);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        businessLogo = getIntent().getStringExtra("businessLogo");
        businessId = getIntent().getStringExtra("businessId");
        businessName = getIntent().getStringExtra("userName");
        userNameTextView.setText(businessName);
        leaveByBusiness = getIntent().getStringExtra("leaveByBusiness");
        leaveByCustomer = getIntent().getStringExtra("leaveByCustomer");
        blockByBusiness = getIntent().getStringExtra("blockByBusiness");
        blockByCustomer = getIntent().getStringExtra("blockByCustomer");
        archiveStatus = getIntent().getStringExtra("archiveStatus");
        readStatus = getIntent().getStringExtra("readStatus");
        disableStatus = getIntent().getStringExtra("messagingStatus");

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("getStatus"));

//        if (mList.size()==0){
//        sendFirstTimeMsg();
//        }

        if (readStatus != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
            HashMap<String, Object> data2 = new HashMap<>();
            data2.put("markUnread", "0");
            reference.updateChildren(data2);
        }

        if (blockByBusiness != null || disableStatus != null) {
            if (blockByBusiness.equals("1") || disableStatus.equals("1")) {
                blockViews();
            } else {
                unBlockViews();
            }
        }
        if (leaveByBusiness != null) {
            if (leaveByBusiness.equals("1")) {
                leaveViews();
            }
        }
        getChatUser(getApplicationContext());
        checkOnlineStatus();
        callAdapter();
        setOnClickListener();
        readMessages();
        checkUser();
        getQuoteList();
        DatabaseReference c_reference;
        c_reference = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
        c_reference.child("is_seen").setValue("0");

    }

    private void checkUser() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("messages");
        rootRef.child(String.valueOf(customerId) + "_" + businessId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(String.valueOf(customerId) + "_" + businessId)) {
                    sendFirstTimeMsg();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setOnClickListener() {
        back.setOnClickListener(this);
        add.setOnClickListener(this);
        more.setOnClickListener(this);
        quotes.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
        leaveTextView.setOnClickListener(this);
        archiveTextView.setOnClickListener(this);
        closeQuotes.setOnClickListener(this);
        userNameTextView.setOnClickListener(this);
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            blockByBusiness = intent.getStringExtra("blockStatus");
            leaveByBusiness = intent.getStringExtra("leaveStatus");
            disableStatus = intent.getStringExtra("messagingStatus");
            if (customerId.equals(intent.getStringExtra("customerId"))) {
                if (blockByCustomer.equals("1")) {
                    blockViews();
                    return;
                } else {
                    unBlockViews();
                }
            }
            if (customerId.equals(intent.getStringExtra("customerId"))) {
                if (leaveByCustomer.equals("1")) {
                    leaveViews();
                }
            }
        }
    };

    //todo get location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,}, 101);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    sendLocation();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_userName:
                Intent intent = new Intent(ActivityChat.this, BusinessProfile.class);
                intent.putExtra("businessCatId", String.valueOf(businessId));
                intent.putExtra("businessLogo", businessLogo);
                startActivity(intent);
                break;
            case R.id.qbs_cross:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.ac_back:
                if (getIntent() != null)
                    if (getIntent().hasExtra("notification"))
                        startActivity(new Intent(this, MainActivity.class));
                    else
                        super.onBackPressed();
                break;
            case R.id.ac_send:
                sendMessage();
                break;
            case R.id.ac_add:
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActivityChat.this);
                View sheetView = this.getLayoutInflater().inflate(R.layout.dialog_add_data, null);
                mBottomSheetDialog.setContentView(sheetView);
                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                LinearLayout openGallery = sheetView.findViewById(R.id.layout_open_gallery);
                openGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        ImagePicker.Companion.with(ActivityChat.this)
                                .galleryOnly()
                                .start();
                    }
                });
                LinearLayout openCamera = sheetView.findViewById(R.id.layout_open_camera);
                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        ImagePicker.Companion.with(ActivityChat.this)
                                .cameraOnly()    //User can only select image from Gallery
                                .start();
                    }
                });
                LinearLayout locationLayout = sheetView.findViewById(R.id.layout_your_location);
                locationLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        getLocation();
                    }
                });

                TextView cancel = sheetView.findViewById(R.id.dad_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
                mBottomSheetDialog.show();
                break;
            case R.id.ac_leave:
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
//                Query query = reference.orderByChild("c_remove").equalTo("1");
//                query.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        HashMap<String, Object> data2 = new HashMap<>();
//                        data2.put("c_remove", "0");
//                        reference.child(snapshot.getKey()).updateChildren(data2);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(customerId);
                HashMap<String, Object> data3 = new HashMap<>();
                data3.put("leaveByCustomer", "1");
                reference2.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                HashMap<String, Object> data4 = new HashMap<>();
                data4.put("leaveByCustomer", "1");
                reference3.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                break;
            case R.id.ac_archive:
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                HashMap<String, Object> data2 = new HashMap<>();
                data2.put("archive", "1");
                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        messageAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.ac_more:
                BottomSheetDialog mBottomSheetDialog1 = new BottomSheetDialog(ActivityChat.this);
                View sheetView1 = this.getLayoutInflater().inflate(R.layout.dialog_chat_more, null);
                mBottomSheetDialog1.setContentView(sheetView1);
                LinearLayout flag = sheetView1.findViewById(R.id.dcm_flagLayout);
                LinearLayout read = sheetView1.findViewById(R.id.dcm_markLayout);
                LinearLayout archive = sheetView1.findViewById(R.id.dcm_archiveLayout);
                LinearLayout block = sheetView1.findViewById(R.id.dcm_blockLayout);
                LinearLayout leave = sheetView1.findViewById(R.id.dcm_leaveLayout);
                TextView cancelBtn = sheetView1.findViewById(R.id.dcm_cancel);
                TextView blockTextView = sheetView1.findViewById(R.id.blockTextView);
                TextView readTextView = sheetView1.findViewById(R.id.dcm_readTextView);
                TextView archiveTextView = sheetView1.findViewById(R.id.dcm_archiveTextView);
                ((View) sheetView1.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                mBottomSheetDialog1.show();

                flag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog1.dismiss();
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                        HashMap<String, Object> data2 = new HashMap<>();
                        data2.put("flag", "1");
                        reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                chatListAdapter.notifyDataSetChanged();
                                //           Toast.makeText(BookingAccepted.this, "Location update", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                if (readStatus != null) {
                    if (readStatus.equals("1")) {
                        readTextView.setText("Mark Unread");
                    } else {
                        readTextView.setText("Mark Read");
                    }
                }

                read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog1.dismiss();
                        if (readStatus != null) {
                            if (readStatus.equals("1")) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                                HashMap<String, Object> data2 = new HashMap<>();
                                data2.put("markUnread", "0");
                                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        readStatus = "0";
                                        readTextView.setText("Mark Read");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                                HashMap<String, Object> data2 = new HashMap<>();
                                data2.put("markUnread", "1");
                                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        readStatus = "1";
                                        readTextView.setText("Mark Unread");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });

                if (archiveStatus != null) {
                    if (archiveStatus.equals("1")) {
                        archiveTextView.setText("Un-Archive");
                    } else {
                        archiveTextView.setText("Archive");
                    }
                }

                archive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog1.dismiss();
                        if (archiveStatus != null) {
                            if (archiveStatus.equals("1")) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                                HashMap<String, Object> data2 = new HashMap<>();
                                data2.put("archive", "0");
                                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        archiveStatus = "0";
                                        archiveTextView.setText("Archive");
                                        Toast.makeText(ActivityChat.this, "Chat Unarchived", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            } else {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                                HashMap<String, Object> data2 = new HashMap<>();
                                data2.put("archive", "1");
                                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        archiveStatus = "1";
                                        archiveTextView.setText("Un-Archive");
                                        Toast.makeText(ActivityChat.this, "Chat archived", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });

                if (blockByCustomer != null) {
                    if (blockByCustomer.equals("1")) {
                        blockTextView.setText("Unblock");
                    } else {
                        blockTextView.setText("Block");
                    }
                }
                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog1.dismiss();
                        if (blockByCustomer != null) {
                            if (blockByCustomer.equals("1")) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(String.valueOf(customerId));
                                HashMap<String, Object> data2 = new HashMap<>();
                                data2.put("blockByCustomer", "0");
                                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        blockByCustomer = "0";
                                        blockTextView.setText("Block");
                                        Toast.makeText(ActivityChat.this, "Business Unblocked", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                                HashMap<String, Object> data3 = new HashMap<>();
                                data3.put("blockByCustomer", "0");
                                reference2.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        blockByCustomer = "0";
                                        blockTextView.setText("Block");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                LayoutInflater li = LayoutInflater.from(ActivityChat.this);
                                View dialogView = li.inflate(R.layout.dialog_reset, null);
                                AlertDialog sDialog = new AlertDialog.Builder(ActivityChat.this).setView(dialogView).setCancelable(false).create();
                                TextView title = dialogView.findViewById(R.id.dr_title);
                                TextView desc = dialogView.findViewById(R.id.dr_desc);
                                TextView back = dialogView.findViewById(R.id.tv_cancel);
                                TextView block = dialogView.findViewById(R.id.tv_reset);
                                back.setText("Back");
                                block.setText("Block");
                                title.setText("Block");
                                desc.setText("By pressing block, the business cannot respond to you nor can you message them. To message them again, and receive a response, you must unblock them.");
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
                                        sDialog.dismiss();
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(String.valueOf(customerId));
                                        HashMap<String, Object> data2 = new HashMap<>();
                                        data2.put("blockByCustomer", "1");
                                        reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                blockByCustomer = "1";
                                                blockTextView.setText("Unblock");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
                                        HashMap<String, Object> data3 = new HashMap<>();
                                        data3.put("blockByCustomer", "1");
                                        reference2.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                blockByCustomer = "1";
                                                blockTextView.setText("Unblock");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                });

                leave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog1.dismiss();
                        LayoutInflater li = LayoutInflater.from(ActivityChat.this);
                        View dialogView = li.inflate(R.layout.dialog_reset, null);
                        AlertDialog sDialog = new AlertDialog.Builder(ActivityChat.this).setView(dialogView).setCancelable(false).create();
                        TextView title = dialogView.findViewById(R.id.dr_title);
                        TextView desc = dialogView.findViewById(R.id.dr_desc);
                        TextView back = dialogView.findViewById(R.id.tv_cancel);
                        TextView leave = dialogView.findViewById(R.id.tv_reset);
                        back.setText("Back");
                        leave.setText("Leave");
                        title.setText("Leave Conversation");
                        desc.setText("If you leave this conversation, the chat and all its contents will disappear and the business will no longer be able to contact you. You can contact the business again at anytime by visiting their profile.");
                        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
                        sDialog.show();

                        leave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sDialog.dismiss();
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(customerId);
                                HashMap<String, Object> data2 = new HashMap<>();
                                data2.put("leaveByCustomer", "1");
                                reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                                HashMap<String, Object> data3 = new HashMap<>();
                                data3.put("leaveByCustomer", "1");
                                reference2.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sDialog.dismiss();
                            }
                        });
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog1.dismiss();
                    }
                });
                break;
            case R.id.ac_quotes:
                isOpen=true;
                updateUnseenQuates();
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int newState) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                        }
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                        }
                    }
                    @Override
                    public void onSlide(@NonNull View view, float v) {
                    }
                });
                break;
        }
    }
    private boolean isOpen=false;
   private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quoteMessages");
    private void updateUnseenQuates(){
        reference = FirebaseDatabase.getInstance().getReference("quoteMessages")
                .child("customer")
                .child(customerId)
                .child(businessId);
        Query query = reference.orderByChild("is_seen").equalTo("1");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (isOpen)
                {
                    isOpen=false;
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("is_seen", "0");
                    reference.child(snapshot.getKey()).updateChildren(data2);
                }
            }
            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void callAdapter() {
        msgRecyclerView = findViewById(R.id.ac_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.scrollToPosition(mList.size() - 1);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        msgRecyclerView.setHasFixedSize(true);
        messageAdapter = new MessageAdapter(mList, getApplicationContext(), customerId, businessId, businessLogo);
        msgRecyclerView.setAdapter(messageAdapter);
        msgRecyclerView.scrollToPosition(mList.size() - 1);

//        if (mList.size()==0){
//            sendFirstTimeMsg();
//        }
    }

    //todo send message method
    private void sendMessage() {
        String m_msg = enterMsg.getText().toString();
        enterMsg.setText("");
        if (m_msg.trim().isEmpty()) {
            Toast.makeText(ActivityChat.this, "Type a message", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference reference;
            reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
            SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDateandTime = sdf.format(new Date());
            HashMap<String, String> data2 = new HashMap<>();
            data2.put("message", m_msg.trim());
            data2.put("senderId", String.valueOf(customerId));
            data2.put("senderName", userName);
            data2.put("senderType", "Customer");
            data2.put("timeStamp", currentDateandTime);
            data2.put("is_seen", "1");
            data2.put("b_remove", "1");
            data2.put("c_remove", "1");
            data2.put("messageType", "text");
            reference.push().setValue(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });

            update(m_msg);
            DatabaseReference c_reference;
            c_reference = FirebaseDatabase.getInstance().getReference("firebase_token").child(businessId);
            c_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                        token = data.get("token");
                        type = data.get("type");
                        if (type == null) {
                            type = "A";
                        }
                        sendmNotification(m_msg, String.valueOf(customerId), userName, type);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    private void sendFirstTimeMsg() {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
        SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDateandTime = sdf.format(new Date());
        HashMap<String, String> data2 = new HashMap<>();
        data2.put("message", "Hi there. Type a message to begin a conversation with this business. This message can be a question or to book an appointment. The business will be notified about your message.");
        data2.put("senderId", String.valueOf(customerId));
        data2.put("senderName", userName);
        data2.put("senderType", "First");
        data2.put("timeStamp", currentDateandTime);
        data2.put("is_seen", "1");
        data2.put("b_remove", "1");
        data2.put("c_remove", "1");
        data2.put("messageType", "text");
        reference.push().setValue(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    //    private List<ChatModel> tempMsgList = new ArrayList<>();
    //todo read message
    private void readMessages() {
//        mList.add(new ChatModel("Hi there. Type a message to begin a conversation with this business. This message can be a question or to book an appointment. The business will be notified about your message.", "Business", "", "", "", "", "", "1", "text", ""));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
        Query query = reference.orderByChild("c_remove").equalTo("1");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                ChatModel msgs = new ChatModel(
                        data.get("message"),
                        data.get("senderId"),
                        data.get("senderName"),
                        data.get("senderType"),
                        data.get("timeStamp"),
                        data.get("is_seen"),
                        data.get("b_remove"),
                        data.get("c_remove"),
                        data.get("messageType"),
                        snapshot.getKey());
//                tempMsgList.add(msgs);
                mList.add(msgs);
//                isSeen=msgs.getIs_seen();
                messageAdapter.notifyDataSetChanged();
                msgRecyclerView.scrollToPosition(mList.size() - 1);

                if (isActive) {
                    if (data.get("senderType").equals("Business")) {
                        HashMap<String, Object> data2 = new HashMap<>();
                        data2.put("is_seen", "0");
                        reference.child(snapshot.getKey()).updateChildren(data2);
                        messageAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                ChatModel msgs = new ChatModel(
                        data.get("message"),
                        data.get("senderId"),
                        data.get("senderName"),
                        data.get("senderType"),
                        data.get("timeStamp"),
                        data.get("is_seen"),
                        data.get("b_remove"),
                        data.get("c_remove"),
                        data.get("messageType"),
                        snapshot.getKey());
                for (int i = 1; i < mList.size(); i++) {
                    if (mList.get(i).getKey().equalsIgnoreCase(snapshot.getKey())) {
                        mList.set(i, msgs);
                    }
                }

                messageAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void update(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String m_time = sdf.format(new Date());

//        Long tsLong = System.currentTimeMillis() / 1000;
//        String time_stamp = tsLong.toString();

        DatabaseReference reference, c_reference;
        c_reference = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);

        c_reference.child("message").setValue(msg);
        c_reference.child("businessId").setValue(businessId);
        c_reference.child("businessName").setValue(businessName);
        c_reference.child("chatSubject").setValue("");
        c_reference.child("businessLogo").setValue(businessLogo);
        c_reference.child("blockByCustomer").setValue("0");
        c_reference.child("blockByBusiness").setValue("0");
        c_reference.child("flag").setValue("0");
        c_reference.child("messageType").setValue("text");
        c_reference.child("senderType").setValue("Customer");
        c_reference.child("timeStamp").setValue(m_time);
        c_reference.child("leaveByBusiness").setValue("0");
        c_reference.child("leaveByCustomer").setValue("0");
        c_reference.child("archive").setValue("0");
        c_reference.child("markUnread").setValue("0");
        c_reference.child("messagingStatus").setValue("0");
        reference = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(String.valueOf(customerId));
        reference.child("message").setValue(msg);
        reference.child("customerId").setValue(String.valueOf(customerId));
        reference.child("customerName").setValue(userName);
        reference.child("is_seen").setValue("1");
        reference.child("customerImage").setValue(loginPreferences.getString(Constants.USER_IMAGE, ""));
        reference.child("chatSubject").setValue("");
        reference.child("blockByCustomer").setValue("0");
        reference.child("blockByBusiness").setValue("0");
        reference.child("flag").setValue("0");
        reference.child("messageType").setValue("text");
        reference.child("senderType").setValue("Customer");
        reference.child("timeStamp").setValue(m_time);
        reference.child("leaveByBusiness").setValue("0");
        reference.child("leaveByCustomer").setValue("0");
        reference.child("archive").setValue("0");
        reference.child("markUnread").setValue("0");
        reference.child("messagingStatus").setValue("0");

    }

    //send image
    private void uploadImage(File f1) {
        if (!GlobalUtil.isNetworkAvailable(ActivityChat.this)) {
            UIUtil.showNetworkDialog(ActivityChat.this);
            return;
        }

        RequestBody r_image_one = RequestBody.create(f1, MediaType.parse("image/*"));
        MultipartBody.Part imageFiles = MultipartBody.Part.createFormData("file", f1.getName(), r_image_one);
        apiInterface.uploadImage(BEARER.concat(this.headerToken), imageFiles).enqueue(new Callback<sendImageResponse>() {
            @Override
            public void onResponse(Call<sendImageResponse> call, Response<sendImageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        String url = response.body().getPayload();
                        DatabaseReference reference;
                        reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
                        SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String currentDateandTime = sdf.format(new Date());
                        HashMap<String, String> data2 = new HashMap<>();
                        data2.put("message", url);
                        data2.put("senderId", String.valueOf(customerId));
                        data2.put("senderName", userName);
                        data2.put("senderType", "Customer");
                        data2.put("timeStamp", currentDateandTime);
                        data2.put("is_seen", "1");
                        data2.put("b_remove", "1");
                        data2.put("c_remove", "1");
                        data2.put("messageType", "image");
                        reference.push().setValue(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                        update(enterMsg.getText().toString());
                        DatabaseReference c_reference;
                        c_reference = FirebaseDatabase.getInstance().getReference("firebase_token").child(businessId);
                        c_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                                token = data.get("token");
                                sendmNotification("\uD83D\uDCF7 photo", String.valueOf(customerId), userName, type);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                    }
                } else {
                    Toast.makeText(ActivityChat.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
                progress.hideProgressBar();
            }

            @Override
            public void onFailure(Call<sendImageResponse> call, Throwable t) {
                progress.hideProgressBar();
                Toast.makeText(ActivityChat.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendmNotification(String msg, String id, String userName, String type) {
        if (type.equals("I")) {
            RootModel rootModel = new RootModel(token, "high", new NotificationModel("New Message", msg, id, "default", userName));
            apiInterface.sendNotification(rootModel).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Toast.makeText(ChatMessage.this, response.message(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } else {
            RootModel rootModel = new RootModel(token, "high", new DataModel("New Message", id, msg, userName));
            apiInterface.sendNotification(rootModel).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Toast.makeText(ChatMessage.this, response.message(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    // todo send location
    private void sendLocation() {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
        SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDateandTime = sdf.format(new Date());
        HashMap<String, String> data2 = new HashMap<>();
        data2.put("message", String.valueOf(lat) + " " + String.valueOf(lng));
        data2.put("senderId", String.valueOf(customerId));
        data2.put("senderName", userName);
        data2.put("senderType", "Customer");
        data2.put("timeStamp", currentDateandTime);
        data2.put("is_seen", "1");
        data2.put("b_remove", "1");
        data2.put("c_remove", "1");
        data2.put("messageType", "location");
        reference.push().setValue(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
        update("\uD83D\uDCCD Location");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File finalFile = new File(ImagePicker.Companion.getFilePath(data));
            progress.showProgressBar();
            uploadImage(finalFile);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void updateCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages").child(String.valueOf(customerId) + "_" + businessId);
        Query query = reference.orderByChild("is_seen").equalTo("1");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                if (isActive) {
                    if (data.get("senderType").equals("Business")) {
                        HashMap<String, Object> data2 = new HashMap<>();
                        data2.put("is_seen", "0");
                        reference.child(snapshot.getKey()).updateChildren(data2);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOnlineStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("onlineUser").child(businessId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {
                    if (snapshot.child("isOnline").getValue().equals(true)) {
                        lastSeenTextView.setText("Online");
                    } else {
                        Object time = snapshot.child("lastSeenTimeStamp").getValue();
                        long time1 = Long.parseLong(String.valueOf(time));
                        String t = getTimeAgo(time1);
                        lastSeenTextView.setText(t);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    //todo get chat user list
    private void getChatUser(Context context) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerTable").child(String.valueOf(customerId)).child(businessId);
        Query query = reference.orderByChild("archive").equalTo("0");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> data = (HashMap<String, String>) ds.getValue();
                    us = new ChatUserList(
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

//                    blockStatus=us.getBlockByCustomer();
//                    readStatus=us.getMarkUnread();
//                    archiveStatus=us.getArchive();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private int quateSeenCount = 0;

    private void getQuoteList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quoteMessages")
                .child("customer")
                .child(customerId)
                .child(businessId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                qList.clear();
                quateSeenCount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> data = (HashMap<String, String>) ds.getValue();
                    QuoteModel quoteModel = new QuoteModel(
                            data.get("VATInformation"),
                            data.get("businessAddress"),
                            data.get("businessTelephone"),
                            data.get("jobReferenceNumber"),
                            data.get("jobTitle"),
                            data.get("qouteDate"),
                            data.get("recipientName"),
                            data.get("summaryOfWork"),
                            data.get("typeOfCost"),
                            (Boolean) ds.child("vatRegistered").getValue(),
                            ds.getKey(),
                            data.get("businessEmail"),
                            data.get("price")

                    );
                    if (data.containsKey("is_seen")) {
                        if (data.get("is_seen").equals("1")) {
                            quateSeenCount++;
                        }
                    }
                    qList.add(quoteModel);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setStackFromEnd(true);
                    linearLayoutManager.setReverseLayout(true);
                    quRecyclerView.setLayoutManager(linearLayoutManager);
                    QuoteAdapter quoteAdapter = new QuoteAdapter(qList, businessName);
                    quRecyclerView.setAdapter(quoteAdapter);
                }
                if (quateSeenCount > 0) {
                    quateSeenTextView.setText(String.valueOf(quateSeenCount));
                    quateSeenTextView.setVisibility(View.VISIBLE);
                } else {
                    quateSeenTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void blockViews() {
        if (blockByBusiness.equals("1")) {
            add.setEnabled(false);
            sendMsg.setEnabled(false);
            enterMsg.setEnabled(false);
            add.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.color_gray_text));
            sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.color_gray_text));
        }

        if (disableStatus.equals("1")) {
            add.setEnabled(false);
            sendMsg.setEnabled(false);
            enterMsg.setEnabled(false);
            add.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.color_gray_text));
            sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.color_gray_text));
        }
    }

    private void unBlockViews() {
        if (blockByBusiness.equals("0")) {
            add.setEnabled(true);
            sendMsg.setEnabled(true);
            enterMsg.setEnabled(true);
            add.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
            sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
        }
    }

    private void leaveViews() {
        if (leaveByBusiness.equals("1")) {
            leaveLayout.setVisibility(View.VISIBLE);
            leaveUserName.setText(getIntent().getStringExtra("userName") + " has left the chat");
            add.setEnabled(false);
            sendMsg.setEnabled(false);
            enterMsg.setEnabled(false);
            add.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.color_gray_text));
            sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.color_gray_text));
        }
    }

    private void unLeaveViews() {
        if (leaveByBusiness.equals("0")) {
            leaveLayout.setVisibility(View.GONE);
            add.setEnabled(true);
            sendMsg.setEnabled(true);
            enterMsg.setEnabled(true);
            add.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
            sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
        }
    }


    @Override
    protected void onResume() {
        isActive = true;
        BaseUtil.putSenderAccount(getApplicationContext(), businessId);
        updateCount();
        super.onResume();
    }

    @Override
    protected void onStop() {
        isActive = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseUtil.putSenderAccount(getApplicationContext(), "");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        reference.onDisconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseUtil.putSenderAccount(getApplicationContext(), "");
    }
}
