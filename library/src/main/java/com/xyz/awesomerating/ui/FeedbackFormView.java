package com.xyz.awesomerating.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.xyz.awesomerating.FeedbackMode;
import com.xyz.awesomerating.MailSettings;
import com.xyz.awesomerating.R;
import com.xyz.awesomerating.RatingConfig;
import com.xyz.awesomerating.internal.DeviceInfoCollector;
import com.xyz.awesomerating.internal.FeedbackUtils;
import com.xyz.awesomerating.internal.RateDialogFragment;
import com.xyz.awesomerating.internal.RatingLogger;
import com.xyz.awesomerating.model.DeviceInfo;

/**
 * Feedback form shown when the user rates below the threshold.
 *
 * <p>Layout: header + multiline EditText + device-info checkbox + preview chip + Send button
 * + "not now" ghost button.</p>
 *
 * <p>Send button is disabled until the user types at least 1 character (whitespace stripped).</p>
 */
public final class FeedbackFormView extends LinearLayout {

    private TextInputLayout inputLayout;
    private TextInputEditText editText;
    private MaterialCheckBox deviceInfoCheckBox;
    private TextView deviceInfoSubLabel;
    private Chip deviceChip;
    private MaterialButton sendButton;
    private View notNowButton;
    private TextView titleView;

    public FeedbackFormView(@NonNull Context context) { super(context); init(); }
    public FeedbackFormView(@NonNull Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }
    public FeedbackFormView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView = findViewById(R.id.ar_title);
        inputLayout = findViewById(R.id.ar_feedback_input_layout);
        editText = findViewById(R.id.ar_feedback_edittext);
        deviceInfoCheckBox = findViewById(R.id.ar_device_info_checkbox);
        deviceInfoSubLabel = findViewById(R.id.ar_device_info_sublabel);
        deviceChip = findViewById(R.id.ar_device_chip);
        sendButton = findViewById(R.id.ar_feedback_send_button);
        notNowButton = findViewById(R.id.ar_feedback_not_now_button);
    }

    /** Called by DialogManager after inflation. */
    public void bind(@NonNull RateDialogFragment fragment, @NonNull RatingConfig config) {
        // Title override
        if (titleView != null && config.feedbackTitleTextRes != 0) {
            titleView.setText(config.feedbackTitleTextRes);
        }

        // Hint override
        if (inputLayout != null && config.feedbackHintTextRes != 0) {
            inputLayout.setHint(getContext().getString(config.feedbackHintTextRes));
        }

        // Pre-fill from saved draft (rotation)
        if (editText != null) {
            editText.setText(fragment.getDraftFeedback());
            editText.setSelection(editText.getText() == null ? 0 : editText.getText().length());
            editText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void afterTextChanged(Editable s) {
                    String text = s == null ? "" : s.toString();
                    fragment.setDraftFeedback(text);
                    sendButton.setEnabled(text.trim().length() > 0);
                }
            });
        }

        // Device-info checkbox
        if (deviceInfoCheckBox != null) {
            deviceInfoCheckBox.setVisibility(config.isDeviceInfoCheckboxVisible() ? VISIBLE : GONE);
            deviceInfoCheckBox.setChecked(fragment.isIncludeDevice());
            updateDeviceChip(fragment.isIncludeDevice());
            deviceInfoCheckBox.setOnCheckedChangeListener((cb, checked) -> {
                fragment.setIncludeDevice(checked);
                updateDeviceChip(checked);
            });
        } else {
            if (deviceInfoSubLabel != null) deviceInfoSubLabel.setVisibility(GONE);
            if (deviceChip != null) deviceChip.setVisibility(GONE);
        }

        // Send button text override + click
        if (sendButton != null) {
            if (config.feedbackSendButtonTextRes != 0) {
                sendButton.setText(config.feedbackSendButtonTextRes);
            }
            sendButton.setEnabled(fragment.getDraftFeedback().trim().length() > 0);
            sendButton.setOnClickListener(v -> submit(fragment, config));
        }

        // Not-now ghost button
        if (notNowButton != null) {
            notNowButton.setOnClickListener(v -> fragment.dismissAllowingStateLoss());
        }
    }

    private void updateDeviceChip(boolean checked) {
        if (deviceChip == null) return;
        if (!checked) {
            deviceChip.setVisibility(GONE);
            return;
        }
        DeviceInfo info = DeviceInfoCollector.collect(getContext());
        deviceChip.setText(info.toShortString());
        deviceChip.setVisibility(VISIBLE);
    }

    private void submit(RateDialogFragment fragment, RatingConfig config) {
        String text = fragment.getDraftFeedback().trim();
        if (text.isEmpty()) return;

        boolean include = fragment.isIncludeDevice();
        DeviceInfo info = include ? DeviceInfoCollector.collect(getContext()) : null;
        float rating = fragment.getRating();

        switch (config.getFeedbackMode()) {
            case EMAIL: {
                MailSettings settings = config.getMailSettings();
                if (settings == null) {
                    RatingLogger.error("FeedbackMode.EMAIL but mailSettings missing");
                    return;
                }
                FeedbackUtils.sendFeedbackMail(getContext(), settings, text, info, rating);
                break;
            }
            case CUSTOM: {
                if (config.feedbackSubmittedListener != null) {
                    config.feedbackSubmittedListener.onFeedbackSubmitted(text, include, info, rating);
                } else {
                    RatingLogger.error("FeedbackMode.CUSTOM but listener missing");
                }
                break;
            }
            case DISABLED:
            default:
                break;
        }
        fragment.dismissAllowingStateLoss();
    }
}
