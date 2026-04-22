package com.xyz.awesomerating.internal;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * SharedPreferences-backed state store for rating dialog gating.
 *
 * <p>Keys are intentionally namespaced under {@code awesome_app_rate} to avoid collision
 * with the host app's own preferences.</p>
 */
public final class PreferenceUtil {
    public static final String PREF_FILE = "awesome_app_rate";

    // keys
    private static final String K_LAUNCH_TIMES = "launch_times";
    private static final String K_REMIND_TS = "remind_timestamp";
    private static final String K_MIN_LAUNCHES = "min_launches";
    private static final String K_MIN_LAUNCHES_AGAIN = "min_launches_to_show_again";
    private static final String K_MIN_DAYS = "min_days";
    private static final String K_MIN_DAYS_AGAIN = "min_days_to_show_again";
    private static final String K_AGREED = "dialog_agreed";
    private static final String K_LATER_CLICKED = "dialog_show_later";
    private static final String K_DO_NOT_SHOW = "dialog_do_not_show_again";
    private static final String K_LATER_COUNT = "number_of_later_button_clicks";

    // defaults
    private static final int DEFAULT_MIN_LAUNCHES = 5;
    private static final int DEFAULT_MIN_DAYS = 3;
    private static final int DEFAULT_MIN_LAUNCHES_AGAIN = 5;
    private static final int DEFAULT_MIN_DAYS_AGAIN = 14;

    private PreferenceUtil() {}

    private static SharedPreferences prefs(@NonNull Context ctx) {
        return ctx.getApplicationContext()
                .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    // ─── Launches ───
    public static void increaseLaunchTimes(@NonNull Context ctx) {
        int n = getLaunchTimes(ctx) + 1;
        prefs(ctx).edit().putInt(K_LAUNCH_TIMES, n).apply();
        RatingLogger.verbose("launch count → " + n);
    }

    public static int getLaunchTimes(@NonNull Context ctx) {
        return prefs(ctx).getInt(K_LAUNCH_TIMES, 0);
    }

    // ─── Thresholds ───
    public static void setMinLaunches(@NonNull Context ctx, int v) {
        prefs(ctx).edit().putInt(K_MIN_LAUNCHES, v).apply();
    }
    public static int getMinLaunches(@NonNull Context ctx) {
        return prefs(ctx).getInt(K_MIN_LAUNCHES, DEFAULT_MIN_LAUNCHES);
    }

    public static void setMinLaunchesToShowAgain(@NonNull Context ctx, int v) {
        prefs(ctx).edit().putInt(K_MIN_LAUNCHES_AGAIN, v).apply();
    }
    public static int getMinLaunchesToShowAgain(@NonNull Context ctx) {
        return prefs(ctx).getInt(K_MIN_LAUNCHES_AGAIN, DEFAULT_MIN_LAUNCHES_AGAIN);
    }

    public static void setMinDays(@NonNull Context ctx, int v) {
        prefs(ctx).edit().putInt(K_MIN_DAYS, v).apply();
    }
    public static int getMinDays(@NonNull Context ctx) {
        return prefs(ctx).getInt(K_MIN_DAYS, DEFAULT_MIN_DAYS);
    }

    public static void setMinDaysToShowAgain(@NonNull Context ctx, int v) {
        prefs(ctx).edit().putInt(K_MIN_DAYS_AGAIN, v).apply();
    }
    public static int getMinDaysToShowAgain(@NonNull Context ctx) {
        return prefs(ctx).getInt(K_MIN_DAYS_AGAIN, DEFAULT_MIN_DAYS_AGAIN);
    }

    // ─── Timestamps ───
    public static long getRemindTimestamp(@NonNull Context ctx) {
        long v = prefs(ctx).getLong(K_REMIND_TS, -1L);
        if (v == -1L) {
            long now = System.currentTimeMillis();
            prefs(ctx).edit().putLong(K_REMIND_TS, now).apply();
            return now;
        }
        return v;
    }

    // ─── State flags ───
    public static void setDialogAgreed(@NonNull Context ctx) {
        prefs(ctx).edit().putBoolean(K_AGREED, true).apply();
    }

    public static boolean isDialogAgreed(@NonNull Context ctx) {
        return prefs(ctx).getBoolean(K_AGREED, false);
    }

    public static void onLaterClicked(@NonNull Context ctx) {
        prefs(ctx).edit()
                .putLong(K_REMIND_TS, System.currentTimeMillis())
                .putInt(K_LAUNCH_TIMES, 0)
                .putBoolean(K_LATER_CLICKED, true)
                .apply();
        incrementLaterClickCount(ctx);
    }

    public static boolean wasLaterClicked(@NonNull Context ctx) {
        return prefs(ctx).getBoolean(K_LATER_CLICKED, false);
    }

    public static void setDoNotShowAgain(@NonNull Context ctx) {
        prefs(ctx).edit().putBoolean(K_DO_NOT_SHOW, true).apply();
    }

    public static boolean isDoNotShowAgain(@NonNull Context ctx) {
        return prefs(ctx).getBoolean(K_DO_NOT_SHOW, false);
    }

    public static void incrementLaterClickCount(@NonNull Context ctx) {
        int n = getLaterClickCount(ctx) + 1;
        prefs(ctx).edit().putInt(K_LATER_COUNT, n).apply();
    }

    public static int getLaterClickCount(@NonNull Context ctx) {
        return prefs(ctx).getInt(K_LATER_COUNT, 0);
    }

    public static void onInAppReviewCompleted(@NonNull Context ctx) {
        prefs(ctx).edit()
                .putLong(K_REMIND_TS, System.currentTimeMillis())
                .putInt(K_LAUNCH_TIMES, 0)
                .putBoolean(K_LATER_CLICKED, true)   // subsequent prompts use "show again" thresholds
                .apply();
    }

    public static void reset(@NonNull Context ctx) {
        prefs(ctx).edit().clear().apply();
        RatingLogger.warn("AwesomeRating preferences reset");
    }
}
