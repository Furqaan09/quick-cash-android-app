package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quickcash.util.employerView.ProcessPaymentActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PaymentEspressoTest {

    private static final String TEST_JOB_ID = "test123";
    private static final String TEST_SALARY = "100.00";

    @Rule
    public ActivityScenarioRule<ProcessPaymentActivity> activityRule =
            new ActivityScenarioRule<>(getPaymentActivityIntent());

    private static Intent getPaymentActivityIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProcessPaymentActivity.class);
        intent.putExtra("jobId", TEST_JOB_ID);
        intent.putExtra("salary", TEST_SALARY);
        return intent;
    }

    @Test
    public void testPaymentUIInitialization() {
        // Test initial UI elements are displayed correctly
        String expectedText = "Payment of: $" + TEST_SALARY + " for JobID: " + TEST_JOB_ID;
        onView(withId(R.id.paymentAmountTextView))
                .check(matches(isDisplayed()))
                .check(matches(withText(expectedText)));

        onView(withId(R.id.payNowButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPayNowButtonClickable() {
        // Verify Pay Now button is clickable
        onView(withId(R.id.payNowButton))
                .check(matches(isDisplayed()))
                .perform(click());

    }
}