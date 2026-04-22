package com.xyz.awesomerating.callbacks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xyz.awesomerating.model.DeviceInfo;

import java.io.Serializable;

/**
 * Fired when the user taps Send on the feedback form.
 *
 * <p>Used when the developer picks {@link com.xyz.awesomerating.FeedbackMode#CUSTOM}.
 * Developer is responsible for transmitting the feedback (REST API, Crashlytics, etc.).</p>
 */
public interface FeedbackSubmittedListener extends Serializable {
    /**
     * @param feedbackText     user-entered feedback. Never null, never empty.
     * @param includeDeviceInfo true if the user ticked the "include device info" checkbox.
     * @param deviceInfo       collected device/app info if the checkbox was ticked, otherwise null.
     * @param rating           the rating the user gave (0.0f–5.0f) before the feedback form opened.
     */
    void onFeedbackSubmitted(@NonNull String feedbackText,
                             boolean includeDeviceInfo,
                             @Nullable DeviceInfo deviceInfo,
                             float rating);
}
