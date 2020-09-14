package com.denno.internetcheck.observing;

import android.content.Context;

import com.denno.internetcheck.Connectivity;

import io.reactivex.Observable;

public interface NetworkObservingStrategy {

    Observable<Connectivity> observeNetworkConnectivity(final Context context);

    void onError(final String message, final Exception exception);
}
