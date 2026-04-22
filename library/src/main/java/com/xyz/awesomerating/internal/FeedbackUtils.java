package com.xyz.awesomerating.internal;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xyz.awesomerating.MailSettings;
import com.xyz.awesomerating.R;
import com.xyz.awesomerating.model.DeviceInfo;

/**
 * Intent builders for the Play Store listing and mail feedback composer.
 *
 * <p>Mail intent uses {@code ACTION_SENDTO} + {@code mailto:} URI so the chooser is filtered
 * to mail clients only (no share-sheet pollution).</p>
 */
public final class FeedbackUtils {

    private static final String PLAY_IN_APP_URL = "market://details?id=";
    private static final String PLAY_WEB_URL = "https://play.google.com/store/apps/details?id=";
    private static final String URI_MAILTO = "mailto:";

    private FeedbackUtils() {}

    // ─── Play Store ───

    public static void openPlayStoreListing(@NonNull Context ctx) {
        String pkg = ctx.getPackageName();
        Uri inApp = Uri.parse(PLAY_IN_APP_URL + pkg);
        Intent intent = new Intent(Intent.ACTION_VIEW, inApp)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException notFound) {
            Uri web = Uri.parse(PLAY_WEB_URL + pkg);
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, web)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    // ─── Mail feedback ───

    /** Compose a rich feedback mail with rating + user text + optional device info. */
    public static void sendFeedbackMail(@NonNull Context ctx,
                                        @NonNull MailSettings settings,
                                        @NonNull String userText,
                                        @Nullable DeviceInfo deviceInfo,
                                        float rating) {
        String subject = settings.subject != null
                ? settings.subject
                : appLabel(ctx) + " feedback";

        StringBuilder body = new StringBuilder();
        if (settings.bodyPrefix != null && !settings.bodyPrefix.isEmpty()) {
            body.append(settings.bodyPrefix).append("\n\n");
        }
        body.append("Rating: ").append(rating).append(" / 5\n\n");
        body.append("Feedback:\n").append(userText).append("\n\n");
        if (deviceInfo != null) {
            body.append(deviceInfo.toMailBlock());
        }

        launchMailIntent(ctx, settings, subject, body.toString());
    }

    /** Fire a raw mail composer with nothing but dev-provided settings. */
    public static void openRawMail(@NonNull Context ctx, @NonNull MailSettings settings) {
        String subject = settings.subject != null ? settings.subject : "";
        String body = settings.bodyPrefix != null ? settings.bodyPrefix : "";
        launchMailIntent(ctx, settings, subject, body);
    }

    private static void launchMailIntent(Context ctx,
                                         MailSettings settings,
                                         String subject,
                                         String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(URI_MAILTO));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{settings.recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(intent, ctx.getString(R.string.ar_feedback_pick_mail_app))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(chooser);
            RatingLogger.info("Launched mail feedback intent to " + settings.recipient);
        } else {
            String err = settings.errorToastMessage != null
                    ? settings.errorToastMessage
                    : ctx.getString(R.string.ar_feedback_no_mail_app);
            Toast.makeText(ctx, err, Toast.LENGTH_LONG).show();
            RatingLogger.error("No mail app resolved for feedback intent");
        }
    }

    @NonNull
    private static String appLabel(@NonNull Context ctx) {
        try {
            ApplicationInfo ai = ctx.getApplicationInfo();
            CharSequence label = ctx.getPackageManager().getApplicationLabel(ai);
            return label != null ? label.toString() : ctx.getPackageName();
        } catch (Exception e) {
            return ctx.getPackageName();
        }
    }
}
