# PROG5121 PoE - Parts 1 and 2: Registration, Login and Sending Messages

A console-based chat application written in Java using Maven and tested
with JUnit 5. Part 1 registers and logs in a user. Part 2 (QuickChat) lets
a logged-in user send, disregard or store messages.
No GUI or JOptionPane is used, as required by the brief.

## Requirements

- JDK 17 or later
- Maven 3.8 or later (NetBeans and IntelliJ both bundle Maven)

## Running the application

From the terminal:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.mycompany.chatapp.ChatApp"
```

In NetBeans: open the project folder as a Maven project, then press F6.

In VS Code: install the Extension Pack for Java, open the folder, then
run `ChatApp.java` from the Run button above the `main` method.

## Part 2 - QuickChat

After a successful login the app shows "Welcome to QuickChat.", asks how
many messages the user wants to enter, then shows a menu:

1. Send Messages - capture a recipient and message, then choose to send,
   disregard or store it. Only the number of messages entered at the start
   can be captured.
2. Show recently sent messages - displays "Coming Soon."
3. Quit

### Methods in the Message class

| Method | Functionality |
| --- | --- |
| `boolean checkMessageID()` | Ensures the message ID is not more than ten characters. |
| `String checkRecipientCell()` | Checks the recipient number (re-uses `Login.checkCellPhoneNumber` from Part 1). |
| `String checkMessageLength()` | Ensures the message is 250 characters or less. |
| `String createMessageHash()` | Creates the hash, e.g. `00:0:HITONIGHT`. |
| `String sentMessage(int)` / `sentMessage(Scanner)` | Send, disregard or store the message. |
| `static String printMessages()` | Details of every message sent while the program runs. |
| `static int returnTotalMessages()` | Total number of messages sent. |
| `boolean storeMessage()` | Stores the message in `messages.json`. |

### Notes on the brief

* **Wording:** as in Part 1, where the brief gives two versions of a message
  the unit-test table wording is used (e.g. "Message ready to send.").
* **Recipient number:** the brief says "no more than ten characters", but
  its own test number `+27718693002` is twelve characters long. The Part 1
  rule is used instead (a `+` country code followed by up to ten digits).
  Test Data 2's number `08575975889` has no international code, so it
  correctly fails the recipient check.
* **Message number in the hash:** the first message is 0, the second is 1,
  and so on, which matches the `00:0:HITONIGHT` example.
* **JSON storage:** `storeMessage()` appends to a JSON array in
  `messages.json` (created in the folder the app is run from) and escapes
  special characters. No external library is needed.
* **Total sent:** only messages that were actually sent are counted;
  discarded and stored messages are not.

## Running the unit tests

```bash
mvn test
```

In NetBeans: right-click the project and choose Test.

## Project structure

```
pom.xml                                     Maven build file
src/main/java/com/mycompany/chatapp/Login.java     The Login class
src/main/java/com/mycompany/chatapp/ChatApp.java   Console application
src/main/java/com/mycompany/chatapp/Message.java   The Message class (Part 2)
src/test/java/com/mycompany/chatapp/LoginTest.java Unit tests (Part 1)
src/test/java/com/mycompany/chatapp/MessageTest.java Unit tests (Part 2)
.github/workflows/maven.yml                 Automated tests on every push
```

## Methods in the Login class

| Method | Functionality |
| --- | --- |
| `boolean checkUserName(String)` | Returns true if the username contains an underscore and is no more than five characters long. |
| `boolean checkPasswordComplexity(String)` | Returns true if the password is at least eight characters long and contains a capital letter, a number and a special character. |
| `boolean checkCellPhoneNumber(String)` | Returns true if the number contains an international country code followed by no more than ten digits. |
| `String registerUser(...)` | Returns the appropriate registration message. |
| `boolean loginUser(String, String)` | Returns true if the entered details match those captured at registration. |
| `String returnLoginStatus(String, String)` | Returns the message for a successful or failed login. |

## Test data used

| Test | Data |
| --- | --- |
| Username correctly formatted | `kyl_1` |
| Username incorrectly formatted | `kyle!!!!!!` |
| Password meets complexity | `Ch&&sec@ke99!` |
| Password does not meet complexity | `password` |
| Cell number correctly formatted | `+27838968976` |
| Cell number incorrectly formatted | `08966553` |

## Note on the cell phone messages

The brief gives two different versions of the cell phone messages. The
condition table uses "Cell phone number successfully added." while the
unit test table uses "Cell number successfully captured." Because the
brief states that the test data table is what will be used for marking,
the test table wording is used. Both versions appear as comments in
`Login.java` if the wording needs to be changed.

## References

Oracle. (n.d.) *Class Pattern, Java Platform SE 17 API Documentation*.
Available at: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html

JUnit. (n.d.) *JUnit 5 User Guide*. Available at:
https://junit.org/junit5/docs/current/user-guide/
