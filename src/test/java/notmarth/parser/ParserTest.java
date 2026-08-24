package notmarth.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.nio.file.Path;
import java.util.List;

import notmarth.command.Command;
import notmarth.command.ExitCommand;
import notmarth.command.MarkCommand;
import notmarth.command.OnCommand;
import notmarth.exception.NotMarthException;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.model.ToDo;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests conversion of user command text into commands and typed task values. */
class ParserTest {
    private final Parser parser = new Parser();

    @TempDir
    Path temporaryDirectory;

    @Test
    void parseCreatesTheCorrectTaskSubtypeAndValues() throws Exception {
        TaskList tasks = new TaskList(List.of(), 3);
        Storage storage = new Storage(temporaryDirectory.resolve("battle-plan.txt").toString());
        Ui ui = new Ui();

        execute(parser.parse("todo borrow book"), tasks, ui, storage);
        execute(parser.parse("deadline return book /by 2/12/2019 1800"), tasks, ui, storage);
        execute(parser.parse("event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600"),
                tasks, ui, storage);

        Task todo = tasks.get(0);
        Task deadline = tasks.get(1);
        Task event = tasks.get(2);

        assertInstanceOf(ToDo.class, todo);
        assertEquals("borrow book", todo.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), ((Deadline) deadline).getBy());
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 0), ((Event) event).getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 15, 16, 0), ((Event) event).getTo());
    }

    @Test
    void parseRecognizesControlCommandsAndDateQueries() throws NotMarthException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 2"));
        assertInstanceOf(OnCommand.class, parser.parse("on 2019-10-15"));
    }

    @Test
    void parseRejectsMissingAndUnknownCommands() {
        NotMarthException empty = assertThrows(NotMarthException.class, () -> parser.parse(""));
        NotMarthException unknown = assertThrows(NotMarthException.class, () -> parser.parse("launch mission"));

        assertEquals("Please enter a command. Try todo, deadline, event, list, on, mark, unmark, or delete.",
                empty.getMessage());
        assertEquals("I don't recognize that command. Try todo, deadline, event, list, on, mark, unmark, or delete.",
                unknown.getMessage());
    }

    @Test
    void parseRejectsMalformedTaskAndTaskNumberCommands() {
        assertThrows(NotMarthException.class, () -> parser.parse("todo"));
        assertThrows(NotMarthException.class, () -> parser.parse("deadline report /by"));
        assertThrows(NotMarthException.class,
                () -> parser.parse("event meeting /from 2019-10-15 1400 /to 2019-10-14 1400"));
        assertThrows(NotMarthException.class, () -> parser.parse("mark two"));
        assertThrows(NotMarthException.class, () -> parser.parse("on 2019-10-15 1400"));
    }

    private void execute(Command command, TaskList tasks, Ui ui, Storage storage) throws NotMarthException {
        command.execute(tasks, ui, storage);
    }
}
