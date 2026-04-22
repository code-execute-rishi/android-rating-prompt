package com.xyz.awesomerating;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.FragmentActivity;

import com.xyz.awesomerating.callbacks.ConfirmClickListener;
import com.xyz.awesomerating.callbacks.FeedbackSubmittedListener;
import com.xyz.awesomerating.callbacks.InAppReviewListener;
import com.xyz.awesomerating.callbacks.RateClickListener;
import com.xyz.awesomerating.internal.ConditionsChecker;
import com.xyz.awesomerating.internal.InAppReviewHelper;
import com.xyz.awesomerating.internal.PreferenceUtil;
import com.xyz.awesomerating.internal.RateDialogFragment;
import com.xyz.awesomerating.internal.RatingLogger;

import java.lang.ref.WeakReference;

/**
 * Fluent configuration builder for the rating dialog.
 *
 * <p>Hold a WeakReference to the host activity — the Builder is discarded immediately after
 * {@code showIfMeetsConditions()} / {@code showNow()} so we never leak the activity, but we keep
 * the reference weak defensively.</p>
 */
public final class Builder {

    private static final String TAG = "AwesomeRatingDialog";

    private final WeakReference<FragmentActivity> activityRef;
    private final RatingConfig config;
    private boolean debug = false;

    public Builder(@NonNull FragmentActivity activity) {
        if (activity == null) throw new IllegalArgumentException("activity cannot be null");
        this.activityRef = new WeakReference<>(activity);
        this.config = new RatingConfig();
    }

    // ─── Variant ───

    public Builder variant(@NonNull Variant variant) {
        config.variant = variant;
        return this;
    }

    // ─── Conditions ───

    public Builder minDays(int minDays) {
        PreferenceUtil.setMinDays(requireContext(), minDays);
        return this;
    }

    public Builder minLaunches(int minLaunches) {
        PreferenceUtil.setMinLaunches(requireContext(), minLaunches);
        return this;
    }

    public Builder minDaysToShowAgain(int days) {
        PreferenceUtil.setMinDaysToShowAgain(requireContext(), days);
        return this;
    }

    public Builder minLaunchesToShowAgain(int launches) {
        PreferenceUtil.setMinLaunchesToShowAgain(requireContext(), launches);
        return this;
    }

    public Builder customCondition(@NonNull RatingConfig.Condition condition) {
        config.customCondition = condition;
        return this;
    }

    public Builder customConditionToShowAgain(@NonNull RatingConfig.Condition condition) {
        config.customConditionToShowAgain = condition;
        return this;
    }

    /**
     * Skip counting this call as an app launch. Useful when combined with {@code customCondition()}
     * to trigger the dialog off a non-onCreate event.
     */
    public Builder dontCountThisAsAppLaunch() {
        config.countAppLaunch = false;
        return this;
    }

    // ─── Theme & branding ───

    public Builder customTheme(@StyleRes int themeRes) {
        config.customThemeRes = themeRes;
        return this;
    }

    public Builder icon(@DrawableRes int drawableRes) {
        config.iconDrawableRes = drawableRes;
        return this;
    }

    public Builder primaryColor(@ColorInt int color) {
        config.primaryColorOverride = color;
        return this;
    }

    public Builder positiveColor(@ColorInt int color) {
        config.positiveColorOverride = color;
        return this;
    }

    public Builder negativeColor(@ColorInt int color) {
        config.negativeColorOverride = color;
        return this;
    }

    public Builder starFillColor(@ColorInt int color) {
        config.starFillColorOverride = color;
        return this;
    }

    // ─── Text overrides ───

    public Builder titleText(@StringRes int res) { config.titleTextRes = res; return this; }
    public Builder messageText(@StringRes int res) { config.messageTextRes = res; return this; }
    public Builder confirmButtonText(@StringRes int res) { config.confirmButtonTextRes = res; return this; }
    public Builder rateNowButtonText(@StringRes int res) { config.rateNowButtonTextRes = res; return this; }
    public Builder rateLaterButtonText(@StringRes int res) { config.rateLaterButtonTextRes = res; return this; }
    public Builder rateNeverButtonText(@StringRes int res) { config.rateNeverButtonTextRes = res; return this; }
    public Builder feedbackTitleText(@StringRes int res) { config.feedbackTitleTextRes = res; return this; }
    public Builder feedbackHintText(@StringRes int res) { config.feedbackHintTextRes = res; return this; }
    public Builder feedbackSendButtonText(@StringRes int res) { config.feedbackSendButtonTextRes = res; return this; }
    public Builder storeTitleText(@StringRes int res) { config.storeTitleTextRes = res; return this; }
    public Builder storeMessageText(@StringRes int res) { config.storeMessageTextRes = res; return this; }

    // ─── Behaviour ───

    public Builder threshold(@NonNull RatingThreshold threshold) {
        config.ratingThreshold = threshold;
        return this;
    }

    public Builder showOnlyFullStars(boolean value) {
        config.showOnlyFullStars = value;
        return this;
    }

    public Builder cancelable(boolean value) {
        config.cancelable = value;
        return this;
    }

    public Builder cancelListener(@NonNull Runnable listener) {
        config.cancelListener = listener;
        return this;
    }

    public Builder showRateNeverButton() {
        config.showRateNeverButton = true;
        return this;
    }

    public Builder showRateNeverButtonAfterLaterClicks(int laterClicks) {
        config.showRateNeverButton = true;
        config.countOfLaterClicksBeforeNeverButton = laterClicks;
        return this;
    }

    public Builder debug(boolean value) {
        this.debug = value;
        return this;
    }

    public Builder loggingEnabled(boolean value) {
        RatingLogger.setEnabled(value);
        return this;
    }

    // ─── Feedback ───

    /**
     * Enable email-based feedback. When the user rates below the threshold, the feedback
     * form opens with the configured mail settings.
     *
     * @throws IllegalArgumentException if {@code mailSettings} is null
     */
    public Builder enableEmailFeedback(@NonNull MailSettings mailSettings) {
        if (mailSettings == null) throw new IllegalArgumentException("mailSettings cannot be null");
        config.feedbackMode = FeedbackMode.EMAIL;
        config.mailSettings = mailSettings;
        return this;
    }

    /** Convenience overload. */
    public Builder enableEmailFeedback(@NonNull String recipientEmail) {
        return enableEmailFeedback(new MailSettings(recipientEmail));
    }

    /**
     * Enable custom feedback handling. When the user rates below the threshold, the feedback
     * form is shown; on Send, the listener is invoked with the user-entered text and (optionally)
     * the collected device info.
     */
    public Builder enableCustomFeedback(@NonNull FeedbackSubmittedListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        config.feedbackMode = FeedbackMode.CUSTOM;
        config.feedbackSubmittedListener = listener;
        return this;
    }

    /** Disable the feedback form. Low-rating users see a short "thanks" and the dialog closes. */
    public Builder disableFeedback() {
        config.feedbackMode = FeedbackMode.DISABLED;
        config.mailSettings = null;
        config.feedbackSubmittedListener = null;
        return this;
    }

    public Builder showDeviceInfoCheckbox(boolean visible) {
        config.deviceInfoCheckboxVisible = visible;
        return this;
    }

    public Builder deviceInfoCheckboxDefaultChecked(boolean checked) {
        config.deviceInfoCheckboxDefaultChecked = checked;
        return this;
    }

    // ─── Click listeners ───

    public Builder onConfirm(@NonNull ConfirmClickListener listener) {
        config.confirmClickListener = listener;
        return this;
    }

    public Builder overrideRateNowClick(@NonNull RateClickListener listener) {
        config.rateNowClickListener = listener;
        return this;
    }

    public Builder onRateNowClick(@NonNull RateClickListener listener) {
        config.additionalRateNowClickListener = listener;
        return this;
    }

    public Builder onRateLaterClick(@NonNull RateClickListener listener) {
        config.rateLaterClickListener = listener;
        return this;
    }

    public Builder onRateNeverClick(@NonNull RateClickListener listener) {
        config.rateNeverClickListener = listener;
        return this;
    }

    public Builder onFeedbackSubmitted(@NonNull FeedbackSubmittedListener listener) {
        config.feedbackSubmittedListener = listener;
        return this;
    }

    // ─── Google in-app review ───

    public Builder useGoogleInAppReview() {
        config.useGoogleInAppReview = true;
        return this;
    }

    public Builder onInAppReviewCompleted(@NonNull InAppReviewListener listener) {
        config.inAppReviewListener = listener;
        return this;
    }

    // ─── Terminal methods ───

    /**
     * Show dialog immediately if all gating conditions are met (min days, min launches,
     * not-already-agreed, custom condition).
     *
     * @return true if the dialog (or in-app review) was shown; false otherwise.
     */
    public boolean showIfMeetsConditions() {
        FragmentActivity activity = activityRef.get();
        if (activity == null) {
            RatingLogger.warn("Activity released before showIfMeetsConditions()");
            return false;
        }

        validate();

        // Dedupe — if already showing, bail.
        if (activity.getSupportFragmentManager().findFragmentByTag(TAG) != null) {
            RatingLogger.info("Rating dialog already on-screen; not re-showing");
            return false;
        }

        if (config.countAppLaunch) {
            PreferenceUtil.increaseLaunchTimes(activity);
        }

        if (debug || ConditionsChecker.shouldShow(activity, config)) {
            showNow();
            return true;
        }
        RatingLogger.debug("Conditions not met; dialog not shown");
        return false;
    }

    /** Show dialog immediately, bypassing all gating conditions. */
    public void showNow() {
        FragmentActivity activity = activityRef.get();
        if (activity == null) {
            RatingLogger.warn("Activity released before showNow()");
            return;
        }
        validate();

        if (config.useGoogleInAppReview) {
            InAppReviewHelper.launch(activity, config);
        } else {
            RateDialogFragment frag = RateDialogFragment.newInstance(config);
            frag.show(activity.getSupportFragmentManager(), TAG);
        }
    }

    private void validate() {
        if (config.feedbackMode == FeedbackMode.EMAIL && config.mailSettings == null) {
            throw new IllegalStateException(
                    "FeedbackMode.EMAIL requires enableEmailFeedback(MailSettings). " +
                    "Use disableFeedback() if you don't want feedback.");
        }
        if (config.feedbackMode == FeedbackMode.CUSTOM && config.feedbackSubmittedListener == null) {
            throw new IllegalStateException(
                    "FeedbackMode.CUSTOM requires enableCustomFeedback(listener).");
        }
    }

    @NonNull
    private Context requireContext() {
        FragmentActivity activity = activityRef.get();
        if (activity == null) throw new IllegalStateException("Activity has been GC'd");
        return activity;
    }

    @NonNull
    public RatingConfig buildConfig() {
        validate();
        return config;
    }

    @Nullable
    FragmentActivity getActivity() {
        return activityRef.get();
    }
}
