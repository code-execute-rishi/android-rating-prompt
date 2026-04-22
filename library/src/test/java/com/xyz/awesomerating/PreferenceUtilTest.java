package com.xyz.awesomerating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.xyz.awesomerating.internal.PreferenceUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class PreferenceUtilTest {
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
    public void defaultsMatchDocumentedGatingValues() {
        assertEquals(0, PreferenceUtil.getLaunchTimes(context));
        assertEquals(5, PreferenceUtil.getMinLaunches(context));
        assertEquals(3, PreferenceUtil.getMinDays(context));
        assertEquals(5, PreferenceUtil.getMinLaunchesToShowAgain(context));
        assertEquals(14, PreferenceUtil.getMinDaysToShowAgain(context));
        assertFalse(PreferenceUtil.isDialogAgreed(context));
        assertFalse(PreferenceUtil.wasLaterClicked(context));
        assertFalse(PreferenceUtil.isDoNotShowAgain(context));
        assertEquals(0, PreferenceUtil.getLaterClickCount(context));
    }

    @Test
    public void increaseLaunchTimesPersistsIncrementingCount() {
        PreferenceUtil.increaseLaunchTimes(context);
        PreferenceUtil.increaseLaunchTimes(context);

        assertEquals(2, PreferenceUtil.getLaunchTimes(context));
    }

    @Test
    public void thresholdSettersPersistValues() {
        PreferenceUtil.setMinLaunches(context, 2);
        PreferenceUtil.setMinDays(context, 1);
        PreferenceUtil.setMinLaunchesToShowAgain(context, 4);
        PreferenceUtil.setMinDaysToShowAgain(context, 9);

        assertEquals(2, PreferenceUtil.getMinLaunches(context));
        assertEquals(1, PreferenceUtil.getMinDays(context));
        assertEquals(4, PreferenceUtil.getMinLaunchesToShowAgain(context));
        assertEquals(9, PreferenceUtil.getMinDaysToShowAgain(context));
    }

    @Test
    public void getRemindTimestampInitializesOnce() {
        long firstTimestamp = PreferenceUtil.getRemindTimestamp(context);
        long secondTimestamp = PreferenceUtil.getRemindTimestamp(context);

        assertTrue(firstTimestamp > 0L);
        assertEquals(firstTimestamp, secondTimestamp);
    }

    @Test
    public void onLaterClickedResetsLaunchesMarksLaterAndCountsClick() {
        PreferenceUtil.increaseLaunchTimes(context);
        PreferenceUtil.increaseLaunchTimes(context);

        PreferenceUtil.onLaterClicked(context);

        assertEquals(0, PreferenceUtil.getLaunchTimes(context));
        assertTrue(PreferenceUtil.wasLaterClicked(context));
        assertEquals(1, PreferenceUtil.getLaterClickCount(context));
    }

    @Test
    public void onInAppReviewCompletedUsesShowAgainBranchOnNextPrompt() {
        PreferenceUtil.increaseLaunchTimes(context);

        PreferenceUtil.onInAppReviewCompleted(context);

        assertEquals(0, PreferenceUtil.getLaunchTimes(context));
        assertTrue(PreferenceUtil.wasLaterClicked(context));
    }

    @Test
    public void resetClearsStoredState() {
        PreferenceUtil.increaseLaunchTimes(context);
        PreferenceUtil.setDialogAgreed(context);
        PreferenceUtil.setDoNotShowAgain(context);
        PreferenceUtil.onLaterClicked(context);

        PreferenceUtil.reset(context);

        assertEquals(0, PreferenceUtil.getLaunchTimes(context));
        assertFalse(PreferenceUtil.isDialogAgreed(context));
        assertFalse(PreferenceUtil.wasLaterClicked(context));
        assertFalse(PreferenceUtil.isDoNotShowAgain(context));
        assertEquals(0, PreferenceUtil.getLaterClickCount(context));
    }
}
