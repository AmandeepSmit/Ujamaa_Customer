package com.ujamaaonline.customer.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.ujamaaonline.customer.fragments.FeedFragment;
import com.ujamaaonline.customer.fragments.HomeFragment;
import com.ujamaaonline.customer.fragments.MessagesFragment;
import com.ujamaaonline.customer.fragments.SearchGalleryFragment;
import com.ujamaaonline.customer.fragments.UserProfileFragment;
import com.ujamaaonline.customer.models.chat_models.ChatUserList;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.ObjectUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;
import static com.ujamaaonline.customer.utils.Constants.IS_LOGGED_IN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String type = "";
    private boolean isLogin = false;
    private SessionSecuredPreferences loginPreferences;
    private BottomNavigationView navigation;
    private String customerId;
    private TextView tvBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        isLogin = loginPreferences.getBoolean(IS_LOGGED_IN, false);
        this.customerId = String.valueOf(this.loginPreferences.getInt(Constants.USER_ID, 0));

        loadFragment(new HomeFragment());
        checkLocationPermission("");
        navigation = findViewById(R.id.bottomnav);
        navigation.setOnNavigationItemSelectedListener(this);




        BottomNavigationItemView itemView = navigation.findViewById(R.id.message_tab);
        View badge = LayoutInflater.from(this).inflate(R.layout.bottom_nav_badge, navigation, false);
        tvBadge = badge.findViewById(R.id.ah_badge);
        itemView.addView(badge);

        getChatUser(MainActivity.this);

        //getSupportFragmentManager().beginTransaction().add(R.id.dashboard_container, new HomeFragment()).commit();
    }

    //todo get chat user list

    private void getChatUser(Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerTable").child(customerId);
        Query query = reference.orderByChild("is_seen").equalTo("1");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
               Integer badgeCount=0;
                tvBadge.setVisibility(View.GONE);
                badgeCount=0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    HashMap<String, String> data = (HashMap<String, String>) ds.getValue();
                    if (data.get("archive") !=null){
                        if (data.get("archive").equals("0")){
                            badgeCount++;
                        }}
                }
                if (badgeCount>0)
                {
                    tvBadge.setVisibility(View.VISIBLE);
                    tvBadge.setText(String.valueOf(badgeCount));
                }
                else
                {
                    tvBadge.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void checkLocationPermission(String type) {
        this.type = type;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            } else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                if (type.equalsIgnoreCase("gallery")) {
                    sendBroadcast(new Intent("location"));
                    this.type = "";
                }
            }
        } else {
            if (type.equalsIgnoreCase("gallery")) {
                sendBroadcast(new Intent("location"));
                this.type = "";
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (type.equalsIgnoreCase("gallery"))
                        sendBroadcast(new Intent("location"));

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                    if (!showRationale) {
                        if (!type.equalsIgnoreCase("searchCat"))
                            ObjectUtil.showPermissionDialog(this);
                    }
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        return myLocation;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.home_tab:
                fragment = new HomeFragment();
                break;
            case R.id.message_tab:
                if (isLogin)
                    fragment = new MessagesFragment();
                else askLoginDialog();
                break;
            case R.id.search_tab:
                fragment = new SearchGalleryFragment();
                break;
            case R.id.feed_tab:
                if (isLogin)
                    fragment = new FeedFragment();
                else askLoginDialog();
                break;
            case R.id.user_tab:
                if (isLogin)
                    fragment = new UserProfileFragment();
                else
                    askLoginDialog();
                break;
        }

        return loadFragment(fragment);
    }

    private void askLoginDialog() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View dialogView = li.inflate(R.layout.dialog_reset, null);
        AlertDialog sDialog = new AlertDialog.Builder(MainActivity.this).setView(dialogView).setCancelable(false).create();
        TextView title = dialogView.findViewById(R.id.dr_title);
        TextView desc = dialogView.findViewById(R.id.dr_desc);
        TextView back = dialogView.findViewById(R.id.tv_cancel);
        TextView block = dialogView.findViewById(R.id.tv_reset);
        back.setText("Cancel");
        block.setText("Login Now");
        title.setText("Login");
        desc.setText("You have to be logged in to use this");
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
                startActivity(new Intent(MainActivity.this, ViewPagerActivity.class));
                sDialog.dismiss();
            }
        });
    }

    public void loadFragmentfromOther(String type, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(type, "");
        navigation.setSelectedItemId(R.id.feed_tab);

        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_container, fragment)
                .commit();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dashboard_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
