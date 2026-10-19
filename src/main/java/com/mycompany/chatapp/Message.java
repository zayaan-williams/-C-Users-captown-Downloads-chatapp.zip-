package com.mycompany.chatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * PROG5121 PoE - Part 2: Sending Messages.
 *
 * A Message holds the details of one message (ID, number, recipient and
 * text) and knows how to validate itself, create its hash, and be sent,
 * disregarded or stored.
 *
 * The list of messages that have been sent while the program is running
 * is shared by every Message, so printMessages() and returnTotalMessages()
 * are static. clearSentMessages() resets that list (used by the tests).
 *
 * All output messages are stored as constants so that the wording matches
 * the PoE brief exactly.
 *
 * @author Zayaan Williams ST10445293
 */
public class Message {

    // ---------------------------------------------------------------
    // Messages taken directly from the PoE brief
    // ---------------------------------------------------------------

    public static final int MAX_MESSAGE_LENGTH = 250;

    public static final String MESSAGE_READY = "Message ready to send.";

    public static final String RECIPIENT_OK
            = "Cell phone number successfully captured.";

    public static final String RECIPIENT_FAIL
            = "Cell phone number is incorrectly formatted or does not "
            + "contain an international code. Please correct the number "
            + "and try again.";

    public static final String MESSAGE_SENT = "Message successfully sent.";

    public static final String MESSAGE_DISREGARD
            = "Press 0 to delete the message.";

    public static final String MESSAGE_DELETED = "Message deleted.";

    public static final String MESSAGE_STORED = "Message successfully stored.";

    public static final String MESSAGE_STORE_FAILED
            = "Message could not be stored.";

    public static final String INVALID_CHOICE
            = "Invalid option. Please choose 1, 2 or 3.";

    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    // ---------------------------------------------------------------
    // Shared state (messages sent while the program is running)
    // ---------------------------------------------------------------

    private static final List<Message> SENT_MESSAGES = new ArrayList<>();

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Re-uses the Part 1 cell phone check, as suggested by the brief. */
    private static final Login CELL_VALIDATOR = new Login();

    private static Path storageFile = Paths.get("messages.json");

    // ---------------------------------------------------------------
    // Instance variables
    // ---------------------------------------------------------------

    private final String messageID;
    private final int messageNumber;
    private String recipient;
    private String messageText;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /**
     * Creates a message with a randomly generated ten-digit message ID.
     *
     * @param messageNumber the number of this message (the first is 0)
     */
    public Message(int messageNumber) {
        this(generateMessageID(), messageNumber, "", "");
    }

    /**
     * Creates a message with a known ID. Mainly used by the unit tests so
     * that the hash can be checked against fixed data.
     */
    public Message(String messageID, int messageNumber, String recipient,
            String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    /**
     * Generates a random ten-digit ID. Each digit is chosen separately so
     * the ID can start with zeros (for example 0012345678).
     */
    private static String generateMessageID() {
        StringBuilder id = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    // ---------------------------------------------------------------
    // Validation methods
    // ---------------------------------------------------------------

    /**
     * Ensures that the message ID is not more than ten characters.
     *
     * @return true if the ID is valid
     */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty()
                && messageID.length() <= 10;
    }

    /**
     * Returns the message shown once an ID has been created.
     */
    public String messageIDStatus() {
        if (checkMessageID()) {
            return "Message ID generated: " + messageID;
        }
        return "Message ID is invalid; it may not be more than ten "
                + "characters.";
    }

    /**
     * Ensures that the recipient cell number is correctly formatted and
     * starts with an international code (re-uses the Part 1 check).
     *
     * @return the success or failure message
     */
    public String checkRecipientCell() {
        return isRecipientValid() ? RECIPIENT_OK : RECIPIENT_FAIL;
    }

    public boolean isRecipientValid() {
        return CELL_VALIDATOR.checkCellPhoneNumber(recipient);
    }

    /**
     * Ensures that the message is not more than 250 characters.
     *
     * @return the success message, or the failure message stating by how
     * many characters the message is too long
     */
    public String checkMessageLength() {
        if (isMessageLengthValid()) {
            return MESSAGE_READY;
        }
        int excess = messageText.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds " + MAX_MESSAGE_LENGTH + " characters by "
                + excess + "; please reduce the size.";
    }

    public boolean isMessageLengthValid() {
        return messageText != null
                && messageText.length() <= MAX_MESSAGE_LENGTH;
    }

    // ---------------------------------------------------------------
    // Message hash
    // ---------------------------------------------------------------

    /**
     * Creates the message hash: the first two digits of the message ID, a
     * colon, the message number, a colon, then the first and last words of
     * the message, all in capitals. For example 00:0:HITONIGHT.
     *
     * @return the message hash
     */
    public String createMessageHash() {
        String firstTwo = messageID.length() >= 2
                ? messageID.substring(0, 2) : messageID;

        String[] words = messageText == null
                ? new String[0] : messageText.trim().split("\\s+");

        String first = "";
        String last = "";
        if (words.length > 0 && !words[0].isEmpty()) {
            first = stripPunctuation(words[0]);
            last = stripPunctuation(words[words.length - 1]);
        }

        return (firstTwo + ":" + messageNumber + ":" + first + last)
                .toUpperCase();
    }

    /** Removes punctuation from the start and end of a word. */
    private static String stripPunctuation(String word) {
        return word.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");
    }

    // ---------------------------------------------------------------
    // Sending, disregarding and storing
    // ---------------------------------------------------------------

    /**
     * Applies the user's choice to this message.
     *
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the matching message from the brief
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case SEND:
                SENT_MESSAGES.add(this);
                return MESSAGE_SENT;
            case DISREGARD:
                return MESSAGE_DISREGARD;
            case STORE:
                return storeMessage() ? MESSAGE_STORED : MESSAGE_STORE_FAILED;
            default:
                return INVALID_CHOICE;
        }
    }

    /**
     * Lets the user choose whether to send, disregard or store the
     * message, then carries out that choice.
     *
     * @return the final status message
     */
    public String sentMessage(Scanner scanner) {
        int choice = 0;

        while (choice < SEND || choice > STORE) {
            System.out.println("What would you like to do with this message?");
            System.out.println("  1) Send Message");
            System.out.println("  2) Disregard Message");
            System.out.println("  3) Store Message to send later");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                choice = 0;
            }
            if (choice < SEND || choice > STORE) {
                System.out.println(INVALID_CHOICE);
            }
        }

        String result = sentMessage(choice);

        if (choice == DISREGARD) {
            System.out.println(result);
            String input;
            do {
                input = scanner.nextLine().trim();
                if (!input.equals("0")) {
                    System.out.println(MESSAGE_DISREGARD);
                }
            } while (!input.equals("0"));
            return MESSAGE_DELETED;
        }

        return result;
    }

    // ---------------------------------------------------------------
    // Reporting on sent messages
    // ---------------------------------------------------------------

    /**
     * Returns the full details of this message in the order required by
     * the brief: Message ID, Message Hash, Recipient, Message.
     */
    public String getDetails() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + createMessageHash()
                + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText;
    }

    /**
     * Returns the details of every message sent while the program has been
     * running.
     */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : SENT_MESSAGES) {
            if (sb.length() > 0) {
                sb.append(System.lineSeparator())
                        .append(System.lineSeparator());
            }
            sb.append(m.getDetails());
        }
        return sb.toString();
    }

    /**
     * Returns the total number of messages sent.
     */
    public static int returnTotalMessages() {
        return SENT_MESSAGES.size();
    }

    /**
     * Empties the list of sent messages.
     */
    public static void clearSentMessages() {
        SENT_MESSAGES.clear();
    }

    // ---------------------------------------------------------------
    // JSON storage
    // ---------------------------------------------------------------

    /**
     * Changes the file used by storeMessage(). The default is
     * messages.json in the folder the program is run from.
     */
    public static void setStorageFile(Path file) {
        storageFile = file;
    }

    public static Path getStorageFile() {
        return storageFile;
    }

    /**
     * Stores this message in a JSON file so it can be sent later. The file
     * holds a JSON array, and each stored message is added to the end of
     * it, so messages stored in earlier runs are kept.
     *
     * @return true if the message was written successfully
     */
    public boolean storeMessage() {
        try {
            String existing = "";
            if (Files.exists(storageFile)) {
                existing = new String(Files.readAllBytes(storageFile),
                        StandardCharsets.UTF_8).trim();
            }

            String entry = toJson();
            String output;

            if (existing.startsWith("[") && existing.endsWith("]")) {
                String body = existing.substring(1, existing.length() - 1)
                        .trim();
                output = body.isEmpty()
                        ? "[" + System.lineSeparator() + entry
                        + System.lineSeparator() + "]"
                        : "[" + System.lineSeparator() + body + ","
                        + System.lineSeparator() + entry
                        + System.lineSeparator() + "]";
            } else {
                output = "[" + System.lineSeparator() + entry
                        + System.lineSeparator() + "]";
            }

            Files.write(storageFile, output.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Builds the JSON object for this message by hand (no library needed).
     */
    String toJson() {
        String nl = System.lineSeparator();
        return "  {" + nl
                + "    \"messageID\": \"" + escapeJson(messageID) + "\"," + nl
                + "    \"messageNumber\": " + messageNumber + "," + nl
                + "    \"messageHash\": \"" + escapeJson(createMessageHash())
                + "\"," + nl
                + "    \"recipient\": \"" + escapeJson(recipient) + "\"," + nl
                + "    \"message\": \"" + escapeJson(messageText) + "\"" + nl
                + "  }";
    }

    /**
     * Escapes the characters that are not allowed inside a JSON string.
     */
    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // Getters and setters
    // ---------------------------------------------------------------

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getMessageHash() {
        return createMessageHash();
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /** Returns an unmodifiable view of the sent messages. */
    public static List<Message> getSentMessages() {
        return Collections.unmodifiableList(SENT_MESSAGES);
    }
}
