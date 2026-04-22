package com.xyz.awesomerating.callbacks;

import java.io.Serializable;

/** Fired when the user confirms their rating. Useful for analytics. */
public interface ConfirmClickListener extends Serializable {
    /**
     * @param rating user-selected rating in the range 0.0f–5.0f
     */
    void onConfirm(float rating);
}
