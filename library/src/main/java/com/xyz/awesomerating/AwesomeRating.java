package com.xyz.awesomerating;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.xyz.awesomerating.internal.FeedbackUtils;
import com.xyz.awesomerating.internal.PreferenceUtil;

/**
 * Public entry point. Use {@link #with(FragmentActivity)} to begin a fluent configuration chain.
 *
 * <p>Also exposes static helpers for opening the Play Store listing, opening a mail feedback
 * intent, and querying / resetting the persisted dialog state.</p>
 */
public final class AwesomeRating {

    private AwesomeRating() {}

    /** Start a new Builder bound to the given activity. */
    @NonNull
    public static Builder with(@NonNull FragmentActivity activity) {
        return new Builder(activity);
    }

    /** Reset all persisted rating state (launch counter, agreed flag, etc.). */
    public static void reset(@NonNull Context context) {
        PreferenceUtil.reset(context);
    }

    public static boolean isDialogAgreed(@NonNull Context context) {
        return PreferenceUtil.isDialogAgreed(context);
    }

    public static boolean wasLaterButtonClicked(@NonNull Context context) {
        return PreferenceUtil.wasLaterClicked(context);
    }

    public static boolean wasNeverButtonClicked(@NonNull Context context) {
        return PreferenceUtil.isDoNotShowAgain(context);
    }

    public static int getNumberOfLaterButtonClicks(@NonNull Context context) {
        return PreferenceUtil.getLaterClickCount(context);
    }

    /** Open the Play Store listing for the caller's package. */
    public static void openPlayStoreListing(@NonNull Context context) {
        FeedbackUtils.openPlayStoreListing(context);
    }

    /** Open a mail composer prefilled with the given settings. No-op on devices without mail app. */
    public static void openMailFeedback(@NonNull Context context, @NonNull MailSettings settings) {
        FeedbackUtils.openRawMail(context, settings);
    }
}
