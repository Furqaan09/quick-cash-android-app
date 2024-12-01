package com.example.quickcash.util.employerView;

public class HiredEmployee {
    private String jobTitleAndId;
    private String jobCompany;
    private String salary;
    private String startDate;
    private String employeeEmail;
    private String employeeName;
    private String jobStatus;
    private String ratingStatus;

    private String applicationID;

    // constructor with all fields.
    public HiredEmployee(String jobTitleAndId, String jobCompany, String startDate, String salary,
                         String employeeName, String employeeEmail, String jobStatus,
                         String ratingStatus, String applicationID) {
        this.jobTitleAndId = jobTitleAndId;
        this.jobCompany = jobCompany;
        this.salary = salary;
        this.startDate = startDate;
        this.employeeEmail = employeeEmail;
        this.employeeName = employeeName;
        this.jobStatus = jobStatus;
        this.ratingStatus = ratingStatus;
        this.applicationID = applicationID;
    }

    // getters
    public String getJobTitleAndId() {
        return jobTitleAndId;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public String getJobCompany() {
        return jobCompany;
    }

    public String getSalary() {
        return salary;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public String getRatingStatus() {
        return ratingStatus;
    }

}
