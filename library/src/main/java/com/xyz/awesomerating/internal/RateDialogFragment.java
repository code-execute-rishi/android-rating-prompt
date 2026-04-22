package com.xyz.awesomerating.internal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.xyz.awesomerating.R;
import com.xyz.awesomerating.RatingConfig;

/**
 * Hosts the rating dialog as a Material {@link BottomSheetDialogFragment}. Swaps between steps
 * (rating → store / feedback / thanks) via {@link DialogManager#swapStep}.
 */
public final class RateDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_CONFIG = "ar_config";
    private static final String STATE_RATING = "ar_state_rating";
    private static final String STATE_STEP = "ar_state_step";
    private static final String STATE_FEEDBACK = "ar_state_feedback";
    private static final String STATE_INCLUDE_DEVICE = "ar_state_include_device";

    private RatingConfig config;
    private float rating = -1f;
    private DialogStep step = DialogStep.RATING;
    private String draftFeedback = "";
    private boolean includeDevice;

    private ViewGroup contentHost;

    @NonNull
    public static RateDialogFragment newInstance(@NonNull RatingConfig config) {
        RateDialogFragment f = new RateDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIG, config);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) throw new IllegalStateException("RateDialogFragment missing arguments");
        config = (RatingConfig) args.getSerializable(ARG_CONFIG);
        if (config == null) throw new IllegalStateException("RatingConfig missing");
        setCancelable(config.isCancelable());
        includeDevice = config.isDeviceInfoCheckboxDefaultChecked();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context themed = themedContext(requireContext());
        BottomSheetDialog dialog = new BottomSheetDialog(themed, resolveTheme(themed));
        dialog.setCancelable(config.isCancelable());
        dialog.setCanceledOnTouchOutside(config.isCancelable());
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context themed = themedContext(inflater.getContext());
        LayoutInflater themedInflater = inflater.cloneInContext(themed);
        contentHost = (ViewGroup) themedInflater.inflate(R.layout.ar_sheet_host, container, false);

        if (savedInstanceState != null) {
            rating = savedInstanceState.getFloat(STATE_RATING, -1f);
            Object s = savedInstanceState.getSerializable(STATE_STEP);
            if (s instanceof DialogStep) step = (DialogStep) s;
            draftFeedback = savedInstanceState.getString(STATE_FEEDBACK, "");
            includeDevice = savedInstanceState.getBoolean(STATE_INCLUDE_DEVICE, includeDevice);
        }

        DialogManager.render(this, contentHost, config, step);
        return contentHost;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(STATE_RATING, rating);
        outState.putSerializable(STATE_STEP, step);
        outState.putString(STATE_FEEDBACK, draftFeedback);
        outState.putBoolean(STATE_INCLUDE_DEVICE, includeDevice);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        RatingLogger.info("Rating dialog cancelled; treating as Later click");
        PreferenceUtil.onLaterClicked(requireContext());
        if (config.cancelListener != null) config.cancelListener.run();
    }

    // ─── Step helpers used by DialogManager ───

    void swapStep(@NonNull DialogStep next) {
        this.step = next;
        DialogManager.render(this, contentHost, config, step);
    }

    void setRating(float rating) { this.rating = rating; }
    float getRating() { return rating; }

    void setDraftFeedback(String s) { this.draftFeedback = s == null ? "" : s; }
    String getDraftFeedback() { return draftFeedback; }

    void setIncludeDevice(boolean v) { this.includeDevice = v; }
    boolean isIncludeDevice() { return includeDevice; }

    // ─── Theme plumbing ───

    private Context themedContext(Context base) {
        int themeRes = config.getCustomThemeRes() != 0
                ? config.getCustomThemeRes()
                : R.style.Theme_AwesomeRating;
        return new ContextThemeWrapper(base, themeRes);
    }

    private int resolveTheme(Context themed) {
        return config.getCustomThemeRes() != 0
                ? config.getCustomThemeRes()
                : com.google.android.material.R.style.Theme_Material3_DayNight_BottomSheetDialog;
    }
}
