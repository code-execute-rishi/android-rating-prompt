package com.xyz.awesomerating;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.xyz.awesomerating.internal.ConditionsChecker;
import com.xyz.awesomerating.internal.PreferenceUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ConditionsCheckerTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        PreferenceUtil.reset(context);
    }

    @After
    public void tearDown() {
        PreferenceUtil.reset(context);
    }

    @Test
    public void shouldShowFreshPromptWhenInitialThresholdsAreMet() {
        RatingConfig config = new RatingConfig();
        PreferenceUtil.setMinDays(context, 0);
        PreferenceUtil.setMinLaunches(context, 2);
        PreferenceUtil.increaseLaunchTimes(context);
        PreferenceUtil.increaseLaunchTimes(context);

        assertTrue(ConditionsChecker.shouldShow(context, config));
    }

    @Test
    public void shouldNotShowFreshPromptBeforeLaunchThreshold() {
        RatingConfig config = new RatingConfig();
        PreferenceUtil.setMinDays(context, 0);
        PreferenceUtil.setMinLaunches(context, 2);
        PreferenceUtil.increaseLaunchTimes(context);

        assertFalse(ConditionsChecker.shouldShow(context, config));
    }

    @Test
    public void shouldNotShowWhenInitialCustomConditionFails() {
        RatingConfig config = new RatingConfig();
        config.customCondition = () -> false;
        PreferenceUtil.setMinDays(context, 0);
        PreferenceUtil.setMinLaunches(context, 0);

        assertFalse(ConditionsChecker.shouldShow(context, config));
    }

    @Test
    public void shouldShowAfterLaterClickWhenShowAgainThresholdsAreMet() {
        RatingConfig config = new RatingConfig();
        PreferenceUtil.setMinDays(context, 0);
        PreferenceUtil.setMinLaunches(context, 99);
        PreferenceUtil.setMinDaysToShowAgain(context, 0);
        PreferenceUtil.setMinLaunchesToShowAgain(context, 2);

        PreferenceUtil.onLaterClicked(context);
        PreferenceUtil.increaseLaunchTimes(context);
        PreferenceUtil.increaseLaunchTimes(context);

        assertTrue(ConditionsChecker.shouldShow(context, config));
    }

    @Test
    public void shouldNotShowAgainWhenShowAgainCustomConditionFails() {
        RatingConfig config = new RatingConfig();
        config.customConditionToShowAgain = () -> false;
        PreferenceUtil.setMinDaysToShowAgain(context, 0);
        PreferenceUtil.setMinLaunchesToShowAgain(context, 0);

        PreferenceUtil.onLaterClicked(context);

        assertFalse(ConditionsChecker.shouldShow(context, config));
    }

    @Test
    public void shouldNotShowWhenDialogWasAlreadyAgreed() {
        RatingConfig config = new RatingConfig();
        PreferenceUtil.setMinDays(context, 0);
        PreferenceUtil.setMinLaunches(context, 0);
        PreferenceUtil.setDialogAgreed(context);

        assertFalse(ConditionsChecker.shouldShow(context, config));
    }

    @Test
    public void shouldNotShowWhenUserOptedOut() {
        RatingConfig config = new RatingConfig();
        PreferenceUtil.setMinDays(context, 0);
        PreferenceUtil.setMinLaunches(context, 0);
        PreferenceUtil.setDoNotShowAgain(context);

        assertFalse(ConditionsChecker.shouldShow(context, config));
    }
}
