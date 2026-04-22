package com.xyz.awesomerating;

/**
 * How low-rating feedback is handled.
 * <ul>
 *     <li>{@link #DISABLED} — no feedback form shown; low raters just see a "thanks" and dialog closes.</li>
 *     <li>{@link #EMAIL} — feedback form opens the device mail app with developer email prefilled.</li>
 *     <li>{@link #CUSTOM} — feedback text is passed to a developer-supplied callback; developer handles submission (e.g. REST API).</li>
 * </ul>
 */
public enum FeedbackMode {
    DISABLED,
    EMAIL,
    CUSTOM
}
