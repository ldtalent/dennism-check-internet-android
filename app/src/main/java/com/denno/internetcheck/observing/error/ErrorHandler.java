package com.denno.internetcheck.observing.error;

public interface ErrorHandler {
    void handleError(final Exception exception, final String message);
}
