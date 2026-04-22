package com.xyz.awesomerating.example;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.xyz.awesomerating.AwesomeRating;
import com.xyz.awesomerating.MailSettings;
import com.xyz.awesomerating.RatingThreshold;
import com.xyz.awesomerating.Variant;

/**
 * Demo showcases the three variants + feedback modes.
 *
 * <p>Each demo button calls {@code showNow()} bypassing conditions, so every tap opens the
 * dialog immediately for testing. In a real app, use {@code showIfMeetsConditions()} only
 * from {@code onCreate()} when {@code savedInstanceState == null}.</p>
 */
public class MainActivity extends AppCompatActivity {

    private static final String DEV_EMAIL = "hello@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reset on every launch so demo is stateless
        AwesomeRating.reset(this);

        findViewById(R.id.btn_emoji).setOnClickListener(v ->
                AwesomeRating.with(this)
                        .variant(Variant.EMOJI)
                        .threshold(RatingThreshold.FOUR)
                        .enableEmailFeedback(new MailSettings(DEV_EMAIL))
                        .showRateNeverButton()
                        .debug(true)
                        .onConfirm(rating -> Toast.makeText(this, "Rated: " + rating, Toast.LENGTH_SHORT).show())
                        .showNow());

        findViewById(R.id.btn_stars).setOnClickListener(v ->
                AwesomeRating.with(this)
                        .variant(Variant.STARS)
                        .threshold(RatingThreshold.FOUR)
                        .enableEmailFeedback(DEV_EMAIL)
                        .debug(true)
                        .showNow());

        findViewById(R.id.btn_binary).setOnClickListener(v ->
                AwesomeRating.with(this)
                        .variant(Variant.BINARY)
                        .threshold(RatingThreshold.FOUR)
                        .enableEmailFeedback(DEV_EMAIL)
                        .debug(true)
                        .showNow());

        findViewById(R.id.btn_custom_feedback).setOnClickListener(v ->
                AwesomeRating.with(this)
                        .variant(Variant.STARS)
                        .threshold(RatingThreshold.FOUR)
                        .enableCustomFeedback((text, includeDeviceInfo, deviceInfo, rating) -> {
                            String msg = "Got: " + text + (deviceInfo != null ? "\n\n" + deviceInfo.toShortString() : "");
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        })
                        .debug(true)
                        .showNow());

        findViewById(R.id.btn_in_app_review).setOnClickListener(v ->
                AwesomeRating.with(this)
                        .useGoogleInAppReview()
                        .onInAppReviewCompleted(ok -> Toast.makeText(this,
                                "In-app review completed: " + ok, Toast.LENGTH_SHORT).show())
                        .debug(true)
                        .showNow());

        findViewById(R.id.btn_branded).setOnClickListener(v ->
                AwesomeRating.with(this)
                        .variant(Variant.EMOJI)
                        .primaryColor(0xFFEF4444)        // red brand
                        .positiveColor(0xFF10B981)
                        .negativeColor(0xFFF59E0B)
                        .icon(R.mipmap.ic_launcher)
                        .enableEmailFeedback(DEV_EMAIL)
                        .debug(true)
                        .showNow());
    }
}
