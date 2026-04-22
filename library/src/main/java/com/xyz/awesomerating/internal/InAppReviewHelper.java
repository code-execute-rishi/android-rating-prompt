package com.xyz.awesomerating.internal;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.xyz.awesomerating.RatingConfig;

/**
 * Wraps Google Play Core ReviewManager. Uses reflection so the Play Core dep can be
 * {@code compileOnly} — host apps that don't use in-app review can skip the dep.
 */
public final class InAppReviewHelper {

    private InAppReviewHelper() {}

    public static void launch(@NonNull FragmentActivity activity, @NonNull RatingConfig config) {
        try {
            Class<?> factoryClass = Class.forName("com.google.android.play.core.review.ReviewManagerFactory");
            Object manager = factoryClass.getMethod("create", android.content.Context.class)
                    .invoke(null, activity);
            if (manager == null) {
                fail(config, "ReviewManagerFactory.create returned null");
                return;
            }

            Object requestTask = manager.getClass().getMethod("requestReviewFlow").invoke(manager);
            if (requestTask == null) {
                fail(config, "requestReviewFlow returned null");
                return;
            }

            Class<?> listenerClass = Class.forName("com.google.android.gms.tasks.OnCompleteListener");
            final Object finalManager = manager;

            Object onComplete = java.lang.reflect.Proxy.newProxyInstance(
                    listenerClass.getClassLoader(),
                    new Class<?>[]{listenerClass},
                    (proxy, method, args) -> {
                        Object task = args[0];
                        boolean success = (Boolean) task.getClass().getMethod("isSuccessful").invoke(task);
                        if (!success) {
                            fail(config, "requestReviewFlow task not successful");
                            return null;
                        }
                        Object reviewInfo = task.getClass().getMethod("getResult").invoke(task);
                        if (reviewInfo == null) {
                            fail(config, "reviewInfo is null");
                            return null;
                        }
                        Class<?> reviewInfoClass = Class.forName("com.google.android.play.core.review.ReviewInfo");
                        Object launchTask = finalManager.getClass()
                                .getMethod("launchReviewFlow", android.app.Activity.class, reviewInfoClass)
                                .invoke(finalManager, activity, reviewInfo);
                        Object launchListener = java.lang.reflect.Proxy.newProxyInstance(
                                listenerClass.getClassLoader(),
                                new Class<?>[]{listenerClass},
                                (p2, m2, a2) -> {
                                    Object t2 = a2[0];
                                    boolean ok = (Boolean) t2.getClass().getMethod("isSuccessful").invoke(t2);
                                    PreferenceUtil.onInAppReviewCompleted(activity);
                                    if (config.getInAppReviewListener() != null) {
                                        config.getInAppReviewListener().onInAppReviewCompleted(ok);
                                    }
                                    return null;
                                });
                        launchTask.getClass().getMethod("addOnCompleteListener", listenerClass)
                                .invoke(launchTask, launchListener);
                        return null;
                    });
            requestTask.getClass().getMethod("addOnCompleteListener", listenerClass)
                    .invoke(requestTask, onComplete);
        } catch (ClassNotFoundException cnfe) {
            RatingLogger.error("Play Core not on classpath. Add 'com.google.android.play:review:2.0.2' " +
                    "to your app's dependencies, or remove useGoogleInAppReview() from the Builder.");
            fail(config, "Play Core missing");
        } catch (Exception e) {
            RatingLogger.error("In-app review reflective invocation failed", e);
            fail(config, e.getMessage());
        }
    }

    private static void fail(RatingConfig config, String reason) {
        RatingLogger.warn("In-app review failed: " + reason);
        if (config.getInAppReviewListener() != null) {
            config.getInAppReviewListener().onInAppReviewCompleted(false);
        }
    }
}
