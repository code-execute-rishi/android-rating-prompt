package com.xyz.awesomerating;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import com.xyz.awesomerating.callbacks.ConfirmClickListener;
import com.xyz.awesomerating.callbacks.FeedbackSubmittedListener;
import com.xyz.awesomerating.callbacks.InAppReviewListener;
import com.xyz.awesomerating.callbacks.RateClickListener;

import java.io.Serializable;

/**
 * Serializable configuration bag passed from Builder to DialogFragment via arguments Bundle.
 *
 * <p>Non-serializable fields (listeners, drawables) are marked {@code transient} — they are
 * re-attached by the Builder if the dialog is rebuilt (e.g. after process death).</p>
 *
 * <p>Package-private setters; public readers. Builder is the canonical configuration API.</p>
 */
public final class RatingConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    // ─── Variant + theme ───
    Variant variant = Variant.STARS;
    @StyleRes int customThemeRes = 0;
    @DrawableRes int iconDrawableRes = 0;         // null = auto-fetch app icon
    transient @Nullable Integer primaryColorOverride;     // @ColorInt — null = use theme attr
    transient @Nullable Integer positiveColorOverride;
    transient @Nullable Integer negativeColorOverride;
    transient @Nullable Integer starFillColorOverride;

    // ─── Conditions ───
    @Nullable transient Condition customCondition;
    @Nullable transient Condition customConditionToShowAgain;
    boolean countAppLaunch = true;
    int countOfLaterClicksBeforeNeverButton = 0;

    // ─── Text ids ───
    @StringRes int titleTextRes = 0;              // 0 = library default per variant
    @StringRes int messageTextRes = 0;
    @StringRes int confirmButtonTextRes = 0;
    @StringRes int rateNowButtonTextRes = 0;
    @StringRes int rateLaterButtonTextRes = 0;
    @StringRes int rateNeverButtonTextRes = 0;
    @StringRes int feedbackTitleTextRes = 0;
    @StringRes int feedbackHintTextRes = 0;
    @StringRes int feedbackSendButtonTextRes = 0;
    @StringRes int storeTitleTextRes = 0;
    @StringRes int storeMessageTextRes = 0;

    // ─── Behaviour ───
    RatingThreshold ratingThreshold = RatingThreshold.FOUR;
    boolean showOnlyFullStars = false;
    boolean cancelable = false;
    boolean showRateNeverButton = false;
    boolean useGoogleInAppReview = false;

    // ─── Feedback ───
    FeedbackMode feedbackMode = FeedbackMode.DISABLED;
    @Nullable MailSettings mailSettings;          // required when feedbackMode == EMAIL
    boolean deviceInfoCheckboxVisible = true;
    boolean deviceInfoCheckboxDefaultChecked = false;

    // ─── Listeners (all transient — cannot survive process death) ───
    transient @Nullable ConfirmClickListener confirmClickListener;
    transient @Nullable RateClickListener rateNowClickListener;      // overrides default (Play Store open)
    transient @Nullable RateClickListener additionalRateNowClickListener;  // runs alongside default
    transient @Nullable RateClickListener rateLaterClickListener;
    transient @Nullable RateClickListener rateNeverClickListener;
    transient @Nullable FeedbackSubmittedListener feedbackSubmittedListener;
    transient @Nullable InAppReviewListener inAppReviewListener;
    transient @Nullable Runnable cancelListener;

    // ─── Accessors ───
    public Variant getVariant() { return variant; }
    public RatingThreshold getRatingThreshold() { return ratingThreshold; }
    public FeedbackMode getFeedbackMode() { return feedbackMode; }
    @Nullable public MailSettings getMailSettings() { return mailSettings; }
    public boolean isDeviceInfoCheckboxVisible() { return deviceInfoCheckboxVisible; }
    public boolean isDeviceInfoCheckboxDefaultChecked() { return deviceInfoCheckboxDefaultChecked; }
    @StyleRes public int getCustomThemeRes() { return customThemeRes; }
    @DrawableRes public int getIconDrawableRes() { return iconDrawableRes; }
    @StringRes public int getTitleTextRes() { return titleTextRes; }
    @StringRes public int getMessageTextRes() { return messageTextRes; }
    @StringRes public int getRateNowButtonTextRes() { return rateNowButtonTextRes; }
    @StringRes public int getRateLaterButtonTextRes() { return rateLaterButtonTextRes; }
    @StringRes public int getRateNeverButtonTextRes() { return rateNeverButtonTextRes; }
    @StringRes public int getFeedbackTitleTextRes() { return feedbackTitleTextRes; }
    @StringRes public int getFeedbackHintTextRes() { return feedbackHintTextRes; }
    @StringRes public int getFeedbackSendButtonTextRes() { return feedbackSendButtonTextRes; }
    @StringRes public int getStoreTitleTextRes() { return storeTitleTextRes; }
    @StringRes public int getStoreMessageTextRes() { return storeMessageTextRes; }
    public boolean isShowOnlyFullStars() { return showOnlyFullStars; }
    public boolean isCancelable() { return cancelable; }
    public boolean isShowRateNeverButton() { return showRateNeverButton; }
    public int getCountOfLaterClicksBeforeNeverButton() { return countOfLaterClicksBeforeNeverButton; }
    public boolean isUseGoogleInAppReview() { return useGoogleInAppReview; }
    public boolean isCountAppLaunch() { return countAppLaunch; }
    @ColorInt @Nullable public Integer getPrimaryColorOverride() { return primaryColorOverride; }
    @ColorInt @Nullable public Integer getPositiveColorOverride() { return positiveColorOverride; }
    @ColorInt @Nullable public Integer getNegativeColorOverride() { return negativeColorOverride; }
    @ColorInt @Nullable public Integer getStarFillColorOverride() { return starFillColorOverride; }
    @Nullable public Condition getCustomCondition() { return customCondition; }
    @Nullable public Condition getCustomConditionToShowAgain() { return customConditionToShowAgain; }
    @Nullable public ConfirmClickListener getConfirmClickListener() { return confirmClickListener; }
    @Nullable public RateClickListener getRateNowClickListener() { return rateNowClickListener; }
    @Nullable public RateClickListener getAdditionalRateNowClickListener() { return additionalRateNowClickListener; }
    @Nullable public RateClickListener getRateLaterClickListener() { return rateLaterClickListener; }
    @Nullable public RateClickListener getRateNeverClickListener() { return rateNeverClickListener; }
    @Nullable public FeedbackSubmittedListener getFeedbackSubmittedListener() { return feedbackSubmittedListener; }
    @Nullable public InAppReviewListener getInAppReviewListener() { return inAppReviewListener; }
    @Nullable public Runnable getCancelListener() { return cancelListener; }

    /** Simple lambda-friendly condition interface. */
    public interface Condition extends Serializable {
        boolean evaluate();
    }
}
