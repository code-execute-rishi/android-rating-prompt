package com.xyz.awesomerating.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Diagnostic info about the device + app, gathered on demand only when the user
 * explicitly consents via the "Include device info" checkbox.
 *
 * <p>No PII: no advertising ID, no account identifiers, no IMEI, no location.</p>
 */
public final class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable public final String manufacturer;
    @Nullable public final String model;
    @Nullable public final String device;
    @Nullable public final String osVersion;
    public final int sdkInt;
    @Nullable public final String appVersionName;
    public final long appVersionCode;
    @Nullable public final String abi;
    @Nullable public final String locale;

    private DeviceInfo(Builder b) {
        this.manufacturer = b.manufacturer;
        this.model = b.model;
        this.device = b.device;
        this.osVersion = b.osVersion;
        this.sdkInt = b.sdkInt;
        this.appVersionName = b.appVersionName;
        this.appVersionCode = b.appVersionCode;
        this.abi = b.abi;
        this.locale = b.locale;
    }

    /** One-line summary, e.g. {@code "Google Pixel 8 · Android 15 · MyApp 2.3.1"}. */
    @NonNull
    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        if (manufacturer != null) sb.append(manufacturer).append(' ');
        if (model != null) sb.append(model);
        sb.append(" · Android ");
        sb.append(osVersion != null ? osVersion : String.valueOf(sdkInt));
        if (appVersionName != null) sb.append(" · ").append(appVersionName);
        return sb.toString();
    }

    /** Multi-line block suitable for embedding in a mail body. */
    @NonNull
    public String toMailBlock() {
        StringBuilder sb = new StringBuilder();
        sb.append("─── Device info ───\n");
        if (appVersionName != null || appVersionCode > 0) {
            sb.append("App: ").append(appVersionName != null ? appVersionName : "?")
                    .append(" (").append(appVersionCode).append(")\n");
        }
        sb.append("Device: ").append(manufacturer != null ? manufacturer : "?").append(' ')
                .append(model != null ? model : "?").append('\n');
        sb.append("Android: ").append(osVersion != null ? osVersion : "?")
                .append(" (SDK ").append(sdkInt).append(")\n");
        if (abi != null) sb.append("ABI: ").append(abi).append('\n');
        if (locale != null) sb.append("Locale: ").append(locale).append('\n');
        return sb.toString();
    }

    public static final class Builder {
        private String manufacturer, model, device, osVersion, appVersionName, abi, locale;
        private int sdkInt;
        private long appVersionCode;

        public Builder manufacturer(String v) { this.manufacturer = v; return this; }
        public Builder model(String v)        { this.model = v; return this; }
        public Builder device(String v)       { this.device = v; return this; }
        public Builder osVersion(String v)    { this.osVersion = v; return this; }
        public Builder sdkInt(int v)          { this.sdkInt = v; return this; }
        public Builder appVersionName(String v){ this.appVersionName = v; return this; }
        public Builder appVersionCode(long v) { this.appVersionCode = v; return this; }
        public Builder abi(String v)          { this.abi = v; return this; }
        public Builder locale(String v)       { this.locale = v; return this; }

        public DeviceInfo build() { return new DeviceInfo(this); }
    }
}
