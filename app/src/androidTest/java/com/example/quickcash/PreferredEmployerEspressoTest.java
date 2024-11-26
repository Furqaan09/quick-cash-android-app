package com.example.quickcash;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import com.example.quickcash.ui.EmployerActivity;

@RunWith(AndroidJUnit4.class)

public class PreferredEmployerEspressoTest {

    // Test to verify adding an employer to the preferred list updates the UI accordingly.

    @Test
    public void testAddEmployerToPreferred() {
        // Launch the EmployerActivity for a specific employer
        String employer = ""; // Replace with a valid test employer ID
        ActivityScenario<EmployerActivity> scenario =
                ActivityScenario.launch(EmployerActivity.createIntent(
                        androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                        employer));

        // Perform click on "Add to Preferred" button
        onView(withId(R.id.addToPreferredButton)).perform(click());

        // Click the dashboard button
        onView(withId(R.id.dashboardButton))
                .perform(click());

        // Click the "Preferred Employers" button
        onView(withId(R.id.preferredEmployersRecyclerView)).perform(click());

        // Verify that the employer is listed in the preferred employers dashboard
        onView(withText(employer)).check(matches(withText(employer)));
    }
}