package com.example.quickcash.util.employeeView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcash.validator.CredentialsValidator;
import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseApplicationSubmission;

import java.io.File;

// This class controls the behaviour of Job Application submission
public class JobApplicationActivity extends AppCompatActivity {

    private static final int FILE_SELECTION_REQUEST = 1;
    private Uri fileUri;
    private FirebaseApplicationSubmission applicationSubmission;
    private String jobId;
    private String jobTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_application);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.jobId = getIntent().getStringExtra("jobId");
        this.jobTitle = getIntent().getStringExtra("jobTitle");
        setUploadResumeButton();
        setApplicationSubmitButton();
        setPageTitle("Application for "+jobTitle);
    }

    // Getters to get information from fields
    protected String getName() {
        EditText name = findViewById(R.id.applicationNameBox);
        return name.getText().toString().trim();
    }

    protected String getPhone() {
        EditText phone = findViewById(R.id.applicationPhoneBox);
        return phone.getText().toString().trim();
    }

    protected String getEmail() {
        EditText email = findViewById(R.id.applicationEmailBox);
        return email.getText().toString().trim();
    }

    protected String getFile() {
        TextView fileName = findViewById(R.id.fileNameTextView);
        return fileName.getText().toString().trim();
    }

    // Set the page title based on the Job Title
    public void setPageTitle(String title) {
        TextView pageTitle = findViewById(R.id.applicationTitle);
        pageTitle.setText(title);
    }

    // Set the file name field with uploaded file
    public void setFileName(String fName) {
        TextView fileName = findViewById(R.id.fileNameTextView);
        fileName.setText(fName);
    }

    // Set the status message
    public void setStatusMessage(String message) {
        TextView statusLabel = findViewById(R.id.applicationStatusLabel);
        statusLabel.setText(message.trim());
    }

    // Get the file name from the specified file path
    private String extractFileName(Uri fileUri) {
        String path = fileUri.getPath();
        File file = new File(path);
        return file.getName();
    }

    // Set the upload resume button
    public void setUploadResumeButton() {
        Button uploadResume = findViewById(R.id.resumeUploadButton);
        uploadResume.setOnClickListener(view -> handelUploadResume());
    }

    // Open file selection menu to upload a resume. Only pdf file allowed
    public void handelUploadResume() {
        Intent fileSelection = new Intent(Intent.ACTION_GET_CONTENT);
        fileSelection.setType("application/pdf");
        startActivityForResult(fileSelection, FILE_SELECTION_REQUEST);
    }

    // Once file is selected, extract the file name to show the selected file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECTION_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            this.fileUri = data.getData();  // Get the Uri of the selected file

            // Extract the file name from the Uri and display it
            String fileName = extractFileName(fileUri);
            setFileName(fileName);
        }
    }

    // Setup application submission button
    public void setApplicationSubmitButton() {
        Button submitApplication = findViewById(R.id.applicationSubmitButton);
        submitApplication.setOnClickListener(view -> handelSubmitApplication());
    }

    // On Submit, validate the credentials and submit the application
    public void handelSubmitApplication() {
        CredentialsValidator validator = new CredentialsValidator();

        String name = getName();
        String email = getEmail();
        String phone = getPhone();
        String file = getFile();
        String errorMessage;

        if (!validator.isValidName(name)) {
            errorMessage = getResources().getString(R.string.INVALID_NAME);
        } else if (!validator.isValidPhone(phone)) {
            errorMessage = getResources().getString(R.string.INVALID_PHONE_NUMBER);
        } else if (!validator.isValidEmail(email)) {
            errorMessage = getResources().getString(R.string.INVALID_EMAIL);
        } else if (!validator.isFileUploaded(file)) {
            errorMessage = getResources().getString(R.string.RESUME_NOT_SELECTED);
        } else {
            errorMessage = getResources().getString(R.string.SUBMIT_APPLICATION_SUCCESSFUL);
            // Submit the application to Firebase
            applicationSubmission = new FirebaseApplicationSubmission(name, phone, email, jobId, fileUri, JobApplicationActivity.this);
            applicationSubmission.submit2Firebase();
        }

        setStatusMessage(errorMessage);
    }
}
