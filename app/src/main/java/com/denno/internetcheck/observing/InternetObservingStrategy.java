package com.denno.internetcheck.observing;

import com.denno.internetcheck.observing.error.ErrorHandler;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface InternetObservingStrategy {

    Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
                                                    final int intervalInMs, final String host, final int port, final int timeoutInMs,
                                                    final int httpResponse, final ErrorHandler errorHandler);

    Single<Boolean> checkInternetConnectivity(final String host, final int port,
                                              final int timeoutInMs, final int httpResponse, final ErrorHandler errorHandler);

    String getDefaultPingHost();
}
