package com.example.coursework.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.example.coursework.utils.Validation.validateUserFields;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationTest {
    private static final String LOGIN = "jonas";
    private static final String PASSWORD = "Abc123";
    private static final String NAME = "Jonas";
    private static final String SURNAME = "Jonaitis";
    private static final String EMAIL = "Jonas+test@example.lt";
    private static final String PHONE = "+370 (600) 12345";
    private static final String ADDRESS = "Vilniaus g. 1";

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void blankLoginIsRequired(String login) {
        assertEquals(List.of("Login is required."),
                validateUserFields(login, PASSWORD, NAME, SURNAME, EMAIL, PHONE, ADDRESS));
    }

    @ParameterizedTest
    @MethodSource("passwordCases")
    void passwordErrorsDependOnBlanknessAndLength(String password, List<String> expectedErrors) {
        assertEquals(expectedErrors,
                validateUserFields(LOGIN, password, NAME, SURNAME, EMAIL, PHONE, ADDRESS));
    }

    private static Stream<Arguments> passwordCases() {
        String required = "Password is required.";
        String tooShort = "Password must be at least 6 characters.";
        return Stream.of(
                Arguments.of(null, List.of(required)),
                Arguments.of("", List.of(required, tooShort)),
                Arguments.of("  ", List.of(required, tooShort)),
                Arguments.of("      ", List.of(required)),
                Arguments.of("abc12", List.of(tooShort)),
                Arguments.of(PASSWORD, List.of())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void blankNameIsRequired(String name) {
        assertEquals(List.of("Name is required."),
                validateUserFields(LOGIN, PASSWORD, name, SURNAME, EMAIL, PHONE, ADDRESS));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void blankSurnameIsRequired(String surname) {
        assertEquals(List.of("Surname is required."),
                validateUserFields(LOGIN, PASSWORD, NAME, surname, EMAIL, PHONE, ADDRESS));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void blankEmailProducesOnlyTheRequiredError(String email) {
        assertEquals(List.of("Email is required."),
                validateUserFields(LOGIN, PASSWORD, NAME, SURNAME, email, PHONE, ADDRESS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jonas.example.lt", "jonas@example.c"})
    void malformedEmailProducesFormatError(String email) {
        assertEquals(List.of("Email format is invalid."),
                validateUserFields(LOGIN, PASSWORD, NAME, SURNAME, email, PHONE, ADDRESS));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void blankPhoneProducesOnlyTheRequiredError(String phone) {
        assertEquals(List.of("Phone number is required."),
                validateUserFields(LOGIN, PASSWORD, NAME, SURNAME, EMAIL, phone, ADDRESS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "+370ABC123"})
    void malformedPhoneProducesFormatError(String phone) {
        assertEquals(List.of("Phone number format is invalid."),
                validateUserFields(LOGIN, PASSWORD, NAME, SURNAME, EMAIL, phone, ADDRESS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void suppliedBlankAddressIsRequired(String address) {
        assertEquals(List.of("Address is required for clients or restaurants"),
                validateUserFields(LOGIN, PASSWORD, NAME, SURNAME, EMAIL, PHONE, address));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ADDRESS)
    void validFieldsAcceptNullOrFilledAddress(String address) {
        assertEquals(List.of(),
                validateUserFields(LOGIN, PASSWORD, NAME, SURNAME, EMAIL, PHONE, address));
    }

    @Test
    void reportsAllErrorsInFieldOrder() {
        assertEquals(List.of(
                        "Login is required.",
                        "Password is required.",
                        "Password must be at least 6 characters.",
                        "Name is required.",
                        "Surname is required.",
                        "Email format is invalid.",
                        "Phone number format is invalid.",
                        "Address is required for clients or restaurants"
                ),
                validateUserFields("", "", "", "", "invalid", "invalid", ""));
    }
}
