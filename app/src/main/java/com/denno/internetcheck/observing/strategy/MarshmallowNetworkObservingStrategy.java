package com.denno.internetcheck.observing.strategy;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import com.jakewharton.nopen.annotation.Open;
import com.denno.internetcheck.Connectivity;
import com.denno.internetcheck.observing.NetworkObservingStrategy;

import org.reactivestreams.Publisher;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.denno.internetcheck.ConnectivityCheck.LOG_TAG;


@Open @TargetApi(23) public class MarshmallowNetworkObservingStrategy
    implements NetworkObservingStrategy {
  protected static final String ERROR_MSG_NETWORK_CALLBACK =
      "could not unregister network callback";
  protected static final String ERROR_MSG_RECEIVER = "could not unregister receiver";

  @SuppressWarnings("NullAway") // it has to be initialized in the Observable due to Context
  private ConnectivityManager.NetworkCallback networkCallback;
  private final Subject<Connectivity> connectivitySubject;
  private final BroadcastReceiver idleReceiver;
  private Connectivity lastConnectivity = Connectivity.create();

  @SuppressWarnings("NullAway") // networkCallback cannot be initialized here
  public MarshmallowNetworkObservingStrategy() {
    this.idleReceiver = createIdleBroadcastReceiver();
    this.connectivitySubject = PublishSubject.<Connectivity>create().toSerialized();
  }

  @Override
  public Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final String service = Context.CONNECTIVITY_SERVICE;
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
    networkCallback = createNetworkCallback(context);

    registerIdleReceiver(context);

    final NetworkRequest request =
        new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build();

    manager.registerNetworkCallback(request, networkCallback);

    return connectivitySubject.toFlowable(BackpressureStrategy.LATEST).doOnCancel(new Action() {
      @Override
      public void run() {
        tryToUnregisterCallback(manager);
        tryToUnregisterReceiver(context);
      }
    }).doAfterNext(new Consumer<Connectivity>() {
      @Override
      public void accept(final Connectivity connectivity) {
        lastConnectivity = connectivity;
      }
    }).flatMap(new Function<Connectivity, Publisher<Connectivity>>() {
      @Override
      public Publisher<Connectivity> apply(final Connectivity connectivity) {
        return propagateAnyConnectedState(lastConnectivity, connectivity);
      }
    }).startWith(Connectivity.create(context)).distinctUntilChanged().toObservable();
  }

  protected Publisher<Connectivity> propagateAnyConnectedState(final Connectivity last,
      final Connectivity current) {
    final boolean typeChanged = last.type() != current.type();
    final boolean wasConnected = last.state() == NetworkInfo.State.CONNECTED;
    final boolean isDisconnected = current.state() == NetworkInfo.State.DISCONNECTED;
    final boolean isNotIdle = current.detailedState() != NetworkInfo.DetailedState.IDLE;

    if (typeChanged && wasConnected && isDisconnected && isNotIdle) {
      return Flowable.fromArray(current, last);
    } else {
      return Flowable.fromArray(current);
    }
  }

  protected void registerIdleReceiver(final Context context) {
    final IntentFilter filter = new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    context.registerReceiver(idleReceiver, filter);
  }

  @NonNull protected BroadcastReceiver createIdleBroadcastReceiver() {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(final Context context, final Intent intent) {
        if (isIdleMode(context)) {
          onNext(Connectivity.create());
        } else {
          onNext(Connectivity.create(context));
        }
      }
    };
  }

  protected boolean isIdleMode(final Context context) {
    final String packageName = context.getPackageName();
    final PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isIgnoringOptimizations = manager.isIgnoringBatteryOptimizations(packageName);
    return manager.isDeviceIdleMode() && !isIgnoringOptimizations;
  }

  protected void tryToUnregisterCallback(final ConnectivityManager manager) {
    try {
      manager.unregisterNetworkCallback(networkCallback);
    } catch (Exception exception) {
      onError(ERROR_MSG_NETWORK_CALLBACK, exception);
    }
  }

  protected void tryToUnregisterReceiver(Context context) {
    try {
      context.unregisterReceiver(idleReceiver);
    } catch (Exception exception) {
      onError(ERROR_MSG_RECEIVER, exception);
    }
  }

  @Override
  public void onError(final String message, final Exception exception) {
    Log.e(LOG_TAG, message, exception);
  }

  protected ConnectivityManager.NetworkCallback createNetworkCallback(final Context context) {
    return new ConnectivityManager.NetworkCallback() {
      @Override
      public void onAvailable(Network network) {
        onNext(Connectivity.create(context));
      }

      @Override
      public void onLost(Network network) {
        onNext(Connectivity.create(context));
      }
    };
  }

  protected void onNext(Connectivity connectivity) {
    connectivitySubject.onNext(connectivity);
  }
}
