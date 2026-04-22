package com.xyz.awesomerating.callbacks;

/** Fired when the Google in-app review flow completes (or fails to start). */
public interface InAppReviewListener {
    /**
     * @param successful true if the flow launched successfully. Note: this does NOT mean the user
     *                   submitted a review — Google does not expose that signal to apps.
     */
    void onInAppReviewCompleted(boolean successful);
}
