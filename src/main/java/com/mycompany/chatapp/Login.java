package com.mycompany.chatapp;

/**
 * PROG5121 PoE - Part 1: Registration and Login feature.
 *
 * The Login class handles the registration of a new user and the
 * verification of that user's login details.
 *
 * All output messages are stored as constants so that the wording
 * matches the PoE brief exactly.
 *
 * @author Zayaan Williams ST10445293
 */
public class Login {

    // ---------------------------------------------------------------
    // Messages taken directly from the PoE brief
    // ---------------------------------------------------------------

    public static final String USERNAME_OK
            = "Username successfully captured.";

    public static final String USERNAME_FAIL
            = "Username is not correctly formatted; please ensure that your "
            + "username contains an underscore and is no more than five "
            + "characters in length.";

    public static final String PASSWORD_OK
            = "Password successfully captured.";

    public static final String PASSWORD_FAIL
            = "Password is not correctly formatted; please ensure that the "
            + "password contains at least eight characters, a capital letter, "
            + "a number, and a special character.";

    /*
     * The brief gives two versions of the cell phone messages.
     * The wording below comes from the unit test table, because the brief
     * states that the test data table is what will be used for marking.
     * The condition table version is:
     *   "Cell phone number successfully added."
     *   "Cell phone number incorrectly formatted or does not contain
     *    international code."
     */
    public static final String CELL_OK
            = "Cell number successfully captured.";

    public static final String CELL_FAIL
            = "Cell number is incorrectly formatted or does not contain an "
            + "international code; please correct the number and try again.";

    public static final String LOGIN_FAIL
            = "Username or password incorrect, please try again.";

    // ---------------------------------------------------------------
    // Instance variables
    // ---------------------------------------------------------------

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    // ---------------------------------------------------------------
    // Validation methods
    // ---------------------------------------------------------------

    /**
     * Ensures that the username contains an underscore and is no more
     * than five characters long.
     *
     * @param username the username entered by the user
     * @return true if the username is correctly formatted
     */
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Ensures that the password meets the complexity rules: at least
     * eight characters long, containing a capital letter, a number and
     * a special character.
     *
     * @param password the password entered by the user
     * @return true if the password meets the complexity requirements
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }

        boolean longEnough = password.length() >= 8;
        boolean hasCapital = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*");

        return longEnough && hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Regular expression based cell phone checker. The number must begin
     * with an international dialling code and the digits following the
     * code may be no more than ten characters long.
     *
     * Reference: the regular expression syntax used below was adapted from
     * Oracle (n.d.) Class Pattern, Java Platform SE 17 API documentation.
     * Available at:
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
     *
     * @param cellPhoneNumber the cell phone number entered by the user
     * @return true if the number is correctly formatted
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        return cellPhoneNumber.trim().matches("^\\+\\d{1,3}\\d{1,10}$");
    }

    // ---------------------------------------------------------------
    // Individual capture messages
    // ---------------------------------------------------------------

    public String usernameMessage(String username) {
        return checkUserName(username) ? USERNAME_OK : USERNAME_FAIL;
    }

    public String passwordMessage(String password) {
        return checkPasswordComplexity(password) ? PASSWORD_OK : PASSWORD_FAIL;
    }

    public String cellPhoneMessage(String cellPhoneNumber) {
        return checkCellPhoneNumber(cellPhoneNumber) ? CELL_OK : CELL_FAIL;
    }

    // ---------------------------------------------------------------
    // Registration and login
    // ---------------------------------------------------------------

    /**
     * Registers the user. The username, password and cell phone number
     * are checked in that order and the first failing check returns its
     * message. If all checks pass the details are stored and the welcome
     * message is returned.
     *
     * @return the appropriate registration message
     */
    public String registerUser(String firstName, String lastName,
            String username, String password, String cellPhoneNumber) {

        if (!checkUserName(username)) {
            return USERNAME_FAIL;
        }
        if (!checkPasswordComplexity(password)) {
            return PASSWORD_FAIL;
        }
        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return CELL_FAIL;
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;

        return "Welcome " + firstName + " ," + lastName
                + " it is great to see you.";
    }

    /**
     * Verifies that the login details entered match the details stored
     * when the user registered.
     *
     * @return true if the details match
     */
    public boolean loginUser(String username, String password) {
        if (this.username == null || this.password == null) {
            return false;
        }
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Returns the messaging for a successful or a failed login.
     *
     * @return the login status message
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }
        return LOGIN_FAIL;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
}
