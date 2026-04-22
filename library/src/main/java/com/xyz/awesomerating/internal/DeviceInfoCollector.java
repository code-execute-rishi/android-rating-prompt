package com.xyz.awesomerating.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.pm.PackageInfoCompat;

import com.xyz.awesomerating.model.DeviceInfo;

import java.util.Locale;

/** Collects non-PII device + app metadata. Called only after user consents via checkbox. */
public final class DeviceInfoCollector {

    private DeviceInfoCollector() {}

    @NonNull
    public static DeviceInfo collect(@NonNull Context ctx) {
        DeviceInfo.Builder b = new DeviceInfo.Builder()
                .manufacturer(Build.MANUFACTURER)
                .model(Build.MODEL)
                .device(Build.DEVICE)
                .osVersion(Build.VERSION.RELEASE)
                .sdkInt(Build.VERSION.SDK_INT)
                .abi(Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0
                        ? Build.SUPPORTED_ABIS[0] : null)
                .locale(Locale.getDefault().toString());

        try {
            PackageInfo pi = ctx.getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            b.appVersionName(pi.versionName);
            b.appVersionCode(PackageInfoCompat.getLongVersionCode(pi));
        } catch (PackageManager.NameNotFoundException e) {
            RatingLogger.warn("Cannot read app PackageInfo: " + e.getMessage());
        }

        return b.build();
    }
}
