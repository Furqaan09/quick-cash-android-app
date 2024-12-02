package com.example.quickcash.util.employerView;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.firebase.FirebaseCompleteJob;

import com.example.quickcash.R;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class ProcessPaymentActivity extends AppCompatActivity {

    private static final String CLIENT_KEY = "AUJVGR_8i6AOY0_cXuzLZgNNTO08R65CC9gGbBJ3BMJOGHOjJDs-fbFBELR7COakrnECzR1S6cKD-1Ur";
    private PayPalConfiguration payPalConfig;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String amount;
    private String jobId;
    private String applicationId;

    // UI Elements
    TextView paymentAmountTextView;
    Button payNowButton;
    TextView paymentStatusTextView;

    FirebaseCompleteJob completeJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        paymentAmountTextView = findViewById(R.id.paymentAmountTextView);
        payNowButton = findViewById(R.id.payNowButton);
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView);
        configurePayPal();
        initActivityLauncher();
        setListeners();

        completeJob = new FirebaseCompleteJob();
        applicationId = getIntent().getStringExtra("applicationId");
        jobId = getIntent().getStringExtra("jobId");
        amount = getIntent().getStringExtra("salary");
        Log.d("Salary", "Set salary: " + amount);
        String displayText = "Payment of: $" + amount + " for JobID: " + jobId;
        paymentAmountTextView.setText(displayText);
    }

    private void configurePayPal() {
        payPalConfig = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(CLIENT_KEY);
    }

    private void initActivityLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        PaymentConfirmation confirmation = result.getData().getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                        if (confirmation != null) {
                            try {
                                String paymentDetails = confirmation.toJSONObject().toString(4);
                                JSONObject payObj = new JSONObject(paymentDetails);
                                String payID = payObj.getJSONObject("response").getString("id");
                                String state = payObj.getJSONObject("response").getString("state");
                                paymentStatusTextView.setText(String.format("Payment %s\nPayment ID: %s", state, payID));

                                // Update the status in the database
                                completeJob.setPaymentStatusPaid(applicationId);

                            } catch (JSONException e) {
                                Log.e("PaymentError", "Failed to parse payment confirmation.", e);
                            }
                        }
                    } else if (result.getResultCode() == PaymentActivity.RESULT_EXTRAS_INVALID) {
                        Log.e("PaymentError", "Invalid Payment");
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.e("PaymentError", "Payment Canceled");
                    }
                });
    }

    private void setListeners() {
        payNowButton.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "CAD", "Job Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        activityResultLauncher.launch(intent);
    }
}
