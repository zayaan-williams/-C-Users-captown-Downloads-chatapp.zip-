package com.mycompany.chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Login class.
 *
 * The test data used below is taken directly from the tables supplied
 * in the PoE brief.
 *
 * @author Zayaan Williams ST10445293
 */
public class LoginTest {

    private Login login;

    // Test data from the brief
    private static final String VALID_USERNAME = "kyl_1";
    private static final String INVALID_USERNAME = "kyle!!!!!!";
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String INVALID_PASSWORD = "password";
    private static final String VALID_CELL = "+27838968976";
    private static final String INVALID_CELL = "08966553";

    private static final String FIRST_NAME = "Kyle";
    private static final String LAST_NAME = "Smith";

    @BeforeEach
    public void setUp() {
        login = new Login();
    }

    /**
     * Helper that registers a valid user so that login tests have
     * stored details to compare against.
     */
    private void registerValidUser() {
        login.registerUser(FIRST_NAME, LAST_NAME, VALID_USERNAME,
                VALID_PASSWORD, VALID_CELL);
    }

    // ---------------------------------------------------------------
    // Test: assertEquals
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Username is correctly formatted")
    public void testUsernameCorrectlyFormatted() {
        String result = login.registerUser(FIRST_NAME, LAST_NAME,
                VALID_USERNAME, VALID_PASSWORD, VALID_CELL);

        assertEquals("Welcome Kyle ,Smith it is great to see you.", result);
    }

    @Test
    @DisplayName("Username incorrectly formatted")
    public void testUsernameIncorrectlyFormatted() {
        String result = login.registerUser(FIRST_NAME, LAST_NAME,
                INVALID_USERNAME, VALID_PASSWORD, VALID_CELL);

        assertEquals(Login.USERNAME_FAIL, result);
    }

    @Test
    @DisplayName("Password meets the complexity requirements")
    public void testPasswordMeetsComplexity() {
        assertEquals(Login.PASSWORD_OK, login.passwordMessage(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Password does not meet the complexity requirements")
    public void testPasswordDoesNotMeetComplexity() {
        assertEquals(Login.PASSWORD_FAIL,
                login.passwordMessage(INVALID_PASSWORD));
    }

    @Test
    @DisplayName("Cell phone number is correctly formatted")
    public void testCellPhoneCorrectlyFormatted() {
        assertEquals(Login.CELL_OK, login.cellPhoneMessage(VALID_CELL));
    }

    @Test
    @DisplayName("Cell phone number is incorrectly formatted")
    public void testCellPhoneIncorrectlyFormatted() {
        assertEquals(Login.CELL_FAIL, login.cellPhoneMessage(INVALID_CELL));
    }

    // ---------------------------------------------------------------
    // Test: assertTrue / assertFalse
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Login successful returns true")
    public void testLoginSuccessful() {
        registerValidUser();
        assertTrue(login.loginUser(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    @DisplayName("Login failed returns false")
    public void testLoginFailed() {
        registerValidUser();
        assertFalse(login.loginUser(VALID_USERNAME, "wrongPassword"));
    }

    @Test
    @DisplayName("Username correctly formatted returns true")
    public void testCheckUserNameTrue() {
        assertTrue(login.checkUserName(VALID_USERNAME));
    }

    @Test
    @DisplayName("Username incorrectly formatted returns false")
    public void testCheckUserNameFalse() {
        assertFalse(login.checkUserName(INVALID_USERNAME));
    }

    @Test
    @DisplayName("Password meets complexity requirements returns true")
    public void testCheckPasswordComplexityTrue() {
        assertTrue(login.checkPasswordComplexity(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Password does not meet complexity requirements returns false")
    public void testCheckPasswordComplexityFalse() {
        assertFalse(login.checkPasswordComplexity(INVALID_PASSWORD));
    }

    @Test
    @DisplayName("Cell phone number correctly formatted returns true")
    public void testCheckCellPhoneNumberTrue() {
        assertTrue(login.checkCellPhoneNumber(VALID_CELL));
    }

    @Test
    @DisplayName("Cell phone number incorrectly formatted returns false")
    public void testCheckCellPhoneNumberFalse() {
        assertFalse(login.checkCellPhoneNumber(INVALID_CELL));
    }

    // ---------------------------------------------------------------
    // Login status messaging
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Successful login returns the welcome message")
    public void testReturnLoginStatusSuccess() {
        registerValidUser();

        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    @DisplayName("Failed login returns the error message")
    public void testReturnLoginStatusFailure() {
        registerValidUser();

        assertEquals(Login.LOGIN_FAIL,
                login.returnLoginStatus(VALID_USERNAME, "nope"));
    }
}
