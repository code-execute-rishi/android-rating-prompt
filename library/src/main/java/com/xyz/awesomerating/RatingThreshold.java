package com.xyz.awesomerating;

import java.io.Serializable;

/**
 * Rating threshold. Ratings at or above the threshold branch to the Play Store CTA;
 * ratings below branch to the feedback form (if enabled).
 *
 * <p>Half-step values supported for sub-star granularity. {@link #asFloat()} returns
 * the numeric rating (0.0 through 5.0).</p>
 *
 * <p>Use {@link #NONE} to disable the feedback branch entirely — every rating goes to Play Store.</p>
 */
public enum RatingThreshold implements Serializable {
    NONE,
    HALF,
    ONE,
    ONE_AND_A_HALF,
    TWO,
    TWO_AND_A_HALF,
    THREE,
    THREE_AND_A_HALF,
    FOUR,
    FOUR_AND_A_HALF,
    FIVE;

    public float asFloat() {
        return ordinal() / 2f;
    }
}
