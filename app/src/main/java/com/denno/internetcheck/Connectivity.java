package com.denno.internetcheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public final class Connectivity {
    static final int UNKNOWN_TYPE = -1;
    static final int UNKNOWN_SUB_TYPE = -1;
    private NetworkInfo.State state;
    private NetworkInfo.DetailedState detailedState;
    private int type;
    private int subType;
    private boolean available;
    private boolean failover;
    private boolean roaming;
    private String typeName;
    private String subTypeName;
    private String reason;
    private String extraInfo;

    public static Connectivity create() {
        return builder().build();
    }

    public static Connectivity create(@NonNull Context context) {
        Preconditions.checkNotNull(context, "context == null");
        return create(context, getConnectivityManager(context));
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        final String service = Context.CONNECTIVITY_SERVICE;
        return (ConnectivityManager) context.getSystemService(service);
    }

    protected static Connectivity create(@NonNull Context context, ConnectivityManager manager) {
        Preconditions.checkNotNull(context, "context == null");

        if (manager == null) {
            return create();
        }

        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo == null) ? create() : create(networkInfo);
    }

    private static Connectivity create(NetworkInfo networkInfo) {
        return new Builder()
                .state(networkInfo.getState())
                .detailedState(networkInfo.getDetailedState())
                .type(networkInfo.getType())
                .subType(networkInfo.getSubtype())
                .available(networkInfo.isAvailable())
                .failover(networkInfo.isFailover())
                .roaming(networkInfo.isRoaming())
                .typeName(networkInfo.getTypeName())
                .subTypeName(networkInfo.getSubtypeName())
                .reason(networkInfo.getReason())
                .extraInfo(networkInfo.getExtraInfo())
                .build();
    }

    private Connectivity(Builder builder) {
        state = builder.state;
        detailedState = builder.detailedState;
        type = builder.type;
        subType = builder.subType;
        available = builder.available;
        failover = builder.failover;
        roaming = builder.roaming;
        typeName = builder.typeName;
        subTypeName = builder.subTypeName;
        reason = builder.reason;
        extraInfo = builder.extraInfo;
    }

    private Connectivity() {
        this(builder());
    }

    private static Builder builder() {
        return new Connectivity.Builder();
    }

    public NetworkInfo.State state() {
        return state;
    }

    public static Builder state(NetworkInfo.State state) {
        return builder().state(state);
    }

    public NetworkInfo.DetailedState detailedState() {
        return detailedState;
    }

    public static Builder state(NetworkInfo.DetailedState detailedState) {
        return builder().detailedState(detailedState);
    }

    public int type() {
        return type;
    }

    public static Builder type(int type) {
        return builder().type(type);
    }

    public int subType() {
        return subType;
    }

    public static Builder subType(int subType) {
        return builder().subType(subType);
    }

    public boolean available() {
        return available;
    }

    public static Builder available(boolean available) {
        return builder().available(available);
    }

    public boolean failover() {
        return failover;
    }

    public static Builder failover(boolean failover) {
        return builder().failover(failover);
    }

    public boolean roaming() {
        return roaming;
    }

    public static Builder roaming(boolean roaming) {
        return builder().roaming(roaming);
    }

    public String typeName() {
        return typeName;
    }

    public static Builder typeName(String typeName) {
        return builder().typeName(typeName);
    }

    public String subTypeName() {
        return subTypeName;
    }

    public static Builder subTypeName(String subTypeName) {
        return builder().subTypeName(subTypeName);
    }

    public String reason() {
        return reason;
    }

    public static Builder reason(String reason) {
        return builder().reason(reason);
    }

    public String extraInfo() {
        return extraInfo;
    }

    public static Builder extraInfo(String extraInfo) {
        return builder().extraInfo(extraInfo);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Connectivity that = (Connectivity) o;

        if (type != that.type) {
            return false;
        }
        if (subType != that.subType) {
            return false;
        }
        if (available != that.available) {
            return false;
        }
        if (failover != that.failover) {
            return false;
        }
        if (roaming != that.roaming) {
            return false;
        }
        if (state != that.state) {
            return false;
        }
        if (detailedState != that.detailedState) {
            return false;
        }
        if (!typeName.equals(that.typeName)) {
            return false;
        }
        if (!Objects.equals(subTypeName, that.subTypeName)) {
            return false;
        }
        if (!Objects.equals(reason, that.reason)) {
            return false;
        }

        return Objects.equals(extraInfo, that.extraInfo);
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + (detailedState != null ? detailedState.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + subType;
        result = 31 * result + (available ? 1 : 0);
        result = 31 * result + (failover ? 1 : 0);
        result = 31 * result + (roaming ? 1 : 0);
        result = 31 * result + typeName.hashCode();
        result = 31 * result + (subTypeName != null ? subTypeName.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (extraInfo != null ? extraInfo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Connectivity{"
                + "state="
                + state
                + ", detailedState="
                + detailedState
                + ", type="
                + type
                + ", subType="
                + subType
                + ", available="
                + available
                + ", failover="
                + failover
                + ", roaming="
                + roaming
                + ", typeName='"
                + typeName
                + '\''
                + ", subTypeName='"
                + subTypeName
                + '\''
                + ", reason='"
                + reason
                + '\''
                + ", extraInfo='"
                + extraInfo
                + '\''
                + '}';
    }

    public final static class Builder {

        // disabling PMD for builder class attributes
        // because we want to have the same method names as names of the attributes for builder

        private NetworkInfo.State state = NetworkInfo.State.DISCONNECTED;
        private NetworkInfo.DetailedState detailedState = NetworkInfo.DetailedState.IDLE;
        private int type = UNKNOWN_TYPE;
        private int subType = UNKNOWN_SUB_TYPE;
        private boolean available = false;
        private boolean failover = false;
        private boolean roaming = false;
        private String typeName = "NONE";
        private String subTypeName = "NONE";
        private String reason = "";
        private String extraInfo = "";

        public Builder state(NetworkInfo.State state) {
            this.state = state;
            return this;
        }

        public Builder detailedState(NetworkInfo.DetailedState detailedState) {
            this.detailedState = detailedState;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder subType(int subType) {
            this.subType = subType;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder failover(boolean failover) {
            this.failover = failover;
            return this;
        }

        public Builder roaming(boolean roaming) {
            this.roaming = roaming;
            return this;
        }

        public Builder typeName(String name) {
            this.typeName = name;
            return this;
        }

        public Builder subTypeName(String subTypeName) {
            this.subTypeName = subTypeName;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder extraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
            return this;
        }

        public Connectivity build() {
            return new Connectivity(this);
        }
    }
}
