package com.example.quickcash;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RatingUIAutomatorTest {

    private static final int LAUNCH_TIMEOUT = 10000;
    final String launcherPackage = "com.example.quickcash";
    private UiDevice device;

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        final Intent appIntent = context.getPackageManager().getLaunchIntentForPackage(launcherPackage);
        assert appIntent != null;
        appIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(appIntent);
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void testEmployeeRatingFlow() throws UiObjectNotFoundException {
        // Login as Employee.
        Espresso.onView(withId(R.id.roleSelectionSpinner)).perform(click());
        Espresso.onView(withText("Employee")).perform(click());

        Espresso.onView(withId(R.id.emailAddressEditField)).perform(typeText("furqaanmalik09@gmail.com"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.passwordEditField)).perform(typeText("Pass123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        // Wait for Employee dashboard to load.
        device.wait(Until.hasObject(By.textContains("Available Jobs")), 5000);

        // Click on Application Status button.
        Espresso.onView(withId(R.id.applicationStatusButton)).perform(click());

        // Wait for Applications page to load.
        device.wait(Until.hasObject(By.textContains("My Applications")), 5000);

        // Click on Rate button (assuming it's visible for a completed job).
        UiObject rateButton = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/rateEmployeeButton"));
        rateButton.click();

        // Wait for Rating page to appear.
        device.wait(Until.hasObject(By.textContains("Rate Employee")), 5000);

        // Click on a star rating (let's say 4 stars).
        Espresso.onView(withId(R.id.star4)).perform(click());

        // Click submit button.
        Espresso.onView(withId(R.id.submitButton)).perform(click());

        // Verify we're back at the Applications page.
        device.wait(Until.hasObject(By.textContains("My Applications")), 5000);
        UiObject applicationsTitle = device.findObject(new UiSelector().textContains("My Applications"));
        assertTrue("Should return to Applications page after rating", applicationsTitle.exists());
    }
}
