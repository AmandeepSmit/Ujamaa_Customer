package com.ujamaaonline.customer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BaseUtil {

    private static String PREF_NAME = "login_pref";
    static AlertDialog number_dialog;

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    // check network status (Available or not)
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Network Not Available !!!");
        }
        return false;
    }

    // set user Token
    public static void putUserToken(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    // get user Token
    public static String getUserToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    // set user currency
    public static void putCurrency(Context context, String currency) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currency", currency);
        editor.apply();
    }

    // set user currency
    public static void putUserProfile(Context context,String profileUrl) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_profile","");
        editor.apply();
    }

    // get user currency
    public static String getUserProfile(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("user_profile", "");
    }



    // get user currency
    public static String getCurrency(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("currency", "");
    }

    // set user name
    public static void putUserName(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_name", name);
        editor.apply();
    }

    // get user name
    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("user_name", "");
    }


    // set user login status
    public static void putUserLogIn(Context context, boolean status) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_login", status);
        editor.apply();
    }

    // get user login status
    public static boolean getUserLogIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("is_login", false);
    }

    // set profile update
    public static void putProfileUpdate(Context context, boolean status) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_profile_update", status);
        editor.apply();
    }


    // get profile update
    public static boolean getProfileUpdate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("is_profile_update", false);
    }

    // set user email id
    public static void putUserEmail(Context context, String email) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_email", email);
        editor.apply();
    }


    // get user email_id
    public static String getUserEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("user_email", "");
    }



    // set user account id
    public static void putUserAccount(Context context, String acc_id) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account_id", acc_id);
        editor.apply();
    }

    // get user account id
    public static String getUserAccountId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("account_id","");
    }

    // set user account id
    public static void putPhoneNumber(Context context, String number) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("number", number);
        editor.apply();
    }

    // get user account id
    public static String getPhoneNumber(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("number", "");
    }

    public static void signOut(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void hideKeyboard(Context context, View view) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // set account setting update
    public static void putAccountUpdate(Context context, boolean status) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_account_update", status);
        editor.apply();
    }

    // get account setting update
    public static boolean getAccountUpdate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("is_account_update", false);
    }

    // set user account id
    public static void putSenderAccount(Context context, String acc_id) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sender_id", acc_id);
        editor.apply();
    }

    // get user account id
    public static String getSenderAccountId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString("sender_id", "");
    }

}
