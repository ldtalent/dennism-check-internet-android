package com.denno.internetcheck;

import android.net.NetworkInfo;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

public final class ConnectivityPredicate {

    private ConnectivityPredicate() {
    }

    public static Predicate<Connectivity> hasState(final NetworkInfo.State... states) {
        return new Predicate<Connectivity>() {
            @Override
            public boolean test(@NonNull Connectivity connectivity) throws Exception {
                for (NetworkInfo.State state : states) {
                    if (connectivity.state() == state) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Predicate<Connectivity> hasType(final int... types) {
        final int[] extendedTypes = appendUnknownNetworkTypeToTypes(types);
        return new Predicate<Connectivity>() {
            @Override
            public boolean test(@NonNull Connectivity connectivity) throws Exception {
                for (int type : extendedTypes) {
                    if (connectivity.type() == type) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    protected static int[] appendUnknownNetworkTypeToTypes(int[] types) {
        int i = 0;
        final int[] extendedTypes = new int[types.length + 1];
        for (int type : types) {
            extendedTypes[i] = type;
            i++;
        }
        extendedTypes[i] = Connectivity.UNKNOWN_TYPE;
        return extendedTypes;
    }
}
