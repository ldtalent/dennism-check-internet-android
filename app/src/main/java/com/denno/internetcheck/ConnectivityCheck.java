package com.denno.internetcheck;

import android.Manifest;
import android.content.Context;

import androidx.annotation.RequiresPermission;
import com.jakewharton.nopen.annotation.Open;
import com.denno.internetcheck.observing.InternetObservingSettings;
import com.denno.internetcheck.observing.InternetObservingStrategy;
import com.denno.internetcheck.observing.NetworkObservingStrategy;
import com.denno.internetcheck.observing.error.ErrorHandler;
import com.denno.internetcheck.observing.strategy.LollipopNetworkObservingStrategy;
import com.denno.internetcheck.observing.strategy.MarshmallowNetworkObservingStrategy;
import com.denno.internetcheck.observing.strategy.PreLollipopNetworkObservingStrategy;

import io.reactivex.Observable;
import io.reactivex.Single;

@Open public class ConnectivityCheck {
  public final static String LOG_TAG = "ConnectivityCheck";

  protected ConnectivityCheck() {
  }

  public static ConnectivityCheck create() {
    return new ConnectivityCheck();
  }

  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final NetworkObservingStrategy strategy;

    if (Preconditions.isAtLeastAndroidMarshmallow()) {
      strategy = new MarshmallowNetworkObservingStrategy();
    } else if (Preconditions.isAtLeastAndroidLollipop()) {
      strategy = new LollipopNetworkObservingStrategy();
    } else {
      strategy = new PreLollipopNetworkObservingStrategy();
    }

    return observeNetworkConnectivity(context, strategy);
  }

  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static Observable<Connectivity> observeNetworkConnectivity(final Context context,
      final NetworkObservingStrategy strategy) {
    Preconditions.checkNotNull(context, "context == null");
    Preconditions.checkNotNull(strategy, "strategy == null");
    return strategy.observeNetworkConnectivity(context);
  }

  @RequiresPermission(Manifest.permission.INTERNET)
  public static Observable<Boolean> observeInternetConnectivity() {
    InternetObservingSettings settings = InternetObservingSettings.create();
    return observeInternetConnectivity(settings.strategy(), settings.initialInterval(),
        settings.interval(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  @RequiresPermission(Manifest.permission.INTERNET)
  public static Observable<Boolean> observeInternetConnectivity(
      InternetObservingSettings settings) {
    return observeInternetConnectivity(settings.strategy(), settings.initialInterval(),
        settings.interval(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  @RequiresPermission(Manifest.permission.INTERNET)
  protected static Observable<Boolean> observeInternetConnectivity(
          final InternetObservingStrategy strategy, final int initialIntervalInMs,
          final int intervalInMs, final String host, final int port, final int timeoutInMs,
          final int httpResponse, final ErrorHandler errorHandler) {
    checkStrategyIsNotNull(strategy);
    return strategy.observeInternetConnectivity(initialIntervalInMs, intervalInMs, host, port,
        timeoutInMs, httpResponse, errorHandler);
  }

  @RequiresPermission(Manifest.permission.INTERNET)
  public static Single<Boolean> checkInternetConnectivity() {
    InternetObservingSettings settings = InternetObservingSettings.create();
    return checkInternetConnectivity(settings.strategy(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  @RequiresPermission(Manifest.permission.INTERNET)
  public static Single<Boolean> checkInternetConnectivity(InternetObservingSettings settings) {
    return checkInternetConnectivity(settings.strategy(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  @RequiresPermission(Manifest.permission.INTERNET)
  protected static Single<Boolean> checkInternetConnectivity(
          final InternetObservingStrategy strategy,
          final String host, final int port, final int timeoutInMs, final int httpResponse,
          final ErrorHandler errorHandler) {
    checkStrategyIsNotNull(strategy);
    return strategy.checkInternetConnectivity(host, port, timeoutInMs, httpResponse, errorHandler);
  }

  private static void checkStrategyIsNotNull(InternetObservingStrategy strategy) {
    Preconditions.checkNotNull(strategy, "strategy == null");
  }
}
