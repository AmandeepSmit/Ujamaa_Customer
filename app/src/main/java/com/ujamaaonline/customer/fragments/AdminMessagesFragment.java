package com.ujamaaonline.customer.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.adapters.AdminMessageAdapter;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.models.AdminMessageModel;
import com.ujamaaonline.customer.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AdminMessagesFragment extends Fragment {

    private RecyclerView msRecyclerView;
    DatabaseReference reference;
    private SessionSecuredPreferences loginPreferences;
    private String customerId;
    private List<AdminMessageModel> nList=new ArrayList<>();
    private AdminMessageAdapter adminMessageAdapter;
    private GlobalProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_admin_messages, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        progressDialog=new GlobalProgressDialog(getActivity());
        this.loginPreferences = ApplicationHelper.application().loginPreferences(Constants.LOGIN_PREFERENCE);
        this.customerId = String.valueOf(this.loginPreferences.getInt(Constants.USER_ID, 0));
        msRecyclerView=view.findViewById(R.id.adm_RecyeclerView);
        getAdminMessage();
    }

    private void getAdminMessage(){
        reference= FirebaseDatabase.getInstance().getReference("admin_messages_customer").child("1"+"_"+customerId);
        progressDialog.showProgressBar();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    AdminMessageModel adminMessageModel=ds.getValue(AdminMessageModel.class);
                    nList.add(adminMessageModel);
                }
                adminMessageAdapter=new AdminMessageAdapter(nList);
                msRecyclerView.setAdapter(adminMessageAdapter);
                progressDialog.hideProgressBar();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hideProgressBar();
            }
        });
    }
}