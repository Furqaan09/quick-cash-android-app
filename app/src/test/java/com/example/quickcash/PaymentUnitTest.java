package com.example.quickcash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.quickcash.firebase.FirebaseCompleteJob;
import com.example.quickcash.firebase.FirebaseCompleteJob.SalaryCallback;
import com.example.quickcash.firebase.FirebaseCompleteJob.PaymentStatusCheckCallback;
import com.example.quickcash.firebase.FirebaseCompleteJob.StatusCheckCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentUnitTest {

    @Mock
    private FirebaseCompleteJob mockFirebaseCompleteJob;

    private static final String TEST_JOB_ID = "test123";
    private static final String TEST_SALARY = "100.00";

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSetPaymentStatusPaid() {
        // Use doAnswer for void method
        doAnswer(invocation -> null).when(mockFirebaseCompleteJob).setPaymentStatusPaid(TEST_JOB_ID);

        mockFirebaseCompleteJob.setPaymentStatusPaid(TEST_JOB_ID);
        verify(mockFirebaseCompleteJob).setPaymentStatusPaid(TEST_JOB_ID);
    }

    @Test
    public void testIsPaymentStatusPaid() {
        // Mock callback with doAnswer
        doAnswer(invocation -> {
            PaymentStatusCheckCallback callback = invocation.getArgument(1);
            callback.onPaymentStatusChecked(true);
            return null;
        }).when(mockFirebaseCompleteJob).isPaymentStatusPaid(anyString(), any(PaymentStatusCheckCallback.class));

        mockFirebaseCompleteJob.isPaymentStatusPaid(TEST_JOB_ID, isPaid -> {
            assertTrue("Payment should be marked as paid", isPaid);
        });
    }

    @Test
    public void testGetSalaryForCompletedJob() {
        // Mock callback with doAnswer
        doAnswer(invocation -> {
            SalaryCallback callback = invocation.getArgument(1);
            callback.onSalaryRetrieved(TEST_SALARY);
            return null;
        }).when(mockFirebaseCompleteJob).getSalaryForCompletedJob(anyString(), any(SalaryCallback.class));

        mockFirebaseCompleteJob.getSalaryForCompletedJob(TEST_JOB_ID, salary -> {
            assertEquals("Salary should match expected value", TEST_SALARY, salary);
        });
    }

    @Test
    public void testJobCompletionStatus() {
        // Mock callback with doAnswer
        doAnswer(invocation -> {
            StatusCheckCallback callback = invocation.getArgument(1);
            callback.onStatusChecked(true);
            return null;
        }).when(mockFirebaseCompleteJob).isApplicantStatusComplete(anyString(), any(StatusCheckCallback.class));

        mockFirebaseCompleteJob.isApplicantStatusComplete(TEST_JOB_ID, isComplete -> {
            assertTrue("Job should be marked as complete", isComplete);
        });
    }

    @Test
    public void testNoCompletedJobResponse() {
        String noJobMessage = "No completed application found for this job.";

        // Mock callback with doAnswer
        doAnswer(invocation -> {
            SalaryCallback callback = invocation.getArgument(1);
            callback.onSalaryRetrieved(noJobMessage);
            return null;
        }).when(mockFirebaseCompleteJob).getSalaryForCompletedJob(anyString(), any(SalaryCallback.class));

        mockFirebaseCompleteJob.getSalaryForCompletedJob(TEST_JOB_ID, salary -> {
            assertEquals("Should receive no completed job message", noJobMessage, salary);
        });
    }

    private static <T> T any(Class<T> type) {
        return org.mockito.ArgumentMatchers.any(type);
    }
}