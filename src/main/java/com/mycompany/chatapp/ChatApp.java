package com.mycompany.chatapp;

import java.util.Scanner;

/**
 * PROG5121 PoE - Parts 1 and 2.
 *
 * Part 1: registration and login.
 * Part 2: once logged in, the user can send messages through QuickChat.
 *
 * Console application only. No GUI or JOptionPane is used, as required
 * by the PoE brief.
 *
 * @author Zayaan Williams ST10445293
 */
public class ChatApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("--------------------------------------------");
        System.out.println("        Chat Application");
        System.out.println("--------------------------------------------");

        registerUser(scanner, login);

        boolean loggedIn = signIn(scanner, login);

        // Messages can only be sent once the user has logged in.
        if (loggedIn) {
            runQuickChat(scanner);
        }

        scanner.close();
    }

    /**
     * Prompts the user for their registration details, re-prompting on
     * each field until the entered value is valid.
     */
    private static void registerUser(Scanner scanner, Login login) {

        System.out.println();
        System.out.println("=== REGISTRATION ===");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        String username;
        do {
            System.out.print("Enter a username: ");
            username = scanner.nextLine();
            System.out.println(login.usernameMessage(username));
        } while (!login.checkUserName(username));

        String password;
        do {
            System.out.print("Enter a password: ");
            password = scanner.nextLine();
            System.out.println(login.passwordMessage(password));
        } while (!login.checkPasswordComplexity(password));

        String cellPhoneNumber;
        do {
            System.out.print("Enter your cell phone number "
                    + "(for example +27838968976): ");
            cellPhoneNumber = scanner.nextLine();
            System.out.println(login.cellPhoneMessage(cellPhoneNumber));
        } while (!login.checkCellPhoneNumber(cellPhoneNumber));

        System.out.println(login.registerUser(firstName, lastName,
                username, password, cellPhoneNumber));
    }

    /**
     * Allows the user three attempts to log in.
     *
     * @return true if the user logged in successfully
     */
    private static boolean signIn(Scanner scanner, Login login) {

        System.out.println();
        System.out.println("=== LOGIN ===");

        int attempts = 0;

        while (attempts < 3) {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.println(login.returnLoginStatus(username, password));

            if (login.loginUser(username, password)) {
                return true;
            }
            attempts++;
        }

        System.out.println("Too many failed attempts. "
                + "Closing the application.");
        return false;
    }

    // ---------------------------------------------------------------
    // Part 2: QuickChat
    // ---------------------------------------------------------------

    /**
     * Welcomes the user, asks how many messages they want to enter, then
     * shows the menu until the user chooses to quit.
     */
    private static void runQuickChat(Scanner scanner) {

        System.out.println();
        System.out.println("Welcome to QuickChat.");

        int maxMessages = readInt(scanner,
                "How many messages do you wish to enter? ", 1,
                Integer.MAX_VALUE);

        int entered = 0;
        boolean totalShown = false;
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== MENU ===");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");

            int choice = readInt(scanner, "Choose an option: ", 1, 3);

            switch (choice) {
                case 1:
                    if (entered >= maxMessages) {
                        System.out.println("You have already entered all "
                                + maxMessages + " message(s).");
                    } else {
                        sendOneMessage(scanner, entered);
                        entered++;

                        if (entered == maxMessages) {
                            showTotal();
                            totalShown = true;
                        }
                    }
                    break;

                case 2:
                    System.out.println("Coming Soon.");
                    break;

                default:
                    if (!totalShown) {
                        showTotal();
                    }
                    System.out.println("Goodbye.");
                    running = false;
            }
        }
    }

    /**
     * Captures one message (recipient, then text), then lets the user
     * send, disregard or store it.
     *
     * @param messageNumber the number of this message (the first is 0)
     */
    private static void sendOneMessage(Scanner scanner, int messageNumber) {

        Message message = new Message(messageNumber);

        System.out.println();
        System.out.println(message.messageIDStatus());

        do {
            System.out.print("Enter the recipient's cell phone number "
                    + "(for example +27718693002): ");
            message.setRecipient(scanner.nextLine());
            System.out.println(message.checkRecipientCell());
        } while (!message.isRecipientValid());

        do {
            System.out.print("Enter your message (max "
                    + Message.MAX_MESSAGE_LENGTH + " characters): ");
            message.setMessageText(scanner.nextLine());

            if (message.getMessageText().trim().isEmpty()) {
                System.out.println("The message cannot be empty.");
                continue;
            }
            System.out.println(message.checkMessageLength());
        } while (message.getMessageText().trim().isEmpty()
                || !message.isMessageLengthValid());

        System.out.println();
        String result = message.sentMessage(scanner);
        System.out.println(result);

        // The full details are displayed once the message has been sent.
        if (result.equals(Message.MESSAGE_SENT)) {
            System.out.println();
            System.out.println("--- Message details ---");
            System.out.println(message.getDetails());
        }
    }

    private static void showTotal() {
        System.out.println();
        System.out.println("Total messages sent: "
                + Message.returnTotalMessages());
    }

    /**
     * Keeps asking until the user enters a whole number in the range.
     */
    private static int readInt(Scanner scanner, String prompt, int min,
            int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message below
            }
            System.out.println(max == Integer.MAX_VALUE
                    ? "Please enter a whole number of " + min + " or more."
                    : "Please enter a number from " + min + " to " + max
                    + ".");
        }
    }
}
