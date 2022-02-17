package com.ujamaaonline.customer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.dialogs.GlobalAlertDialog;

import io.reactivex.disposables.CompositeDisposable;

import static android.content.Context.LOCATION_SERVICE;

public interface GPSUtils {

    default void decompose(CompositeDisposable compositeDisposable) {
        if (compositeDisposable != null) {
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
        }
    }

    default boolean isGpsEnabled(Context context, Activity activity) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager != null)
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.i("About GPS", "GPS is Enabled in your device");
                    return true;
                } else {
                    showGpsLocationDialog(activity);
                    return false;
                }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    default void showGpsLocationDialog(Activity activity) {
        new GlobalAlertDialog(activity, true, false) {
            @Override
            public void onConfirmation() {
                super.onConfirmation();
                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        }.show(R.string.turn_on_location);
    }
}
