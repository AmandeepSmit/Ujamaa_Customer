package com.ujamaaonline.customer.utils;


import com.ujamaaonline.customer.gps.GpsLocationProvider;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {

    //TODO for GPS provider Update
    private PublishSubject<GpsLocationProvider> gpsProviderSubject = PublishSubject.create();

    public Observable<GpsLocationProvider> toObservableGpsProvider() {
        return gpsProviderSubject;
    }

    public void send(GpsLocationProvider gpsLocationProvider) {
        gpsProviderSubject.onNext(gpsLocationProvider);
    }

}
