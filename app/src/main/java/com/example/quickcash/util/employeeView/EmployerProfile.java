package com.example.quickcash.util.employeeView;

public class EmployerProfile {
    private String name;
    private String email;
    private String phone;
    private String ratingValue;
    private String ratingCount;

    public EmployerProfile(String name, String email, String phone, String ratingValue, String ratingCount) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.ratingValue = ratingValue;
        this.ratingCount = ratingCount;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public String getRatingCount() {
        return ratingCount;
    }
}