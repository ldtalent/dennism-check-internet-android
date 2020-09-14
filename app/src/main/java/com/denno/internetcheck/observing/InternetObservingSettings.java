package com.denno.internetcheck.observing;

import com.denno.internetcheck.observing.error.DefaultErrorHandler;
import com.denno.internetcheck.observing.error.ErrorHandler;
import com.denno.internetcheck.observing.strategy.WalledGardenInternetObservingStrategy;

import java.net.HttpURLConnection;

@SuppressWarnings("PMD") // I want to have the same method names as variable names on purpose
public final class InternetObservingSettings {
    private final int initialInterval;
    private final int interval;
    private final String host;
    private final int port;
    private final int timeout;
    private final int httpResponse;
    private final ErrorHandler errorHandler;
    private final InternetObservingStrategy strategy;

    private InternetObservingSettings(int initialInterval, int interval, String host, int port,
                                      int timeout, int httpResponse, ErrorHandler errorHandler,
                                      InternetObservingStrategy strategy) {
        this.initialInterval = initialInterval;
        this.interval = interval;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.httpResponse = httpResponse;
        this.errorHandler = errorHandler;
        this.strategy = strategy;
    }

    public static InternetObservingSettings create() {
        return new Builder().build();
    }

    private InternetObservingSettings(Builder builder) {
        this(builder.initialInterval, builder.interval, builder.host, builder.port, builder.timeout,
                builder.httpResponse, builder.errorHandler, builder.strategy);
    }

    private InternetObservingSettings() {
        this(builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public int initialInterval() {
        return initialInterval;
    }

    public int interval() {
        return interval;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public int timeout() {
        return timeout;
    }

    public int httpResponse() {
        return httpResponse;
    }

    public ErrorHandler errorHandler() {
        return errorHandler;
    }

    public InternetObservingStrategy strategy() {
        return strategy;
    }

    public final static class Builder {
        private int initialInterval = 0;
        private int interval = 2000;
        private String host = "http://clients3.google.com/generate_204";
        private int port = 80;
        private int timeout = 2000;
        private int httpResponse = HttpURLConnection.HTTP_NO_CONTENT;
        private ErrorHandler errorHandler = new DefaultErrorHandler();
        private InternetObservingStrategy strategy = new WalledGardenInternetObservingStrategy();

        private Builder() {
        }

        public Builder initialInterval(int initialInterval) {
            this.initialInterval = initialInterval;
            return this;
        }

        public Builder interval(int interval) {
            this.interval = interval;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder httpResponse(final int httpResponse) {
            this.httpResponse = httpResponse;
            return this;
        }

        public Builder errorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder strategy(InternetObservingStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public InternetObservingSettings build() {
            return new InternetObservingSettings(this);
        }
    }
}
