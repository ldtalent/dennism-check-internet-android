package com.denno.internetcheck.observing.strategy;

import com.jakewharton.nopen.annotation.Open;
import com.denno.internetcheck.Preconditions;
import com.denno.internetcheck.observing.InternetObservingStrategy;
import com.denno.internetcheck.observing.error.ErrorHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Open
public class SocketInternetObservingStrategy implements InternetObservingStrategy {
    private static final String EMPTY_STRING = "";
    private static final String DEFAULT_HOST = "www.google.com";
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";

    @Override
    public String getDefaultPingHost() {
        return DEFAULT_HOST;
    }

    @Override
    public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
                                                           final int intervalInMs, final String host, final int port, final int timeoutInMs,
                                                           final int httpResponse, final ErrorHandler errorHandler) {
        Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
                "initialIntervalInMs is not a positive number");
        Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number");
        checkGeneralPreconditions(host, port, timeoutInMs, errorHandler);

        final String adjustedHost = adjustHost(host);

        return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
                Schedulers.io()).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(@NonNull Long tick) throws Exception {
                return isConnected(adjustedHost, port, timeoutInMs, errorHandler);
            }
        }).distinctUntilChanged();
    }

    @Override
    public Single<Boolean> checkInternetConnectivity(final String host, final int port,
                                                     final int timeoutInMs, final int httpResponse, final ErrorHandler errorHandler) {
        checkGeneralPreconditions(host, port, timeoutInMs, errorHandler);

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                emitter.onSuccess(isConnected(host, port, timeoutInMs, errorHandler));
            }
        });
    }

    protected String adjustHost(final String host) {
        if (host.startsWith(HTTP_PROTOCOL)) {
            return host.replace(HTTP_PROTOCOL, EMPTY_STRING);
        } else if (host.startsWith(HTTPS_PROTOCOL)) {
            return host.replace(HTTPS_PROTOCOL, EMPTY_STRING);
        }
        return host;
    }

    private void checkGeneralPreconditions(String host, int port, int timeoutInMs,
                                           ErrorHandler errorHandler) {
        Preconditions.checkNotNullOrEmpty(host, "host is null or empty");
        Preconditions.checkGreaterThanZero(port, "port is not a positive number");
        Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number");
        Preconditions.checkNotNull(errorHandler, "errorHandler is null");
    }

    protected boolean isConnected(final String host, final int port, final int timeoutInMs,
                                  final ErrorHandler errorHandler) {
        final Socket socket = new Socket();
        return isConnected(socket, host, port, timeoutInMs, errorHandler);
    }

    protected boolean isConnected(final Socket socket, final String host, final int port,
                                  final int timeoutInMs, final ErrorHandler errorHandler) {
        boolean isConnected;
        try {
            socket.connect(new InetSocketAddress(host, port), timeoutInMs);
            isConnected = socket.isConnected();
        } catch (IOException e) {
            isConnected = Boolean.FALSE;
        } finally {
            try {
                socket.close();
            } catch (IOException exception) {
                errorHandler.handleError(exception, "Could not close the socket");
            }
        }
        return isConnected;
    }
}
