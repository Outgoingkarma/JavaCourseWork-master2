package com.example.coursework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Validation {
    private Validation() {
    }

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PHONE_RX =
            Pattern.compile("^\\+?[0-9 ()-]{6,}$");

    public static List<String> validateUserFields(String login,
                                                  String password,
                                                  String name,
                                                  String surname,
                                                  String email,
                                                  String phone,
                                                  String addressOrNullIfNA) {
        List<String> errors = new ArrayList<>();

        if (isBlank(login)) errors.add("Login is required.");
        if (isBlank(password)) errors.add("Password is required.");
        if (password != null && password.length() < 6) errors.add("Password must be at least 6 characters.");
        if (isBlank(name)) errors.add("Name is required.");
        if (isBlank(surname)) errors.add("Surname is required.");

        if (isBlank(email)) errors.add("Email is required.");
        else if (!EMAIL_RX.matcher(email).matches()) errors.add("Email format is invalid.");

        if (isBlank(phone)) errors.add("Phone number is required.");
        else if (!PHONE_RX.matcher(phone).matches()) errors.add("Phone number format is invalid.");

        if (addressOrNullIfNA != null && addressOrNullIfNA.isBlank()) {
            errors.add("Address is required for clients or restaurants");
        }

        return errors;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}