package com.example.quickcash;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.quickcash.util.employeeView.JobApplicationActivity;

@RunWith(AndroidJUnit4.class)
public class JobApplicationEspressoTest {

    @Rule
    public ActivityScenarioRule<JobApplicationActivity> activityScenarioRule = new ActivityScenarioRule<>(JobApplicationActivity.class);

    @Test
    public void testInvalidName() throws InterruptedException {
        onView(withId(R.id.applicationNameBox)).perform(typeText("John119"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationPhoneBox)).perform(typeText("1234567890"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationEmailBox)).perform(typeText("johndoe@example.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationSubmitButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.applicationStatusLabel)).check(matches(withText(R.string.INVALID_NAME)));
    }

    @Test
    public void testInvalidPhone() throws InterruptedException {
        onView(withId(R.id.applicationNameBox)).perform(typeText("John Doe"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationPhoneBox)).perform(typeText("123456789"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationEmailBox)).perform(typeText("johndoe@example.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationSubmitButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.applicationStatusLabel)).check(matches(withText(R.string.INVALID_PHONE_NUMBER)));
    }

    @Test
    public void testInvalidEmail() throws InterruptedException {
        onView(withId(R.id.applicationNameBox)).perform(typeText("John Doe"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationPhoneBox)).perform(typeText("1234567890"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationEmailBox)).perform(typeText("johndoe.example.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationSubmitButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.applicationStatusLabel)).check(matches(withText(R.string.INVALID_EMAIL)));
    }

    @Test
    public void testResumeNotUploaded() throws InterruptedException {
        onView(withId(R.id.applicationNameBox)).perform(typeText("John Doe"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationPhoneBox)).perform(typeText("1234567890"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationEmailBox)).perform(typeText("johndoe@example.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applicationSubmitButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.applicationStatusLabel)).check(matches(withText(R.string.RESUME_NOT_SELECTED)));
    }
}
