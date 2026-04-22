package com.xyz.awesomerating.internal;

import android.content.Context;

import androidx.annotation.NonNull;

import com.xyz.awesomerating.RatingConfig;

import java.util.concurrent.TimeUnit;

/**
 * Pure evaluator for dialog gating. Two branches:
 * <ol>
 *     <li>User has never clicked "later" — check minDays + minLaunches + customCondition.</li>
 *     <li>User already clicked "later" — check minDaysToShowAgain + minLaunchesToShowAgain +
 *         customConditionToShowAgain.</li>
 * </ol>
 *
 * <p>Always rejects if user has agreed or marked "do not show again".</p>
 */
public final class ConditionsChecker {

    private ConditionsChecker() {}

    public static boolean shouldShow(@NonNull Context ctx, @NonNull RatingConfig config) {
        if (PreferenceUtil.isDialogAgreed(ctx)) {
            RatingLogger.debug("Dialog already agreed; skipping");
            return false;
        }
        if (PreferenceUtil.isDoNotShowAgain(ctx)) {
            RatingLogger.debug("User chose never; skipping");
            return false;
        }

        long now = System.currentTimeMillis();
        long remindTs = PreferenceUtil.getRemindTimestamp(ctx);
        long daysBetween = TimeUnit.MILLISECONDS.toDays(now - remindTs);
        int launchTimes = PreferenceUtil.getLaunchTimes(ctx);

        boolean laterClicked = PreferenceUtil.wasLaterClicked(ctx);

        if (laterClicked) {
            if (!evaluateCustom(config.getCustomConditionToShowAgain(), "customConditionToShowAgain")) {
                return false;
            }
            int needDays = PreferenceUtil.getMinDaysToShowAgain(ctx);
            int needLaunches = PreferenceUtil.getMinLaunchesToShowAgain(ctx);
            boolean ok = daysBetween >= needDays && launchTimes >= needLaunches;
            RatingLogger.debug("show-again branch: days=" + daysBetween + "/" + needDays
                    + " launches=" + launchTimes + "/" + needLaunches + " → " + ok);
            return ok;
        } else {
            if (!evaluateCustom(config.getCustomCondition(), "customCondition")) {
                return false;
            }
            int needDays = PreferenceUtil.getMinDays(ctx);
            int needLaunches = PreferenceUtil.getMinLaunches(ctx);
            boolean ok = daysBetween >= needDays && launchTimes >= needLaunches;
            RatingLogger.debug("fresh branch: days=" + daysBetween + "/" + needDays
                    + " launches=" + launchTimes + "/" + needLaunches + " → " + ok);
            return ok;
        }
    }

    private static boolean evaluateCustom(RatingConfig.Condition condition, String label) {
        if (condition == null) return true;
        boolean r = condition.evaluate();
        RatingLogger.debug(label + " → " + r);
        return r;
    }
}
