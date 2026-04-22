package com.xyz.awesomerating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.xyz.awesomerating.internal.DeviceInfoCollector;
import com.xyz.awesomerating.model.DeviceInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
public class DeviceInfoCollectorTest {
    @Test
    public void collectIncludesDeviceAndAppMetadata() throws Exception {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "com.example.app";
        packageInfo.versionName = "2.4.6";
        packageInfo.versionCode = 246;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.setLongVersionCode(246L);
        }

        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn("com.example.app");
        when(packageManager.getPackageInfo("com.example.app", 0)).thenReturn(packageInfo);

        DeviceInfo info = DeviceInfoCollector.collect(context);

        assertEquals(Build.MANUFACTURER, info.manufacturer);
        assertEquals(Build.MODEL, info.model);
        assertEquals(Build.DEVICE, info.device);
        assertEquals(Build.VERSION.RELEASE, info.osVersion);
        assertEquals(Build.VERSION.SDK_INT, info.sdkInt);
        assertEquals("2.4.6", info.appVersionName);
        assertEquals(246L, info.appVersionCode);
        assertEquals(Locale.getDefault().toString(), info.locale);
        if (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0) {
            assertEquals(Build.SUPPORTED_ABIS[0], info.abi);
        }
    }

    @Test
    public void collectSkipsAppMetadataWhenPackageInfoIsMissing() throws Exception {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);

        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn("com.example.missing");
        when(packageManager.getPackageInfo("com.example.missing", 0))
                .thenThrow(new PackageManager.NameNotFoundException("missing"));

        DeviceInfo info = DeviceInfoCollector.collect(context);

        assertNull(info.appVersionName);
        assertEquals(0L, info.appVersionCode);
        assertEquals(Build.VERSION.SDK_INT, info.sdkInt);
    }

    @Test
    public void collectedDeviceInfoFormatsUsefulSummaryAndMailBlock() throws Exception {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "com.example.app";
        packageInfo.versionName = "3.0.0";
        packageInfo.versionCode = 300;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.setLongVersionCode(300L);
        }

        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn("com.example.app");
        when(packageManager.getPackageInfo("com.example.app", 0)).thenReturn(packageInfo);

        DeviceInfo info = DeviceInfoCollector.collect(context);

        assertTrue(info.toShortString().contains("Android"));
        assertTrue(info.toShortString().contains("3.0.0"));
        assertTrue(info.toMailBlock().contains("App: 3.0.0 (300)"));
        assertTrue(info.toMailBlock().contains("SDK " + Build.VERSION.SDK_INT));
    }
}
