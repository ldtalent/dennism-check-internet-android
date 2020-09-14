package com.denno.internetcheck.observing.strategy;

import com.jakewharton.nopen.annotation.Open;
import com.denno.internetcheck.Preconditions;
import com.denno.internetcheck.observing.InternetObservingStrategy;
import com.denno.internetcheck.observing.error.ErrorHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Open
public class WalledGardenInternetObservingStrategy implements InternetObservingStrategy {
    private static final String DEFAULT_HOST = "https://clients3.google.com/generate_204";
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";

    @Override
    public String getDefaultPingHost() {
        return DEFAULT_HOST;
    }

    @Override
    public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
                                                           final int intervalInMs, final String host, final int port, final int timeoutInMs,
                                                           final int httpResponse,
                                                           final ErrorHandler errorHandler) {

        Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
                "initialIntervalInMs is not a positive number");
        Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number");
        checkGeneralPreconditions(host, port, timeoutInMs, httpResponse, errorHandler);

        final String adjustedHost = adjustHost(host);

        return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
                Schedulers.io()).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(@NonNull Long tick) {
                return isConnected(adjustedHost, port, timeoutInMs, httpResponse, errorHandler);
            }
        }).distinctUntilChanged();
    }

    @Override
    public Single<Boolean> checkInternetConnectivity(final String host, final int port,
                                                     final int timeoutInMs, final int httpResponse, final ErrorHandler errorHandler) {
        checkGeneralPreconditions(host, port, timeoutInMs, httpResponse, errorHandler);

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) {
                emitter.onSuccess(isConnected(host, port, timeoutInMs, httpResponse, errorHandler));
            }
        });
    }

    protected String adjustHost(final String host) {
        if (!host.startsWith(HTTP_PROTOCOL) && !host.startsWith(HTTPS_PROTOCOL)) {
            return HTTPS_PROTOCOL.concat(host);
        }

        return host;
    }

    private void checkGeneralPreconditions(final String host, final int port, final int timeoutInMs,
                                           final int httpResponse, final ErrorHandler errorHandler) {
        Preconditions.checkNotNullOrEmpty(host, "host is null or empty");
        Preconditions.checkGreaterThanZero(port, "port is not a positive number");
        Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number");
        Preconditions.checkNotNull(errorHandler, "errorHandler is null");
        Preconditions.checkNotNull(httpResponse, "httpResponse is null");
        Preconditions.checkGreaterThanZero(httpResponse, "httpResponse is not a positive number");
    }

    protected Boolean isConnected(final String host, final int port, final int timeoutInMs,
                                  final int httpResponse, final ErrorHandler errorHandler) {
        HttpURLConnection urlConnection = null;
        try {
            if (host.startsWith(HTTPS_PROTOCOL)) {
                urlConnection = createHttpsUrlConnection(host, port, timeoutInMs);
            } else {
                urlConnection = createHttpUrlConnection(host, port, timeoutInMs);
            }
            return urlConnection.getResponseCode() == httpResponse;
        } catch (IOException e) {
            errorHandler.handleError(e, "Could not establish connection with WalledGardenStrategy");
            return Boolean.FALSE;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    protected HttpURLConnection createHttpUrlConnection(final String host, final int port,
                                                        final int timeoutInMs) throws IOException {
        URL initialUrl = new URL(host);
        URL url = new URL(initialUrl.getProtocol(), initialUrl.getHost(), port, initialUrl.getFile());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(timeoutInMs);
        urlConnection.setReadTimeout(timeoutInMs);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setUseCaches(false);
        return urlConnection;
    }

    protected HttpsURLConnection createHttpsUrlConnection(final String host, final int port,
                                                          final int timeoutInMs) throws IOException {
        URL initialUrl = new URL(host);
        URL url = new URL(initialUrl.getProtocol(), initialUrl.getHost(), port, initialUrl.getFile());
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(timeoutInMs);
        urlConnection.setReadTimeout(timeoutInMs);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setUseCaches(false);
        return urlConnection;
    }
}
