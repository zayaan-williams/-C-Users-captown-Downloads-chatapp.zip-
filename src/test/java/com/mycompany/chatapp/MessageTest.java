package com.mycompany.chatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Message class (PoE Part 2).
 *
 * The test data used below is taken directly from the tables supplied in
 * the PoE brief.
 *
 * @author Zayaan Williams ST10445293
 */
public class MessageTest {

    // Test Data for Message 1
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    // Test Data for Message 2
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        Message.clearSentMessages();
        Message.setStorageFile(tempDir.resolve("messages.json"));
    }

    private Message message1() {
        return new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
    }

    private Message message2() {
        return new Message("0498765432", 1, RECIPIENT_2, TEXT_2);
    }

    // ---------------------------------------------------------------
    // Test: message length (assertEquals)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Message of less than 250 characters - success")
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", message1().checkMessageLength());
    }

    @Test
    @DisplayName("Message of exactly 250 characters - success")
    public void testMessageLengthBoundary() {
        Message m = new Message(0);
        m.setMessageText("a".repeat(250));
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    @DisplayName("Message of more than 250 characters - failure")
    public void testMessageLengthFailure() {
        Message m = new Message(0);
        m.setMessageText("a".repeat(255));
        assertEquals("Message exceeds 250 characters by 5; "
                + "please reduce the size.", m.checkMessageLength());
        assertFalse(m.isMessageLengthValid());
    }

    // ---------------------------------------------------------------
    // Test: recipient number (re-uses the Part 1 check)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Recipient number correctly formatted - success")
    public void testRecipientSuccess() {
        assertEquals("Cell phone number successfully captured.",
                message1().checkRecipientCell());
        assertTrue(message1().isRecipientValid());
    }

    @Test
    @DisplayName("Recipient number incorrectly formatted - failure")
    public void testRecipientFailure() {
        Message m = new Message(0);
        m.setRecipient("08966553");

        assertEquals("Cell phone number is incorrectly formatted or does "
                + "not contain an international code. Please correct the "
                + "number and try again.", m.checkRecipientCell());
        assertFalse(m.isRecipientValid());
    }

    @Test
    @DisplayName("Recipient number without an international code - failure")
    public void testRecipientTestData2HasNoInternationalCode() {
        assertEquals(Message.RECIPIENT_FAIL, message2().checkRecipientCell());
    }

    // ---------------------------------------------------------------
    // Test: message hash
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Message hash is correct for Test Data 1")
    public void testMessageHashTestData1() {
        assertEquals("00:0:HITONIGHT", message1().createMessageHash());
    }

    @Test
    @DisplayName("Message hash is correct for the remaining messages (loop)")
    public void testMessageHashLoop() {
        Message[] messages = {
            message2(),
            new Message("0098765432", 2, RECIPIENT_1, "Hi thanks"),
            new Message("7512345678", 3, RECIPIENT_1, "Okay"),
            new Message("1234567890", 4, RECIPIENT_1, "  Hello   there, friend!  ")
        };
        String[] expected = {
            "04:1:HIPAYMENT",
            "00:2:HITHANKS",
            "75:3:OKAYOKAY",
            "12:4:HELLOFRIEND"
        };

        for (int i = 0; i < messages.length; i++) {
            assertEquals(expected[i], messages[i].createMessageHash(),
                    "Hash mismatch for message " + i);
        }
    }

    // ---------------------------------------------------------------
    // Test: message ID
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Message ID is created")
    public void testMessageIDCreated() {
        Message m = new Message(0);

        assertTrue(m.checkMessageID());
        assertEquals("Message ID generated: " + m.getMessageID(),
                m.messageIDStatus());
    }

    @Test
    @DisplayName("Auto generated message IDs are always ten digits")
    public void testMessageIDIsTenDigits() {
        for (int i = 0; i < 200; i++) {
            assertTrue(new Message(i).getMessageID().matches("\\d{10}"));
        }
    }

    @Test
    @DisplayName("Message ID longer than ten characters is rejected")
    public void testMessageIDTooLong() {
        Message m = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
        assertFalse(m.checkMessageID());
    }

    // ---------------------------------------------------------------
    // Test: MessageSent (send / disregard / store)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("User selected 'Send Message'")
    public void testSendMessage() {
        assertEquals("Message successfully sent.", message1().sentMessage(1));
    }

    @Test
    @DisplayName("User selected 'Disregard Message'")
    public void testDisregardMessage() {
        assertEquals("Press 0 to delete the message.", message2().sentMessage(2));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("User selected 'Store Message'")
    public void testStoreMessage() throws IOException {
        assertEquals("Message successfully stored.", message1().sentMessage(3));
        assertEquals(0, Message.returnTotalMessages());

        String json = Files.readString(Message.getStorageFile());
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
    }

    @Test
    @DisplayName("An invalid choice is rejected")
    public void testInvalidChoice() {
        assertEquals(Message.INVALID_CHOICE, message1().sentMessage(7));
    }

    // ---------------------------------------------------------------
    // Test: total number of messages sent
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Total messages sent for the PoE test data (1 sent, 1 discarded)")
    public void testReturnTotalMessages() {
        message1().sentMessage(1);
        message2().sentMessage(2);

        assertEquals(1, Message.returnTotalMessages());
    }

    // ---------------------------------------------------------------
    // Test: printing sent messages
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Details are shown in the order ID, hash, recipient, message")
    public void testPrintMessagesOrder() {
        message1().sentMessage(1);

        String output = Message.printMessages();
        int id = output.indexOf("Message ID: 0012345678");
        int hash = output.indexOf("Message Hash: 00:0:HITONIGHT");
        int recipient = output.indexOf("Recipient: " + RECIPIENT_1);
        int text = output.indexOf("Message: " + TEXT_1);

        assertTrue(id >= 0 && hash > id && recipient > hash && text > recipient);
    }

    @Test
    @DisplayName("Nothing is printed when no messages were sent")
    public void testPrintMessagesEmpty() {
        assertEquals("", Message.printMessages());
    }

    // ---------------------------------------------------------------
    // Test: JSON storage
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Stored messages are added to one valid JSON array")
    public void testStoreTwoMessages() throws IOException {
        message1().storeMessage();
        message2().storeMessage();

        String json = Files.readString(Message.getStorageFile()).trim();

        assertTrue(json.startsWith("[") && json.endsWith("]"));
        assertEquals(2, json.split("\"messageID\"", -1).length - 1);
        assertTrue(json.contains("},"));
    }

    @Test
    @DisplayName("Special characters are escaped in the JSON file")
    public void testJsonEscaping() throws IOException {
        Message m = new Message("1111111111", 0, RECIPIENT_1,
                "He said \"hi\" \\ bye");
        m.storeMessage();

        String json = new String(Files.readAllBytes(Message.getStorageFile()),
                StandardCharsets.UTF_8);
        assertTrue(json.contains("He said \\\"hi\\\" \\\\ bye"));
    }
}
