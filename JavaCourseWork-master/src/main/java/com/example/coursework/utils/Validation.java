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

        validateRequired(login, "Login is required.", errors);
        validatePassword(password, errors);
        validateRequired(name, "Name is required.", errors);
        validateRequired(surname, "Surname is required.", errors);
        validateEmail(email, errors);
        validatePhone(phone, errors);
        validateAddress(addressOrNullIfNA, errors);

        return errors;
    }

    private static void validateRequired(String value, String message, List<String> errors) {
        if (isBlank(value)) {
            errors.add(message);
        }
    }

    private static void validatePassword(String password, List<String> errors) {
        validateRequired(password, "Password is required.", errors);
        if (password != null && password.length() < 6) {
            errors.add("Password must be at least 6 characters.");
        }
    }

    private static void validateEmail(String email, List<String> errors) {
        if (isBlank(email)) {
            errors.add("Email is required.");
        } else if (!EMAIL_RX.matcher(email).matches()) {
            errors.add("Email format is invalid.");
        }
    }

    private static void validatePhone(String phone, List<String> errors) {
        if (isBlank(phone)) {
            errors.add("Phone number is required.");
        } else if (!PHONE_RX.matcher(phone).matches()) {
            errors.add("Phone number format is invalid.");
        }
    }

    private static void validateAddress(String addressOrNullIfNA, List<String> errors) {
        if (addressOrNullIfNA != null && addressOrNullIfNA.isBlank()) {
            errors.add("Address is required for clients or restaurants");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
