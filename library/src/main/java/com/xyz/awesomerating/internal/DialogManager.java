package com.xyz.awesomerating.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.xyz.awesomerating.FeedbackMode;
import com.xyz.awesomerating.R;
import com.xyz.awesomerating.RatingConfig;
import com.xyz.awesomerating.Variant;
import com.xyz.awesomerating.ui.FeedbackFormView;

/**
 * Inflates and binds the correct step inside the RateDialogFragment's host container.
 */
public final class DialogManager {

    private DialogManager() {}

    static void render(@NonNull RateDialogFragment fragment,
                       @NonNull ViewGroup host,
                       @NonNull RatingConfig config,
                       @NonNull DialogStep step) {
        host.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(host.getContext());

        switch (step) {
            case RATING:
                bindRating(fragment, host, config, inflater);
                break;
            case STORE:
                bindStore(fragment, host, config, inflater);
                break;
            case FEEDBACK:
                bindFeedback(fragment, host, config, inflater);
                break;
            case THANKS:
                bindThanks(fragment, host, config, inflater);
                break;
        }
    }

    // ─── Step: RATING ───

    private static void bindRating(RateDialogFragment fragment,
                                   ViewGroup host,
                                   RatingConfig config,
                                   LayoutInflater inflater) {
        @LayoutRes int layout;
        switch (config.getVariant()) {
            case EMOJI:  layout = R.layout.ar_sheet_emoji; break;
            case BINARY: layout = R.layout.ar_sheet_binary; break;
            case STARS:
            default:     layout = R.layout.ar_sheet_stars; break;
        }

        View view = inflater.inflate(layout, host, false);
        host.addView(view);
        applyIcon(view.findViewById(R.id.ar_icon), config);
        applyTitle(view.findViewById(R.id.ar_title), config);
        applyMessage(view.findViewById(R.id.ar_message), config);
        wireRatingInputs(fragment, view, config);
        wireLaterAndNever(fragment, view, config);
    }

    private static void wireRatingInputs(RateDialogFragment fragment, View root, RatingConfig config) {
        // Stars
        RatingBar stars = root.findViewById(R.id.ar_rating_bar);
        if (stars != null) {
            if (config.isShowOnlyFullStars()) stars.setStepSize(1f);
            stars.setOnRatingBarChangeListener((bar, r, fromUser) -> fragment.setRating(r));
            if (fragment.getRating() > 0) stars.setRating(fragment.getRating());

            MaterialButton confirm = root.findViewById(R.id.ar_confirm_button);
            if (confirm != null) {
                confirm.setEnabled(fragment.getRating() > 0);
                stars.setOnRatingBarChangeListener((bar, r, fromUser) -> {
                    fragment.setRating(r);
                    confirm.setEnabled(r > 0);
                });
                confirm.setOnClickListener(v -> handleRatingConfirmed(fragment, config));
            }
        }

        // Emoji chips
        int[] emojiIds = {R.id.ar_emoji_1, R.id.ar_emoji_2, R.id.ar_emoji_3, R.id.ar_emoji_4, R.id.ar_emoji_5};
        for (int i = 0; i < emojiIds.length; i++) {
            View chip = root.findViewById(emojiIds[i]);
            if (chip == null) continue;
            final float r = (i + 1);   // 1..5
            chip.setOnClickListener(v -> {
                fragment.setRating(r);
                handleRatingConfirmed(fragment, config);
            });
        }

        // Binary cards
        View loveCard = root.findViewById(R.id.ar_love_card);
        View needsCard = root.findViewById(R.id.ar_needs_card);
        if (loveCard != null) loveCard.setOnClickListener(v -> {
            fragment.setRating(5f);
            handleRatingConfirmed(fragment, config);
        });
        if (needsCard != null) needsCard.setOnClickListener(v -> {
            fragment.setRating(1f);
            handleRatingConfirmed(fragment, config);
        });
    }

    private static void handleRatingConfirmed(RateDialogFragment fragment, RatingConfig config) {
        float rating = fragment.getRating();
        if (rating <= 0) return;

        if (config.getConfirmClickListener() != null) {
            config.getConfirmClickListener().onConfirm(rating);
        }

        boolean aboveThreshold = rating >= config.getRatingThreshold().asFloat();
        if (aboveThreshold) {
            RatingLogger.info("Rating " + rating + " ≥ threshold → Store step");
            fragment.swapStep(DialogStep.STORE);
        } else {
            PreferenceUtil.setDialogAgreed(fragment.requireContext());
            switch (config.getFeedbackMode()) {
                case EMAIL:
                case CUSTOM:
                    RatingLogger.info("Rating " + rating + " < threshold → Feedback step");
                    fragment.swapStep(DialogStep.FEEDBACK);
                    break;
                case DISABLED:
                default:
                    RatingLogger.info("Rating " + rating + " < threshold, feedback disabled → Thanks step");
                    fragment.swapStep(DialogStep.THANKS);
                    break;
            }
        }
    }

    // ─── Step: STORE ───

    private static void bindStore(RateDialogFragment fragment,
                                  ViewGroup host,
                                  RatingConfig config,
                                  LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.ar_sheet_store, host, false);
        host.addView(view);
        applyIcon(view.findViewById(R.id.ar_icon), config);
        TextView title = view.findViewById(R.id.ar_title);
        TextView message = view.findViewById(R.id.ar_message);
        if (title != null && config.getStoreTitleTextRes() != 0) title.setText(config.getStoreTitleTextRes());
        if (message != null && config.getStoreMessageTextRes() != 0) message.setText(config.getStoreMessageTextRes());

        MaterialButton rateNow = view.findViewById(R.id.ar_rate_now_button);
        if (rateNow != null) {
            if (config.getRateNowButtonTextRes() != 0) rateNow.setText(config.getRateNowButtonTextRes());
            rateNow.setOnClickListener(v -> {
                PreferenceUtil.setDialogAgreed(fragment.requireContext());
                if (config.getRateNowClickListener() != null) {
                    config.getRateNowClickListener().onClick();
                } else {
                    FeedbackUtils.openPlayStoreListing(fragment.requireContext());
                }
                if (config.getAdditionalRateNowClickListener() != null) {
                    config.getAdditionalRateNowClickListener().onClick();
                }
                fragment.dismissAllowingStateLoss();
            });
        }

        wireLaterAndNever(fragment, view, config);
    }

    // ─── Step: FEEDBACK ───

    private static void bindFeedback(RateDialogFragment fragment,
                                     ViewGroup host,
                                     RatingConfig config,
                                     LayoutInflater inflater) {
        FeedbackFormView form = (FeedbackFormView) inflater.inflate(R.layout.ar_sheet_feedback, host, false);
        host.addView(form);
        form.bind(fragment, config);
    }

    // ─── Step: THANKS ───

    private static void bindThanks(RateDialogFragment fragment,
                                   ViewGroup host,
                                   RatingConfig config,
                                   LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.ar_sheet_thanks, host, false);
        host.addView(view);
        applyIcon(view.findViewById(R.id.ar_icon), config);
        // Auto-dismiss after a short delay for a non-blocking UX.
        view.postDelayed(() -> {
            if (fragment.isAdded()) fragment.dismissAllowingStateLoss();
        }, 1500L);
    }

    // ─── Shared helpers ───

    private static void wireLaterAndNever(RateDialogFragment fragment, View root, RatingConfig config) {
        View later = root.findViewById(R.id.ar_later_button);
        if (later != null) {
            if (later instanceof TextView && config.getRateLaterButtonTextRes() != 0) {
                ((TextView) later).setText(config.getRateLaterButtonTextRes());
            }
            later.setOnClickListener(v -> {
                PreferenceUtil.onLaterClicked(fragment.requireContext());
                if (config.getRateLaterClickListener() != null) config.getRateLaterClickListener().onClick();
                fragment.dismissAllowingStateLoss();
            });
        }

        View never = root.findViewById(R.id.ar_never_button);
        if (never != null) {
            int laterClicks = PreferenceUtil.getLaterClickCount(fragment.requireContext());
            boolean visible = config.isShowRateNeverButton()
                    && laterClicks >= config.getCountOfLaterClicksBeforeNeverButton();
            never.setVisibility(visible ? View.VISIBLE : View.GONE);
            if (visible) {
                if (never instanceof TextView && config.getRateNeverButtonTextRes() != 0) {
                    ((TextView) never).setText(config.getRateNeverButtonTextRes());
                }
                never.setOnClickListener(v -> {
                    PreferenceUtil.setDoNotShowAgain(fragment.requireContext());
                    if (config.getRateNeverClickListener() != null) config.getRateNeverClickListener().onClick();
                    fragment.dismissAllowingStateLoss();
                });
            }
        }
    }

    private static void applyIcon(@Nullable ImageView view, RatingConfig config) {
        if (view == null) return;
        if (config.getIconDrawableRes() != 0) {
            view.setImageResource(config.getIconDrawableRes());
        } else {
            Context ctx = view.getContext();
            try {
                Drawable appIcon = ctx.getPackageManager().getApplicationIcon(ctx.getApplicationInfo());
                view.setImageDrawable(appIcon);
            } catch (Exception e) {
                RatingLogger.warn("Could not load app icon: " + e.getMessage());
            }
        }
    }

    private static void applyTitle(@Nullable TextView view, RatingConfig config) {
        if (view == null || config.getTitleTextRes() == 0) return;
        view.setText(config.getTitleTextRes());
    }

    private static void applyMessage(@Nullable TextView view, RatingConfig config) {
        if (view == null) return;
        if (config.getMessageTextRes() == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(config.getMessageTextRes());
            view.setVisibility(View.VISIBLE);
        }
    }

    @ColorInt
    static int resolvePrimary(Context ctx, RatingConfig config) {
        if (config.getPrimaryColorOverride() != null) return config.getPrimaryColorOverride();
        return resolveThemeAttr(ctx, R.attr.arPrimaryColor);
    }

    @ColorInt
    static int resolveThemeAttr(Context ctx, int attrRes) {
        TypedArray ta = ctx.obtainStyledAttributes(new int[]{attrRes});
        try {
            return ta.getColor(0, 0);
        } finally {
            ta.recycle();
        }
    }
}
