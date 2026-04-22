package com.xyz.awesomerating.callbacks;

import java.io.Serializable;

/** Fired when a plain button in the rating dialog is clicked (later, never, rate-now, etc.). */
public interface RateClickListener extends Serializable {
    void onClick();
}
