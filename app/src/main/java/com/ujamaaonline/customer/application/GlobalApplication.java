package com.ujamaaonline.customer.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.utils.Constants;
import com.ujamaaonline.customer.utils.LocalizationHelper;
import com.ujamaaonline.customer.utils.ObjectUtil;
import com.ujamaaonline.customer.utils.RxBus;

import org.jetbrains.annotations.NotNull;

public class GlobalApplication extends Application {

    private String packageName;
    private Typeface fontAwesomeTypeface;
    private SessionSecuredPreferences loginPref;
    private SessionSecuredPreferences languagePref;
    private RxBus rxBus;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalizationHelper.onAttach(base, "en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        this.setAppInstance();
        this.rxBus = new RxBus();
    }

    protected void setAppInstance() {
        ApplicationHelper.setApplicationObj(this);
    }

    public Typeface getTypeface() {
        if (this.fontAwesomeTypeface == null) {
            this.initIcons();
        }
        return this.fontAwesomeTypeface;
    }

    public void initIcons() {
        this.fontAwesomeTypeface = Typeface.createFromAsset(this.getAssets(), "fontawesome-webfont.ttf");
    }

    public SessionSecuredPreferences loginPreferences(String preferenceName) {
        if (this.loginPref == null) {
            this.loginPref = new SessionSecuredPreferences(this.getBaseContext(), this.getBaseContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE));
        }
        return this.loginPref;
    }

    public SessionSecuredPreferences languagePreferences(String preferenceName) {
        if (this.languagePref == null) {
            this.languagePref = new SessionSecuredPreferences(this.getBaseContext(), this.getBaseContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE));
        }
        return this.languagePref;
    }

    public Context getContext() {
        return this.getApplicationContext();
    }

    public String phoneFormat() {
        return Constants.phoneFormat;
    }

    public int getResourceId(String resourceName, String defType) {
        return getResourceId(resourceName, defType, this.packageName());
    }

    public int getResourceId(String resourceName, String defType, String packageName) {
        return (!ObjectUtil.isNumber(resourceName) && ObjectUtil.isNonEmptyStr(resourceName)) ? this.getResources().getIdentifier(resourceName, defType, packageName) : 0;
    }

    public PackageInfo packageInfo() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            Toast.makeText(this.getBaseContext(), "Package Not Found : " + e2, Toast.LENGTH_SHORT).show();
        }
        return info;
    }

    public String packageName() {
        if (this.packageName == null) {
            PackageInfo info = this.packageInfo();
            this.packageName = info != null ? info.packageName : "com.smit.vamos";
        }
        return this.packageName;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalizationHelper.setLocale(this);
    }

    public RxBus getBus() {
        return this.rxBus;
    }
}
