package com.example.quickcash;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PaymentUIAutomatorTest {

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String PACKAGE_NAME = "com.example.quickcash";

    private UiDevice device;

    @Before
    public void setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressHome();

        // Launch the app
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void testCompletePaymentFlow() throws UiObjectNotFoundException {
        // First login as employer
        loginAsEmployer();

        // Find and click Mark Complete button
        UiObject markCompleteButton = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/markCompleteButton"));
        assertTrue("Mark Complete button should be visible", markCompleteButton.exists());
        markCompleteButton.click();

        // After marking complete, Process Payment button should appear
        UiObject processPaymentButton = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/processPaymentButton"));
        assertTrue("Process Payment button should be visible", processPaymentButton.exists());
        processPaymentButton.click();

        // Verify we're on payment screen and elements are present
        UiObject paymentAmount = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/paymentAmountTextView"));
        assertTrue("Payment amount should be displayed", paymentAmount.exists());

        // Click Pay Now button to launch PayPal
        UiObject payNowButton = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/payNowButton"));
        assertTrue("Pay Now button should be displayed", payNowButton.exists());

    }

    private void loginAsEmployer() throws UiObjectNotFoundException {
        // Select Employer role
        UiObject roleSpinner = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/roleSelectionSpinner"));
        roleSpinner.click();

        UiObject employerOption = device.findObject(new UiSelector().text("Employer"));
        employerOption.click();

        // Enter credentials (use test account)
        UiObject emailField = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/emailAddressEditField"));
        emailField.setText("royalking665500@gmail.com");

        UiObject passwordField = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/passwordEditField"));
        passwordField.setText("Pass123");

        UiObject loginButton = device.findObject(new UiSelector()
                .resourceId(PACKAGE_NAME + ":id/loginButton"));
        loginButton.click();

        // Wait for employer screen
        device.wait(Until.hasObject(By.text("My Postings")), LAUNCH_TIMEOUT);
    }
}