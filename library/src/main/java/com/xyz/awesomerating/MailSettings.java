package com.xyz.awesomerating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Settings that control the mail intent composed when the user sends feedback via email.
 *
 * <p>{@link #recipient} is required. Everything else is optional; sensible defaults are
 * applied by the library.</p>
 */
public final class MailSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull public final String recipient;
    @Nullable public final String subject;
    @Nullable public final String bodyPrefix;
    @Nullable public final String errorToastMessage;

    public MailSettings(@NonNull String recipient,
                        @Nullable String subject,
                        @Nullable String bodyPrefix,
                        @Nullable String errorToastMessage) {
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new IllegalArgumentException("recipient must not be empty");
        }
        this.recipient = recipient.trim();
        this.subject = subject;
        this.bodyPrefix = bodyPrefix;
        this.errorToastMessage = errorToastMessage;
    }

    public MailSettings(@NonNull String recipient) {
        this(recipient, null, null, null);
    }
}
