package com.example.quickcash;

public class CredentialValidator {
    protected boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+.]+@[A-Za-z0-9]+.[A-Za-z][A-Za-z]+$");
    }

    protected boolean isValidPhone(String phone) {
        return phone.matches("^[0-9][0-9][0-9][ -]*[0-9][0-9][0-9][ -]*[0-9][0-9][0-9][0-9]$");
    }

    protected boolean isValidName(String name) {
        return name.matches("^[A-Za-z]+$");
    }

    protected boolean isValidPassword(String password) {
        return password.matches("^[A-Za-z0-9]+$");
    }

    protected boolean isValidRole(String role) {
        return !role.equals("select your role");
    }
}
