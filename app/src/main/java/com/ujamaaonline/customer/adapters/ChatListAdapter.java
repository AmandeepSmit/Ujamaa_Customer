package com.ujamaaonline.customer.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.BatchingListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityChat;
import com.ujamaaonline.customer.models.chat_models.ChatUserList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHolder> {

    private List<ChatUserList> uList;
    private String customerId;

    public ChatListAdapter(List uList, String customerId) {
        this.uList = uList;
        this.customerId = customerId;
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

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, int position) {
       String businessId=uList.get(position).getBusinessId();
        if (!TextUtils.isEmpty(uList.get(position).getBusinessLogo())){
            Picasso.get().load(uList.get(position).getBusinessLogo()).into(holder.userImage);
        }

        SimpleDateFormat df = new SimpleDateFormat("E dd MMM yyyy. HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(uList.get(position).getTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);

        String tm[]=formattedDate.split(" ");
        String fTime=tm[4];
        holder.time.setText(fTime);

        holder.name.setText(uList.get(position).getBusinessName());

        String md = uList.get(position).getMessage();
        if (md != null) {
            if (md.equals("")) {
                md = "\uD83D\uDCF7  photo";
            } else {
                if (md.length() > 36) {
                    md = md.substring(0, 36) + "...";
                }
            }
        }


        if (uList.get(position).getBlockByBusiness().equals("1")) {
            holder.lastMsg.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blockColor));
            holder.lastMsg.setText("You have been blocked by this business");
        } else if (uList.get(position).getLeaveByBusiness().equals("1")) {
            holder.lastMsg.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.label_rejected));
            holder.lastMsg.setText("The business has left the conversation");
        } else if(uList.get(position).getMessagingStatus().equals("1")){
            holder.lastMsg.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_disable));
            holder.lastMsg.setText("This business no longer has messaging...");
        }
         else if(uList.get(position).getBlockByCustomer().equals("1")){
            holder.lastMsg.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.label_rejected));
            holder.lastMsg.setText("Business blocked by you");
        }
        else{
            holder.lastMsg.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_gray_text));
            holder.lastMsg.setText(md);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (uList.get(position).getBlockByCustomer().equals("1"))
                {
                    Toast.makeText(v.getContext(), "pleae unblock this business first to continue chat", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(holder.itemView.getContext(), ActivityChat.class);
                intent.putExtra("leaveByCustomer", uList.get(position).getLeaveByCustomer());
                intent.putExtra("leaveByBusiness", uList.get(position).getLeaveByBusiness());
                intent.putExtra("userName", uList.get(position).getBusinessName());
                intent.putExtra("blockByBusiness",uList.get(position).getBlockByBusiness());
                intent.putExtra("blockByCustomer",uList.get(position).getBlockByCustomer());
                intent.putExtra("businessId",uList.get(position).getBusinessId());
                intent.putExtra("businessLogo",uList.get(position).getBusinessLogo());
                intent.putExtra("archiveStatus",uList.get(position).getArchive());
                intent.putExtra("readStatus",uList.get(position).getMarkUnread());
                intent.putExtra("messagingStatus",uList.get(position).getMessagingStatus());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        if (uList.get(position).getBlockByCustomer().equals("1")) {
            holder.blockTextView.setText("Un-Block");
        } else {
            holder.blockTextView.setText("Block");
        }

        if (uList.get(position).getArchive().equals("1")) {
            holder.archiveTextView.setText("Un-Archive");
        } else {
            holder.archiveTextView.setText("Archive");
        }

        holder.archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                HashMap<String, Object> data2 = new HashMap<>();
                if (uList.get(position).getArchive().equals("1")){
                    data2.put("archive", "0");
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    data2.put("archive", "1");
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        holder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (uList.get(position).getBlockByCustomer().equals("1")){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(customerId);
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("blockByCustomer","0");
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid){
                         notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e){
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    HashMap<String, Object> data3 = new HashMap<>();
                    data3.put("blockByCustomer", "0");
                    reference2.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    LayoutInflater li = LayoutInflater.from(holder.itemView.getContext());
                    View dialogView = li.inflate(R.layout.dialog_reset, null);
                    AlertDialog sDialog = new AlertDialog.Builder(holder.itemView.getContext()).setView(dialogView).setCancelable(false).create();
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
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("businessTable").child(businessId).child(customerId);
                            HashMap<String, Object> data2 = new HashMap<>();
                            data2.put("blockByCustomer", "1");
                            reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                            HashMap<String, Object> data3 = new HashMap<>();
                            data3.put("blockByCustomer", "1");
                            reference2.updateChildren(data3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    notifyDataSetChanged();
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
        });

        holder.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(holder.itemView.getContext());
                View dialogView = li.inflate(R.layout.dialog_reset, null);
                AlertDialog sDialog = new AlertDialog.Builder(holder.itemView.getContext()).setView(dialogView).setCancelable(false).create();
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
                                notifyDataSetChanged();
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("messages");
                                ref.child(customerId+"_"+businessId).removeValue();
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
                                notifyDataSetChanged();
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("messages");
                                ref.child(customerId+"_"+businessId).removeValue();
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

        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uList.get(position).getFlag().equals("1")){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("flag", "0");
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("flag", "1");
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        holder.unread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uList.get(position).getMarkUnread().equals("1")){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("markUnread", "0");
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            uList.get(position).setMarkUnread("0");
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId).child(businessId);
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("markUnread", "1");
                    reference1.updateChildren(data2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           // uList.get(position).setMarkUnread("1");
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        if (uList.get(position).getFlag().equals("1")){
            holder.starIcon.setVisibility(View.VISIBLE);
            holder.starTextView.setText("UnStar");
        }else {
            holder.starIcon.setVisibility(View.GONE);
            holder.starTextView.setText("Star");
        }

        if (uList.get(position).getMarkUnread().equals("1")){
            holder.unReadIcon.setVisibility(View.VISIBLE);
            holder.readTextView.setText("Read");
        }else {
            holder.unReadIcon.setVisibility(View.GONE);
            holder.readTextView.setText("Unread");
        }

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("messages").child(customerId + "_" + businessId);
        Query query = reference.orderByChild("is_seen").equalTo("1");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                int m_unread = 0;
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String, String> data = (HashMap<String, String>) ds.getValue();
                    String us = data.get("senderType");
                    if(us.equals("Business")) {
                        m_unread++;
                    }
                }
                if(m_unread>0){
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(String.valueOf(m_unread));
                    holder.count.setTypeface(holder.count.getTypeface(), Typeface.BOLD);
                    holder.count.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                }else {
                    holder.count.setVisibility(View.GONE);
                    holder.count.setTypeface(holder.count.getTypeface(), Typeface.NORMAL);
                    holder.count.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorGray));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return uList.size();
    }

    public class ChatListHolder extends RecyclerView.ViewHolder {
        private TextView name, time, lastMsg, blockTextView, archiveTextView, readTextView,count,starTextView;
        private LinearLayout archive, block, leave, unread, star;
        private ImageView starIcon,unReadIcon,userImage;

        public ChatListHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rc_name);
            time = itemView.findViewById(R.id.rc_time);
            lastMsg = itemView.findViewById(R.id.rc_lastMsg);
            archive = itemView.findViewById(R.id.rcl_archive);
            block = itemView.findViewById(R.id.rcl_block);
            leave = itemView.findViewById(R.id.rcl_leave);
            unread = itemView.findViewById(R.id.rcl_read);
            star = itemView.findViewById(R.id.rcl_star);
            unReadIcon=itemView.findViewById(R.id.rcl_unReadIcon);
            starIcon=itemView.findViewById(R.id.rcl_starIcon);
            blockTextView = itemView.findViewById(R.id.rcl_blockText);
            archiveTextView = itemView.findViewById(R.id.rcl_archiveText);
            readTextView = itemView.findViewById(R.id.rcl_readText);
            count=itemView.findViewById(R.id.ru_unread);
            starTextView=itemView.findViewById(R.id.rcl_startTextView);
            userImage=itemView.findViewById(R.id.rcl_userImage);
        }
    }
}
