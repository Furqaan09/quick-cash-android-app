package com.example.quickcash.util.employeeView;

public class PreferredEmployer {
    private String name;
    private String email;
    private String phone;
    private String employerUID;

    // constructor with all fields.
    public PreferredEmployer(String name, String email, String phone, String employerUID) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.employerUID = employerUID;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmployerUID() {
        return employerUID;
    }
}
