package com.ujamaaonline.customer.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityChat;
import com.ujamaaonline.customer.activities.ProfileImageDialog;
import com.ujamaaonline.customer.models.chat_models.ChatModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.TimeZone;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private List<ChatModel> chatModels;
    private Marker marker;
    private Context context;
    private LatLng latLng;
    private GoogleMap mMap;
    private String customerId, businessId;
    private List<String> mdeiaList = new ArrayList<>();
    private String userImgUrl;

    public MessageAdapter(List<ChatModel> chatModels, Context context, String customerId, String businessId,String userImgUrl) {
        this.chatModels = chatModels;
        this.context = context;
        this.customerId = customerId;
        this.businessId = businessId;
        this.userImgUrl=userImgUrl;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_messages, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

        SimpleDateFormat df = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(chatModels.get(position).getTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());
        String dt = df.format(date);

        String mDate[] = dt.split(" ");
        String formattedDate = mDate[0] + " " + mDate[1] + " " + mDate[2] + " " + mDate[4];

        if (chatModels.get(position).getIs_seen().equals("0")) {
            holder.sdTick.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_sent_tick));
        } else {
            holder.sdTick.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_signle_tick));
        }


        //        if (chatModels.get(position).getSenderType().equals("First")){
//            holder.firstMsgLayout.setVisibility(View.VISIBLE);
//            holder.receiverLayout.setVisibility(View.GONE);
//        }else{
//            holder.firstMsgLayout.setVisibility(View.GONE);
//            holder.receiverLayout.setVisibility(View.VISIBLE);
//        }

        if (chatModels.get(position).getSenderType().equals("Customer")) {
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.receiverLayout.setVisibility(View.GONE);
            holder.imgUser.setVisibility(View.GONE);
            if (chatModels.get(position).getMessageType().equals("text")) {
                holder.sdMsg.setText(chatModels.get(position).getMessage());
                holder.sdDate.setText(formattedDate);
            } else if (chatModels.get(position).getMessageType().equals("image")) {
                holder.sdImage.setVisibility(View.VISIBLE);
                holder.sdMsg.setVisibility(View.GONE);
                holder.sdDate.setText(formattedDate);
                Picasso.get().load(chatModels.get(position).getMessage()).into(holder.sdImage);
            } else if (chatModels.get(position).getMessageType().equals("location")) {
                String lat1[] = chatModels.get(position).getMessage().split(" ");
                String lat2 = lat1[0];
                String lng1[] = chatModels.get(position).getMessage().split(" ");
                String lng2 = lng1[1];
                latLng = new LatLng(Double.parseDouble(lat2), Double.parseDouble(lng2));
                holder.sdDate.setText(formattedDate);
                holder.sdMsg.setVisibility(View.GONE);
                holder.sdImage.setVisibility(View.GONE);
                holder.sdMapView.setVisibility(View.VISIBLE);
            }
        } else {
//            if (position==0) {
//                holder.firstMsgLayout.setVisibility(View.VISIBLE);
//                holder.fmDate.setText(formattedDate);
//            }
            if (chatModels.get(position).getSenderType().equals("First")) {
                holder.receiverLayout.setBackground(context.getResources().getDrawable(R.drawable.left_chat_bg_black));
                holder.rcMsg.setTextColor(context.getResources().getColor(R.color.white));
                holder.rcDate.setTextColor(context.getResources().getColor(R.color.white));
                holder.imgUser.setVisibility(View.VISIBLE);
            } else {
                holder.receiverLayout.setBackground(context.getResources().getDrawable(R.drawable.left_chat_bg));
                holder.rcMsg.setTextColor(context.getResources().getColor(R.color.black));
                holder.rcDate.setTextColor(context.getResources().getColor(R.color.text_color));
                holder.imgUser.setVisibility(View.GONE);
            }


            holder.senderLayout.setVisibility(View.GONE);
            holder.receiverLayout.setVisibility(View.VISIBLE);
            if (chatModels.get(position).getMessageType().equals("text")) {
                holder.rcMsg.setText(chatModels.get(position).getMessage());
                holder.rcDate.setText(formattedDate);
            } else if (chatModels.get(position).getMessageType().equals("image")) {
                holder.rcImage.setVisibility(View.VISIBLE);
                holder.rcMsg.setVisibility(View.GONE);
                Picasso.get().load(chatModels.get(position).getMessage()).into(holder.rcImage);
                holder.rcDate.setText(formattedDate);
            } else if (chatModels.get(position).getMessageType().equals("location")) {
                String lat1[] = chatModels.get(position).getMessage().split(" ");
                String lat2 = lat1[0];
                String lng1[] = chatModels.get(position).getMessage().split(" ");
                String lng2 = lng1[1];
                latLng = new LatLng(Double.parseDouble(lat2), Double.parseDouble(lng2));
                holder.rcDate.setText(formattedDate);
                holder.rcMsg.setVisibility(View.GONE);
                holder.rcImage.setVisibility(View.GONE);
                holder.rcMapView.setVisibility(View.VISIBLE);
            }
        }

        holder.senderLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteMessage(holder.itemView.getContext(), chatModels.get(position).getKey(), position);
                return false;
            }
        });

        holder.receiverLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteMessage(holder.itemView.getContext(), chatModels.get(position).getKey(), position);
                return false;
            }
        });

        holder.sdImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteMessage(holder.itemView.getContext(), chatModels.get(position).getKey(), position);
                return false;
            }
        });

        holder.rcImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteMessage(holder.itemView.getContext(), chatModels.get(position).getKey(), position);
                return false;
            }
        });

        holder.sdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatModels.get(position).getMessageType().equals("image")) {
                    mdeiaList.clear();
                    mdeiaList.add(chatModels.get(position).getMessage());
                    ProfileImageDialog profileImageDialog = ProfileImageDialog.newInstance(mdeiaList);
                    profileImageDialog.setCancelable(false);
                    profileImageDialog.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), ProfileImageDialog.class.getSimpleName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        private LinearLayout senderLayout, receiverLayout, mapLayout, firstMsgLayout;
        private TextView sdMsg, sdDate, sdTime, fmDate;
        private ImageView sdTick, sdImage, rcImage, imgUser;
        private TextView rcMsg, rcDate, rcTime;
        private MapView sdMapView, rcMapView;


        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            senderLayout = itemView.findViewById(R.id.sender_layout);
            receiverLayout = itemView.findViewById(R.id.receiver_layout);
//          mapLayout=itemView.findViewById(R.id.layout_location);

            sdMsg = itemView.findViewById(R.id.rm_sdMsg);
            sdDate = itemView.findViewById(R.id.rm_sdDate);
            sdImage = itemView.findViewById(R.id.rm_sdImage);
            rcMsg = itemView.findViewById(R.id.rm_rcMsg);
            rcDate = itemView.findViewById(R.id.rm_rcDate);
            rcImage = itemView.findViewById(R.id.rm_rcImage);
            sdTick = itemView.findViewById(R.id.rm_sdTick);
            sdMapView = itemView.findViewById(R.id.rm_sdMapView);
            imgUser = itemView.findViewById(R.id.user_img);


            if (sdMapView != null) {
                sdMapView.onCreate(null);
                sdMapView.onResume();
                sdMapView.getMapAsync(this);
            }

            rcMapView = itemView.findViewById(R.id.rm_rcMapView);
            if (rcMapView != null) {
                rcMapView.onCreate(null);
                rcMapView.onResume();
                rcMapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (latLng != null) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            }
        }
    }

    private void deleteMessage(final Context context, final String key, final int position) {
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        androidx.appcompat.app.AlertDialog sDialog = new androidx.appcompat.app.AlertDialog.Builder(context).setView(dialogView).setCancelable(false).create();
        TextView title = dialogView.findViewById(R.id.dr_title);
        TextView desc = dialogView.findViewById(R.id.dr_desc);
        TextView cancel = dialogView.findViewById(R.id.tv_cancel);
        TextView delete = dialogView.findViewById(R.id.tv_reset);
        cancel.setText("Cancel");
        delete.setText("Delete");
        title.setText("Delete");
        desc.setText("Do you want to delete this message?");
        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(sDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        sDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDialog.dismiss();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages").child(customerId + "_" + businessId);
                HashMap<String, Object> data2 = new HashMap<>();
                data2.put("c_remove", "0");
                reference.child(key).updateChildren(data2);
                chatModels.remove(position);
                notifyDataSetChanged();
            }
        });
    }


}
