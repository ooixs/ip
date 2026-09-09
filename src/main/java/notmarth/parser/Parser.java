package notmarth.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import notmarth.command.AddCommand;
import notmarth.command.AddContactCommand;
import notmarth.command.Command;
import notmarth.command.DeleteCommand;
import notmarth.command.DeleteContactCommand;
import notmarth.command.ExitCommand;
import notmarth.command.FindCommand;
import notmarth.command.FindContactCommand;
import notmarth.command.ListCommand;
import notmarth.command.ListContactsCommand;
import notmarth.command.MarkCommand;
import notmarth.command.OnCommand;
import notmarth.command.SommieCommand;
import notmarth.command.UnmarkCommand;
import notmarth.exception.NotMarthException;
import notmarth.model.Contact;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.ToDo;

/**
 * Interprets user commands and converts their arguments into typed values.
 */
public final class Parser {
    private static final String COMMANDS_HINT =
            "todo, deadline, event, list, find, on, mark, unmark, or delete";
    private static final String DATE_TIME_FORMAT_HINT =
            " Try yyyy-mm-dd or dd/MM/yyyy HHmm, for example: 2019-10-15 or 02/12/2019 1800";

    /**
     * Converts a complete user command into an executable command object.
     *
     * @param fullCommand the trimmed command entered by the user.
     * @return the command represented by the input.
     * @throws NotMarthException if the input is empty, unknown, or malformed.
     */
    public Command parse(String fullCommand) throws NotMarthException {
        validateCommandText(fullCommand);
        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        }
        if (fullCommand.equals("sommie")) {
            return new SommieCommand();
        }
        if (fullCommand.equals("list")) {
            return new ListCommand();
        }
        if (fullCommand.equals("listcontacts")) {
            return new ListContactsCommand();
        }
        if (isCommand(fullCommand, "findcontact")) {
            return new FindContactCommand(parseContactKeyword(fullCommand));
        }
        if (isCommand(fullCommand, "deletecontact")) {
            return new DeleteContactCommand(parseTaskNumber(fullCommand, "deletecontact"));
        }
        if (isCommand(fullCommand, "contact")) {
            return new AddContactCommand(createContact(fullCommand));
        }
        if (isCommand(fullCommand, "find")) {
            return new FindCommand(parseFindKeyword(fullCommand));
        }
        if (isCommand(fullCommand, "on")) {
            return new OnCommand(parseOnDate(fullCommand));
        }
        if (isCommand(fullCommand, "mark")) {
            return new MarkCommand(parseTaskNumber(fullCommand, "mark"));
        }
        if (isCommand(fullCommand, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(fullCommand, "unmark"));
        }
        if (isCommand(fullCommand, "delete")) {
            return new DeleteCommand(parseTaskNumber(fullCommand, "delete"));
        }
        if (isTaskCommand(fullCommand)) {
            return new AddCommand(createTask(fullCommand));
        }
        if (fullCommand.isEmpty()) {
            throw new NotMarthException(
                    "Please enter a command. Try " + COMMANDS_HINT + ".");
        }
        throw new NotMarthException(
                "I don't recognize that command. Try " + COMMANDS_HINT + ".");
    }

    private void validateCommandText(String command) throws NotMarthException {
        if (command == null || command.isBlank()) {
            throw new NotMarthException(
                    "Please enter a command. Try " + COMMANDS_HINT + ".");
        }
        if (!command.equals(command.trim()) || command.matches(".*\\s{2,}.*")
                || command.chars().anyMatch(Character::isISOControl)) {
            throw new NotMarthException(
                    "Use one space between command parts, without leading or trailing spaces.");
        }
    }

    /**
     * Checks whether a command is exactly a keyword or starts with that keyword
     * followed by at least one space.
     *
     * @param command the complete command entered by the user.
     * @param keyword the command keyword to look for.
     * @return whether the command uses the keyword at its beginning.
     */
    private boolean isCommand(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Checks whether the command is one of the supported task-creation commands.
     *
     * @param command the complete command entered by the user.
     * @return whether the command starts with a supported task keyword.
     */
    private boolean isTaskCommand(String command) {
        return isCommand(command, "todo")
                || isCommand(command, "deadline")
                || isCommand(command, "event");
    }

    private Contact createContact(String command) throws NotMarthException {
        String details = command.substring("contact".length()).trim();
        int phoneMarker = findSingleMarker(details, "/phone");
        int addressMarker = findSingleMarker(details, "/address");
        if (phoneMarker <= 0 || addressMarker <= phoneMarker) {
            throw invalidContactMessage();
        }
        String name = details.substring(0, phoneMarker).trim();
        String phone = details.substring(phoneMarker + "/phone".length(), addressMarker).trim();
        String address = details.substring(addressMarker + "/address".length()).trim();
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            throw invalidContactMessage();
        }
        if (!phone.matches("\\+?[0-9][0-9 ()-]*")) {
            throw new NotMarthException("A phone number may contain only digits, spaces, +, -, and parentheses.");
        }
        return new Contact(name, phone, address);
    }

    private int findSingleMarker(String details, String marker) throws NotMarthException {
        int firstIndex = details.indexOf(marker);
        int lastIndex = details.lastIndexOf(marker);
        if (firstIndex < 0 || firstIndex != lastIndex
                || (firstIndex > 0 && details.charAt(firstIndex - 1) != ' ')
                || (firstIndex + marker.length() < details.length()
                && details.charAt(firstIndex + marker.length()) != ' ')) {
            return -1;
        }
        return firstIndex;
    }

    private NotMarthException invalidContactMessage() {
        return new NotMarthException(
                "A contact needs a name, phone number, and address. Try: contact <name> /phone <number> "
                        + "/address <address>");
    }

    private String parseContactKeyword(String command) throws NotMarthException {
        String keyword = command.substring("findcontact".length()).trim();
        if (keyword.isEmpty()) {
            throw new NotMarthException("Findcontact needs a name or keyword. Try: findcontact <keyword>");
        }
        return keyword;
    }

    /**
     * Creates a task from a user command. Deadline values are parsed into
     * typed {@code java.time} values before the task is created.
     *
     * @param command the command entered by the user.
     * @return the parsed task.
     * @throws NotMarthException if the command is missing or has invalid information.
     */
    private Task createTask(String command) throws NotMarthException {
        if (isCommand(command, "todo")) {
            return createTodo(command);
        }

        if (isCommand(command, "deadline")) {
            return createDeadline(command);
        }

        if (isCommand(command, "event")) {
            return createEvent(command);
        }

        throw new NotMarthException("I don't recognize that task type.");
    }

    private Task createTodo(String command) throws NotMarthException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NotMarthException("A todo needs a description. Try: todo <description>");
        }
        return new ToDo(description);
    }

    private Task createDeadline(String command) throws NotMarthException {
        String details = command.substring("deadline".length()).trim();
        int byMarker = findSingleMarker(details, "/by");
        if (byMarker <= 0) {
            throw new NotMarthException(
                    "A deadline needs a description and a due time. Try: deadline <description> "
                            + "/by <date or time>");
        }

        String description = details.substring(0, byMarker).trim();
        String by = details.substring(byMarker + "/by".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new NotMarthException(
                    "A deadline needs a description and a due time. Try: deadline <description> "
                            + "/by <date or time>");
        }

        try {
            return new Deadline(description, by);
        } catch (DateTimeParseException exception) {
            throw new NotMarthException(
                    "That deadline date or time is not valid." + DATE_TIME_FORMAT_HINT,
                    exception);
        }
    }

    private Task createEvent(String command) throws NotMarthException {
        String details = command.substring("event".length()).trim();
        int fromMarker = findSingleMarker(details, "/from");
        int toMarker = findSingleMarker(details, "/to");
        if (fromMarker <= 0 || toMarker <= fromMarker) {
            throw invalidEventMessage();
        }

        String description = details.substring(0, fromMarker).trim();
        String from = details.substring(fromMarker + "/from".length(), toMarker).trim();
        String to = details.substring(toMarker + "/to".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw invalidEventMessage();
        }

        try {
            return new Event(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new NotMarthException(
                    "That event date or time is not valid." + DATE_TIME_FORMAT_HINT,
                    exception);
        } catch (IllegalArgumentException exception) {
            throw new NotMarthException(
                    "An event must end after it starts. Check the /from and /to values.",
                    exception);
        }
    }

    private NotMarthException invalidEventMessage() {
        return new NotMarthException(
                "An event needs a description, start time, and end time. Try: event <description> "
                        + "/from <start> /to <end>");
    }

    /**
     * Parses the date from an {@code on <date>} command.
     *
     * @param command the command containing the requested date.
     * @return the requested calendar date.
     * @throws NotMarthException if the command has no valid date.
     */
    private LocalDate parseOnDate(String command) throws NotMarthException {
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new NotMarthException("The on command needs a date. Try: on <date>");
        }

        try {
            return DateTimeParser.parseDate(dateText);
        } catch (DateTimeParseException exception) {
            throw new NotMarthException(
                    "That date is not valid. Try yyyy-mm-dd or dd/MM/yyyy, for example: "
                            + "2019-10-15 or 15/10/2019",
                    exception);
        }
    }

    /**
     * Parses the keyword from a {@code find <keyword>} command.
     *
     * @param command the command containing the search keyword.
     * @return the keyword to search for.
     * @throws NotMarthException if the command has no keyword.
     */
    private String parseFindKeyword(String command) throws NotMarthException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NotMarthException("The find command needs a keyword. Try: find <keyword>");
        }
        return keyword;
    }

    /**
     * Parses a task number from a command that operates on a task.
     *
     * @param command the complete command entered by the user.
     * @param commandName the command keyword used in the error message.
     * @return the requested task number.
     * @throws NotMarthException if the command does not contain an integer.
     */
    private int parseTaskNumber(String command, String commandName) throws NotMarthException {
        try {
            return Integer.parseInt(command.substring(commandName.length()).trim());
        } catch (NumberFormatException exception) {
            throw new NotMarthException(
                    commandName.substring(0, 1).toUpperCase() + commandName.substring(1)
                            + " needs a task number, for example: " + commandName + " 1",
                    exception);
        }
    }
}
