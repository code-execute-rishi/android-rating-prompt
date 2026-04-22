package com.xyz.awesomerating.internal;

/** Finite states inside the rating bottom sheet. */
public enum DialogStep {
    /** Initial rating capture (variant-specific layout). */
    RATING,
    /** High rating → Play Store CTA. */
    STORE,
    /** Low rating → feedback form (EditText + device-info checkbox). */
    FEEDBACK,
    /** Low rating + feedback disabled → short thank-you then dismiss. */
    THANKS
}
