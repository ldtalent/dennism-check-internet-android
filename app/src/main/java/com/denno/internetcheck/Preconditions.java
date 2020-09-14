package com.denno.internetcheck;

import android.os.Build;

public final class Preconditions {

    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotNullOrEmpty(String string, String message) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkGreaterOrEqualToZero(int number, String message) {
        if (number < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkGreaterThanZero(int number, String message) {
        if (number <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isAtLeastAndroidLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAtLeastAndroidMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}


