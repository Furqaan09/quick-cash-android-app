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


// UiAutomator test to verify navigation to the Preferred Employers page from EmployerActivity.
@RunWith(AndroidJUnit4.class)
public class PreferredEmployerUiTest {

    private static final int LAUNCH_TIMEOUT = 10000;
    final String launcherPackage = "com.example.quickcash";
    private UiDevice device;

    // Setup before every tests
    @Before
    public void setup() {
        // Get an instance of UiDevice for interacting with the device
        device = UiDevice.getInstance(getInstrumentation());

        // Get the application context
        Context context = ApplicationProvider.getApplicationContext();

        // Retrieve the launch intent for the specified package
        final Intent appIntent = context.getPackageManager().getLaunchIntentForPackage(launcherPackage);

        // Add the flag to clear the task before launching the application
        appIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Start the application using the launch intent
        context.startActivity(appIntent);

        // Wait for the launcher package to be in the foreground
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }


    // Test to verify that clicking the Preferred Employers button navigates to the
    // Preferred Employers page and displays the title
    @Test
    public void moveToPreferredEmployersActivity() throws UiObjectNotFoundException {
        Espresso.onView(withId(R.id.roleSelectionSpinner)).perform(click());
        Espresso.onView(withText("Employee")).perform(click());

        Espresso.onView(withId(R.id.emailAddressEditField)).perform(typeText("furqaanmalik09@gmail.com"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.passwordEditField)).perform(typeText("Pass123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        device.wait(Until.hasObject(By.textContains("Available Jobs")), 5000);

        // Ensure the button is visible by scrolling
        Espresso.onView(withId(R.id.preferredEmployersButton))
                .perform(androidx.test.espresso.action.ViewActions.scrollTo());

        Espresso.onView(withId(R.id.preferredEmployersButton)).perform(click());

        // Wait for the Employer Profile page to load
        device.wait(Until.hasObject(By.textContains("Preferred Employers")), 5000);

        UiObject preferredEmployerText = new UiObject(new UiSelector().textContains("Preferred Employers"));
        assertTrue("Preferred Employers text is visible", preferredEmployerText.exists());
    }
}
