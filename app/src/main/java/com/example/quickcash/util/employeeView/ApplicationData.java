package com.example.quickcash.util.employeeView;

import java.io.Serializable;

/**
 * Represents an application
 */
public class ApplicationData implements Serializable {
    private String jobIdAndTitle;
    private String companyName;
    private String jobLocation;
    private String applicationDate;
    private String status;
    private String employerEmail;
    private String applicationId;
    private String employerRatingStatus;


    public ApplicationData(String jobIdAndTitle, String companyName, String jobLocation,
                           String applicationDate, String status, String employerEmail,
                           String applicationId, String employerRatingStatus) {
        this.jobIdAndTitle = jobIdAndTitle;
        this.companyName = companyName;
        this.jobLocation = jobLocation;
        this.applicationDate = applicationDate;
        this.status = status;
        this.employerEmail = employerEmail;
        this.applicationId = applicationId;
        this.employerRatingStatus = employerRatingStatus;
    }

    public String getJobIdAndTitle() {
        return jobIdAndTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getEmployerRatingStatus() {
        return employerRatingStatus;
    }
}
