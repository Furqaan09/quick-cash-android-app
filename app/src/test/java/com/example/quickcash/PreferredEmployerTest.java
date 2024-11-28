package com.example.quickcash;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import com.example.quickcash.ui.EmployerActivity;

// UiAutomator test to verify navigation to the Preferred Employers page from EmployerActivity.

public class PreferredEmployerTest {

    private UiDevice device;

    @Before
    public void setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Launch EmployerActivity
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(context, EmployerActivity.class);

        // Ensure the activity starts fresh
        ActivityScenario.launch(intent);
    }

    /**
     * Test to verify that clicking the Preferred Employers button navigates
     * to the Preferred Employers page and displays the title
     */
    @Test
    public void moveToPreferredEmployersActivity() {
        String preferredEmployersButtonId = "preferredEmployersButton";

        // Find the "Preferred Employers" button by its resource ID
        UiObject2 preferredEmployersButton = device.findObject(By.res(preferredEmployersButtonId));
        assertNotNull("Preferred Employers button not found", preferredEmployersButton);

        preferredEmployersButton.click();

        String expectedTitle = "Preferred Employers";

        assertNotNull("Preferred Employers page title not found", expectedTitle);
    }
}
