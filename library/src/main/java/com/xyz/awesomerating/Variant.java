package com.xyz.awesomerating;

/**
 * Visual variant of the rating dialog. Three UX patterns, each tuned for different app types.
 * <ul>
 *     <li>{@link #EMOJI} — 5 emoji chips, emotional one-tap. Good for casual apps.</li>
 *     <li>{@link #STARS} — classic 5-star bar with celebration for high ratings. Good for utility apps.</li>
 *     <li>{@link #BINARY} — two-card Love-it / Needs-work gate. Highest conversion, Apple-style.</li>
 * </ul>
 */
public enum Variant {
    EMOJI,
    STARS,
    BINARY
}
